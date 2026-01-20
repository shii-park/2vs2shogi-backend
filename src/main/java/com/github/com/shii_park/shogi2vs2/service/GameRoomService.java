package com.github.com.shii_park.shogi2vs2.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.com.shii_park.shogi2vs2.handler.GameWebSocketHandler;
import com.github.com.shii_park.shogi2vs2.model.domain.Board;
import com.github.com.shii_park.shogi2vs2.model.domain.BoardFactory;
import com.github.com.shii_park.shogi2vs2.model.domain.Game;
import com.github.com.shii_park.shogi2vs2.model.domain.Piece;
import com.github.com.shii_park.shogi2vs2.model.domain.Player;
import com.github.com.shii_park.shogi2vs2.model.domain.PlayerDropPiece;
import com.github.com.shii_park.shogi2vs2.model.domain.PlayerMove;
import com.github.com.shii_park.shogi2vs2.model.domain.Position;
import com.github.com.shii_park.shogi2vs2.model.domain.TurnExecutionResult;
import com.github.com.shii_park.shogi2vs2.model.domain.action.DropAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.GameAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.MoveAction;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * ゲームルーム管理サービス
 * 2vs2将棋のゲームルームの初期化、メッセージ処理、ゲームロジックの実行を管理します。
 */
@Service
public class GameRoomService {

    /**
     * 入力統合サービス
     */
    @Autowired
    private InputSynthesisService synthesisService;
    
    /**
     * ゲームコンテキストサービス
     */
    @Autowired
    private GameContextService gameContextService;
    
    /**
     * 通知サービス
     */
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 座標変換サービス
     */
    @Autowired
    private BoardCoordinateService coordinateService;
    
    /**
     * ゲーム時間管理サービス
     */
    @Autowired
    private GameTimeService gameTimeService;
    
    /**
     * WebSocketハンドラー
     */
    @Autowired
    private GameWebSocketHandler webSocketHandler;
    
    /**
     * JSONパーサー
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * ゲームIDとゲームインスタンスのマップ
     */
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    /**
     * ゲームを初期化します。
     * プレイヤーをチームに割り当て、将棋盤を作成し、ゲームを開始します。
     * 
     * @param gameId ゲームID
     * @param connectedSessions 接続済みのWebSocketセッションリスト
     * @param orderedUserIds 予約順のユーザーIDリスト（0,1番目がFIRST、2,3番目がSECOND）
     */
    public void initializeGame(String gameId, List<WebSocketSession> connectedSessions, List<String> orderedUserIds) {
        // セッションマップを作成
        Map<String, WebSocketSession> sessionMap = new HashMap<>();
        for (WebSocketSession s : connectedSessions) {
            String uid = (String) s.getAttributes().get("userId");
            sessionMap.put(uid, s);
        }

        // 予約順に基づいてチーム割り当て（0,1番目がFIRST、2,3番目がSECOND）
        gameContextService.assignTeam(gameId, orderedUserIds.get(0), "FIRST");
        gameContextService.assignTeam(gameId, orderedUserIds.get(1), "FIRST");
        gameContextService.assignTeam(gameId, orderedUserIds.get(2), "SECOND");
        gameContextService.assignTeam(gameId, orderedUserIds.get(3), "SECOND");

        // プレイヤーリストを作成
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String uid = orderedUserIds.get(i);
            Team team = (i < 2) ? Team.FIRST : Team.SECOND;
            playerList.add(new Player(uid, team));
        }

        // 将棋盤を作成
        Board board = BoardFactory.createBoard(); 

        // ゲームインスタンスを作成して管理マップに追加
        Game game = new Game(gameId, playerList, board, Team.FIRST);
        games.put(gameId, game);

        // ゲーム開始を通知してターンタイマーを開始
        notificationService.broadcastGameStart(gameId);
        gameTimeService.startNewTurn(gameId);
    }

    /**
     * クライアントからのメッセージを処理します。
     * 移動リクエスト、駒打ちリクエスト、フェーズ終了などのメッセージタイプを処理します。
     * 
     * @param gameId ゲームID
     * @param userId ユーザーID
     * @param jsonPayload JSONペイロード
     */
    public void handleMessage(String gameId, String userId, String jsonPayload) {
        try {
            // JSONをパースしてメッセージタイプを取得
            JsonNode root = objectMapper.readTree(jsonPayload);
            String type = root.path("type").asText();
            String teamId = gameContextService.getUserTeam(gameId, userId);

            // メッセージタイプに応じて処理を振り分け
            switch (type) {
                case "moveRequest":
                case "dropPieceRequire":
                    processAction(gameId, userId, teamId, root, type);
                    break;
                case "phaseEnd":
                    handlePhaseEnd(gameId);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * プレイヤーのアクション（移動または駒打ち）を処理します。
     * パートナーのアクションと統合し、両方揃った場合は実行します。
     * 
     * @param gameId ゲームID
     * @param userId ユーザーID
     * @param teamId チームID
     * @param root JSONノード
     * @param type アクションタイプ
     */
    private void processAction(String gameId, String userId, String teamId, JsonNode root, String type) {
        // JSONをGameActionオブジェクトに変換
        GameAction action = convertToGameAction(userId, teamId, root, type);
        if (action == null) return;

        // パートナーのアクションと統合
        List<GameAction> actions = synthesisService.handleActionInput(gameId, teamId, action);

        if (actions == null) {
            // パートナーの入力待ち
            notificationService.sendToUser(gameId, userId, "{\"status\":\"WAITING_PARTNER\"}");
        } else {
            // 両方のアクションが揃ったので実行
            gameTimeService.stopTimer(gameId);
            executeStoredActions(gameId, actions);
        }
    }

    /**
     * フェーズ終了を処理します。
     * 新しいターンを開始します。
     * 
     * @param gameId ゲームID
     */
    private void handlePhaseEnd(String gameId) {
        // 新しいターンを開始
        gameTimeService.startNewTurn(gameId);
    }

    /**
     * タイムアウトを処理します。
     * 入力されているアクションのみを強制的に実行し、タイムアウトを通知します。
     * 
     * @param gameId ゲームID
     */
    public void handleTimeout(String gameId) {
        // ゲームインスタンスを取得
        Game game = games.get(gameId);
        if(game == null)return;

        // タイマーを停止
        gameTimeService.stopTimer(gameId);

        // 現在のターンのチームの未実行アクションを強制取得
        String currentTeamId = game.getCurrentTurn().name();
        List<GameAction> pendingActions = synthesisService.forceRetrieveInputs(gameId, currentTeamId);
        
        // 未実行アクションがあれば実行
        if (pendingActions != null && !pendingActions.isEmpty()) {
            executeStoredActions(gameId, pendingActions);
        }
        
        // タイムアウトを全プレイヤーに通知
        notificationService.broadcastTimeout(gameId, pendingActions);
    }

    /**
     * 保存されたアクションを実行します。
     * 移動または駒打ちアクションをゲームに適用し、結果を通知します。
     * 
     * @param gameId ゲームID
     * @param actions 実行するアクションのリスト
     */
    private void executeStoredActions(String gameId, List<GameAction> actions) {
        // ゲームインスタンスを取得
        Game game = games.get(gameId);
        if (game == null) return;

        List<TurnExecutionResult> results = new ArrayList<>();

        // 各アクションを順次実行
        for (GameAction act : actions) {
            Player player = game.getPlayer(act.getUserId());
            if (player == null) continue;

            if (act instanceof MoveAction move) {
                // 移動アクションの処理
                PieceType targetType = PieceType.valueOf(move.pieceType());
                Piece piece = game.getBoard().getPiece(move.pieceId(), targetType);
                
                if (piece != null) {
                    // 移動コマンドを作成してゲームに適用
                    PlayerMove moveCommand = new PlayerMove(player, piece, move.directions(), move.promote());
                    game.applyMove(moveCommand);
                    
                    // 通知用の結果データを作成
                    List<String> dirStrings = new ArrayList<>();
                    move.directions().forEach(d -> dirStrings.add(d.name()));

                    results.add(new TurnExecutionResult(
                        "moveResult",
                        piece.getId(),
                        piece.getType().name(),
                        dirStrings,
                        player.getTeam().name(),
                        move.promote()
                    ));
                }

            } else if (act instanceof DropAction drop) {
                // 駒打ちアクションの処理
                PieceType pType = PieceType.valueOf(drop.pieceType());

                // プレイヤーの持ち駒から指定された種類の駒を検索
                Piece pieceToDrop = null;
                List<Piece> hand = game.getBoard().getCapturedPieces().getCapturedPieces(player.getTeam());
                
                if (hand != null) {
                    for (Piece p : hand) {
                        if (p.getType() == pType) {
                            pieceToDrop = p;
                            break;
                        }
                    }
                }

                if (pieceToDrop != null) {
                    // 駒打ちコマンドを作成してゲームに適用
                    PlayerDropPiece dropCommand = new PlayerDropPiece(player, pieceToDrop, drop.position());
                    game.applyDrop(dropCommand);

                    results.add(new TurnExecutionResult(
                        "dropResult",
                        pieceToDrop.getId(),
                        pieceToDrop.getType().name(),
                        new ArrayList<>(),
                        player.getTeam().name(),
                        false
                    ));
                }
            }
        }
        
        // ターン終了処理を実行して結果を通知
        game.handleTurnEnd();
        notificationService.broadcastTurnResult(gameId, results);
    }

    /**
     * JSONノードをGameActionオブジェクトに変換します。
     * 
     * @param userId ユーザーID
     * @param teamId チームID
     * @param root JSONノード
     * @param type アクションタイプ（"moveRequest" または "dropPieceRequire"）
     * @return GameActionオブジェクト
     */
    private GameAction convertToGameAction(String userId, String teamId, JsonNode root, String type) {
        Team team = Team.valueOf(teamId);

        if ("moveRequest".equals(type)) {
            // 移動リクエストの処理
            int dx = root.path("direction").path("x").asInt();
            int dy = root.path("direction").path("y").asInt();
            
            int pieceId = root.path("piece").path("id").asInt();
            String pieceType = root.path("piece").path("type").asText();
            boolean promote = root.path("promote").asBoolean();

            // 移動ベクトルをDirection列に変換
            List<Direction> rawDirs = convertToDirectionList(dx, dy);
            
            // チームに応じて方向を正規化
            List<Direction> normalizedDirs = new ArrayList<>();
            for (Direction d : rawDirs) {
                normalizedDirs.add(d.forTeam(team));
            }

            return new MoveAction(userId, teamId, pieceId, pieceType, normalizedDirs, promote, Instant.now());

        } else {
            // 駒打ちリクエストの処理
            int x = root.path("position").path("x").asInt();
            int y = root.path("position").path("y").asInt();
            String pieceType = root.path("piece").asText();

            Position pos = new Position(x, y);
            
            // SECONDチームの場合は座標を正規化
            if (team == Team.SECOND) {
                pos = coordinateService.normalize(pos, teamId);
            }

            return new DropAction(userId, teamId, pieceType, pos, Instant.now());
        }
    }

    /**
     * 移動ベクトル（dx, dy）をDirection列に変換します。
     * 桂馬の動きや1マス移動は完全一致で処理し、長距離移動は単位ベクトルを繰り返します。
     * 
     * @param dx X方向の移動量
     * @param dy Y方向の移動量
     * @return Direction列
     */
    private List<Direction> convertToDirectionList(int dx, int dy) {
        List<Direction> dirs = new ArrayList<>();

        // 1. 完全一致を探す（桂馬の動きや1マス移動）
        for (Direction d : Direction.values()) {
            if (d.dx == dx && d.dy == dy) {
                dirs.add(d);
                return dirs; 
            }
        }

        // 2. 長距離移動の処理
        // 単位ベクトルを計算
        int unitX = Integer.signum(dx);
        int unitY = Integer.signum(dy);

        // 移動マス数を計算
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        // 単位ベクトルに対応するDirectionを検索
        Direction unitDir = null;
        for (Direction d : Direction.values()) {
            if (d.dx == unitX && d.dy == unitY) {
                unitDir = d;
                break;
            }
        }

        // 3. 移動マス数分だけDirectionを追加（例: [UP, UP, UP]）
        if (unitDir != null) {
            for (int i = 0; i < steps; i++) {
                dirs.add(unitDir);
            }
        }

        return dirs;
    }

    /**
     * ユーザーをゲームルームに参加させます。
     * 
     * @param gameId ゲームID
     * @param userId ユーザーID
     * @param session WebSocketセッション
     */
    public void joinRoom(String gameId, String userId, WebSocketSession session) {
        // セッションをゲームルームに追加
        webSocketHandler.addSession(gameId, session);
    }
}
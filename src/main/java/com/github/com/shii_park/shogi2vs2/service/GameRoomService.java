package com.github.com.shii_park.shogi2vs2.service;

import java.time.Instant;
import java.util.ArrayList;
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

@Service
public class GameRoomService {

    @Autowired
    private InputSynthesisService synthesisService;
    @Autowired
    private GameContextService gameContextService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private BoardCoordinateService coordinateService;
    @Autowired
    private GameTimeService gameTimeService;
    @Autowired
    private GameWebSocketHandler webSocketHandler;
    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, Game> games = new ConcurrentHashMap<>();

    public void initializeGame(String gameId, List<WebSocketSession> players) {
        gameContextService.assignTeam(gameId, (String) players.get(0).getAttributes().get("userId"), "FIRST");
        gameContextService.assignTeam(gameId, (String) players.get(1).getAttributes().get("userId"), "FIRST");
        gameContextService.assignTeam(gameId, (String) players.get(2).getAttributes().get("userId"), "SECOND");
        gameContextService.assignTeam(gameId, (String) players.get(3).getAttributes().get("userId"), "SECOND");

        for (WebSocketSession s : players) {
            s.getAttributes().put("gameId", gameId);
            webSocketHandler.addSession(gameId, s);
        }

        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            String userId = (String) players.get(i).getAttributes().get("userId");
            Team team = (i < 2) ? Team.FIRST : Team.SECOND;
            playerList.add(new Player(userId, team));
        }

        // Board作成 
        Board board = BoardFactory.createBoard(); 

        Game game = new Game(gameId, playerList, board, Team.FIRST);
        games.put(gameId, game);

        notificationService.broadcastGameStart(gameId);
        gameTimeService.startNewTurn(gameId);
    }

    public void handleMessage(String gameId, String userId, String jsonPayload) {
        try {
            JsonNode root = objectMapper.readTree(jsonPayload);
            String type = root.path("type").asText();
            String teamId = gameContextService.getUserTeam(gameId, userId);

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

    private void processAction(String gameId, String userId, String teamId, JsonNode root, String type) {
        GameAction action = convertToGameAction(userId, teamId, root, type);
        if (action == null) return;

        List<GameAction> actions = synthesisService.handleActionInput(gameId, teamId, action);

        if (actions == null) {
            notificationService.sendToUser(gameId, userId, "{\"status\":\"WAITING_PARTNER\"}");
        } else {
            gameTimeService.stopTimer(gameId);
            executeStoredActions(gameId, actions);
        }
    }

    private void handlePhaseEnd(String gameId) {
        gameTimeService.startNewTurn(gameId);
    }

    public void handleTimeout(String gameId) {
        Game game = games.get(gameId);
        if(game == null)return;

        gameTimeService.stopTimer(gameId);

        String currentTeamId = game.getCurrentTurn().name();
        List<GameAction> pendingActions = synthesisService.forceRetrieveInputs(gameId, currentTeamId);
        
        if (pendingActions != null && !pendingActions.isEmpty()) {
            executeStoredActions(gameId, pendingActions);
        }
        
        notificationService.broadcastTimeout(gameId, pendingActions);
    }

    private void executeStoredActions(String gameId, List<GameAction> actions) {
        Game game = games.get(gameId);
        if (game == null) return;

        List<TurnExecutionResult> results = new ArrayList<>();

        for (GameAction act : actions) {
            Player player = game.getPlayer(act.getUserId());
            if (player == null) continue;

            if (act instanceof MoveAction move) {
                PieceType targetType = PieceType.valueOf(move.pieceType());
                Piece piece = game.getBoard().getPiece(move.pieceId(), targetType);
                
                if (piece != null) {
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
                // --- Drop ---
                PieceType pType = PieceType.valueOf(drop.pieceType());

                // ★修正: 既存の getCapturedPieces() を使って、リストから自力で探す
                // (新しいメソッド実装は不要！)
                Piece pieceToDrop = null;
                List<Piece> hand = game.getBoard().getCapturedPieces().getCapturedPieces(player.getTeam());
                
                // リストから欲しい種類の駒を1つ探す
                if (hand != null) {
                    for (Piece p : hand) {
                        if (p.getType() == pType) {
                            pieceToDrop = p;
                            break; // 見つかったらループ終了
                        }
                    }
                }

                if (pieceToDrop != null) {
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
        game.handleTurnEnd();
        notificationService.broadcastTurnResult(gameId, results);
    }

    private GameAction convertToGameAction(String userId, String teamId, JsonNode root, String type) {
        Team team = Team.valueOf(teamId);

        if ("moveRequest".equals(type)) {
            int dx = root.path("direction").path("x").asInt();
            int dy = root.path("direction").path("y").asInt();
            
            int pieceId = root.path("piece").path("id").asInt();
            String pieceType = root.path("piece").path("type").asText();
            boolean promote = root.path("promote").asBoolean();

            List<Direction> rawDirs = convertToDirectionList(dx, dy);
            List<Direction> normalizedDirs = new ArrayList<>();
            for (Direction d : rawDirs) {
                normalizedDirs.add(d.forTeam(team));
            }

            return new MoveAction(userId, teamId, pieceId, pieceType, normalizedDirs, promote, Instant.now());

        } else {
            int x = root.path("position").path("x").asInt();
            int y = root.path("position").path("y").asInt();
            String pieceType = root.path("piece").asText();

            Position pos = new Position(x, y);
            if (team == Team.SECOND) {
                pos = coordinateService.normalize(pos, teamId);
            }

            return new DropAction(userId, teamId, pieceType, pos, Instant.now());
        }
    }

    private List<Direction> convertToDirectionList(int dx, int dy) {
        List<Direction> dirs = new ArrayList<>();

        // 1. まず「完全一致」を探す
        // (桂馬、または1マスだけの移動ならここで即決)
        for (Direction d : Direction.values()) {
            if (d.dx == dx && d.dy == dy) {
                dirs.add(d);
                return dirs; 
            }
        }

        // 2. 完全一致しない = 「長距離移動 (スライド)」
        // まず、1マスあたりの方向(単位ベクトル)を出す
        int unitX = Integer.signum(dx); // 例: 0
        int unitY = Integer.signum(dy); // 例: 1

        // 移動回数（距離）を計算
        // 将棋の動きなら、dxかdyの絶対値の大きい方が「マス数」になる
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        // その方向のDirectionを探す
        Direction unitDir = null;
        for (Direction d : Direction.values()) {
            if (d.dx == unitX && d.dy == unitY) {
                unitDir = d;
                break;
            }
        }

        // 3. 距離の分だけリストに追加する [UP, UP, UP]
        if (unitDir != null) {
            for (int i = 0; i < steps; i++) {
                dirs.add(unitDir);
            }
        }

        return dirs;
    }

    public void joinRoom(String gameId, String userId, WebSocketSession session) {
        webSocketHandler.addSession(gameId, session);
    }
}
package com.github.com.shii_park.shogi2vs2.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ロジック側のクラス
import com.github.com.shii_park.shogi2vs2.model.domain.Game;
import com.github.com.shii_park.shogi2vs2.model.domain.Player;
import com.github.com.shii_park.shogi2vs2.model.domain.Piece;
import com.github.com.shii_park.shogi2vs2.model.domain.PlayerMove;
import com.github.com.shii_park.shogi2vs2.model.domain.PlayerDropPiece;

// サービス側のクラス
import com.github.com.shii_park.shogi2vs2.model.domain.Position;
import com.github.com.shii_park.shogi2vs2.model.domain.TurnExecutionResult;
import com.github.com.shii_park.shogi2vs2.model.domain.action.DropAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.GameAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.MoveAction;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.com.shii_park.shogi2vs2.handler.GameWebSocketHandler;

@Service
public class GameRoomService {
    @Autowired
    private InputSynthesisService synthesisService;       //二人の入力をまとめるサービス
    @Autowired
    private GameContextService gameContextService;        //ユーザがどのゲームのどのチームかを管理するサービス
    @Autowired
    private NotificationService notificationService;      //WebSocketを経由して全員または個人に通知を送るサービス
    @Autowired
    private BoardCoodinateService coodinateService;  //secondチームの座標を反転させるサービス
    @Autowired
    private GameTimeService gameTimeService;              //ターン制限時間を管理するサービス
    @Autowired
    private GameWebSocketHandler gameWebSocketHandler;    //WebSocketセッション管理サービス
    @Autowired
    private ObjectMapper objectMapper;                    //JSON文字列をJavaオブジェクトに変換する

    private final Map<String, Game> games = new ConcurrentHashMap<>();

    // ゲーム開始時に一度だけ呼び出す。
    public void initializeGame(String gameId, List<WebSocketSession> players){
        //チーム分け
        gameContextService.assignTeam(gameId, (String)players.get(0).getAttributes().get("userId"), "FIRST");
        gameContextService.assignTeam(gameId, (String)players.get(1).getAttributes().get("userId"), "FIRST");
        gameContextService.assignTeam(gameId, (String)players.get(2).getAttributes().get("userId"), "SECOND");
        gameContextService.assignTeam(gameId, (String)players.get(3).getAttributes().get("userId"), "SECOND");

        //セッション登録
        for(WebSocketSession s : players){
            s.getAttributes().put("gameId",gameId);
            webSocketHandler.addSession(gameId, s);
        }

        //ゲームを作成 國廣に聞く
        Game game = new Game(gameId,playerList,board,Team.FIRST);
        games.put(gameId,game);

        notificationService.broadcastGameStart(gameId);
        gameTimeService.startNewTurn(gameId);

    }

    //WebSocketで届いた文字列をゲーム用の処理に振り分ける
    public void handleMessage(String gameId, String userId,String jsonPayload){
        try {
            JsonNode root = objectMapper.readTree(jsonPayload);
            String type = root.path("type").asText();
            String teamId = gameContextService.getUserTeam(gameId,userId);

            switch(type){
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
        //JSON -> GameAction (座標をTeam1視点に正規化)
        GameAction action = convertToGameAction(userId, teamId, root, type);

        //Redisで合議(1人目->待機戻り値null、2人目->二人分返す)
        List<GameAction> actions = synthesisService.handleActionInput(gameId, teamId, action);

        if (actions == null) {
            notificationService.sendToUser(gameId, userId, "{\"status\":\"WAITING_PARTNER\"}");
        } else {
            //揃った -> タイマー停止 & 実行
            gameTimeService.stopTimer(gameId);
            Game game = games.get(gameId);
            
            List<TurnExecutionResult> results = new ArrayList<>();

            
            // ★重要: 型を見て、個別のメソッドを呼び出す
            for (GameAction act : actions) {

                Player player = game.getPlayer(act.getUserId());
                if(player == null) continue;

                if (act instanceof MoveAction move) {
                    Piece piece = game.getBoard().getPieceById(move.pieceId());
                    PlayerMove pm = new PlayerMove(player, piece,move.diirections(),move.promote());
                    game.applyMove(pm);

                    // フロントへの結果用データ作成
                    List<String> dirStrings = new ArrayList<>();
                    move.directions().forEach(d -> dirStrings.add(d.name()));

                    results.add(new TurnExecutionResult(
                        "moveResult",
                        String.valueOf(move.pieceId()),
                        dirStrings,
                        player.getTeam().name(),
                        move.promote()
                    ));

                } else if (act instanceof DropAction drop) {
                    // --- 打つ指令 ---
                    PlayerDropPiece pdp = new PlayerDropPiece(player, drop.pieceType(), drop.position());
                    
                    // 実行 (void - 予約リストへ追加)
                    game.applyDrop(pdp);

                    // 結果用データ作成
                    results.add(new TurnExecutionResult(
                        "dropResult",
                        drop.pieceType(),
                        new ArrayList<>(),
                        player.getTeam().name(),
                        false
                    ));
                }
            }
            // ターン終了処理
            game.handleTurnEnd();
            // 結果通知 (Team2用に反転して送信)
            notificationService.broadcastTurnResult(gameId, results);
        }
    }

    private void handlePhaseEnd(String gameId){
        gameTimeService.startNewTurn(gameId);
    }

    public void handleTimeout(String gameId){
        gameTimeService.stopTimer(gameId);
        String currentTeamId = "";//gameから取得國廣
        List<GameAction> pendingActions = synthesisService.forceRetrieveInputs(gameId, currentTeamId);
        notificationService.broadcastTimeout(gameId, pendingActions);
    }


    private GameAction convertToGameAction(String userId, String teamId, JsonNode root, String type) {
        boolean isTeam2 = "SECOND".equals(teamId); // または "TEAM_2"

        if ("moveRequest".equals(type)) {
            // --- 移動指令 ---
            int dx = root.path("direction").path("x").asInt();
            int dy = root.path("direction").path("y").asInt();
            int pieceId = root.path("piece").asInt(); 
            boolean promote = root.path("promote").asBoolean();

            // ★Team2なら入力方向を反転
            if (isTeam2) {
                dx = -dx;
                dy = -dy;
            }
            
            // (dx, dy) を [UP, UP, UP] のようなリストに変換
            List<Direction> dirs = convertToDirectionList(dx, dy);

            return new MoveAction(userId, teamId, pieceId, dirs, promote, Instant.now());

        } else {
            // --- 打つ指令 ---
            int x = root.path("position").path("x").asInt();
            int y = root.path("position").path("y").asInt();
            String pieceType = root.path("piece").asText();

            Position pos = new Position(x, y);
            // ★Team2なら座標を180度回転 (正規化)
            if (isTeam2) {
                pos = boardcoordinateService.normalize(pos, teamId);
            }

            return new DropAction(userId, teamId, pieceType, pos, Instant.now());
        }
    }

    /**
     * dx, dy (移動量) を受け取り、Directionのリストに変換する
     * 例: (0, 3) -> [UP, UP, UP]
     * 例: (1, 2) -> [KNIGHT_RIGHT]
     */
    private List<Direction> convertToDirectionList(int dx, int dy) {
        List<Direction> list = new ArrayList<>();
        if (dx == 0 && dy == 0) return list;

        // 1. 桂馬 (Knight) の判定
        for (Direction d : Direction.values()) {
            if (isKnight(d) && d.dx == dx && d.dy == dy) {
                list.add(d);
                return list;
            }
        }

        // 2. 通常の移動 (歩・香・飛・角・銀・金・王)
        int signX = Integer.signum(dx);
        int signY = Integer.signum(dy);

        // 向きが一致するDirectionを探す
        Direction unitDir = null;
        for (Direction d : Direction.values()) {
            // 桂馬は除外して、向き(sign)だけで判定
            if (!isKnight(d) && d.dx == signX && d.dy == signY) {
                unitDir = d;
                break;
            }
        }

        if (unitDir == null) return list; // 定義外の移動

        // ステップ数 (距離)
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        // ステップ数分だけリストに追加 (例: UP, UP, UP)
        for (int i = 0; i < steps; i++) {
            list.add(unitDir);
        }

        return list;
    }

    private boolean isKnight(Direction d) {
        return d == Direction.KNIGHT_LEFT || d == Direction.KNIGHT_RIGHT;
    }

    // --- 部屋管理 ---
    public void joinRoom(String gameId, String userId, WebSocketSession session) {
        webSocketHandler.addSession(gameId, session);
    }

}

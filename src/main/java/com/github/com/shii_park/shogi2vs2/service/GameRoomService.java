// package com.github.com.shii_park.shogi2vs2.service;

// import java.time.Instant;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// // ロジック側のクラス
// import com.github.com.shii_park.shogi2vs2.model.domain.Game;
// import com.github.com.shii_park.shogi2vs2.model.domain.Player;
// import com.github.com.shii_park.shogi2vs2.model.domain.Piece;
// import com.github.com.shii_park.shogi2vs2.model.domain.PlayerMove;
// import com.github.com.shii_park.shogi2vs2.model.domain.PlayerDropPiece;

// // サービス側のクラス
// import com.github.com.shii_park.shogi2vs2.model.domain.Position;
// import com.github.com.shii_park.shogi2vs2.model.domain.TurnExecutionResult;
// import com.github.com.shii_park.shogi2vs2.model.domain.action.DropAction;
// import com.github.com.shii_park.shogi2vs2.model.domain.action.GameAction;
// import com.github.com.shii_park.shogi2vs2.model.domain.action.MoveAction;
// import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
// import com.github.com.shii_park.shogi2vs2.model.enums.Team;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.socket.WebSocketSession;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.github.com.shii_park.shogi2vs2.handler.GameWebSocketHandler;

// @Service
// public class GameRoomService {
// @Autowired
// private InputSynthesisService synthesisService; //二人の入力をまとめるサービス
// @Autowired
// private GameContextService gameContextService; //ユーザがどのゲームのどのチームかを管理するサービス
// @Autowired
// private NotificationService notificationService;
// //WebSocketを経由して全員または個人に通知を送るサービス
// @Autowired
// private BoardCoodinateService coodinateService; //secondチームの座標を反転させるサービス
// @Autowired
// private GameTimeService gameTimeService; //ターン制限時間を管理するサービス
// @Autowired
// private GameWebSocketHandler gameWebSocketHandler; //WebSocketセッション管理サービス
// @Autowired
// private ObjectMapper objectMapper; //JSON文字列をJavaオブジェクトに変換する

// private final Map<String, Game> games = new ConcurrentHashMap<>();

// // ゲーム開始時に一度だけ呼び出す。
// public void initializeGame(String gameId, List<WebSocketSession> players){
// //チーム分け
// gameContextService.assignTeam(gameId,
// (String)players.get(0).getAttributes().get("userId"), "FIRST");
// gameContextService.assignTeam(gameId,
// (String)players.get(1).getAttributes().get("userId"), "FIRST");
// gameContextService.assignTeam(gameId,
// (String)players.get(2).getAttributes().get("userId"), "SECOND");
// gameContextService.assignTeam(gameId,
// (String)players.get(3).getAttributes().get("userId"), "SECOND");

// //セッション登録
// for(WebSocketSession s : players){
// s.getAttributes().put("gameId",gameId);
// webSocketHandler.addSession(gameId, s);
// }

// //ゲームを作成 國廣に聞く
// Game game = new Game(gameId,playerList,board,Team.FIRST);
// games.put(gameId,game);

// notificationService.broadcastGameStart(gameId);
// gameTimeService.startNewTurn(gameId);

// }

// //WebSocketで届いた文字列をゲーム用の処理に振り分ける
// public void handleMessage(String gameId, String userId,String jsonPayload){
// try {
// JsonNode root = objectMapper.readTree(jsonPayload);
// String type = root.path("type").asText();
// String teamId = gameContextService.getUserTeam(gameId,userId);

// switch(type){
// case "moveRequest":
// case "dropPieceRequire":
// processAction(gameId, userId, teamId, root, type);
// break;
// case "phaseEnd":
// handlePhaseEnd(gameId);
// break;
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }

// private void processAction(String gameId, String userId, String teamId,
// JsonNode root, String type) {
// //JSON -> GameAction (座標をTeam1視点に正規化)
// GameAction action = convertToGameAction(userId, teamId, root, type);

// //Redisで合議(1人目->待機戻り値null、2人目->二人分返す)
// List<GameAction> actions = synthesisService.handleActionInput(gameId, teamId,
// action);

// if (actions == null) {
// notificationService.sendToUser(gameId, userId,
// "{\"status\":\"WAITING_PARTNER\"}");
// } else {
// //揃った -> タイマー停止 & 実行
// gameTimeService.stopTimer(gameId);
// Game game = games.get(gameId);

// List<TurnExecutionResult> results = new ArrayList<>();

// // ★重要: 型を見て、個別のメソッドを呼び出す
// for (GameAction act : actions) {

// Player player = game.getPlayer(act.getUserId());
// if(player == null) continue;

// if (act instanceof MoveAction move) {
// Piece piece = game.getBoard().getPieceById(move.pieceId());
// PlayerMove pm = new PlayerMove(player,
// piece,move.diirections(),move.promote());
// game.applyMove(pm);

// // フロントへの結果用データ作成
// List<String> dirStrings = new ArrayList<>();
// move.directions().forEach(d -> dirStrings.add(d.name()));

// results.add(new TurnExecutionResult(
// "moveResult",
// String.valueOf(move.pieceId()),
// dirStrings,
// player.getTeam().name(),
// move.promote()
// ));

// } else if (act instanceof DropAction drop) {
// // --- 打つ指令 ---
// PlayerDropPiece pdp = new PlayerDropPiece(player, drop.pieceType(),
// drop.position());

// // 実行 (void - 予約リストへ追加)
// game.applyDrop(pdp);

// // 結果用データ作成
// results.add(new TurnExecutionResult(
// "dropResult",
// drop.pieceType(),
// new ArrayList<>(),
// player.getTeam().name(),
// false
// ));
// }
// }
// // ターン終了処理
// game.handleTurnEnd();
// // 結果通知 (Team2用に反転して送信)
// notificationService.broadcastTurnResult(gameId, results);
// }
// }

// private void handlePhaseEnd(String gameId){
// gameTimeService.startNewTurn(gameId);
// }

// public void handleTimeout(String gameId){
// gameTimeService.stopTimer(gameId);
// String currentTeamId = "";//gameから取得國廣
// List<GameAction> pendingActions =
// synthesisService.forceRetrieveInputs(gameId, currentTeamId);
// notificationService.broadcastTimeout(gameId, pendingActions);
// }

// private GameAction convertToGameAction(String userId, String teamId, JsonNode
// root, String type) {

// if ("moveRequest".equals(type)) {
// int dx = root.path("direction").path("x").asInt();
// int dy = root.path("direction").path("y").asInt();
// int pieceId = root.path("piece").asInt();
// boolean promote = root.path("promote").asBoolean();

// // そのまま「ユーザーが見たままの方向」としてリスト化します
// List<Direction> rawDirs = convertToDirectionList(dx, dy);

// // Streamを使って新しいリストを作ります (forEachでの書き換えはできないため)
// List<Direction> normalizedDirs = new ArrayList<>();
// for (Direction d : rawDirs) {
// normalizedDirs.add(d.normalize(teamId));
// }

// return new MoveAction(userId, teamId, pieceId, normalizedDirs, promote,
// Instant.now());

// } else {
// // ... DropAction側は変更なし (座標計算が必要なため) ...
// // (もしDropもEnum管理するなら同様ですが、現状は座標なので8-xのまま)
// int x = root.path("position").path("x").asInt();
// int y = root.path("position").path("y").asInt();
// String pieceType = root.path("piece").asText();

// Position pos = new Position(x, y);
// if ("SECOND".equals(teamId)) {
// pos = coordinateService.normalize(pos, teamId);
// }
// return new DropAction(userId, teamId, pieceType, pos, Instant.now());
// }
// }

// /**
// * dx, dy (移動量) を受け取り、Directionのリストに変換する
// * 例: (0, 3) -> [UP, UP, UP]
// * 例: (1, 2) -> [KNIGHT_RIGHT]
// */
// private TurnExecutionResult reverseResult(TurnExecutionResult original) {
// List<String> reversedDirs = new ArrayList<>();
// for (String dStr : original.directions()) {
// // 文字列からEnumに戻す
// Direction dir = Direction.valueOf(dStr);

// // ★修正点: 「Team2視点」に変換するために normalize("SECOND") を使う
// // (入力の時と同じメソッドを使えば、反対の反対で元に戻る原理です)
// reversedDirs.add(dir.normalize("SECOND").name());
// }
// return new TurnExecutionResult(original.type(), original.pieceId(),
// reversedDirs, original.team(), original.promoted());
// }

// private GameAction reverseAction(GameAction action) {
// if (action instanceof MoveAction m) {
// List<Direction> rDirs = new ArrayList<>();
// for(Direction d : m.directions()) {
// // ★修正点: ここも normalize("SECOND") に統一
// rDirs.add(d.normalize("SECOND"));
// }
// return new MoveAction(m.userId(), m.teamId(), m.pieceId(), rDirs,
// m.promote(), m.at());
// }
// // ... DropAction はそのまま ...
// else if (action instanceof DropAction d) {
// Position rPos = new Position(8 - d.position().x(), 8 - d.position().y());
// return new DropAction(d.userId(), d.teamId(), d.pieceType(), rPos, d.at());
// }
// return action;
// }

// private boolean isKnight(Direction d) {
// return d == Direction.KNIGHT_LEFT || d == Direction.KNIGHT_RIGHT;
// }

// // --- 部屋管理 ---
// public void joinRoom(String gameId, String userId, WebSocketSession session)
// {
// webSocketHandler.addSession(gameId, session);
// }

// }

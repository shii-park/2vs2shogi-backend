package com.github.com.shii_park.shogi2vs2.handler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.github.com.shii_park.shogi2vs2.service.GameManagementService; // ★追加
import com.github.com.shii_park.shogi2vs2.service.GameRoomService;

/**
 * ゲーム用WebSocketハンドラー
 * WebSocket接続の確立、メッセージの送受信、切断処理を管理する
 * 4人のプレイヤーが揃ったらゲームを開始する
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    @Lazy
    private GameRoomService gameRoomService;

    @Autowired
    private GameManagementService gameManagementService; // ★追加: 予約確認用

    /** ゲームIDごとにセッションのリストを管理するマップ */
    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();

    /**
     * WebSocket接続が確立された時の処理
     * クエリパラメータからgameIdとuserIdを取得し、正当なプレイヤーか検証する
     * 4人のプレイヤーが揃った場合にゲームを開始する
     * 
     * @param session WebSocketセッション
     * @throws Exception 処理中に発生した例外
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = parseQuery(session.getUri());
        String gameId = params.get("gameId");
        String userId = params.get("userId");

        // ★修正: GameManagementServiceを使って、正当なプレイヤーか確認する
        if (gameId != null && userId != null && gameManagementService.isValidPlayer(gameId, userId)) {
            
            // セッションに属性として保存
            session.getAttributes().put("gameId", gameId);
            session.getAttributes().put("userId", userId);

            // セッションリストに追加
            addSession(gameId, session);
            System.out.println("Connected: " + userId + " to Game: " + gameId);

            // 4人揃ったらゲーム開始
            List<WebSocketSession> sessions = gameSessions.get(gameId);
            
            // 同時に複数の接続が来て size > 4 になるのを防ぐため、厳密に4人のときだけ発火させるのが安全
            if (sessions.size() == 4) {
                System.out.println("All players connected. Starting game: " + gameId);
                
                // ★追加: 予約時の正しい並び順(チーム分け用)を取得
                List<String> orderedUserIds = gameManagementService.getReservedPlayers(gameId);
                
                // ★修正: 引数に orderedUserIds を追加して呼び出す
                gameRoomService.initializeGame(gameId, sessions, orderedUserIds);
                
                // メモリ節約のため予約情報は削除
                gameManagementService.removePendingGame(gameId);
            }
        } else {
            // 無効な接続（予約されていない、IDがない等）は切断
            System.out.println("Invalid connection attempt: " + userId + " for game " + gameId);
            session.close(CloseStatus.BAD_DATA);
        }
    }

    /**
     * WebSocketメッセージを受信した時の処理
     * 受信したメッセージをGameRoomServiceに転送して処理する
     * 
     * @param session WebSocketセッション
     * @param message 受信したテキストメッセージ
     * @throws Exception 処理中に発生した例外
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String gameId = (String) session.getAttributes().get("gameId");
        String userId = (String) session.getAttributes().get("userId");
        String payload = message.getPayload();

        if (gameId != null && userId != null) {
            gameRoomService.handleMessage(gameId, userId, payload);
        }
    }

    /**
     * WebSocket接続が切断された時の処理
     * セッションをリストから削除し、空になった部屋を削除する
     * 
     * @param session WebSocketセッション
     * @param status 切断のステータス
     * @throws Exception 処理中に発生した例外
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String gameId = (String) session.getAttributes().get("gameId");
        String userId = (String) session.getAttributes().get("userId");

        if (gameId != null) {
            List<WebSocketSession> sessions = gameSessions.get(gameId);
            if (sessions != null) {
                sessions.remove(session);
                
                // メモリリーク対策: 誰もいなくなったら部屋ごとマップから消す
                if (sessions.isEmpty()) {
                    gameSessions.remove(gameId);
                }
            }
            // ゲーム中の切断処理 (再接続や不戦敗など) があれば呼び出す
            // gameRoomService.handleDisconnect(gameId, userId);
            
            System.out.println("Disconnected: " + userId + " from " + gameId);
        }
    }

    // --- 公開メソッド ---

    /**
     * 指定されたゲームにセッションを追加する
     * スレッドセーフにリストを作成・追加する
     * 
     * @param gameId ゲームID
     * @param session 追加するWebSocketセッション
     */
    public void addSession(String gameId, WebSocketSession session) {
        // computeIfAbsent でスレッドセーフにリスト作成
        gameSessions.computeIfAbsent(gameId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    /**
     * 指定されたゲームのセッションリストを取得する
     * 
     * @param gameId ゲームID
     * @return WebSocketセッションのリスト
     */
    public List<WebSocketSession> getSessions(String gameId) {
        return gameSessions.get(gameId);
    }

    /**
     * URIのクエリパラメータを解析してMapに変換する
     * 
     * @param uri 解析するURI
     * @return パラメータ名と値のマップ
     */
    private Map<String, String> parseQuery(URI uri) {
        Map<String, String> queryPairs = new ConcurrentHashMap<>();
        String query = uri.getQuery();
        
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = pair.substring(0, idx);
                    String value = pair.substring(idx + 1);

                    String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
                    String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8);

                    queryPairs.put(decodedKey, decodedValue);
                }
            }
        }
        return queryPairs;
    }
}
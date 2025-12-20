package com.github.com.shii_park.shogi2vs2.handler;

import java.net.URI;
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

import com.github.com.shii_park.shogi2vs2.service.GameRoomService;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    // 循環参照回避のため @Lazy を付与
    @Autowired
    @Lazy
    private GameRoomService gameRoomService;

    // ゲームIDごとにセッションのリストを管理するマップ
    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();

    /**
     * 接続確立時
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = parseQuery(session.getUri());
        String gameId = params.get("gameId");
        String userId = params.get("userId");

        if (gameId != null && userId != null) {
            // セッションに属性として保存
            session.getAttributes().put("gameId", gameId);
            session.getAttributes().put("userId", userId);

            // セッションリストに追加
            addSession(gameId, session);
            System.out.println("Connected: " + userId + " to Game: " + gameId);

            // 4人揃ったらゲーム開始
            List<WebSocketSession> sessions = gameSessions.get(gameId);
            if (sessions.size() == 4) {
                System.out.println("Starting game: " + gameId);
                gameRoomService.initializeGame(gameId, sessions);
            }
        } else {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    /**
     * メッセージ受信時
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
     * 切断時
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
            // 必要なら gameRoomService.leaveRoom(...) を呼ぶ
            System.out.println("Disconnected: " + userId + " from " + gameId);
        }
    }

    // --- 公開メソッド (NotificationServiceなどが使用) ---

    public void addSession(String gameId, WebSocketSession session) {
        // computeIfAbsent でスレッドセーフにリスト作成
        gameSessions.computeIfAbsent(gameId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    public List<WebSocketSession> getSessions(String gameId) {
        return gameSessions.get(gameId);
    }

    // --- 内部ヘルパー: URLクエリパラメータ解析 ---
    private Map<String, String> parseQuery(URI uri) {
        Map<String, String> queryPairs = new ConcurrentHashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    queryPairs.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
        }
        return queryPairs;
    }
}
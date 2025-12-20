package com.github.com.shii_park.shogi2vs2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.com.shii_park.shogi2vs2.handler.GameWebSocketHandler;
import com.github.com.shii_park.shogi2vs2.model.domain.TurnExecutionResult;
import com.github.com.shii_park.shogi2vs2.model.domain.action.DropAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.GameAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.MoveAction;
import com.github.com.shii_park.shogi2vs2.model.domain.Position;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

@Service
public class NotificationService {

    @Autowired
    private GameContextService gameContextService;
    @Autowired
    private GameWebSocketHandler webSocketHandler;
    @Autowired
    private ObjectMapper objectMapper;

    // ゲーム開始
    public void broadcastGameStart(String gameId) {
        broadcastRaw(gameId, String.format("{\"type\":\"gameStart\",\"gameId\":\"%s\"}", gameId));
    }

    // ターン実行結果 (反転あり)
    public void broadcastTurnResult(String gameId, List<TurnExecutionResult> results) {
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null || sessions.isEmpty()) return;

        try {
            // 1. Team1用 (そのまま) のメッセージを作る
            String jsonNormal = objectMapper.writeValueAsString(results);
            TextMessage msgNormal = new TextMessage(String.format("{\"type\":\"moveResult\",\"data\":%s}", jsonNormal));

            // 2. Team2用 (反転済み) のメッセージを作る
            List<TurnExecutionResult> reversedResults = new ArrayList<>();
            for (TurnExecutionResult res : results) {
                reversedResults.add(reverseResult(res));
            }
            String jsonReversed = objectMapper.writeValueAsString(reversedResults);
            TextMessage msgReversed = new TextMessage(String.format("{\"type\":\"moveResult\",\"data\":%s}", jsonReversed));

            // 3. 配る
            for (WebSocketSession s : sessions) {
                String userId = (String) s.getAttributes().get("userId");
                String teamId = gameContextService.getUserTeam(gameId, userId);
                boolean isTeam2 = "SECOND".equals(teamId);

                if (s.isOpen()) {
                    s.sendMessage(isTeam2 ? msgReversed : msgNormal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // タイムアウト (反転あり)
    public void broadcastTimeout(String gameId, List<GameAction> pendingActions) {
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null || sessions.isEmpty()) return;

        try {
            // 1. Team1用メッセージ
            String jsonNormal = objectMapper.writeValueAsString(pendingActions);
            TextMessage msgNormal = new TextMessage(String.format("{\"type\":\"timeUp\", \"actions\":%s}", jsonNormal));

            // 2. Team2用メッセージ
            List<GameAction> reversedActions = new ArrayList<>();
            for (GameAction act : pendingActions) {
                reversedActions.add(reverseAction(act));
            }
            String jsonReversed = objectMapper.writeValueAsString(reversedActions);
            TextMessage msgReversed = new TextMessage(String.format("{\"type\":\"timeUp\", \"actions\":%s}", jsonReversed));

            // 3. 配る
            for (WebSocketSession s : sessions) {
                String userId = (String) s.getAttributes().get("userId");
                String teamId = gameContextService.getUserTeam(gameId, userId);
                boolean isTeam2 = "SECOND".equals(teamId);

                if (s.isOpen()) {
                    s.sendMessage(isTeam2 ? msgReversed : msgNormal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToUser(String gameId, String userId, String message) {
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null) return;
        
        for(WebSocketSession s : sessions) {
            if(userId.equals(s.getAttributes().get("userId"))) {
                try {
                    if (s.isOpen()) s.sendMessage(new TextMessage(message));
                } catch(Exception e){ e.printStackTrace(); }
            }
        }
    }

    private void broadcastRaw(String gameId, String message) {
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null) return;
        
        TextMessage textMessage = new TextMessage(message);
        for(WebSocketSession s : sessions) {
            try {
                if (s.isOpen()) s.sendMessage(textMessage);
            } catch(Exception e){ e.printStackTrace(); }
        }
    }

    // --- 反転ロジック (変更なし) ---
    private TurnExecutionResult reverseResult(TurnExecutionResult original) {
        List<String> reversedDirs = new ArrayList<>();
        for (String d : original.directions()) {
            reversedDirs.add(reverseDirString(d));
        }
        return new TurnExecutionResult(original.type(), original.pieceId(), reversedDirs, original.team(), original.promoted());
    }
    
    private GameAction reverseAction(GameAction action) {
        if (action instanceof MoveAction m) {
            List<Direction> rDirs = new ArrayList<>();
            for(Direction d : m.directions()) rDirs.add(d.reverse());
            return new MoveAction(m.userId(), m.teamId(), m.pieceId(), rDirs, m.promote(), m.at());
        } else if (action instanceof DropAction d) {
            // 将棋盤は0-8なので、8-xで反転
            Position rPos = new Position(8 - d.position().x(), 8 - d.position().y());
            return new DropAction(d.userId(), d.teamId(), d.pieceType(), rPos, d.at());
        }
        return action;
    }

    private String reverseDirString(String dir) {
        try { return Direction.valueOf(dir).reverse().name(); } catch(Exception e) { return dir; }
    }
}
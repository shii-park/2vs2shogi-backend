package com.github.com.shii_park.shogi2vs2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.com.shii_park.shogi2vs2.handler.GameWebSocketHandler;
import com.github.com.shii_park.shogi2vs2.model.domain.Position;
import com.github.com.shii_park.shogi2vs2.model.domain.TurnExecutionResult;
import com.github.com.shii_park.shogi2vs2.model.domain.action.DropAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.GameAction;
import com.github.com.shii_park.shogi2vs2.model.domain.action.MoveAction;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * WebSocket通知サービス
 * ゲームイベントをWebSocketを通じて各プレイヤーに通知します。
 * チームごとに座標や方向を反転させて配信します。
 */
@Service
public class NotificationService {

    /**
     * ゲームコンテキストサービス
     */
    @Autowired
    private GameContextService gameContextService;
    
    /**
     * WebSocketハンドラー
     */
    @Autowired
    private GameWebSocketHandler webSocketHandler;
    
    /**
     * 座標変換サービス
     */
    @Autowired
    private BoardCoordinateService coordinateService;
    
    /**
     * JSONシリアライザー
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * ゲーム開始を全プレイヤーに通知します。
     * 
     * @param gameId ゲームID
     */
    public void broadcastGameStart(String gameId) {
        // ゲーム開始通知を全プレイヤーに配信
        broadcastRaw(gameId, String.format("{\"type\":\"gameStart\",\"gameId\":\"%s\"}", gameId));
    }

    /**
     * ターン実行結果を全プレイヤーに通知します。
     * チームごとに座標と方向を反転させて配信します。
     * 
     * @param gameId ゲームID
     * @param results ターン実行結果のリスト
     */
    public void broadcastTurnResult(String gameId, List<TurnExecutionResult> results) {
        // ゲームに参加している全セッションを取得
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null || sessions.isEmpty())
            return;

        try {
            // FIRSTチーム用の通常メッセージを作成
            String jsonNormal = objectMapper.writeValueAsString(results);
            TextMessage msgNormal = new TextMessage(String.format("{\"type\":\"moveResult\",\"data\":%s}", jsonNormal));

            // SECONDチーム用に座標と方向を反転したメッセージを作成
            List<TurnExecutionResult> reversedResults = new ArrayList<>();
            for (TurnExecutionResult res : results) {
                reversedResults.add(reverseResult(res));
            }
            String jsonReversed = objectMapper.writeValueAsString(reversedResults);
            TextMessage msgReversed = new TextMessage(
                    String.format("{\"type\":\"moveResult\",\"data\":%s}", jsonReversed));

            // 各プレイヤーのチームに応じて適切なメッセージを送信
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

    /**
     * タイムアウトを全プレイヤーに通知します。
     * 未実行のアクションと共に通知します。
     * 
     * @param gameId ゲームID
     * @param pendingActions 未実行のアクションリスト
     */
    public void broadcastTimeout(String gameId, List<GameAction> pendingActions) {
        // ゲームに参加している全セッションを取得
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null || sessions.isEmpty())
            return;

        try {
            // FIRSTチーム用の通常メッセージを作成
            String jsonNormal = objectMapper.writeValueAsString(pendingActions);
            TextMessage msgNormal = new TextMessage(String.format("{\"type\":\"timeUp\", \"actions\":%s}", jsonNormal));

            // SECONDチーム用にアクションを反転したメッセージを作成
            List<GameAction> reversedActions = new ArrayList<>();
            if (pendingActions != null) {
                for (GameAction act : pendingActions) {
                    reversedActions.add(reverseAction(act));
                }
            }
            String jsonReversed = objectMapper.writeValueAsString(reversedActions);
            TextMessage msgReversed = new TextMessage(
                    String.format("{\"type\":\"timeUp\", \"actions\":%s}", jsonReversed));

            // 各プレイヤーのチームに応じて適切なメッセージを送信
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

    /**
     * 特定のユーザーにメッセージを送信します。
     * 
     * @param gameId ゲームID
     * @param userId ユーザーID
     * @param message 送信するメッセージ
     */
    public void sendToUser(String gameId, String userId, String message) {
        // ゲームに参加している全セッションを取得
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null)
            return;

        // 指定されたユーザーのセッションを検索してメッセージを送信
        for (WebSocketSession s : sessions) {
            if (userId.equals(s.getAttributes().get("userId"))) {
                try {
                    if (s.isOpen())
                        s.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生のメッセージを全プレイヤーに配信します。
     * 座標や方向の反転は行いません。
     * 
     * @param gameId ゲームID
     * @param message 送信するメッセージ
     */
    private void broadcastRaw(String gameId, String message) {
        // ゲームに参加している全セッションを取得
        List<WebSocketSession> sessions = webSocketHandler.getSessions(gameId);
        if (sessions == null)
            return;

        // 全セッションにメッセージを配信
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession s : sessions) {
            try {
                if (s.isOpen())
                    s.sendMessage(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ターン実行結果を反転します（SECONDチーム用）。
     * 方向情報を180度回転させます。
     * 
     * @param original 元のターン実行結果
     * @return 反転されたターン実行結果
     */
    private TurnExecutionResult reverseResult(TurnExecutionResult original) {
        // 方向情報をSECONDチーム用に反転
        List<String> reversedDirs = new ArrayList<>();
        for (String dStr : original.directions()) {
            try {
                Direction dir = Direction.valueOf(dStr);
                reversedDirs.add(dir.forTeam(Team.SECOND).name());
            } catch (IllegalArgumentException e) {
                // 無効な方向の場合はそのまま保持
                reversedDirs.add(dStr);
            }
        }
        return new TurnExecutionResult(
                original.type(),
                original.pieceId(),
                original.pieceType(),
                reversedDirs,
                original.teamId(),
                original.promote());
    }

    /**
     * ゲームアクションを反転します（SECONDチーム用）。
     * 移動の場合は方向を、配置の場合は座標を反転させます。
     * 
     * @param action 元のゲームアクション
     * @return 反転されたゲームアクション
     */
    private GameAction reverseAction(GameAction action) {
        if (action instanceof MoveAction m) {
            // 移動アクションの方向を反転
            List<Direction> rDirs = new ArrayList<>();
            for (Direction d : m.directions()) {
                rDirs.add(d.forTeam(Team.SECOND));
            }
            return new MoveAction(
                    m.userId(),
                    m.teamId(),
                    m.pieceId(),
                    m.pieceType(),
                    rDirs,
                    m.promote(),
                    m.at());

        } else if (action instanceof DropAction d) {
            // 配置アクションの座標を反転
            Position rPos = coordinateService.normalize(d.position(), "SECOND");

            return new DropAction(
                    d.userId(),
                    d.teamId(),
                    d.pieceType(),
                    rPos,
                    d.at());
        }
        return action;
    }
}
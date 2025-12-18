package com.github.com.shii_park.shogi2vs2.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.com.shii_park.shogi2vs2.dto.request.MoveRequest;
import com.github.com.shii_park.shogi2vs2.service.GameContextService;
import com.github.com.shii_park.shogi2vs2.service.InputSynthesisService;
import com.github.com.shii_park.shogi2vs2.service.GameTimeService;
@Component
public class GameWebSocketHandler extends TextWebSocketHandler{
    @Autowired
    private InputSynthesisService synthesisService;

    @Autowired
    private GameContextService gameContextService;
    @Autowired
    private GameTimeService gameTimeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String gameId = extractParam(query, "gameId");
        String userId = extractParam(query, "userId");
        
        if (gameId != null && userId != null){
            session.getAttributes().put("gameId",gameId);
            session.getAttributes().put("userId",userId);
            gameSessions.computeIfAbsent(gameId, k-> new CopyOnWriteArrayList<>()).add(session);
            System.out.println("Connected:" + userId +" to Game: "+ gameId);
        } else {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception {
        String gameId = (String)session.getAttributes().get("gameId");

        if(gameId != null){
            List<WebSocketSession> sessions = gameSessions.get(gameid);
            if(sessions != null){
                sessions.remove(session);
                if(sessions.isEmpty()){
                    gameSessions.get(gameId).remove(session);
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)throws Exception {
        String payload = message.getPayload();
        MoveRequest request = objectMapper.readValue(payload, MoveRequest.class);
        String gameId = (String)session.getAttributes().get("gameId");
        String userId = (String)session.getAttributes().get("userId");

        if(gameId == null) return;
        String teamId = gameContextService.getUserTeam(gameId, userId);
        
        position normalizedFrom = boardCoordinateService.normalize(request.getFrom(), teamId);
        position normalizedTo = boardCoordinateService.normalize(request.getTo(), teamId);
        MoveRequest normalizedRequest = new MoveRequest(
            request.getUserId(),
            normalizedFrom,
            normalizedTo,
            request.isPromote()
        );

        List<MoveRequest> moves = synthesisService.handleInput(gameId, teamId, request);
        if (moves == null){
            session.sendMessage(new TextMessage("{\"status\":\"WAITING_PARTNER\"}"));
        }else{
            MoveRequest adoptedMove = normalizedRequest;
            broadcastMoveResult(gameId,adoptedMove);
            gameTimeService.startNewTurn(gameId);
        }
    }

    public void broadcastMoveResult(String gameId, MoveRequest executedMove){
        List<WebSocketSession> sessions = gameSessions.get(gameId);
        if(sessions = null) return;

        for(WebSocketSession session : sessions){
            if(!session.isOpen()) continue;
        

            try{
                String userId = (String) session.getAttributes().get("userId");
                String teamId = gameContextService.getUserTeam(gameId, userId);

                MoveRequest viewMove;

                if("second".equals(teamId)){
                    Position viewedFrom  = broadCoordinateService.normalize(executedMove.getFrom(),"second");
                    Position viewedTo = broadCoordinateService.normalize(executedMove.getTo(),"second");

                    viewMove = new MoveRequest(
                        executedMove.getUserId(),
                        viewedFrom,
                        viewedTo,
                        executedMove.isPromote()
                    );
                } else{
                    viewMove = executedMove;
                }

                String resultJson = objectMapper.writeValueAsString(viewMove);
                String responseMessage = String.format("{\"type\":\"MOVE_EXECUTED\",\"data\":%s}", resultJson);

                session.sendMessage(new TextMessage(responseMessage));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void broadcastToGame(String gameId, String message){
        List<WebSocketSession> sessions = gameSessions.get(gameId);
        if (sessions != null){
            for(WebSocketSession s : sessions){
                if(s.isOpen()){
                    try{
                        s.sendMessage(new TextMessage(message));
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String extractParam(String query, String key){
        if(query == null)return null;
        for(String param:query.split("&")){
            String[] pair = param.split("=");
            if(pair.length == 2 && pair[0].equals(key)){
                return pair[1];
            }
        }
        return null;
    }

    public void handleTimeout(String gameId){
        GameTimeService.startNewTurn(gameId);

        String timeUpMessage = "{\"type\":\"TIME_UP\",\"message\":\"Time is up for your turn.\"}";
        broadcastToGame(gameId,timeUpMessage);
    }
}

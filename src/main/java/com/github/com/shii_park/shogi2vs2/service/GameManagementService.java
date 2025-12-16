package com.github.com.shii_park.shogi2vs2.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameManagementService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GameContextService gameContextService;

    private static final long GAME_TTL_MINUTES = 60;

    //4人そろったらゲームを作成し、チームに分ける
    public String createGameForPlayers(List<String> playerIds){
        String gameId = UUID.randomUUID().toString();

        String team1Id = "TEAM_1";
        String team2Id = "TEAM_2";

        assignPlayerToGame(gameId,playerIds.get(0), team1Id);
        assignPlayerToGame(gameId,playerIds.get(1), team1Id);

        assignPlayerToGame(gameId,playerIds.get(2), team2Id);
        assignPlayerToGame(gameId,playerIds.get(3), team2Id);

        String gameStatusKey = "game:" + gameId + ":status";
        redisTemplate.opsForValue().set(gameStatusKey,"STARTED",GAME_TTL_MINUTES,TimeUnit.MINUTES);
        return gameId;
    }

    private void assignPlayerToGame(String gameId, String userId, String teamId){
        gameContextService.assignTeam(gameId, userId, teamId);
    }
}

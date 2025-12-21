package com.github.com.shii_park.shogi2vs2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.github.com.shii_park.shogi2vs2.dto.response.GameStatusResponse;

@Service
public class MatchingService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GameManagementService gameManagementService;

    private static final String MATCHING_QUEUE_KEY = "matching:queue";
    private static final String USER_MATCH_STATUS_KEY_PREFIX = "matching:status:";
    private static final int PLAYERS_PER_GAME = 4;

    //マッチングキューにユーザを追加する
    public void joinQueue(String userId){
        redisTemplate.opsForList().rightPush(MATCHING_QUEUE_KEY, userId);
        String statusKey = USER_MATCH_STATUS_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(statusKey,"WAITING");
        redisTemplate.expire(statusKey, 30,TimeUnit.MINUTES);

        tryMatchMaking();
    }
    
    //人数がそろったらゲームを作成する
    private synchronized void tryMatchMaking(){
        Long queueSize = redisTemplate.opsForList().size(MATCHING_QUEUE_KEY);
        if(queueSize != null && queueSize >= PLAYERS_PER_GAME){
            List<String> players = new ArrayList<>();
            for(int i = 0 ; i < PLAYERS_PER_GAME; i++){
                String userId = (String) redisTemplate.opsForList().leftPop(MATCHING_QUEUE_KEY);
                if(userId != null){
                    players.add(userId);
                }
            }

            if(players.size()== PLAYERS_PER_GAME){
                String gameId = gameManagementService.createGameForPlayers(players);

                for(String userId : players){
                    String statusKey = USER_MATCH_STATUS_KEY_PREFIX + userId;
                    redisTemplate.opsForValue().set(statusKey,"MATCHED:" + gameId);
                }
            }
        }
    }

    //ユーザがマッチング状況を確認する
    public GameStatusResponse checkStatus(String userId){
        String statusKey = USER_MATCH_STATUS_KEY_PREFIX + userId;
        String statusVal = redisTemplate.opsForValue().get(statusKey);

        if(statusVal == null){
            return new GameStatusResponse("NOT_QUEUED",null);
        }
        if(statusVal.startsWith("MATCHED:")){
            String gameId = statusVal.split(":")[1];
            return new GameStatusResponse("MATCHED",gameId);
        }else{
            return new GameStatusResponse("WAITING",null);
        }
    }
}

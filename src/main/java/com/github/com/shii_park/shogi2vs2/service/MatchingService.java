package com.github.com.shii_park.shogi2vs2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.github.com.shii_park.shogi2vs2.dto.response.GameStatusResponse;

/**
 * プレイヤーのマッチング処理を管理するサービス
 * キューへの参加、マッチング状態の確認、ゲーム作成などを行う
 */
@Service
public class MatchingService {
    /**
     * Redisテンプレート
     * マッチングキューと状態管理に使用
     */
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * ゲーム管理サービス
     * マッチング成立時のゲーム作成に使用
     */
    @Autowired
    private GameManagementService gameManagementService;

    /** マッチングキューのRedisキー */
    private static final String MATCHING_QUEUE_KEY = "matching:queue";
    
    /** ユーザーマッチ状態のRedisキープレフィックス */
    private static final String USER_MATCH_STATUS_KEY_PREFIX = "matching:status:";
    
    /** ゲームあたりの必要プレイヤー数 */
    private static final int PLAYERS_PER_GAME = 4;

    /**
     * マッチングキューにユーザーを追加する
     * ユーザーの状態を「WAITING」に設定し、マッチング処理を試みる
     * 
     * @param userId 参加するユーザーのID
     */
    public void joinQueue(String userId){
        redisTemplate.opsForList().rightPush(MATCHING_QUEUE_KEY, userId);
        String statusKey = USER_MATCH_STATUS_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(statusKey,"WAITING");
        redisTemplate.expire(statusKey, 30,TimeUnit.MINUTES);

        tryMatchMaking();
    }
    
    /**
     * 人数が揃ったらゲームを作成する
     * キューから必要人数分のプレイヤーを取り出し、ゲームを作成する
     * スレッドセーフのためsynchronizedで同期化
     */
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

    /**
     * ユーザーがマッチング状況を確認する
     * 
     * @param userId 確認するユーザーのID
     * @return マッチング状態とゲームID（マッチング済みの場合）
     */
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

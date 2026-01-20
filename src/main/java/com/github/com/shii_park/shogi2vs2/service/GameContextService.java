package com.github.com.shii_park.shogi2vs2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * ゲームコンテキスト管理サービス
 * Redisを使用してゲーム内のユーザーとチームの関連付けを管理します。
 */
@Service
public class GameContextService {
    /**
     * Redisテンプレート
     */
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 指定されたユーザーが所属するチームIDを取得します。
     * 
     * @param gameId ゲームID
     * @param userId ユーザーID
     * @return チームID（"FIRST" または "SECOND"）、存在しない場合はnull
     */
    public String getUserTeam(String gameId, String userId){
        // Redisキーを構築してユーザーのチーム情報を取得
        String key = "game:" + gameId + ":user";
        Object teamObj = redisTemplate.opsForHash().get(key,userId);
        return teamObj != null ? teamObj.toString() : null;
    }

    /**
     * ユーザーにチームを割り当てます。
     * 
     * @param gameId ゲームID
     * @param userId ユーザーID
     * @param teamId チームID（"FIRST" または "SECOND"）
     */
    public void assignTeam(String gameId, String userId, String teamId){
        // Redisキーを構築してユーザーのチーム情報を保存
        String key = "game:" + gameId + ":user";
        redisTemplate.opsForHash().put(key,userId, teamId);
    }

    /**
     * ゲームコンテキストをクリアします。
     * ゲーム終了時にRedisからゲーム情報を削除します。
     * 
     * @param gameId ゲームID
     */
    public void clearGameContext(String gameId) {
        // Redisからゲームコンテキスト情報を削除
        String key = "game:" + gameId + ":users";
        redisTemplate.delete(key);
    }
}
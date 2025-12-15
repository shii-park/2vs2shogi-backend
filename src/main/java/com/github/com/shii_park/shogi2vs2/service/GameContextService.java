package com.github.com.shii_park.shogi2vs2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameContextService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    public String getUserTeam(String gameId, String userId){
        String key = "game:" + gameId + ":user";
        Object teamObj = redisTemplate.opsForHash().get(key,userId);
        return teamObj != null ? teamObj.toString() : null;
    }


public void assignTeam(String gameId, String userId, String teamId){
    String key = "game:" + gameId + ":user";
    redisTemplate.opsForHash().put(key,userId, teamId);
    }
}
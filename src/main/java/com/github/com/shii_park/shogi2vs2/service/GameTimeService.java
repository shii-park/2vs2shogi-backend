package com.github.com.shii_park.shogi2vs2.service;

import java.time.Instant;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameTimeService {
   @Autowired
   private StringRedisTemplate redisTemplate;

   private static final long TURN_TIME_SECONDS = 30;
   private static final String ACTIVE_GAMES_KEY = "games:active";
   

   public void startNewTurn(String gameId){
      long now = Instant.now().getEpochSecond();
      String timeKey = "game:" + gameId + ":turn_start";
      redisTemplate.opsForValue().set(timeKey, String.valueOf(now));

      redisTemplate.opsForSet().add(ACTIVE_GAMES_KEY,gameId);
   }

   public boolean isTimedOut(String gameId){
      String timeKey = "game:" + gameId + ":turn_start";
      String startTimeStr = redisTemplate.opsForValue().get(timeKey);
      if(startTimeStr==null){
         return false;
      }

      long startTime = Long.parseLong(startTimeStr);
      long now = Instant.now().getEpochSecond();
      return (now - startTime) >= TURN_TIME_SECONDS;
   }

   public void finishGame(String gameId){
      redisTemplate.opsForSet().remove(ACTIVE_GAMES_KEY,gameId);
      redisTemplate.delete("game:" + gameId + ":turn_start");
   }

   public Set<String> getAllActiveGameIds(){
      return redisTemplate.opsForSet().members(ACTIVE_GAMES_KEY);
   }
}

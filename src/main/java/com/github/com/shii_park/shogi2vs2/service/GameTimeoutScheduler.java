package com.github.com.shii_park.shogi2vs2.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.com.shii_park.shogi2vs2.handler.GameWebSocketHandler;

@Component
public class GameTimeoutScheduler {
   @Autowired
   private GameTimeService gameTimeService;
   @Autowired
   private GameWebSocketHandler gameWebSocketHandler;

   @Scheduled(fixedRate = 1000) // 1秒ごとに実行
   public void checkTimeouts(){
    Set<String> activeGameIds = gameTimeService.getAllActiveGameIds();
    if(activeGameIds == null || activeGameIds.isEmpty()) return;

    for(String gameId : activeGameIds){
        if(gameTimeService.isTimedOut(gameId)){
            // タイムアウト処理
            gameWebSocketHandler.handleTimeout(gameId);
            }
        }
    }


}
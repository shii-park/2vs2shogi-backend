package com.github.com.shii_park.shogi2vs2.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class GameTimeService {

    // 循環参照を防ぐため @Lazy をつけます
    @Autowired
    @Lazy
    private GameRoomService gameRoomService;

    // ゲームIDと「実行予定のタイマー」を紐づけるマップ
    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();
    
    // タイマーを実行する裏方のスレッド（4つあれば十分）
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    // 制限時間 (30秒)
    private static final int TURN_DURATION_SECONDS = 30;

    /**
     * 新しいターンのタイマーを開始する
     */
    public void startNewTurn(String gameId) {
        // 前のターンのタイマーが残っていたら消す
        stopTimer(gameId);

        // 「30秒後に handleTimeout を呼んでねと予約する
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                // 時間が来たらここが実行される
                System.out.println("⏰ タイムアウト発生: " + gameId);
                gameRoomService.handleTimeout(gameId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, TURN_DURATION_SECONDS, TimeUnit.SECONDS);

        // 予約チケットをMapに保存しておく（あとでキャンセルするため）
        timers.put(gameId, future);
    }

    /**
     * タイマーを止める
     */
    public void stopTimer(String gameId) {
        ScheduledFuture<?> future = timers.remove(gameId);
        if (future != null) {
            // 予約を取り消す（false = もし実行中なら中断まではしない）
            future.cancel(false);
        }
    }
}
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

import jakarta.annotation.PreDestroy;

/**
 * ゲームのターン時間を管理するサービス
 * 各ゲームのターンタイマーの開始、停止、タイムアウト処理を担当
 */
@Service
public class GameTimeService {

    /**
     * ゲームルームサービス
     * 循環参照を防ぐため@Lazyを付与
     */
    @Autowired
    @Lazy
    private GameRoomService gameRoomService;

    /**
     * ゲームIDと実行予定のタイマーを紐づけるマップ
     * key: gameId, value: ScheduledFuture
     */
    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();
    
    /**
     * タイマーを実行するスレッドプール
     * 4つのスレッドで複数ゲームのタイマーを管理
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    /** ターンの制限時間（秒） */
    private static final int TURN_DURATION_SECONDS = 30;

    /**
     * 新しいターンのタイマーを開始する
     * 前のターンのタイマーが残っている場合は停止してから新しいタイマーを開始
     * 
     * @param gameId ゲームID
     */
    public void startNewTurn(String gameId) {
        // 前のターンのタイマーが残っていたら消す
        stopTimer(gameId);

        // 「30秒後に handleTimeout を呼んでねと予約する
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                // 時間が来たらここが実行される
                System.out.println(" タイムアウト発生: " + gameId);
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
     * 指定されたゲームのタイマーをキャンセルする
     * 
     * @param gameId ゲームID
     */
    public void stopTimer(String gameId) {
        ScheduledFuture<?> future = timers.remove(gameId);
        if (future != null) {
            // 予約を取り消す（false = もし実行中なら中断まではしない）
            future.cancel(false);
        }
    }

    /**
     * サービス終了時のクリーンアップ処理
     * スケジューラをシャットダウンし、実行中のタスクを適切に終了させる
     */
    @PreDestroy
    public void cleanup() {
        System.out.println("Stopping timer scheduler...");
        
        // 新しいタスクの受付を停止する
        scheduler.shutdown(); 
        
        try {
            // 現在実行中のタスクが終わるのを少し待つ（必要なら）
            // ここでは即座に強制終了させる shutdownNow() でもゲーム用途ならOK
            if (!scheduler.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // タイムアウトしたら強制終了
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow(); // 割り込みが入ったら強制終了
        }
    }
}
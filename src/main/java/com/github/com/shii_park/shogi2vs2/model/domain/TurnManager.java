package com.github.com.shii_park.shogi2vs2.model.domain;

import java.time.Duration;
import java.time.Instant;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class TurnManager {
    private volatile Team currentTeam;
    private volatile int turnNumber = 0;
    private volatile Instant turnTimer;

    private static final int TIMEOUT = 30;

    public TurnManager(Team firstTeam) {
        this.currentTeam = firstTeam;
        this.turnTimer = Instant.now();
    }

    /**
     * ターンを開始する
     * turnTimerを現在時刻に初期化する
     */
    public void startTurn() {
        this.turnTimer = Instant.now();
    }

    /**
     * 残り時間を返す
     * 
     * @return
     */
    public Duration getTimer() {
        return Duration.between(Instant.now(), turnTimer);
    }

    /**
     * 時間切れかどうかを判定する
     * 
     * @return true:時間切れ
     */
    public boolean isTimeout() {
        return Duration.between(turnTimer, Instant.now()).getSeconds() >= TIMEOUT;
    }

    /**
     * 次のターンに進める
     * turnNumberをインクリメント
     * チームを交代する
     */
    public void nextTurn() {
        turnNumber++;
        this.currentTeam = currentTeam.switchTeam(this.currentTeam);
        startTurn();
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}

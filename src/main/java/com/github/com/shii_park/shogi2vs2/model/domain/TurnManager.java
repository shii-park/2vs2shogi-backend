package com.github.com.shii_park.shogi2vs2.model.domain;

import java.time.Duration;
import java.time.Instant;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * TurnManagerクラスはターンの管理を行うクラスです<br>
 * 現在のターン番号、チーム、タイマーを管理し、ターンの進行や時間切れの判定を行います
 */
public class TurnManager {
    /** 現在ターンのチーム */
    private volatile Team currentTeam;
    /** 現在のターン番号 */
    private volatile int turnNumber = 0;
    /** ターン開始時刻 */
    private volatile Instant turnTimer;

    /** タイムアウト時間(秒) */
    private static final int TIMEOUT = 30;

    /**
     * TurnManagerクラスのコンストラクタ
     * 
     * @param firstTeam 最初のターンのチーム
     */
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
     * @return Duration 残り時間
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
        this.currentTeam = currentTeam.switchTeam();
        startTurn();
    }

    /**
     * 現在ターンのチームを返す
     * 
     * @return ターンのチーム
     */
    public Team getCurrentTurn() {
        return currentTeam;
    }

    /**
     * 現在のターン数を返す
     * 
     * @return ターン数
     */
    public int getTurnNumber() {
        return turnNumber;
    }
}

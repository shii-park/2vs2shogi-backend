package com.github.com.shii_park.shogi2vs2.model.domain;

import java.time.Duration;
import java.time.Instant;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class TurnManager {
    private Team currentTeam;
    private volatile int turnNumber = 0;
    private Instant turnTimer;

    private static final int TIMEOUT = 30;


    public TurnManager(Team firstTeam) {
        this.currentTeam = firstTeam;
        this.turnTimer = Instant.now();
    }

    public void startTurn() {
        this.turnTimer = Instant.now();
    }


    public boolean isTimeout() {
        return Duration.between(turnTimer, Instant.now()).getSeconds() >= TIMEOUT;
    }

    public void nextTurn() {
        turnNumber++;
        this.currentTeam = (this.currentTeam == Team.FIRST) ? Team.SECOND : Team.FIRST;
        startTurn();
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}

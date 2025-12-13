package com.github.com.shii_park.shogi2vs2.model.domain;

import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class Player {
    private final String id;
    private final Team team;
    private boolean resign;
    private boolean connected;

    public Player(String id, Team team) {
        this.id = id;
        this.team = team;
        this.connected = true;
        this.resign = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean v) {
        this.connected = v;
    }

    public String getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }
}

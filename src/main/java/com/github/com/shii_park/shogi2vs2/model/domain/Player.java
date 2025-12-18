package com.github.com.shii_park.shogi2vs2.model.domain;

import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Plyaerクラスはプレイヤーの状態を管理し、それらのゲッター、セッターを提供します
 * 
 * @param id        プレイヤーid
 * @param team      所属チーム
 * @param resign    投了したか
 * @param connected 接続しているか
 * 
 * @author Suiren91
 */
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

    public boolean isResign() {
        return resign;
    }

    public void setResign(boolean v) {
        this.resign = v;
    }
}

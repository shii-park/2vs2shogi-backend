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

    /**
     * Playerクラスのコンストラクタ
     * 
     * @param id プレイヤーid
     * @param team 所属チーム
     */
    public Player(String id, Team team) {
        this.id = id;
        this.team = team;
        this.connected = true;
        this.resign = false;
    }

    /**
     * プレイヤーが接続しているかを返す
     * 
     * @return true:接続中
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * プレイヤーの接続状態を設定する
     * 
     * @param v 接続状態
     */
    public void setConnected(boolean v) {
        this.connected = v;
    }

    /**
     * プレイヤーのidを返す
     * 
     * @return プレイヤーid
     */
    public String getId() {
        return id;
    }

    /**
     * プレイヤーの所属チームを返す
     * 
     * @return 所属チーム
     */
    public Team getTeam() {
        return team;
    }

    /**
     * プレイヤーが投了したかを返す
     * 
     * @return true:投了済み
     */
    public boolean isResign() {
        return resign;
    }

    /**
     * プレイヤーの投了状態を設定する
     * 
     * @param v 投了状態
     */
    public void setResign(boolean v) {
        this.resign = v;
    }
}

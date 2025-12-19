package com.github.com.shii_park.shogi2vs2.model.domain;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Pieceクラスは駒の状態を管理し、ゲッター、セッターを提供します
 * 
 * @param id           駒のid(通し番号(1~9))
 * @param type         駒の種類
 * @param team         駒を保有ているチーム
 * @param isPromoted   駒が成っているか
 * @param isPromotable 駒が成ることが可能化
 * 
 * @author Suiren91
 */
public class Piece {
    private final int id; // TODO: numberに変更
    private final PieceType type;
    private Team team;
    private boolean isPromoted; // 成りの有無
    private final boolean isPromotable; // 成れるかどうか

    public Piece(int id, PieceType type, Team team, boolean promotable) {
        this.id = id;
        this.type = type;
        this.team = team;
        this.isPromoted = false;
        this.isPromotable = promotable;
    }

    public int getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public PieceType getType() {
        return type;
    }

    public boolean isPromoted() {
        return isPromoted;
    }

    public void setTeam(Team t) {
        this.team = t;
    }

    public void setPromoted(boolean p) {
        if (!this.isPromotable) {
            return;
        }
        this.isPromoted = p;
    }

    public boolean isPromotable() {
        return isPromotable;
    }
}

package com.github.com.shii_park.shogi2vs2.model.domain;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;


public class Piece {
    private final int id;
    private final PieceType type;
    private Team team;
    private Position position;
    private boolean isPromoted; // 成りの有無
    private final boolean isPromotable; // 成れるかどうか

    // ownerIdについては要検討
    public Piece(int id, PieceType type, Team team, Position position, boolean promotable) {
        this.id = id;
        this.type = type;
        this.team = team;
        this.position = position;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position p) {
        this.position = p;
    }

    public boolean getPromoted() {
        return isPromoted;
    }

    public void setPromoted(boolean p) {
        this.isPromoted = p;
    }

    public boolean getPromotable() {
        return isPromotable;
    }

    // デバック用
    @Override
    public String toString() {
        return "Piece{" + id + "," + type + ".team=" + team + ",pos=" + position + "}";
    }
}

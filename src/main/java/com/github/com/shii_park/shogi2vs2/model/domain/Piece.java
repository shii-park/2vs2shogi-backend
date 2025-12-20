package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
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
    private final int id;
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

    /**
     * この駒が移動可能な方向のリストを返す
     * チームに応じて方向を調整する
     * 
     * @return 移動可能な方向のリスト
     */
    public List<Direction> getMovableDirections() {
        return type.getMovableDirections(isPromoted).stream()
                .map(dir -> dir.forTeam(team))
                .toList();
    }

    /**
     * 指定方向に移動可能か判定する
     * 
     * @param direction 移動方向
     * @return {@code true}:移動可能
     */
    public boolean canMoveToDirection(Direction direction) {
        return getMovableDirections().contains(direction);
    }

    /**
     * 連続移動可能か判定する
     * 
     * @return {@code true}:連続移動可能
     */
    public boolean canMoveMultipleSteps() {
        return type.canMoveMultipleSteps(isPromoted);
    }

    /**
     * 指定方向に連続移動可能か
     * （飛車は縦横のみ、角行は斜めのみ連続移動可）
     * 
     * @return {@code true}:指定方向に連続移動可能
     */
    public boolean canMoveMultipleStepsInDirection(Direction direction) {
        // チームに応じて方向を逆変換
        Direction normalizedDir = direction.forTeam(team);
        return type.canMoveMultipleStepsInDirection(normalizedDir, isPromoted);
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

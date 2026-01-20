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
    /** 駒のID(通し番号) */
    private final int id;
    /** 駒の種類 */
    private final PieceType type;
    /** 駒を保有しているチーム */
    private Team team;
    /** 成りの有無 */
    private boolean isPromoted;
    /** 成ることが可能かどうか */
    private final boolean isPromotable;

    /**
     * Pieceオブジェクトを生成する
     * 
     * @param id         駒のID
     * @param type       駒の種類
     * @param team       駒を保有しているチーム
     * @param promotable 成ることが可能かどうか
     */
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
     * @param direction 連続移動したい方向
     * @return {@code true}:指定方向に連続移動可能
     */
    public boolean canMoveMultipleStepsInDirection(Direction direction) {
        // チームに応じて方向を逆変換
        Direction normalizedDir = direction.forTeam(team);
        return type.canMoveMultipleStepsInDirection(normalizedDir, isPromoted);
    }

    /**
     * 駒のIDを取得する
     * 
     * @return 駒のID
     */
    public int getId() {
        return id;
    }

    /**
     * 駒を保有しているチームを取得する
     * 
     * @return チーム
     */
    public Team getTeam() {
        return team;
    }

    /**
     * 駒の種類を取得する
     * 
     * @return 駒の種類
     */
    public PieceType getType() {
        return type;
    }

    /**
     * 駒が成っているかどうかを取得する
     * 
     * @return {@code true}:成っている
     */
    public boolean isPromoted() {
        return isPromoted;
    }

    /**
     * 駒のチームを設定する
     * 
     * @param t 設定するチーム
     */
    public void setTeam(Team t) {
        this.team = t;
    }

    /**
     * 駒の成り状態を設定する
     * 成ることができない駒の場合は何もしない
     * 
     * @param p 成り状態
     */
    public void setPromoted(boolean p) {
        if (!this.isPromotable) {
            return;
        }
        this.isPromoted = p;
    }

    /**
     * 駒が成ることが可能かどうかを取得する
     * 
     * @return {@code true}:成ることが可能
     */
    public boolean isPromotable() {
        return isPromotable;
    }
}

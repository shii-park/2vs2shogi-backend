package com.github.com.shii_park.shogi2vs2.model.enums;

/**
 * 将棋盤上の移動方向を表すEnum
 */
public enum Direction {
    /** 上方向 */
    UP(0, +1),
    /** 下方向 */
    DOWN(0, -1),
    /** 左方向 */
    LEFT(-1, 0),
    /** 右方向 */
    RIGHT(+1, 0),
    /** 右上方向 */
    UP_RIGHT(+1, +1),
    /** 左上方向 */
    UP_LEFT(-1, +1),
    /** 右下方向 */
    DOWN_RIGHT(+1, -1),
    /** 左下方向 */
    DOWN_LEFT(-1, -1),
    /** 桂馬の左移動 */
    KNIGHT_LEFT(-1, +2),
    /** 桂馬の右移動 */
    KNIGHT_RIGHT(+1, +2),
    /** 相手側の桂馬の左移動 */
    OPPO_KNIGHT_LEFT(-1, -2),
    /** 相手側の桂馬の右移動 */
    OPPO_KNIGHT_RIGHT(+1, -2);

    /** X方向の移動量 */
    public final int dx;
    /** Y方向の移動量 */
    public final int dy;

    /**
     * コンストラクタ
     * 
     * @param dx X方向の移動量
     * @param dy Y方向の移動量
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * 反対方向を取得する
     * 
     * @return 反対方向
     */
    private Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case UP_RIGHT -> DOWN_LEFT;
            case UP_LEFT -> DOWN_RIGHT;
            case DOWN_RIGHT -> UP_LEFT;
            case DOWN_LEFT -> UP_RIGHT;
            case KNIGHT_LEFT -> OPPO_KNIGHT_RIGHT;
            case KNIGHT_RIGHT -> OPPO_KNIGHT_LEFT;
            case OPPO_KNIGHT_RIGHT -> KNIGHT_LEFT;
            case OPPO_KNIGHT_LEFT -> KNIGHT_RIGHT;
        };
    }

    /**
     * チームによって正しいDirectionを出力
     * Usage: Direction secondDir = dir.forTeam(Team.SECOND)
     * 
     * @param team 移動したいチーム
     * @return FIRSTはそのまま、SECONDは反転した方向
     */
    public Direction forTeam(Team team) {
        return team == Team.SECOND ? opposite() : this;
    }
}

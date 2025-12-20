package com.github.com.shii_park.shogi2vs2.model.enums;

public enum Direction {
    UP(0, +1), DOWN(0, -1), LEFT(-1, 0), RIGHT(+1, 0), UP_RIGHT(+1, +1), UP_LEFT(-1,
            +1),
    DOWN_RIGHT(+1, -1), DOWN_LEFT(-1, -1),
    // 桂馬用
    KNIGHT_LEFT(-1, +2), KNIGHT_RIGHT(+1, +2), OPPO_KNIGHT_LEFT(-1, -2), OPPO_KNIGHT_RIGHT(+1, -2);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

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
            default -> null;
        };
    }

    /**
     * チームによって正しいDirectionを出力
     * 
     * @param team 移動したいチーム
     * @return FIRSTはそのまま、SECONDは反転した方向 (OPPO要素を渡すと{@code null})
     */
    public Direction forTeam(Team team) {
        return team == Team.SECOND ? opposite() : this;
    }
}

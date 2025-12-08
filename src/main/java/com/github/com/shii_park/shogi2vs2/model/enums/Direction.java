package com.github.com.shii_park.shogi2vs2.model.enums;

public enum Direction {
    UP(0, +1), DOWN(0, -1), LEFT(-1, 0), RIGHT(+1, 0),
    // 桂馬用
    KNIGHT_LEFT(-1, +2), KNIGHT_RIGHT(+1, +2);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

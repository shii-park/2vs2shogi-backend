package com.github.com.shii_park.shogi2vs2.model.domain;

public final class Vector {
    private final int dx;
    private final int dy;

    public Vector(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx(){ return dx; }
    public int getDy(){ return dy; }

    // Position, Vectorは移動するたびに再生成する
    public Vector add(Vector o){
        return new Vector(this.dx + o.dx, this.dy + o.dy);
    }

    public static Vector of(Position from, Position to){
        return new Vector(to.getX() - from.getX(), to.getY() - from.getY());
    }
}

package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.Objects;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){ return x; }
    public int getY(){ return y; }

    // Position and Vector are regenerated on each move
    public Position add(Vector v){
        return new Position(this.x + v.getDx(), this.y + v.getDy());
    }

    public Vector subtract(Position other){
        return new Vector(this.x - other.x, this.y - other.y);
    }

    // Determine if two Positions are at the same coordinates
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

    // Debug
    @Override
    public String toString(){
        return "(" + x + "," + y + ")";
    }
}

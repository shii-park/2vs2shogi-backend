package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.Objects;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Positionは移動する度に再生成
    public Position add(Direction dir) {
        return new Position(this.x + dir.dx, this.y + dir.dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Position))
            return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // Debug
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

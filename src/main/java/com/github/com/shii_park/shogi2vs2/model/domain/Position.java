package com.github.com.shii_park.shogi2vs2.model.domain;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

public record Position(int x, int y) {
    // Positionは移動する度に再生成
    public Position add(Direction dir) {
        return new Position(this.x + dir.dx, this.y + dir.dy);
    }
}

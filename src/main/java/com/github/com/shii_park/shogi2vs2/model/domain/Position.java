package com.github.com.shii_park.shogi2vs2.model.domain;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * Positionクラスは駒のマスを示す。不変であり、移動するたびに再生成される
 * 
 * @param x 横方向の位置(左端が1)
 * @param y 縦方向の位置(下端が1)
 * 
 * @author Suiren91
 */
public record Position(int x, int y) {
    /**
     * 指定された方向に移動した新しいPositionを返す
     * 
     * @param dir 移動方向
     * @return 移動後の新しいPosition
     */
    public Position add(Direction dir) {
        return new Position(this.x + dir.dx, this.y + dir.dy);
    }
}

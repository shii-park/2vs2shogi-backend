package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * Positionクラスのテスト
 * 盤面上の座標を表すPositionの生成、移動、等価性を検証
 */
class PositionTest {

    /**
     * Positionの生成テスト
     * 処理: Position(5, 5)を生成し、x座標とy座標が正しく設定されることを確認
     */
    @Test
    void testPositionCreation() {
        // (5, 5)の位置を生成
        Position pos = new Position(5, 5);
        // x座標が5であることを確認
        assertEquals(5, pos.x());
        // y座標が5であることを確認
        assertEquals(5, pos.y());
    }

    /**
     * 方向を加算した新しいPositionの生成テスト
     * 処理: (5, 5)の位置から上方向(UP)に移動した新しい位置が(5, 6)になることを確認
     */
    @Test
    void testAddDirection() {
        // 開始位置(5, 5)
        Position pos = new Position(5, 5);
        // 上方向に移動（y座標+1）
        Position newPos = pos.add(Direction.UP);
        // x座標は変わらず5
        assertEquals(5, newPos.x());
        // y座標が6に増加
        assertEquals(6, newPos.y());
    }

    /**
     * 複数の方向を連続して加算するテスト
     * 処理: (5, 5)から上に移動、さらに右に移動して(6, 6)になることを確認
     */
    @Test
    void testAddMultipleDirections() {
        // 開始位置(5, 5)
        Position pos = new Position(5, 5);
        // 上方向に移動 -> (5, 6)
        Position up = pos.add(Direction.UP);
        // さらに右方向に移動 -> (6, 6)
        Position upRight = up.add(Direction.RIGHT);
        // 最終的なx座標は6
        assertEquals(6, upRight.x());
        // 最終的なy座標は6
        assertEquals(6, upRight.y());
    }

    /**
     * Positionの等価性テスト
     * 処理: 同じ座標を持つPositionが等しいと判定され、異なる座標は等しくないことを確認
     */
    @Test
    void testPositionEquality() {
        // 同じ座標(3, 4)を持つ2つのPosition
        Position pos1 = new Position(3, 4);
        Position pos2 = new Position(3, 4);
        // 異なる座標(4, 3)を持つPosition
        Position pos3 = new Position(4, 3);
        // 同じ座標のPositionは等しい
        assertEquals(pos1, pos2);
        // 異なる座標のPositionは等しくない
        assertNotEquals(pos1, pos3);
    }
}

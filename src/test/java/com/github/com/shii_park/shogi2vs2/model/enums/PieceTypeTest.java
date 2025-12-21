package com.github.com.shii_park.shogi2vs2.model.enums;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * PieceTypeクラスのテスト
 * 各駒の移動可能方向、連続移動可否、成り駒の動きを検証
 */
class PieceTypeTest {

    /**
     * 歩兵の移動可能方向のテスト（成っていない）
     */
    @Test
    void testPawnMovableDirections() {
        List<Direction> directions = PieceType.PAWN.getMovableDirections(false);
        
        assertEquals(1, directions.size());
        assertTrue(directions.contains(Direction.UP));
    }

    /**
     * 歩兵の成り駒（金将の動き）のテスト
     */
    @Test
    void testPromotedPawnMovableDirections() {
        List<Direction> directions = PieceType.PAWN.getMovableDirections(true);
        
        assertEquals(6, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        assertTrue(directions.contains(Direction.DOWN));
        // 斜め後ろには移動できない
        assertFalse(directions.contains(Direction.DOWN_LEFT));
        assertFalse(directions.contains(Direction.DOWN_RIGHT));
    }

    /**
     * 香車の移動可能方向のテスト（成っていない）
     */
    @Test
    void testLanceMovableDirections() {
        List<Direction> directions = PieceType.LANCE.getMovableDirections(false);
        
        assertEquals(1, directions.size());
        assertTrue(directions.contains(Direction.UP));
    }

    /**
     * 香車の成り駒（金将の動き）のテスト
     */
    @Test
    void testPromotedLanceMovableDirections() {
        List<Direction> directions = PieceType.LANCE.getMovableDirections(true);
        
        assertEquals(6, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        assertTrue(directions.contains(Direction.DOWN));
    }

    /**
     * 桂馬の移動可能方向のテスト（成っていない）
     */
    @Test
    void testKnightMovableDirections() {
        List<Direction> directions = PieceType.KNIGHT.getMovableDirections(false);
        
        assertEquals(2, directions.size());
        assertTrue(directions.contains(Direction.KNIGHT_LEFT));
        assertTrue(directions.contains(Direction.KNIGHT_RIGHT));
    }

    /**
     * 桂馬の成り駒（金将の動き）のテスト
     */
    @Test
    void testPromotedKnightMovableDirections() {
        List<Direction> directions = PieceType.KNIGHT.getMovableDirections(true);
        
        assertEquals(6, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        assertTrue(directions.contains(Direction.DOWN));
    }

    /**
     * 銀将の移動可能方向のテスト（成っていない）
     */
    @Test
    void testSilverMovableDirections() {
        List<Direction> directions = PieceType.SILVER.getMovableDirections(false);
        
        assertEquals(5, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.DOWN_LEFT));
        assertTrue(directions.contains(Direction.DOWN_RIGHT));
        // 横と真後ろには移動できない
        assertFalse(directions.contains(Direction.LEFT));
        assertFalse(directions.contains(Direction.RIGHT));
        assertFalse(directions.contains(Direction.DOWN));
    }

    /**
     * 銀将の成り駒（金将の動き）のテスト
     */
    @Test
    void testPromotedSilverMovableDirections() {
        List<Direction> directions = PieceType.SILVER.getMovableDirections(true);
        
        assertEquals(6, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        assertTrue(directions.contains(Direction.DOWN));
    }

    /**
     * 金将の移動可能方向のテスト
     */
    @Test
    void testGoldMovableDirections() {
        List<Direction> directions = PieceType.GOLD.getMovableDirections(false);
        
        assertEquals(6, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        assertTrue(directions.contains(Direction.DOWN));
        // 斜め後ろには移動できない
        assertFalse(directions.contains(Direction.DOWN_LEFT));
        assertFalse(directions.contains(Direction.DOWN_RIGHT));
    }

    /**
     * 金将は成れない（成っても変わらない）
     */
    @Test
    void testGoldCannotBePromoted() {
        List<Direction> unpromoted = PieceType.GOLD.getMovableDirections(false);
        List<Direction> promoted = PieceType.GOLD.getMovableDirections(true);
        
        assertEquals(unpromoted, promoted);
    }

    /**
     * 角行の移動可能方向のテスト（成っていない）
     */
    @Test
    void testBishopMovableDirections() {
        List<Direction> directions = PieceType.BISHOP.getMovableDirections(false);
        
        assertEquals(4, directions.size());
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.DOWN_LEFT));
        assertTrue(directions.contains(Direction.DOWN_RIGHT));
        // 縦横には移動できない
        assertFalse(directions.contains(Direction.UP));
        assertFalse(directions.contains(Direction.DOWN));
        assertFalse(directions.contains(Direction.LEFT));
        assertFalse(directions.contains(Direction.RIGHT));
    }

    /**
     * 角行の成り駒（龍馬）: 斜め + 縦横
     */
    @Test
    void testPromotedBishopMovableDirections() {
        List<Direction> directions = PieceType.BISHOP.getMovableDirections(true);
        
        assertEquals(8, directions.size());
        // 斜め（元々の動き）
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.DOWN_LEFT));
        assertTrue(directions.contains(Direction.DOWN_RIGHT));
        // 縦横（追加された動き）
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.DOWN));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
    }

    /**
     * 飛車の移動可能方向のテスト（成っていない）
     */
    @Test
    void testRookMovableDirections() {
        List<Direction> directions = PieceType.ROOK.getMovableDirections(false);
        
        assertEquals(4, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.DOWN));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        // 斜めには移動できない
        assertFalse(directions.contains(Direction.UP_LEFT));
        assertFalse(directions.contains(Direction.UP_RIGHT));
        assertFalse(directions.contains(Direction.DOWN_LEFT));
        assertFalse(directions.contains(Direction.DOWN_RIGHT));
    }

    /**
     * 飛車の成り駒（龍王）: 縦横 + 斜め
     */
    @Test
    void testPromotedRookMovableDirections() {
        List<Direction> directions = PieceType.ROOK.getMovableDirections(true);
        
        assertEquals(8, directions.size());
        // 縦横（元々の動き）
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.DOWN));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        // 斜め（追加された動き）
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.DOWN_LEFT));
        assertTrue(directions.contains(Direction.DOWN_RIGHT));
    }

    /**
     * 王将の移動可能方向のテスト
     */
    @Test
    void testKingMovableDirections() {
        List<Direction> directions = PieceType.KING.getMovableDirections(false);
        
        assertEquals(8, directions.size());
        assertTrue(directions.contains(Direction.UP));
        assertTrue(directions.contains(Direction.DOWN));
        assertTrue(directions.contains(Direction.LEFT));
        assertTrue(directions.contains(Direction.RIGHT));
        assertTrue(directions.contains(Direction.UP_LEFT));
        assertTrue(directions.contains(Direction.UP_RIGHT));
        assertTrue(directions.contains(Direction.DOWN_LEFT));
        assertTrue(directions.contains(Direction.DOWN_RIGHT));
    }

    /**
     * 王将は成れない（成っても変わらない）
     */
    @Test
    void testKingCannotBePromoted() {
        List<Direction> unpromoted = PieceType.KING.getMovableDirections(false);
        List<Direction> promoted = PieceType.KING.getMovableDirections(true);
        
        assertEquals(unpromoted, promoted);
    }

    /**
     * 連続移動可能な駒のテスト（香車・成っていない）
     */
    @Test
    void testLanceCanMoveMultipleSteps() {
        assertTrue(PieceType.LANCE.canMoveMultipleSteps(false));
        assertFalse(PieceType.LANCE.canMoveMultipleSteps(true)); // 成香は連続移動不可
    }

    /**
     * 連続移動可能な駒のテスト（角行）
     */
    @Test
    void testBishopCanMoveMultipleSteps() {
        assertTrue(PieceType.BISHOP.canMoveMultipleSteps(false));
        assertTrue(PieceType.BISHOP.canMoveMultipleSteps(true)); // 成っても連続移動可
    }

    /**
     * 連続移動可能な駒のテスト（飛車）
     */
    @Test
    void testRookCanMoveMultipleSteps() {
        assertTrue(PieceType.ROOK.canMoveMultipleSteps(false));
        assertTrue(PieceType.ROOK.canMoveMultipleSteps(true)); // 成っても連続移動可
    }

    /**
     * 連続移動不可能な駒のテスト
     */
    @Test
    void testOtherPiecesCannotMoveMultipleSteps() {
        assertFalse(PieceType.PAWN.canMoveMultipleSteps(false));
        assertFalse(PieceType.KNIGHT.canMoveMultipleSteps(false));
        assertFalse(PieceType.SILVER.canMoveMultipleSteps(false));
        assertFalse(PieceType.GOLD.canMoveMultipleSteps(false));
        assertFalse(PieceType.KING.canMoveMultipleSteps(false));
    }

    /**
     * 香車の方向別連続移動テスト
     */
    @Test
    void testLanceCanMoveMultipleStepsInDirection() {
        // 成っていない香車はUP方向のみ連続移動可能
        assertTrue(PieceType.LANCE.canMoveMultipleStepsInDirection(Direction.UP, false));
        assertFalse(PieceType.LANCE.canMoveMultipleStepsInDirection(Direction.DOWN, false));
        assertFalse(PieceType.LANCE.canMoveMultipleStepsInDirection(Direction.LEFT, false));
        assertFalse(PieceType.LANCE.canMoveMultipleStepsInDirection(Direction.RIGHT, false));
        
        // 成香は連続移動不可
        assertFalse(PieceType.LANCE.canMoveMultipleStepsInDirection(Direction.UP, true));
    }

    /**
     * 角行の方向別連続移動テスト
     */
    @Test
    void testBishopCanMoveMultipleStepsInDirection() {
        // 斜め方向のみ連続移動可能
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.UP_LEFT, false));
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.UP_RIGHT, false));
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.DOWN_LEFT, false));
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.DOWN_RIGHT, false));
        
        // 縦横方向は連続移動不可
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.UP, false));
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.DOWN, false));
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.LEFT, false));
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.RIGHT, false));
    }

    /**
     * 成角（龍馬）の方向別連続移動テスト
     * 斜めは連続移動可、縦横は1マスのみ
     */
    @Test
    void testPromotedBishopCanMoveMultipleStepsInDirection() {
        // 斜め方向は連続移動可能
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.UP_LEFT, true));
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.UP_RIGHT, true));
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.DOWN_LEFT, true));
        assertTrue(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.DOWN_RIGHT, true));
        
        // 縦横方向は連続移動不可（1マスのみ）
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.UP, true));
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.DOWN, true));
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.LEFT, true));
        assertFalse(PieceType.BISHOP.canMoveMultipleStepsInDirection(Direction.RIGHT, true));
    }

    /**
     * 飛車の方向別連続移動テスト
     */
    @Test
    void testRookCanMoveMultipleStepsInDirection() {
        // 縦横方向のみ連続移動可能
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.UP, false));
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.DOWN, false));
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.LEFT, false));
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.RIGHT, false));
        
        // 斜め方向は連続移動不可
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.UP_LEFT, false));
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.UP_RIGHT, false));
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.DOWN_LEFT, false));
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.DOWN_RIGHT, false));
    }

    /**
     * 成飛（龍王）の方向別連続移動テスト
     * 縦横は連続移動可、斜めは1マスのみ
     */
    @Test
    void testPromotedRookCanMoveMultipleStepsInDirection() {
        // 縦横方向は連続移動可能
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.UP, true));
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.DOWN, true));
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.LEFT, true));
        assertTrue(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.RIGHT, true));
        
        // 斜め方向は連続移動不可（1マスのみ）
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.UP_LEFT, true));
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.UP_RIGHT, true));
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.DOWN_LEFT, true));
        assertFalse(PieceType.ROOK.canMoveMultipleStepsInDirection(Direction.DOWN_RIGHT, true));
    }

    /**
     * 歩兵は連続移動不可
     */
    @Test
    void testPawnCannotMoveMultipleStepsInAnyDirection() {
        assertFalse(PieceType.PAWN.canMoveMultipleStepsInDirection(Direction.UP, false));
        assertFalse(PieceType.PAWN.canMoveMultipleStepsInDirection(Direction.DOWN, false));
        assertFalse(PieceType.PAWN.canMoveMultipleStepsInDirection(Direction.LEFT, false));
        assertFalse(PieceType.PAWN.canMoveMultipleStepsInDirection(Direction.RIGHT, false));
    }

    /**
     * 全ての駒種が定義されていることを確認
     */
    @Test
    void testAllPieceTypesAreDefined() {
        PieceType[] types = PieceType.values();
        
        assertEquals(8, types.length);
        assertEquals(PieceType.PAWN, types[0]);
        assertEquals(PieceType.LANCE, types[1]);
        assertEquals(PieceType.KNIGHT, types[2]);
        assertEquals(PieceType.SILVER, types[3]);
        assertEquals(PieceType.GOLD, types[4]);
        assertEquals(PieceType.BISHOP, types[5]);
        assertEquals(PieceType.ROOK, types[6]);
        assertEquals(PieceType.KING, types[7]);
    }
}

package com.github.com.shii_park.shogi2vs2.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;

/**
 * BoardCoordinateServiceのテスト
 * チームに応じた座標の正規化（反転）処理を検証
 */
class BoardCoordinateServiceTest {

    private BoardCoordinateService service;

    @BeforeEach
    void setUp() {
        service = new BoardCoordinateService();
    }

    /**
     * FIRSTチームの座標は変換されないことを確認
     */
    @Test
    void testNormalizeFirstTeamNoChange() {
        Position pos = new Position(5, 5);
        Position normalized = service.normalize(pos, "FIRST");
        
        assertEquals(5, normalized.x());
        assertEquals(5, normalized.y());
    }

    /**
     * SECONDチームの座標が180度回転されることを確認
     */
    @Test
    void testNormalizeSecondTeamRotated() {
        Position pos = new Position(1, 1);
        Position normalized = service.normalize(pos, "SECOND");
        
        assertEquals(9, normalized.x());
        assertEquals(9, normalized.y());
    }

    /**
     * SECONDチームの座標反転（盤面端のケース）
     */
    @Test
    void testNormalizeSecondTeamCorners() {
        // 左下 (1,1) -> 右上 (9,9)
        Position pos1 = new Position(1, 1);
        Position normalized1 = service.normalize(pos1, "SECOND");
        assertEquals(9, normalized1.x());
        assertEquals(9, normalized1.y());

        // 右上 (9,9) -> 左下 (1,1)
        Position pos2 = new Position(9, 9);
        Position normalized2 = service.normalize(pos2, "SECOND");
        assertEquals(1, normalized2.x());
        assertEquals(1, normalized2.y());
    }

    /**
     * 中央の座標が変換されないことを確認（対称点）
     */
    @Test
    void testNormalizeSecondTeamCenter() {
        Position pos = new Position(5, 5);
        Position normalized = service.normalize(pos, "SECOND");
        
        assertEquals(5, normalized.x());
        assertEquals(5, normalized.y());
    }

    /**
     * null座標の処理を確認
     */
    @Test
    void testNormalizeNullPosition() {
        Position normalized1 = service.normalize(null, "FIRST");
        assertNull(normalized1);

        Position normalized2 = service.normalize(null, "SECOND");
        assertNull(normalized2);
    }

    /**
     * 複数の座標でFIRSTチームの不変性を確認
     */
    @Test
    void testNormalizeFirstTeamMultiplePositions() {
        Position[] positions = {
            new Position(1, 1),
            new Position(3, 7),
            new Position(9, 5),
            new Position(5, 9)
        };

        for (Position pos : positions) {
            Position normalized = service.normalize(pos, "FIRST");
            assertEquals(pos.x(), normalized.x());
            assertEquals(pos.y(), normalized.y());
        }
    }

    /**
     * SECONDチームの複数座標で反転計算を確認
     */
    @Test
    void testNormalizeSecondTeamMultiplePositions() {
        // (x, y) -> (10-x, 10-y)
        assertEquals(new Position(8, 7), service.normalize(new Position(2, 3), "SECOND"));
        assertEquals(new Position(5, 2), service.normalize(new Position(5, 8), "SECOND"));
        assertEquals(new Position(3, 6), service.normalize(new Position(7, 4), "SECOND"));
    }

    /**
     * 不明なチームIDの場合はFIRSTと同じ動作（変換なし）
     */
    @Test
    void testNormalizeUnknownTeamId() {
        Position pos = new Position(3, 4);
        Position normalized = service.normalize(pos, "UNKNOWN");
        
        assertEquals(3, normalized.x());
        assertEquals(4, normalized.y());
    }

    /**
     * チームIDが空文字列の場合
     */
    @Test
    void testNormalizeEmptyTeamId() {
        Position pos = new Position(6, 7);
        Position normalized = service.normalize(pos, "");
        
        assertEquals(6, normalized.x());
        assertEquals(7, normalized.y());
    }

    /**
     * 対称性のテスト: SECOND変換を2回適用すると元に戻る
     */
    @Test
    void testNormalizeSymmetry() {
        Position original = new Position(2, 8);
        Position normalized1 = service.normalize(original, "SECOND");
        Position normalized2 = service.normalize(normalized1, "SECOND");
        
        assertEquals(original.x(), normalized2.x());
        assertEquals(original.y(), normalized2.y());
    }
}

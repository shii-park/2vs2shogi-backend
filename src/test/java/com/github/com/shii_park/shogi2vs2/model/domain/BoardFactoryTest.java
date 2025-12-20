package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * BoardFactoryクラスのテスト
 * 標準的な将棋の初期配置が正しく生成されることを検証
 */
class BoardFactoryTest {

    private BoardFactory factory;

    @BeforeEach
    void setUp() {
        factory = new BoardFactory();
    }

    /**
     * 標準盤面が生成できることを確認
     */
    @Test
    void testCreateBoard() {
        Board board = factory.createBoard();
        assertNotNull(board);
    }

    /**
     * FIRSTチームの1段目の配置を確認
     * 香、桂、銀、金、王、金、銀、桂、香
     */
    @Test
    void testFirstTeamFirstRow() {
        Board board = factory.createBoard();
        
        assertEquals(PieceType.LANCE, board.getTopPiece(new Position(1, 1)).getType());
        assertEquals(PieceType.KNIGHT, board.getTopPiece(new Position(2, 1)).getType());
        assertEquals(PieceType.SILVER, board.getTopPiece(new Position(3, 1)).getType());
        assertEquals(PieceType.GOLD, board.getTopPiece(new Position(4, 1)).getType());
        assertEquals(PieceType.KING, board.getTopPiece(new Position(5, 1)).getType());
        assertEquals(PieceType.GOLD, board.getTopPiece(new Position(6, 1)).getType());
        assertEquals(PieceType.SILVER, board.getTopPiece(new Position(7, 1)).getType());
        assertEquals(PieceType.KNIGHT, board.getTopPiece(new Position(8, 1)).getType());
        assertEquals(PieceType.LANCE, board.getTopPiece(new Position(9, 1)).getType());
        
        // 全てFIRSTチームであることを確認
        for (int x = 1; x <= 9; x++) {
            assertEquals(Team.FIRST, board.getTopPiece(new Position(x, 1)).getTeam());
        }
    }

    /**
     * FIRSTチームの2段目の配置を確認
     * 飛車（2列目）、角行（8列目）
     */
    @Test
    void testFirstTeamSecondRow() {
        Board board = factory.createBoard();
        
        assertEquals(PieceType.ROOK, board.getTopPiece(new Position(2, 2)).getType());
        assertEquals(Team.FIRST, board.getTopPiece(new Position(2, 2)).getTeam());
        
        assertEquals(PieceType.BISHOP, board.getTopPiece(new Position(8, 2)).getType());
        assertEquals(Team.FIRST, board.getTopPiece(new Position(8, 2)).getTeam());
        
        // 他のマスは空であることを確認
        for (int x = 1; x <= 9; x++) {
            if (x != 2 && x != 8) {
                assertNull(board.getTopPiece(new Position(x, 2)));
            }
        }
    }

    /**
     * FIRSTチームの3段目の配置を確認
     * 歩兵×9
     */
    @Test
    void testFirstTeamThirdRow() {
        Board board = factory.createBoard();
        
        for (int x = 1; x <= 9; x++) {
            Piece piece = board.getTopPiece(new Position(x, 3));
            assertNotNull(piece);
            assertEquals(PieceType.PAWN, piece.getType());
            assertEquals(Team.FIRST, piece.getTeam());
        }
    }

    /**
     * SECONDチームの7段目の配置を確認
     * 歩兵×9
     */
    @Test
    void testSecondTeamSeventhRow() {
        Board board = factory.createBoard();
        
        for (int x = 1; x <= 9; x++) {
            Piece piece = board.getTopPiece(new Position(x, 7));
            assertNotNull(piece);
            assertEquals(PieceType.PAWN, piece.getType());
            assertEquals(Team.SECOND, piece.getTeam());
        }
    }

    /**
     * SECONDチームの8段目の配置を確認
     * 角行（2列目）、飛車（8列目）
     */
    @Test
    void testSecondTeamEighthRow() {
        Board board = factory.createBoard();
        
        assertEquals(PieceType.BISHOP, board.getTopPiece(new Position(2, 8)).getType());
        assertEquals(Team.SECOND, board.getTopPiece(new Position(2, 8)).getTeam());
        
        assertEquals(PieceType.ROOK, board.getTopPiece(new Position(8, 8)).getType());
        assertEquals(Team.SECOND, board.getTopPiece(new Position(8, 8)).getTeam());
        
        // 他のマスは空であることを確認
        for (int x = 1; x <= 9; x++) {
            if (x != 2 && x != 8) {
                assertNull(board.getTopPiece(new Position(x, 8)));
            }
        }
    }

    /**
     * SECONDチームの9段目の配置を確認
     * 香、桂、銀、金、王、金、銀、桂、香
     */
    @Test
    void testSecondTeamNinthRow() {
        Board board = factory.createBoard();
        
        assertEquals(PieceType.LANCE, board.getTopPiece(new Position(1, 9)).getType());
        assertEquals(PieceType.KNIGHT, board.getTopPiece(new Position(2, 9)).getType());
        assertEquals(PieceType.SILVER, board.getTopPiece(new Position(3, 9)).getType());
        assertEquals(PieceType.GOLD, board.getTopPiece(new Position(4, 9)).getType());
        assertEquals(PieceType.KING, board.getTopPiece(new Position(5, 9)).getType());
        assertEquals(PieceType.GOLD, board.getTopPiece(new Position(6, 9)).getType());
        assertEquals(PieceType.SILVER, board.getTopPiece(new Position(7, 9)).getType());
        assertEquals(PieceType.KNIGHT, board.getTopPiece(new Position(8, 9)).getType());
        assertEquals(PieceType.LANCE, board.getTopPiece(new Position(9, 9)).getType());
        
        // 全てSECONDチームであることを確認
        for (int x = 1; x <= 9; x++) {
            assertEquals(Team.SECOND, board.getTopPiece(new Position(x, 9)).getTeam());
        }
    }

    /**
     * 中央の空白エリア（4-6段目）を確認
     */
    @Test
    void testEmptyMiddleRows() {
        Board board = factory.createBoard();
        
        for (int y = 4; y <= 6; y++) {
            for (int x = 1; x <= 9; x++) {
                assertNull(board.getTopPiece(new Position(x, y)));
            }
        }
    }

    /**
     * 歩兵のIDが1-18まで正しく採番されることを確認
     */
    @Test
    void testPawnIdRange() {
        Board board = factory.createBoard();
        
        int minPawnId = Integer.MAX_VALUE;
        int maxPawnId = Integer.MIN_VALUE;
        
        // FIRSTチームの歩兵（3段目）
        for (int x = 1; x <= 9; x++) {
            Piece piece = board.getTopPiece(new Position(x, 3));
            if (piece.getType() == PieceType.PAWN) {
                minPawnId = Math.min(minPawnId, piece.getId());
                maxPawnId = Math.max(maxPawnId, piece.getId());
            }
        }
        
        // SECONDチームの歩兵（7段目）
        for (int x = 1; x <= 9; x++) {
            Piece piece = board.getTopPiece(new Position(x, 7));
            if (piece.getType() == PieceType.PAWN) {
                minPawnId = Math.min(minPawnId, piece.getId());
                maxPawnId = Math.max(maxPawnId, piece.getId());
            }
        }
        
        assertEquals(1, minPawnId);
        assertEquals(18, maxPawnId);
    }

    /**
     * 香車のIDが1-4まで正しく採番されることを確認
     */
    @Test
    void testLanceIdRange() {
        Board board = factory.createBoard();
        
        int minLanceId = Integer.MAX_VALUE;
        int maxLanceId = Integer.MIN_VALUE;
        
        // FIRSTチームの香車
        Piece lance1 = board.getTopPiece(new Position(1, 1));
        Piece lance2 = board.getTopPiece(new Position(9, 1));
        minLanceId = Math.min(minLanceId, Math.min(lance1.getId(), lance2.getId()));
        maxLanceId = Math.max(maxLanceId, Math.max(lance1.getId(), lance2.getId()));
        
        // SECONDチームの香車
        Piece lance3 = board.getTopPiece(new Position(1, 9));
        Piece lance4 = board.getTopPiece(new Position(9, 9));
        minLanceId = Math.min(minLanceId, Math.min(lance3.getId(), lance4.getId()));
        maxLanceId = Math.max(maxLanceId, Math.max(lance3.getId(), lance4.getId()));
        
        assertEquals(1, minLanceId);
        assertEquals(4, maxLanceId);
    }

    /**
     * 王将のIDが1-2であることを確認
     */
    @Test
    void testKingIdRange() {
        Board board = factory.createBoard();
        
        Piece firstKing = board.getTopPiece(new Position(5, 1));
        Piece secondKing = board.getTopPiece(new Position(5, 9));
        
        assertEquals(PieceType.KING, firstKing.getType());
        assertEquals(PieceType.KING, secondKing.getType());
        assertEquals(Team.FIRST, firstKing.getTeam());
        assertEquals(Team.SECOND, secondKing.getTeam());
        
        int minKingId = Math.min(firstKing.getId(), secondKing.getId());
        int maxKingId = Math.max(firstKing.getId(), secondKing.getId());
        
        assertEquals(1, minKingId);
        assertEquals(2, maxKingId);
    }

    /**
     * 成れる駒と成れない駒が正しく設定されていることを確認
     */
    @Test
    void testPromotableFlags() {
        Board board = factory.createBoard();
        
        // 金将と王将は成れない
        assertFalse(board.getTopPiece(new Position(4, 1)).isPromotable());
        assertFalse(board.getTopPiece(new Position(5, 1)).isPromotable());
        assertFalse(board.getTopPiece(new Position(6, 1)).isPromotable());
        
        // 他の駒は成れる
        assertTrue(board.getTopPiece(new Position(1, 1)).isPromotable()); // 香
        assertTrue(board.getTopPiece(new Position(2, 1)).isPromotable()); // 桂
        assertTrue(board.getTopPiece(new Position(3, 1)).isPromotable()); // 銀
        assertTrue(board.getTopPiece(new Position(2, 2)).isPromotable()); // 飛
        assertTrue(board.getTopPiece(new Position(8, 2)).isPromotable()); // 角
        assertTrue(board.getTopPiece(new Position(1, 3)).isPromotable()); // 歩
    }

    /**
     * 駒の総数が40枚であることを確認
     */
    @Test
    void testTotalPieceCount() {
        Board board = factory.createBoard();
        
        int count = 0;
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                if (board.getTopPiece(new Position(x, y)) != null) {
                    count++;
                }
            }
        }
        
        assertEquals(40, count);
    }
}

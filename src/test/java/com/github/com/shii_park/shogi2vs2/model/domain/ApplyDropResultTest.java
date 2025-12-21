package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * ApplyDropResultのテスト
 * applyDrop()が正しい結果を返すことを確認
 */
class ApplyDropResultTest {

    private Game game;
    private Board board;
    private Player player1;
    private Player player2;
    private Piece piece1;

    @BeforeEach
    void setUp() {
        piece1 = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        Piece piece2 = new Piece(2, PieceType.PAWN, Team.SECOND, true);
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(6, 6);

        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(piece1, pos1);
        initialPieces.put(piece2, pos2);
        board = new Board(initialPieces);

        player1 = new Player("p1", Team.FIRST);
        player2 = new Player("p2", Team.SECOND);
        List<Player> players = List.of(player1, player2);

        game = new Game("game1", players, board, Team.FIRST);
    }

    /**
     * 成功する配置の結果が正しく記録されることを確認
     */
    @Test
    void testApplyDropSuccess() {
        Piece capturedPawn = new Piece(10, PieceType.PAWN, Team.SECOND, false);
        board.getCapturedPieces().capturedPiece(Team.FIRST, capturedPawn);

        Position dropPosition = new Position(3, 3);
        PlayerDropPiece drop = new PlayerDropPiece(player1, capturedPawn, dropPosition);

        ApplyDropResult result = game.applyDrop(drop);

        assertNotNull(result);
        assertTrue(result.success());
        assertEquals(dropPosition, result.position());
        assertEquals(capturedPawn, result.piece());
    }

    /**
     * 既に駒がある位置への配置が失敗することを確認
     */
    @Test
    void testApplyDropToOccupiedPosition() {
        Piece capturedPawn = new Piece(10, PieceType.PAWN, Team.SECOND, false);
        board.getCapturedPieces().capturedPiece(Team.FIRST, capturedPawn);

        Position occupiedPosition = new Position(5, 5);
        PlayerDropPiece drop = new PlayerDropPiece(player1, capturedPawn, occupiedPosition);

        ApplyDropResult result = game.applyDrop(drop);

        assertNotNull(result);
        assertFalse(result.success());
        assertEquals(occupiedPosition, result.position());
        assertEquals(capturedPawn, result.piece());
    }

    /**
     * 複数回配置を試みた場合の結果を確認
     */
    @Test
    void testApplyDropMultipleTimes() {
        Piece capturedPawn1 = new Piece(10, PieceType.PAWN, Team.SECOND, false);
        Piece capturedPawn2 = new Piece(11, PieceType.PAWN, Team.SECOND, false);
        board.getCapturedPieces().capturedPiece(Team.FIRST, capturedPawn1);
        board.getCapturedPieces().capturedPiece(Team.FIRST, capturedPawn2);

        Position dropPosition1 = new Position(3, 3);
        Position dropPosition2 = new Position(4, 4);

        ApplyDropResult result1 = game.applyDrop(new PlayerDropPiece(player1, capturedPawn1, dropPosition1));
        ApplyDropResult result2 = game.applyDrop(new PlayerDropPiece(player1, capturedPawn2, dropPosition2));

        assertTrue(result1.success());
        assertTrue(result2.success());
        assertNotEquals(result1.position(), result2.position());
    }
}

package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * 捕獲した駒の記録機能のテスト
 * applyMove()で捕獲した駒が正しくApplyMoveResultに記録されることを確認
 */
class CaptureTrackingTest {

    private Game game;
    private Board board;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1 = new Player("p1", Team.FIRST);
        player2 = new Player("p2", Team.SECOND);
    }

    /**
     * 1つの駒を捕獲した場合の記録を確認
     */
    @Test
    void testCaptureSinglePiece() {
        Piece attacker = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        Piece target = new Piece(2, PieceType.PAWN, Team.SECOND, false);

        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(attacker, new Position(5, 5));
        initialPieces.put(target, new Position(5, 6));
        board = new Board(initialPieces);

        game = new Game("game1", List.of(player1, player2), board, Team.FIRST);

        PlayerMove move = new PlayerMove(player1, attacker, List.of(Direction.UP), false);
        ApplyMoveResult result = game.applyMove(move);

        assertNotNull(result);
        assertEquals(1, result.appliedDirections().size());
        assertEquals(1, result.capturedPieces().size());
        assertEquals(target, result.capturedPieces().get(0));
    }

    /**
     * 複数の駒が積まれている場所を捕獲した場合の記録を確認
     */
    @Test
    void testCaptureMultiplePieces() {
        Piece attacker = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        Piece target1 = new Piece(2, PieceType.PAWN, Team.SECOND, false);
        Piece target2 = new Piece(3, PieceType.PAWN, Team.SECOND, false);

        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(attacker, new Position(5, 5));
        initialPieces.put(target1, new Position(5, 6));
        board = new Board(initialPieces);

        // target2をtarget1の上に積む
        board.stackPiece(new Position(5, 6), target2);

        game = new Game("game1", List.of(player1, player2), board, Team.FIRST);

        PlayerMove move = new PlayerMove(player1, attacker, List.of(Direction.UP), false);
        ApplyMoveResult result = game.applyMove(move);

        assertNotNull(result);
        assertEquals(1, result.appliedDirections().size());
        assertEquals(2, result.capturedPieces().size());
        assertTrue(result.capturedPieces().contains(target1));
        assertTrue(result.capturedPieces().contains(target2));
    }

    /**
     * 飛車が複数マス移動して捕獲した場合の記録を確認
     */
    @Test
    void testCaptureWithMultiStepMove() {
        Piece rook = new Piece(1, PieceType.ROOK, Team.FIRST, true);
        Piece target = new Piece(2, PieceType.PAWN, Team.SECOND, false);

        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(rook, new Position(5, 5));
        initialPieces.put(target, new Position(5, 7));
        board = new Board(initialPieces);

        game = new Game("game1", List.of(player1, player2), board, Team.FIRST);

        // 上に2マス移動（途中で捕獲）
        PlayerMove move = new PlayerMove(player1, rook, List.of(Direction.UP, Direction.UP), false);
        ApplyMoveResult result = game.applyMove(move);

        assertNotNull(result);
        assertEquals(2, result.appliedDirections().size());
        assertEquals(1, result.capturedPieces().size());
        assertEquals(target, result.capturedPieces().get(0));
    }

    /**
     * 捕獲が発生しない移動の場合、capturedPiecesが空であることを確認
     */
    @Test
    void testNoCaptureMove() {
        Piece pawn = new Piece(1, PieceType.PAWN, Team.FIRST, true);

        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(pawn, new Position(5, 5));
        board = new Board(initialPieces);

        game = new Game("game1", List.of(player1, player2), board, Team.FIRST);

        PlayerMove move = new PlayerMove(player1, pawn, List.of(Direction.UP), false);
        ApplyMoveResult result = game.applyMove(move);

        assertNotNull(result);
        assertEquals(1, result.appliedDirections().size());
        assertEquals(0, result.capturedPieces().size());
    }

    /**
     * 自チームの駒の上にスタックする場合、捕獲が発生しないことを確認
     */
    @Test
    void testStackOnSameTeamNoCaptured() {
        Piece pawn1 = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        Piece pawn2 = new Piece(2, PieceType.PAWN, Team.FIRST, false);

        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(pawn1, new Position(5, 5));
        initialPieces.put(pawn2, new Position(5, 6));
        board = new Board(initialPieces);

        game = new Game("game1", List.of(player1, player2), board, Team.FIRST);

        PlayerMove move = new PlayerMove(player1, pawn1, List.of(Direction.UP), false);
        ApplyMoveResult result = game.applyMove(move);

        assertNotNull(result);
        assertEquals(1, result.appliedDirections().size());
        assertEquals(0, result.capturedPieces().size());
    }

    /**
     * handleTurnEnd()で取得できる結果にも捕獲情報が含まれることを確認
     */
    @Test
    void testCaptureInActionResult() {
        Piece attacker = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        Piece target = new Piece(2, PieceType.PAWN, Team.SECOND, false);

        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(attacker, new Position(5, 5));
        initialPieces.put(target, new Position(5, 6));
        board = new Board(initialPieces);

        game = new Game("game1", List.of(player1, player2), board, Team.FIRST);

        PlayerMove move = new PlayerMove(player1, attacker, List.of(Direction.UP), false);
        game.applyMove(move);

        ApplyActionResult actionResult = game.handleTurnEnd();

        assertNotNull(actionResult);
        assertEquals(1, actionResult.moveResults().size());
        
        ApplyMoveResult moveResult = actionResult.moveResults().get(0);
        assertEquals(1, moveResult.capturedPieces().size());
        assertEquals(target, moveResult.capturedPieces().get(0));
    }
}

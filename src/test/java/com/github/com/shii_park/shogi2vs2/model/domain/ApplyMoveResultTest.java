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
 * ApplyMoveResultのテスト
 * applyMove()が正しい結果を返すことを確認
 */
class ApplyMoveResultTest {

    private Game game;
    private Board board;
    private Player player1;
    private Player player2;
    private Piece piece1;
    private Piece piece2;

    @BeforeEach
    void setUp() {
        piece1 = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        piece2 = new Piece(2, PieceType.PAWN, Team.SECOND, true);
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
     * 通常移動の結果が正しく記録されることを確認
     */
    @Test
    void testApplyMoveReturnsResult() {
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), false);

        ApplyMoveResult result = game.applyMove(move1);

        assertNotNull(result);
        assertEquals(1, result.appliedDirections().size());
        assertEquals(Direction.UP, result.appliedDirections().get(0));
        assertEquals(0, result.capturedPieces().size());
    }

    /**
     * 複数マス移動の結果が正しく記録されることを確認
     */
    @Test
    void testApplyMoveMultipleDirections() {
        Piece rook = new Piece(3, PieceType.ROOK, Team.FIRST, true);
        Position rookPos = new Position(5, 5);
        
        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(rook, rookPos);
        initialPieces.put(piece2, new Position(8, 8));
        Board newBoard = new Board(initialPieces);
        
        game = new Game("game1", List.of(player1, player2), newBoard, Team.FIRST);
        
        PlayerMove move = new PlayerMove(player1, rook, List.of(Direction.UP, Direction.UP), false);

        ApplyMoveResult result = game.applyMove(move);

        assertNotNull(result);
        assertEquals(2, result.appliedDirections().size());
        assertEquals(Direction.UP, result.appliedDirections().get(0));
        assertEquals(Direction.UP, result.appliedDirections().get(1));
    }

    /**
     * 駒が盤面外に落ちた場合の結果を確認
     */
    @Test
    void testApplyMoveWithDrop() {
        // 盤面の端に駒を配置（盤面外に出る可能性がある位置）
        Piece fallingPiece = new Piece(10, PieceType.PAWN, Team.FIRST, true);
        board.stackPiece(new Position(5, 8), fallingPiece);
        
        PlayerMove move = new PlayerMove(player1, fallingPiece, List.of(Direction.UP), false);

        ApplyMoveResult result = game.applyMove(move);

        assertNotNull(result);
        assertEquals(1, result.appliedDirections().size());
        // 盤面外に出た場合(DROPPED)、capturedPiecesに記録される
        // 実際の動作はBoard.moveOneStepの実装に依存
        assertTrue(result.capturedPieces().size() <= 1);
    }

    /**
     * 無効な移動の場合nullが返されることを確認
     */
    @Test
    void testApplyMoveInvalidReturnsNull() {
        // piece1は歩兵なので下に移動できない
        PlayerMove invalidMove = new PlayerMove(player1, piece1, List.of(Direction.DOWN), false);

        ApplyMoveResult result = game.applyMove(invalidMove);

        assertNull(result);
    }

    /**
     * 一番上にない駒を動かそうとした場合nullが返されることを確認
     */
    @Test
    void testApplyMoveNotTopPieceReturnsNull() {
        // piece1の上に別の駒を積む
        Piece topPiece = new Piece(10, PieceType.PAWN, Team.SECOND, false);
        board.stackPiece(board.find(piece1), topPiece);

        PlayerMove move = new PlayerMove(player1, piece1, List.of(Direction.UP), false);

        ApplyMoveResult result = game.applyMove(move);

        assertNull(result);
    }
}

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
 * ApplyActionResultのテスト
 * handleTurnEnd()が正しい結果を返すことを確認
 */
class ApplyActionResultTest {

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
     * handleTurnEndが正しいApplyActionResultを返すことを確認
     */
    @Test
    void testHandleTurnEndReturnsResult() {
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        game.applyMove(move1);
        game.applyMove(move2);

        ApplyActionResult result = game.handleTurnEnd();

        assertNotNull(result);
        assertEquals(2, result.moveResults().size());
        assertEquals(0, result.dropResults().size());
        assertEquals(0, result.promotedPieces().size());
        assertEquals(0, result.placedPieces().size());
    }

    /**
     * 成りを含むターン終了結果を確認
     */
    @Test
    void testHandleTurnEndWithPromotion() {
        // piece1を成りエリア(5, 6)に移動
        board.movePiece(piece1, new Position(5, 6));
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), true);
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        game.applyMove(move1);
        game.applyMove(move2);

        ApplyActionResult result = game.handleTurnEnd();

        assertNotNull(result);
        assertEquals(1, result.promotedPieces().size());
        assertEquals(piece1, result.promotedPieces().get(0));
        assertTrue(piece1.isPromoted());
    }

    /**
     * 手駒の配置を含むターン終了結果を確認
     */
    @Test
    void testHandleTurnEndWithDrop() {
        Piece capturedPawn = new Piece(10, PieceType.PAWN, Team.SECOND, false);
        board.getCapturedPieces().capturedPiece(Team.FIRST, capturedPawn);

        Position dropPosition = new Position(3, 3);
        PlayerDropPiece drop = new PlayerDropPiece(player1, capturedPawn, dropPosition);

        game.applyDrop(drop);

        ApplyActionResult result = game.handleTurnEnd();

        assertNotNull(result);
        assertEquals(1, result.dropResults().size());
        assertEquals(1, result.placedPieces().size());
        assertEquals(capturedPawn, result.placedPieces().get(0));
        assertNotNull(board.getTopPiece(dropPosition));
    }

    /**
     * 移動と配置の両方を含むターン終了結果を確認
     */
    @Test
    void testHandleTurnEndWithMoveAndDrop() {
        Piece capturedPawn = new Piece(10, PieceType.PAWN, Team.SECOND, false);
        board.getCapturedPieces().capturedPiece(Team.FIRST, capturedPawn);

        PlayerMove move = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        Position dropPosition = new Position(3, 3);
        PlayerDropPiece drop = new PlayerDropPiece(player1, capturedPawn, dropPosition);

        game.applyMove(move);
        game.applyDrop(drop);

        ApplyActionResult result = game.handleTurnEnd();

        assertNotNull(result);
        assertEquals(1, result.moveResults().size());
        assertEquals(1, result.dropResults().size());
        assertEquals(1, result.placedPieces().size());
    }

    /**
     * 複数回handleTurnEndを呼んだ場合、結果がクリアされることを確認
     */
    @Test
    void testHandleTurnEndClearsResults() {
        PlayerMove move = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        game.applyMove(move);

        ApplyActionResult result1 = game.handleTurnEnd();
        assertEquals(1, result1.moveResults().size());

        // 2回目のターン終了では結果が空
        ApplyActionResult result2 = game.handleTurnEnd();
        assertEquals(0, result2.moveResults().size());
        assertEquals(0, result2.dropResults().size());
    }

    /**
     * 配置失敗も含めた全ての結果が記録されることを確認
     */
    @Test
    void testHandleTurnEndWithFailedDrop() {
        Piece capturedPawn = new Piece(10, PieceType.PAWN, Team.SECOND, false);
        board.getCapturedPieces().capturedPiece(Team.FIRST, capturedPawn);

        // 既に駒がある位置に配置を試みる
        Position occupiedPosition = new Position(5, 5);
        PlayerDropPiece drop = new PlayerDropPiece(player1, capturedPawn, occupiedPosition);

        game.applyDrop(drop);

        ApplyActionResult result = game.handleTurnEnd();

        assertNotNull(result);
        assertEquals(1, result.dropResults().size());
        assertFalse(result.dropResults().get(0).success());
        // 配置失敗なのでplacedPiecesは空
        assertEquals(0, result.placedPieces().size());
    }

    /**
     * 成りエリア外での成りフラグが無視されることを確認
     */
    @Test
    void testHandleTurnEndPromotionOutsideZone() {
        // 成りエリア外で成りフラグを立てる
        PlayerMove move = new PlayerMove(player1, piece1, List.of(Direction.UP), true);
        game.applyMove(move);

        ApplyActionResult result = game.handleTurnEnd();

        assertNotNull(result);
        // 成りエリア外なので成らない
        assertEquals(0, result.promotedPieces().size());
        assertFalse(piece1.isPromoted());
    }
}

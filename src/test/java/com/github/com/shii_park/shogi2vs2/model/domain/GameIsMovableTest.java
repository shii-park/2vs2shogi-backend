package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Game.isMovable()メソッドのテスト
 * 駒の移動可能性バリデーションを検証
 */
class GameIsMovableTest {

    private Game game;
    private Board board;
    private Player player1;
    private Player player2;
    private Piece pawn;
    private Piece rook;
    private Piece bishop;
    private Piece knight;

    @BeforeEach
    void setUp() {
        board = BoardFactory.createBoard();
        
        player1 = new Player("p1", Team.FIRST);
        player2 = new Player("p2", Team.SECOND);
        
        // テスト用の駒を取得
        pawn = board.getTopPiece(new Position(1, 3));     // FIRSTの歩兵
        rook = board.getTopPiece(new Position(2, 2));     // FIRSTの飛車
        bishop = board.getTopPiece(new Position(8, 2));   // FIRSTの角行
        knight = board.getTopPiece(new Position(2, 1));   // FIRSTの桂馬
        
        game = new Game("game1", List.of(player1, player2), board, Team.FIRST);
    }

    /**
     * 歩兵の正常な移動（前に1マス）
     */
    @Test
    void testPawnValidMove() {
        PlayerMove move = new PlayerMove(player1, pawn, List.of(Direction.UP), false);
        
        Position initialPos = board.find(pawn);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が成功したことを確認
        Position newPos = board.find(pawn);
        assertEquals(initialPos.y() + 1, newPos.y());
    }

    /**
     * 歩兵の不正な移動（横に移動しようとする）
     */
    @Test
    void testPawnInvalidMove() {
        PlayerMove move = new PlayerMove(player1, pawn, List.of(Direction.LEFT), false);
        
        Position initialPos = board.find(pawn);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認（位置が変わらない）
        Position currentPos = board.find(pawn);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 歩兵の連続移動は不可
     */
    @Test
    void testPawnCannotMoveMultipleSteps() {
        PlayerMove move = new PlayerMove(player1, pawn, List.of(Direction.UP, Direction.UP), false);
        
        Position initialPos = board.find(pawn);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(pawn);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 飛車の縦方向への連続移動
     */
    @Test
    void testRookValidMultipleSteps() {
        // 飛車の前の歩兵を取り除く
        board.captureAll(new Position(2, 3), Team.SECOND);
        
        PlayerMove move = new PlayerMove(player1, rook, 
                                         List.of(Direction.UP, Direction.UP, Direction.UP), false);
        
        Position initialPos = board.find(rook);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 3マス上に移動したことを確認
        Position newPos = board.find(rook);
        assertEquals(initialPos.y() + 3, newPos.y());
    }

    /**
     * 飛車の斜め移動は不可
     */
    @Test
    void testRookCannotMoveDiagonally() {
        PlayerMove move = new PlayerMove(player1, rook, List.of(Direction.UP_LEFT), false);
        
        Position initialPos = board.find(rook);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(rook);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 角行の斜め方向への連続移動
     */
    @Test
    void testBishopValidMultipleSteps() {
        // 角行の前の歩兵を取り除く
        board.captureAll(new Position(7, 3), Team.SECOND);
        
        PlayerMove move = new PlayerMove(player1, bishop, 
                                         List.of(Direction.UP_LEFT, Direction.UP_LEFT), false);
        
        Position initialPos = board.find(bishop);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 斜め左上に2マス移動したことを確認
        Position newPos = board.find(bishop);
        assertEquals(initialPos.x() - 2, newPos.x());
        assertEquals(initialPos.y() + 2, newPos.y());
    }

    /**
     * 角行の縦方向への移動は不可
     */
    @Test
    void testBishopCannotMoveVertically() {
        PlayerMove move = new PlayerMove(player1, bishop, List.of(Direction.UP), false);
        
        Position initialPos = board.find(bishop);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(bishop);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 桂馬の正常な移動
     */
    @Test
    void testKnightValidMove() {
        PlayerMove move = new PlayerMove(player1, knight, List.of(Direction.KNIGHT_LEFT), false);
        
        Position initialPos = board.find(knight);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が成功したことを確認
        Position newPos = board.find(knight);
        assertNotEquals(initialPos, newPos);
    }

    /**
     * 桂馬の連続移動は不可
     */
    @Test
    void testKnightCannotMoveMultipleSteps() {
        PlayerMove move = new PlayerMove(player1, knight, 
                                         List.of(Direction.KNIGHT_LEFT, Direction.KNIGHT_LEFT), false);
        
        Position initialPos = board.find(knight);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(knight);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 空の方向リストは移動不可
     */
    @Test
    void testEmptyDirectionList() {
        PlayerMove move = new PlayerMove(player1, pawn, List.of(), false);
        
        Position initialPos = board.find(pawn);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(pawn);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 飛車が異なる方向への連続移動は不可（曲がれない）
     */
    @Test
    void testRookCannotChangeDirection() {
        // 飛車の前の歩兵を取り除く
        board.captureAll(new Position(2, 3), Team.SECOND);
        
        // 上に移動してから右に移動しようとする
        PlayerMove move = new PlayerMove(player1, rook, 
                                         List.of(Direction.UP, Direction.RIGHT), false);
        
        Position initialPos = board.find(rook);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(rook);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 成り飛車（龍王）の斜め1マス移動は可能
     */
    @Test
    void testPromotedRookCanMoveDiagonallyOneStep() {
        // 飛車を成らせる
        rook.setPromoted(true);
        
        PlayerMove move = new PlayerMove(player1, rook, List.of(Direction.UP_LEFT), false);
        
        Position initialPos = board.find(rook);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が成功したことを確認
        Position newPos = board.find(rook);
        assertEquals(initialPos.x() - 1, newPos.x());
        assertEquals(initialPos.y() + 1, newPos.y());
    }

    /**
     * 成り飛車（龍王）の斜め連続移動は不可
     */
    @Test
    void testPromotedRookCannotMoveDiagonallyMultipleSteps() {
        // 飛車を成らせる
        rook.setPromoted(true);
        
        // 斜めに2マス移動しようとする
        PlayerMove move = new PlayerMove(player1, rook, 
                                         List.of(Direction.UP_LEFT, Direction.UP_LEFT), false);
        
        Position initialPos = board.find(rook);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(rook);
        assertEquals(initialPos, currentPos);
    }

    /**
     * 成り角（龍馬）の縦方向1マス移動は可能
     */
    @Test
    void testPromotedBishopCanMoveVerticallyOneStep() {
        // 角行を成らせる
        bishop.setPromoted(true);
        
        PlayerMove move = new PlayerMove(player1, bishop, List.of(Direction.UP), false);
        
        Position initialPos = board.find(bishop);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が成功したことを確認
        Position newPos = board.find(bishop);
        assertEquals(initialPos.y() + 1, newPos.y());
    }

    /**
     * 成り角（龍馬）の縦方向連続移動は不可
     */
    @Test
    void testPromotedBishopCannotMoveVerticallyMultipleSteps() {
        // 角行を成らせる
        bishop.setPromoted(true);
        
        // 縦に2マス移動しようとする
        PlayerMove move = new PlayerMove(player1, bishop, 
                                         List.of(Direction.UP, Direction.UP), false);
        
        Position initialPos = board.find(bishop);
        game.applyMove(move);
        game.handleTurnEnd();
        
        // 移動が拒否されたことを確認
        Position currentPos = board.find(bishop);
        assertEquals(initialPos, currentPos);
    }
}

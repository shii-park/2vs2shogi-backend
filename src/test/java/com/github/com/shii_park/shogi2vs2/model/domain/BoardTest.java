package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.MoveResult;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Boardクラスのテスト
 * 将棋盤の初期化、駒の配置・移動・捕獲・スタック、成りエリア判定などの機能を検証
 */
class BoardTest {

    private Board board;
    private Piece piece1;
    private Piece piece2;
    private Position pos1;
    private Position pos2;

    /**
     * 各テストの前に実行される初期化処理
     * 処理: 2つの駒を持つテスト用の盤面を生成
     */
    @BeforeEach
    void setUp() {
        // FIRSTチームの歩兵を生成
        piece1 = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        // SECONDチームの歩兵を生成
        piece2 = new Piece(2, PieceType.PAWN, Team.SECOND, true);
        // piece1の初期位置(5, 5)
        pos1 = new Position(5, 5);
        // piece2の初期位置(6, 6)
        pos2 = new Position(6, 6);

        // 初期配置マップを作成
        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(piece1, pos1);
        initialPieces.put(piece2, pos2);
        // 盤面を初期化
        board = new Board(initialPieces);
    }

    /**
     * 盤面の初期化テスト
     * 処理: Boardが正しく初期化され、指定した位置に駒が配置されていることを確認
     */
    @Test
    void testBoardInitialization() {
        // 盤面が正常に生成されたことを確認
        assertNotNull(board);
        // pos1の一番上にpiece1があることを確認
        assertEquals(piece1, board.getTopPiece(pos1));
        // pos2の一番上にpiece2があることを確認
        assertEquals(piece2, board.getTopPiece(pos2));
    }

    /**
     * スタックの最上位の駒取得テスト
     * 処理: 指定位置の一番上の駒を取得できること、空の位置ではnullが返ることを確認
     */
    @Test
    void testGetTopPiece() {
        // pos1の一番上の駒がpiece1であることを確認
        assertEquals(piece1, board.getTopPiece(pos1));
        // 駒が配置されていない位置(1, 1)ではnullが返ることを確認
        assertNull(board.getTopPiece(new Position(1, 1)));
    }

    /**
     * 駒の位置検索テスト
     * 処理: 駒から現在位置を検索できることを確認
     */
    @Test
    void testFindPiece() {
        // piece1の位置がpos1であることを確認
        assertEquals(pos1, board.find(piece1));
        // piece2の位置がpos2であることを確認
        assertEquals(pos2, board.find(piece2));
    }

    /**
     * 駒の移動テスト
     * 処理: 駒を新しい位置に移動させ、元の位置が空になり新しい位置に駒があることを確認
     */
    @Test
    void testMovePiece() {
        // 新しい位置(5, 6)を指定
        Position newPos = new Position(5, 6);
        // piece1を新しい位置に移動
        board.movePiece(piece1, newPos);
        // piece1の現在位置が新しい位置になったことを確認
        assertEquals(newPos, board.find(piece1));
        // 元の位置pos1に駒がないことを確認
        assertNull(board.getTopPiece(pos1));
        // 新しい位置にpiece1があることを確認
        assertEquals(piece1, board.getTopPiece(newPos));
    }

    /**
     * 盤面内判定テスト
     * 処理: 座標が9x9の盤面内(1~9)にあるかを判定できることを確認
     */
    @Test
    void testIsInsideBoard() {
        // 盤面の左下隅(1, 1)は盤面内
        assertTrue(board.isInsideBoard(new Position(1, 1)));
        // 盤面の右上隅(9, 9)は盤面内
        assertTrue(board.isInsideBoard(new Position(9, 9)));
        // 盤面の中央(5, 5)は盤面内
        assertTrue(board.isInsideBoard(new Position(5, 5)));
        // x=0は盤面外（左端より外）
        assertFalse(board.isInsideBoard(new Position(0, 5)));
        // x=10は盤面外（右端より外）
        assertFalse(board.isInsideBoard(new Position(10, 5)));
        // y=0は盤面外（下端より外）
        assertFalse(board.isInsideBoard(new Position(5, 0)));
        // y=10は盤面外（上端より外）
        assertFalse(board.isInsideBoard(new Position(5, 10)));
    }

    /**
     * 通常の1マス移動テスト
     * 処理: 空いているマスへの移動でMOVEDが返り、正しい位置に移動することを確認
     */
    @Test
    void testMoveOneStepNormal() {
        // piece1を上に1マス移動
        MoveResult result = board.moveOneStep(piece1, Direction.UP);
        // 移動結果がMOVEDであることを確認
        assertEquals(MoveResult.MOVED, result);
        // piece1が(5, 6)に移動したことを確認
        assertEquals(new Position(5, 6), board.find(piece1));
    }

    /**
     * 敵駒を捕獲する移動テスト
     * 処理: 敵チームの駒がいるマスに移動すると、駒を捕獲してCAPTUREDが返ることを確認
     */
    @Test
    void testMoveOneStepCapture() {
        // piece1を(6, 5)に移動（piece2のすぐ下）
        board.movePiece(piece1, new Position(6, 5));
        // piece1を上に移動（piece2を捕獲）
        MoveResult result = board.moveOneStep(piece1, Direction.UP);
        // 捕獲が発生したことを確認
        assertEquals(MoveResult.CAPTURED, result);
        // piece1が(6, 6)に移動したことを確認
        assertEquals(new Position(6, 6), board.find(piece1));
        // (6, 6)の一番上がpiece2ではないことを確認（捕獲された）
        assertNotEquals(piece2, board.getTopPiece(new Position(6, 6)));
    }

    /**
     * 味方駒へのスタック移動テスト
     * 処理: 同じチームの駒がいるマスに移動するとSTACKEDが返ることを確認
     */
    @Test
    void testMoveOneStepStacked() {
        // piece1を(5,6)に移動
        board.movePiece(piece1, new Position(5, 6));
        // piece2(Team.SECOND)を(5,5)に配置
        board.movePiece(piece2, new Position(5, 5));
        // 同じチームのpiece3を作成して(5,4)に配置
        Piece piece3 = new Piece(3, PieceType.PAWN, Team.SECOND, true);
        Map<Piece, Position> newPieces = new HashMap<>();
        newPieces.put(piece3, new Position(5, 4));
        board.stackPiece(new Position(5, 4), piece3);
        // piece3を上に移動させると、同じチームのpiece2がいるのでSTACKED
        MoveResult result = board.moveOneStep(piece3, Direction.UP);
        // スタックが発生したことを確認
        assertEquals(MoveResult.STACKED, result);
    }

    /**
     * 盤外への移動（ドロップ）テスト
     * 処理: 盤面の端から外に出る移動でFALLEDが返ることを確認
     */
    @Test
    void testMoveOneStepDropped() {
        // piece1を右端(9, 5)に移動
        board.movePiece(piece1, new Position(9, 5));
        // さらに右に移動しようとすると盤外に出る
        MoveResult result = board.moveOneStep(piece1, Direction.RIGHT);
        // 盤外に落ちたことを確認
        assertEquals(MoveResult.FELL, result);
    }

    /**
     * 駒をスタックするテスト
     * 処理: 同じ位置に複数の駒を積めることを確認
     */
    @Test
    void testStackPiece() {
        // 新しい駒piece3を生成
        Piece piece3 = new Piece(3, PieceType.PAWN, Team.FIRST, true);
        // piece3をpos1（piece1がいる位置）にスタック
        board.stackPiece(pos1, piece3);
        // pos1にある全ての駒を取得
        List<Piece> pieces = board.getAllPiecesAt(pos1);
        // 2つの駒がスタックされていることを確認
        assertEquals(2, pieces.size());
        // piece1が含まれていることを確認
        assertTrue(pieces.contains(piece1));
        // piece3が含まれていることを確認
        assertTrue(pieces.contains(piece3));
    }

    /**
     * 位置にある全ての駒を捕獲するテスト
     * 処理: スタックされた駒を全て一度に捕獲できることを確認
     */
    @Test
    void testCaptureAll() {
        // piece3を生成してpos2にスタック（piece2の上に積む）
        Piece piece3 = new Piece(3, PieceType.PAWN, Team.SECOND, true);
        board.stackPiece(pos2, piece3);
        // FIRSTチームがpos2の全ての駒を捕獲
        List<Piece> captured = board.captureAll(pos2, Team.FIRST);
        // 2つの駒が捕獲されたことを確認
        assertEquals(2, captured.size());
        // piece2が捕獲されたことを確認
        assertTrue(captured.contains(piece2));
        // piece3が捕獲されたことを確認
        assertTrue(captured.contains(piece3));
        // pos2に駒がなくなったことを確認
        assertNull(board.getTopPiece(pos2));
    }

    /**
     * 駒がスタックの最上位にあるか判定するテスト
     * 処理: 駒をスタックすると、下の駒は最上位でなくなることを確認
     */
    @Test
    void testIsTop() {
        // piece1は最初は最上位
        assertTrue(board.isTop(piece1));
        // piece2も最上位
        assertTrue(board.isTop(piece2));
        // piece3をpiece1と同じ位置にスタック
        Piece piece3 = new Piece(3, PieceType.PAWN, Team.FIRST, true);
        board.stackPiece(pos1, piece3);
        // piece1はもう一番上ではない
        assertFalse(board.isTop(piece1));
        // piece3が一番上
        assertTrue(board.isTop(piece3));
    }

    /**
     * FIRSTチームの成りエリア判定テスト
     * 処理: FIRSTチーム（下から上に進む）はy>=7が成りエリアであることを確認
     */
    @Test
    void testIsInPromotionZoneFirstTeam() {
        // y=7はFIRSTチームの成りエリア
        assertTrue(board.isInPromotionZone(new Position(5, 7), Team.FIRST));
        // y=9もFIRSTチームの成りエリア
        assertTrue(board.isInPromotionZone(new Position(5, 9), Team.FIRST));
        // y=6はFIRSTチームの成りエリアではない
        assertFalse(board.isInPromotionZone(new Position(5, 6), Team.FIRST));
    }

    /**
     * SECONDチームの成りエリア判定テスト
     * 処理: SECONDチーム（上から下に進む）はy<=3が成りエリアであることを確認
     */
    @Test
    void testIsInPromotionZoneSecondTeam() {
        // y=3はSECONDチームの成りエリア
        assertTrue(board.isInPromotionZone(new Position(5, 3), Team.SECOND));
        // y=1もSECONDチームの成りエリア
        assertTrue(board.isInPromotionZone(new Position(5, 1), Team.SECOND));
        // y=4はSECONDチームの成りエリアではない
        assertFalse(board.isInPromotionZone(new Position(5, 4), Team.SECOND));
    }

    /**
     * 駒を成らせるテスト
     * 処理: promotePiece()を呼び出すと駒が成り状態になることを確認
     */
    @Test
    void testPromotePiece() {
        // piece1は初期状態では成っていない
        assertFalse(piece1.isPromoted());
        // piece1を成らせる
        board.promotePiece(piece1);
        // piece1が成り状態になったことを確認
        assertTrue(piece1.isPromoted());
    }

    /**
     * 駒のチーム変更テスト
     * 処理: changeTeam()を呼び出すとFIRST⇔SECONDが切り替わることを確認
     */
    @Test
    void testChangeTeam() {
        // piece1は初期状態でFIRSTチーム
        assertEquals(Team.FIRST, piece1.getTeam());
        // チームを変更（FIRST→SECOND）
        board.changeTeam(piece1);
        // SECONDチームになったことを確認
        assertEquals(Team.SECOND, piece1.getTeam());
        // もう一度変更（SECOND→FIRST）
        board.changeTeam(piece1);
        // FIRSTチームに戻ったことを確認
        assertEquals(Team.FIRST, piece1.getTeam());
    }

    /**
     * 空の位置の駒リスト取得テスト
     * 処理: 駒が配置されていない位置では空のリストが返ることを確認
     */
    @Test
    void testGetAllPiecesAtEmptyPosition() {
        // 駒が配置されていない位置(1, 1)の駒リストを取得
        List<Piece> pieces = board.getAllPiecesAt(new Position(1, 1));
        // 空のリストが返ることを確認
        assertTrue(pieces.isEmpty());
    }
}

package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * CapturedPiecesクラスのテスト
 * 駒の捕獲管理、勝利判定の機能を検証
 */
class CapturedPiecesTest {

    private CapturedPieces capturedPieces;
    private Piece pawn1;
    private Piece pawn2;
    private Piece king;

    /**
     * 各テストの前に実行される初期化処理
     * 処理: CapturedPiecesインスタンスとテスト用の駒を生成
     */
    @BeforeEach
    void setUp() {
        // 捕獲駒管理オブジェクトを生成
        capturedPieces = new CapturedPieces();
        // FIRSTチームの歩兵を生成
        pawn1 = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        // SECONDチームの歩兵を生成
        pawn2 = new Piece(2, PieceType.PAWN, Team.SECOND, true);
        // FIRSTチームの王将を生成
        king = new Piece(3, PieceType.KING, Team.FIRST, false);
    }

    /**
     * CapturedPiecesの初期化テスト
     * 処理: 初期状態では両チームの捕獲駒リストが空で、勝者が未設定であることを確認
     */
    @Test
    void testInitialization() {
        // FIRSTチームの捕獲リストが空であることを確認
        assertTrue(capturedPieces.getCapturedPieces(Team.FIRST).isEmpty());
        // SECONDチームの捕獲リストが空であることを確認
        assertTrue(capturedPieces.getCapturedPieces(Team.SECOND).isEmpty());
        // 勝者が未設定であることを確認
        assertTrue(capturedPieces.getWinnerTeam().isEmpty());
    }

    /**
     * 駒の捕獲テスト
     * 処理: FIRSTチームがSECONDチームの歩兵を捕獲し、駒のチームが変更されることを確認
     */
    @Test
    void testCapturePiece() {
        // FIRSTチームがpawn2を捕獲
        capturedPieces.capturedPiece(Team.FIRST, pawn2);
        // FIRSTチームの捕獲リストを取得
        List<Piece> captured = capturedPieces.getCapturedPieces(Team.FIRST);
        // 捕獲リストに1つの駒が含まれることを確認
        assertEquals(1, captured.size());
        // 捕獲した駒がpawn2であることを確認
        assertTrue(captured.contains(pawn2));
        // 捕獲された駒のチームがFIRSTに変更されたことを確認
        assertEquals(Team.FIRST, pawn2.getTeam());
    }

    /**
     * 複数の駒を捕獲するテスト
     * 処理: 同じチームが複数の駒を捕獲できることを確認
     */
    @Test
    void testCaptureMultiplePieces() {
        // FIRSTチームがpawn2を捕獲
        capturedPieces.capturedPiece(Team.FIRST, pawn2);
        // 新しい歩兵を生成
        Piece pawn3 = new Piece(4, PieceType.PAWN, Team.SECOND, true);
        // FIRSTチームがpawn3も捕獲
        capturedPieces.capturedPiece(Team.FIRST, pawn3);
        // FIRSTチームの捕獲リストを取得
        List<Piece> captured = capturedPieces.getCapturedPieces(Team.FIRST);
        // 捕獲リストに2つの駒が含まれることを確認
        assertEquals(2, captured.size());
    }

    /**
     * 王将の捕獲による勝利判定テスト
     * 処理: 王将を捕獲したチームが勝者として設定されることを確認
     */
    @Test
    void testCaptureKingSetsWinner() {
        // 初期状態では勝者が未設定
        assertTrue(capturedPieces.getWinnerTeam().isEmpty());
        // SECONDチームがFIRSTチームの王将を捕獲
        capturedPieces.capturedPiece(Team.SECOND, king);
        // 勝者が設定されたことを確認
        assertTrue(capturedPieces.getWinnerTeam().isPresent());
        // SECONDチームが勝者として設定されたことを確認
        assertEquals(Team.SECOND, capturedPieces.getWinnerTeam().get());
    }

    /**
     * 捕獲時の成り状態リセットテスト
     * 処理: 成った駒を捕獲すると、成り状態がリセットされることを確認
     */
    @Test
    void testCaptureResetsPromotion() {
        // pawn1を成らせる
        pawn1.setPromoted(true);
        // 成り状態であることを確認
        assertTrue(pawn1.isPromoted());
        // SECONDチームがpawn1を捕獲
        capturedPieces.capturedPiece(Team.SECOND, pawn1);
        // 捕獲により成り状態がリセットされたことを確認
        assertFalse(pawn1.isPromoted());
    }

    /**
     * 捕獲時のチーム変更テスト
     * 処理: 駒を捕獲すると、その駒のチームが捕獲したチームに変更されることを確認
     */
    @Test
    void testCaptureChangesTeam() {
        // pawn2は初期状態でSECONDチーム
        assertEquals(Team.SECOND, pawn2.getTeam());
        // FIRSTチームがpawn2を捕獲
        capturedPieces.capturedPiece(Team.FIRST, pawn2);
        // pawn2のチームがFIRSTに変更されたことを確認
        assertEquals(Team.FIRST, pawn2.getTeam());
    }

    /**
     * 各チーム別の捕獲管理テスト
     * 処理: 各チームが独立して駒を捕獲できることを確認
     */
    @Test
    void testSeparateTeamCaptures() {
        // FIRSTチームがpawn2を捕獲
        capturedPieces.capturedPiece(Team.FIRST, pawn2);
        // SECONDチームがpawn1を捕獲
        capturedPieces.capturedPiece(Team.SECOND, pawn1);
        // 各チームの捕獲リストに1つずつ駒が含まれることを確認
        assertEquals(1, capturedPieces.getCapturedPieces(Team.FIRST).size());
        assertEquals(1, capturedPieces.getCapturedPieces(Team.SECOND).size());
    }
}

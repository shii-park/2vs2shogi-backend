package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * TurnManagerクラスのテスト
 * ゲームのターン管理、チーム交代、時間制限の機能を検証
 */
class TurnManagerTest {

    private TurnManager turnManager;

    /**
     * 各テストの前に実行される初期化処理
     * 処理: FIRSTチームから開始するTurnManagerインスタンスを生成
     */
    @BeforeEach
    void setUp() {
        // FIRSTチームを先攻として初期化
        turnManager = new TurnManager(Team.FIRST);
    }

    /**
     * TurnManagerの初期化テスト
     * 処理: TurnManagerが正しく初期化され、先攻チームとターン番号0が設定されることを確認
     */
    @Test
    void testInitialization() {
        // 現在のチームがFIRSTであることを確認
        assertEquals(Team.FIRST, turnManager.getCurrentTurn());
        // ターン番号が0（初期状態）であることを確認
        assertEquals(0, turnManager.getTurnNumber());
    }

    /**
     * 次のターンへの進行テスト
     * 処理: nextTurn()を呼び出すとチームが交代し、ターン番号がインクリメントされることを確認
     */
    @Test
    void testNextTurn() {
        // 次のターンに進める
        turnManager.nextTurn();
        // チームがSECONDに交代したことを確認
        assertEquals(Team.SECOND, turnManager.getCurrentTurn());
        // ターン番号が1にインクリメントされたことを確認
        assertEquals(1, turnManager.getTurnNumber());
    }

    /**
     * 複数ターンの進行テスト
     * 処理: 3ターン進めて、チームが交互に切り替わることとターン番号が正しく増加することを確認
     */
    @Test
    void testMultipleTurns() {
        // 1ターン目: FIRSTからSECONDへ
        turnManager.nextTurn();
        assertEquals(Team.SECOND, turnManager.getCurrentTurn());
        // 2ターン目: SECONDからFIRSTへ
        turnManager.nextTurn();
        assertEquals(Team.FIRST, turnManager.getCurrentTurn());
        // 3ターン目: FIRSTからSECONDへ
        turnManager.nextTurn();
        assertEquals(Team.SECOND, turnManager.getCurrentTurn());
        // 合計3ターン経過していることを確認
        assertEquals(3, turnManager.getTurnNumber());
    }

    /**
     * ターン開始時刻の設定テスト
     * 処理: startTurn()を呼び出した後、短時間では時間切れにならないことを確認
     */
    @Test
    void testStartTurn() throws InterruptedException {
        // ターンを開始（タイマー開始）
        turnManager.startTurn();
        // 100ミリ秒待機
        Thread.sleep(100);
        // まだ時間切れではないことを確認（制限時間は30秒）
        assertFalse(turnManager.isTimeout());
    }

    /**
     * タイムアウト判定の初期状態テスト
     * 処理: ターンを開始した直後は時間切れではないことを確認
     */
    @Test
    void testIsNotTimeout() {
        // ターンを開始
        turnManager.startTurn();
        // 時間切れではないことを確認
        assertFalse(turnManager.isTimeout());
    }

    /**
     * タイマー取得テスト
     * 処理: getTimer()が有効なDurationオブジェクトを返すことを確認
     */
    @Test
    void testGetTimer() {
        // ターンを開始
        turnManager.startTurn();
        // タイマーがnullでないことを確認
        assertNotNull(turnManager.getTimer());
    }

    /**
     * チーム交代のパターンテスト
     * 処理: 先攻がFIRSTの場合とSECONDの場合、どちらも正しくチームが交代することを確認
     */
    @Test
    void testAlternatingTeams() {
        // FIRSTチームから開始するケース
        TurnManager firstTurn = new TurnManager(Team.FIRST);
        // 初期状態はFIRST
        assertEquals(Team.FIRST, firstTurn.getCurrentTurn());
        // 次のターンでSECONDへ
        firstTurn.nextTurn();
        assertEquals(Team.SECOND, firstTurn.getCurrentTurn());

        // SECONDチームから開始するケース
        TurnManager secondTurn = new TurnManager(Team.SECOND);
        // 初期状態はSECOND
        assertEquals(Team.SECOND, secondTurn.getCurrentTurn());
        // 次のターンでFIRSTへ
        secondTurn.nextTurn();
        assertEquals(Team.FIRST, secondTurn.getCurrentTurn());
    }
}

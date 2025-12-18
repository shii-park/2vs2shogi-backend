package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Playerクラスのテスト
 * プレイヤーの状態管理（接続状態、投了状態）を検証
 */
class PlayerTest {

    private Player player;

    /**
     * 各テストの前に実行される初期化処理
     * 処理: テスト用のプレイヤーインスタンスを生成
     */
    @BeforeEach
    void setUp() {
        // ID "player1"、FIRSTチームのプレイヤーを生成
        player = new Player("player1", Team.FIRST);
    }

    /**
     * プレイヤーの生成テスト
     * 処理: プレイヤーのID、チーム、初期状態（接続中、投了していない）が正しく設定されることを確認
     */
    @Test
    void testPlayerCreation() {
        // プレイヤーIDが正しく設定されている
        assertEquals("player1", player.getId());
        // FIRSTチームに所属している
        assertEquals(Team.FIRST, player.getTeam());
        // 初期状態で接続中である
        assertTrue(player.isConnected());
        // 初期状態で投了していない
        assertFalse(player.isResign());
    }

    /**
     * プレイヤーの接続状態変更テスト
     * 処理: プレイヤーの接続状態を切断→接続と変更できることを確認
     */
    @Test
    void testSetConnected() {
        // 初期状態は接続中
        assertTrue(player.isConnected());
        // 切断状態に変更
        player.setConnected(false);
        // 切断状態になったことを確認
        assertFalse(player.isConnected());
        // 再び接続状態に変更
        player.setConnected(true);
        // 接続状態に戻ったことを確認
        assertTrue(player.isConnected());
    }

    /**
     * プレイヤーの投了状態変更テスト
     * 処理: プレイヤーが投了する→取り消すという状態変更ができることを確認
     */
    @Test
    void testSetResign() {
        // 初期状態は投了していない
        assertFalse(player.isResign());
        // 投了状態に変更
        player.setResign(true);
        // 投了したことを確認
        assertTrue(player.isResign());
        // 投了を取り消し（テスト用）
        player.setResign(false);
        // 投了していない状態に戻ったことを確認
        assertFalse(player.isResign());
    }

    /**
     * 異なるチームのプレイヤー生成テスト
     * 処理: FIRSTチームとSECONDチームのプレイヤーを生成し、それぞれ正しいチームに所属していることを確認
     */
    @Test
    void testDifferentTeams() {
        // FIRSTチームのプレイヤーを生成
        Player player1 = new Player("p1", Team.FIRST);
        // SECONDチームのプレイヤーを生成
        Player player2 = new Player("p2", Team.SECOND);
        // player1がFIRSTチームに所属していることを確認
        assertEquals(Team.FIRST, player1.getTeam());
        // player2がSECONDチームに所属していることを確認
        assertEquals(Team.SECOND, player2.getTeam());
    }

    /**
     * 異なるIDのプレイヤーテスト
     * 処理: 同じチームでも異なるIDを持つプレイヤーを区別できることを確認
     */
    @Test
    void testDifferentIds() {
        // ID "p1"のプレイヤーを生成
        Player player1 = new Player("p1", Team.FIRST);
        // ID "p2"のプレイヤーを生成
        Player player2 = new Player("p2", Team.FIRST);
        // 2つのプレイヤーのIDが異なることを確認
        assertNotEquals(player1.getId(), player2.getId());
    }
}

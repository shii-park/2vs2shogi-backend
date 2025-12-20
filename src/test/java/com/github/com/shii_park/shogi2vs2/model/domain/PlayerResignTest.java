package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.GameStatus;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * PlayerResignクラスとGame.handleResign()のテスト
 * プレイヤーの投了処理が正しく機能することを検証
 */
class PlayerResignTest {

    private Game game;
    private Board board;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;

    @BeforeEach
    void setUp() {
        board = BoardFactory.createBoard();

        // FIRSTチームのプレイヤー（2人）
        player1 = new Player("p1", Team.FIRST);
        player2 = new Player("p2", Team.FIRST);

        // SECONDチームのプレイヤー（2人）
        player3 = new Player("p3", Team.SECOND);
        player4 = new Player("p4", Team.SECOND);

        game = new Game("game1", java.util.List.of(player1, player2, player3, player4), board, Team.FIRST);
    }

    /**
     * PlayerResignオブジェクトが正しく生成できることを確認
     */
    @Test
    void testPlayerResignCreation() {
        PlayerResign resign = new PlayerResign(player1, 10);

        assertNotNull(resign);
        assertEquals(player1, resign.player());
        assertEquals(10, resign.turnNumber());
    }

    /**
     * PlayerResignのレコード特性を確認（equals/hashCode）
     */
    @Test
    void testPlayerResignEquality() {
        PlayerResign resign1 = new PlayerResign(player1, 5);
        PlayerResign resign2 = new PlayerResign(player1, 5);
        PlayerResign resign3 = new PlayerResign(player1, 6);
        PlayerResign resign4 = new PlayerResign(player2, 5);

        // 同じプレイヤー、同じターン数は等しい
        assertEquals(resign1, resign2);
        assertEquals(resign1.hashCode(), resign2.hashCode());

        // ターン数が異なれば等しくない
        assertNotEquals(resign1, resign3);

        // プレイヤーが異なれば等しくない
        assertNotEquals(resign1, resign4);
    }

    /**
     * FIRSTチームのプレイヤーが投了したときの処理を確認
     * SECONDチームが勝者となり、ゲームが終了すること
     */
    @Test
    void testHandleResignFirstTeam() {
        // 初期状態の確認
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertNull(game.getWinnerTeam());

        // FIRSTチームが投了
        game.handleResign(Team.FIRST);

        // ゲームが終了状態になったことを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
        // SECONDチームが勝者になったことを確認
        assertEquals(Team.SECOND, game.getWinnerTeam());
    }

    /**
     * SECONDチームのプレイヤーが投了したときの処理を確認
     * FIRSTチームが勝者となり、ゲームが終了すること
     */
    @Test
    void testHandleResignSecondTeam() {
        // 初期状態の確認
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertNull(game.getWinnerTeam());

        // SECONDチームが投了
        game.handleResign(Team.SECOND);

        // ゲームが終了状態になったことを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
        // FIRSTチームが勝者になったことを確認
        assertEquals(Team.FIRST, game.getWinnerTeam());
    }

    /**
     * PlayerResignオブジェクトを使った投了シナリオのテスト
     * プレイヤーの投了意思表示からゲーム終了までの流れを確認
     */
    @Test
    void testResignScenarioWithPlayerResignObject() {
        // player1が5ターン目で投了を宣言
        PlayerResign resign = new PlayerResign(player1, 5);

        // 投了処理を実行
        game.handleResign(resign.player().getTeam());

        // ゲームが終了していることを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
        // 投了したプレイヤーの相手チームが勝者であることを確認
        assertEquals(Team.SECOND, game.getWinnerTeam());
    }

    /**
     * ゲーム進行中に投了した場合のテスト
     */
    @Test
    void testResignDuringGame() {
        // ゲームが進行中であることを確認
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertEquals(Team.FIRST, game.getCurrentTurn());

        // FIRSTチームが投了
        PlayerResign resign = new PlayerResign(player1, 1);
        game.handleResign(resign.player().getTeam());

        // ゲームが終了していることを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(Team.SECOND, game.getWinnerTeam());
    }

    /**
     * 複数のプレイヤーが同じチームで投了した場合のテスト
     * （最初の投了でゲームが終了するため、2回目は無視される想定）
     */
    @Test
    void testMultipleResignsFromSameTeam() {
        // player1が投了
        game.handleResign(Team.FIRST);
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(Team.SECOND, game.getWinnerTeam());

        // 同じチームのplayer2がさらに投了しようとする
        // （すでに終了しているので状態は変わらない）
        game.handleResign(Team.FIRST);

        // 状態が変わっていないことを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertEquals(Team.SECOND, game.getWinnerTeam());
    }

    /**
     * 異なるターン番号でのPlayerResignオブジェクトの生成テスト
     */
    @Test
    void testPlayerResignWithDifferentTurnNumbers() {
        PlayerResign earlyResign = new PlayerResign(player1, 1);
        PlayerResign midGameResign = new PlayerResign(player2, 15);
        PlayerResign lateGameResign = new PlayerResign(player3, 50);

        assertEquals(1, earlyResign.turnNumber());
        assertEquals(15, midGameResign.turnNumber());
        assertEquals(50, lateGameResign.turnNumber());

        // それぞれのプレイヤーが正しく保持されていることを確認
        assertEquals(player1, earlyResign.player());
        assertEquals(player2, midGameResign.player());
        assertEquals(player3, lateGameResign.player());
    }

    /**
     * PlayerResignのtoString()メソッドのテスト
     * recordのデフォルト実装が機能することを確認
     */
    @Test
    void testPlayerResignToString() {
        PlayerResign resign = new PlayerResign(player1, 10);
        String str = resign.toString();

        assertNotNull(str);
        assertTrue(str.contains("PlayerResign"));
        assertTrue(str.contains("10"));
    }

    /**
     * 投了前後でターンが変わらないことを確認
     * （投了は即座にゲームを終了させるため）
     */
    @Test
    void testTurnDoesNotChangeAfterResign() {
        Team initialTurn = game.getCurrentTurn();

        // FIRSTチームが投了
        game.handleResign(Team.FIRST);

        // ターンが変わっていないことを確認
        assertEquals(initialTurn, game.getCurrentTurn());
        // ゲームが終了していることを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }
}

package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.GameStatus;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Gameクラスのテスト
 * ゲーム全体の流れ（初期化、駒の移動適用、勝利判定、投了処理）を検証
 */
class GameTest {

    private Game game;
    private Board board;
    private Player player1;
    private Player player2;
    private Piece piece1;
    private Piece piece2;

    /**
     * 各テストの前に実行される初期化処理
     * 処理: 2対2将棋のゲームを初期化（2人のプレイヤー、盤面、駒を配置）
     */
    @BeforeEach
    void setUp() {
        // FIRSTチームの歩兵を生成
        piece1 = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        // SECONDチームの歩兵を生成
        piece2 = new Piece(2, PieceType.PAWN, Team.SECOND, true);
        // 各駒の初期位置を設定
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(6, 6);

        // 盤面に駒を配置
        Map<Piece, Position> initialPieces = new HashMap<>();
        initialPieces.put(piece1, pos1);
        initialPieces.put(piece2, pos2);
        board = new Board(initialPieces);

        // 2人のプレイヤーを生成（各チームに1人ずつ）
        player1 = new Player("p1", Team.FIRST);
        player2 = new Player("p2", Team.SECOND);
        List<Player> players = List.of(player1, player2);

        // ゲームを初期化（FIRSTチームが先攻）
        game = new Game("game1", players, board, Team.FIRST);
    }

    /**
     * ゲームの初期化テスト
     * 処理: ゲームが正しく初期化され、IN_PROGRESS状態で先攻チームが設定されることを確認
     */
    @Test
    void testGameInitialization() {
        // ゲームが正常に生成されたことを確認
        assertNotNull(game);
        // ゲーム状態が進行中であることを確認
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        // 現在のターンがFIRSTチームであることを確認
        assertEquals(Team.FIRST, game.getCurrentTurn());
    }

    /**
     * 通常の移動適用テスト
     * 処理: 2人のプレイヤーの移動を同時に適用し、駒が正しく移動してターンが切り替わることを確認
     */
    @Test
    void testApplyMovesNormalMove() {
        // player1の移動：piece1を上に1マス移動、成りなし
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        // player2の移動：piece2を下に1マス移動、成りなし
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        // 両プレイヤーの移動を適用
        game.applyMoves(move1, move2);
        // piece1が(5, 6)に移動したことを確認
        assertEquals(new Position(5, 6), board.find(piece1));
        // piece2が(6, 5)に移動したことを確認
        assertEquals(new Position(6, 5), board.find(piece2));
        // ターンがSECONDチームに切り替わったことを確認
        assertEquals(Team.SECOND, game.getCurrentTurn());
    }

    /**
     * 成りを伴う移動テスト
     * 処理: 成りエリアに入った駒が成りフラグtrueで移動すると、正しく成ることを確認
     */
    @Test
    void testApplyMovesWithPromotion() {
        // piece1を成りエリア手前(5, 6)に移動
        board.movePiece(piece1, new Position(5, 6));
        // player1の移動：上に移動して成りを選択
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), true);
        // player2の移動：通常移動
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        // 移動を適用
        game.applyMoves(move1, move2);
        // piece1が成ったことを確認
        assertTrue(piece1.isPromoted());
    }

    /**
     * 王将を捕獲して勝利するテスト
     * 処理: 王将を捕獲するとゲームが終了し、捕獲したチームが勝者となることを確認
     */
    @Test
    void testApplyMovesWithCapture() {
        // SECONDチームの王将を(5, 6)に配置
        Piece king = new Piece(3, PieceType.KING, Team.SECOND, false);
        board.stackPiece(new Position(5, 6), king);
        // piece1を(5, 5)に配置（王将の下）
        board.movePiece(piece1, new Position(5, 5));

        // piece1が上に移動して王将を捕獲
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        // 移動を適用
        game.applyMoves(move1, move2);
        // FIRSTチームが勝者となったことを確認
        assertEquals(Team.FIRST, game.getWinnerTeam());
        // ゲームが終了したことを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    /**
     * 投了によるゲーム終了テスト
     * 処理: プレイヤーが投了すると、相手チームが勝者となりゲームが終了することを確認
     */
    @Test
    void testApplyMovesWithResignation() {
        // player1が投了
        player1.setResign(true);
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        // 移動を適用
        game.applyMoves(move1, move2);
        // SECONDチームが勝者となったことを確認
        assertEquals(Team.SECOND, game.getWinnerTeam());
        // ゲームが終了したことを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    /**
     * 盤面取得テスト
     * 処理: ゲームから盤面オブジェクトを取得できることを確認
     */
    @Test
    void testGetBoard() {
        // 盤面が取得できることを確認
        assertNotNull(game.getBoard());
        // 取得した盤面が初期化時の盤面と同じであることを確認
        assertEquals(board, game.getBoard());
    }

    /**
     * ターン進行テスト
     * 処理: 移動を適用するとターンが進み、チームが交代することを確認
     */
    @Test
    void testTurnProgression() {
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        // 初期状態でFIRSTチームのターン
        assertEquals(Team.FIRST, game.getCurrentTurn());
        // 移動を適用
        game.applyMoves(move1, move2);
        // SECONDチームのターンに変わったことを確認
        assertEquals(Team.SECOND, game.getCurrentTurn());
    }

    /**
     * 複数マス移動テスト
     * 処理: 1つの移動コマンドで複数の方向を指定して、駒が連続して移動することを確認
     */
    @Test
    void testMultipleMoves() {
        // piece1を初期位置に配置
        board.movePiece(piece1, new Position(5, 5));
        // 上に2マス移動する指示
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP, Direction.UP), false);
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        // 移動を適用
        game.applyMoves(move1, move2);
        // piece1が(5, 7)に移動したことを確認（2マス上）
        assertEquals(new Position(5, 7), board.find(piece1));
    }

    /**
     * 王将捕獲後のゲーム終了状態テスト
     * 処理: 王将が捕獲されるとゲームステータスがFINISHEDになり、勝者が決定することを確認
     */
    @Test
    void testGameStatusFinishedAfterKingCapture() {
        // SECONDチームの王将を(5, 6)に配置
        Piece king = new Piece(10, PieceType.KING, Team.SECOND, false);
        board.stackPiece(new Position(5, 6), king);

        // piece1が王将のいる位置に移動（捕獲）
        PlayerMove move1 = new PlayerMove(player1, piece1, List.of(Direction.UP), false);
        PlayerMove move2 = new PlayerMove(player2, piece2, List.of(Direction.DOWN), false);

        // 移動を適用
        game.applyMoves(move1, move2);
        // ゲームが終了状態になったことを確認
        assertEquals(GameStatus.FINISHED, game.getStatus());
        // 勝者が決定したことを確認
        assertNotNull(game.getWinnerTeam());
    }
}

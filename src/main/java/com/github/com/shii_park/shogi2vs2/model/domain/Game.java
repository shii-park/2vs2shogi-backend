package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.Map;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.MoveResult;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.GameStatus;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class Game {
    private final String gameId;
    private final Map<String, Player> players = new HashMap<>();
    private final Board board;
    private volatile GameStatus status = GameStatus.WAITING;
    private Team winnerTeam;
    private CapturedPieces capturedPieces;
    private TurnManager turnManager;

    /**
     * ゲームを初期化する
     * 
     * @param gameId      ゲームID
     * @param playersList プレイヤーのリスト
     * @param board       盤面
     * @param firstTeam   先攻チーム
     */
    public Game(String gameId, List<Player> playersList, Board board, Team firstTeam) {
        this.gameId = gameId;
        playersList.forEach(p -> players.put(p.getId(), p));
        this.board = board;
        this.status = GameStatus.IN_PROGRESS;
        this.capturedPieces = board.getCapturedPieces();
        this.turnManager = new TurnManager(firstTeam);
    }

    private void handleTimeout() {
        turnManager.nextTurn();
    }

    private void handleResign(Team t) {
        switch (t) {
            case FIRST:
                winnerTeam = Team.SECOND;
                status = GameStatus.FINISHED;
                break;

            case SECOND:
                winnerTeam = Team.FIRST;
                status = GameStatus.FINISHED;
                break;
        }

    }

    /**
     * プレイヤーの移動を盤面に適用する
     * 
     * @param m1 1人目の移動
     * @param m2 2人目の移動
     */
    public void applyMoves(PlayerMove m1, PlayerMove m2) {
        if (!board.isTop(m1.piece()) && !board.isTop(m2.piece())) {
            return;
        }

        if (m1.player().isResign() || m2.player().isResign()) {
            handleResign(m1.player().getTeam());
        }
        // 1人目の移動処理:移動する駒の数だけ実行する
        for (Direction dir1 : m1.direction()) {
            MoveResult res1 = board.moveOneStep(m1.piece(), dir1);
            if (res1 == MoveResult.DROPPED) {
                if (m1.player().getTeam() == Team.FIRST) {
                    capturedPieces.capturedPiece(Team.SECOND, m1.piece());
                    break;
                } else {
                    capturedPieces.capturedPiece(Team.FIRST, m1.piece());
                    break;
                }

            } else if (res1 == MoveResult.CAPTURED) {
                break;
            } else if (res1 == MoveResult.STACKED) {
                board.stackPiece(board.find(m1.piece()), m1.piece());
                break;
            }
        }
        // 2人目の移動処理
        for (Direction dir2 : m2.direction()) {
            MoveResult res2 = board.moveOneStep(m2.piece(), dir2);
            if (res2 == MoveResult.DROPPED) {
                if (m2.player().getTeam() == Team.FIRST) {
                    capturedPieces.capturedPiece(Team.SECOND, m2.piece());
                    break;
                } else {
                    capturedPieces.capturedPiece(Team.FIRST, m2.piece());
                    break;
                }
            } else if (res2 == MoveResult.CAPTURED) {
                break;
            } else if (res2 == MoveResult.STACKED) {
                board.stackPiece(board.find(m2.piece()), m2.piece());
                break;
            }
        }
        // 駒が王将、玉将を捕獲していたらゲーム終了
        capturedPieces.getWinnerTeam().ifPresent(team -> {
            winnerTeam = team;
            status = GameStatus.FINISHED;
        });
        // 駒の成り処理
        if (m1.promote() && board.isInPromotionZone(board.find(m1.piece()), m1.player().getTeam())) {
            board.promotePiece(m1.piece());
        }
        if (m2.promote() && board.isInPromotionZone(board.find(m2.piece()), m2.player().getTeam())) {
            board.promotePiece(m2.piece());
        }
        turnManager.nextTurn();

    }

    public GameStatus getStatus() {
        return status;
    }

    public Team getWinnerTeam() {
        return winnerTeam;
    }

    public Team getCurrentTeam() {
        return turnManager.getCurrentTeam();
    }

    public Board getBoard() {
        return board;
    }
}

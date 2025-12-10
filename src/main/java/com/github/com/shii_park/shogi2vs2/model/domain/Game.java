package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.Map;
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
    private volatile int turnNumber = 0;
    private Team currentTeam;
    private volatile GameStatus status = GameStatus.WAITING;
    private long turnTimer = 0;

    private static final int TIMEOUT = 30;

    public Game(String gameId, List<Player> playersList, Board board, Team team) {
        this.gameId = gameId;
        playersList.forEach(p -> players.put(p.getId(), p));
        this.board = board;
        this.currentTeam = team;
        this.status = GameStatus.IN_PROGRESS;
    }

    private void switchTurn() {
        this.currentTeam = (this.currentTeam == Team.FIRST) ? Team.SECOND : Team.FIRST;
    }

    private void nextTurn() {
        turnNumber++;
        switchTurn();
        resetTimer();
    }

    public void startTurnTimer() {
        this.turnTimer = System.currentTimeMillis();
    }

    public boolean isTimeout() {
        return (System.currentTimeMillis() - turnTimer) >= TIMEOUT * 1000;
    }

    private void resetTimer() {
        this.turnTimer = System.currentTimeMillis();
    }

    private void handleTimeout() {
        nextTurn();
    }

    public void applyMoves(/* PlayerMove m1,PlayerMove m2, */Piece piece) {
        if (isTimeout()) {
            handleTimeout();
            return;
        }
        if (!board.isTop(piece)) {
            return;
        }
        // モックデータ: p1 飛車 上に3マス / p2 飛車 右に2マス
        CapturedPieces c = new CapturedPieces();
        Player p1 = players.get("p1");
        Player p2 = players.get("p2");

        PlayerMove m1 =
                new PlayerMove(p1, piece, List.of(Direction.UP, Direction.UP, Direction.UP));

        PlayerMove m2 = new PlayerMove(p2, piece, List.of(Direction.RIGHT, Direction.RIGHT));


        for (Direction dir1 : m1.direction()) {
            MoveResult res1 = board.moveOneStep(m1.piece(), dir1);
            if (res1 == MoveResult.DROPPED_PIECE) {
                c.Captured(m1.player().getTeam(), m1.piece());
                break;
            } else if (res1 == MoveResult.CAPTURED) {
                c.Captured(m1.player().getTeam(), m1.piece());
                break;
            } else if (res1 == MoveResult.STACKED) {
                board.stackPiece(board.find(piece), piece);
                break;
            }
        }

        for (Direction dir2 : m2.direction()) {
            MoveResult res2 = board.moveOneStep(m2.piece(), dir2);
            if (res2 == MoveResult.DROPPED_PIECE) {
                c.Captured(m2.player().getTeam(), m2.piece());
                break;
            } else if (res2 == MoveResult.CAPTURED) {
                c.Captured(m1.player().getTeam(), m2.piece());
                break;
            } else if (res2 == MoveResult.STACKED) {
                board.stackPiece(board.find(piece), piece);
                break;
            }
            if (isGameOver()) {
                return;
            }
            nextTurn();
        }

    }

    public boolean isGameOver() {
        if (board.isKingCaptured(Team.FIRST)) {
            status = GameStatus.FINISHED;
            return true;
        }
        if (board.isKingCaptured(Team.SECOND)) {
            status = GameStatus.FINISHED;
            return true;
        }
        return false;
    }
}

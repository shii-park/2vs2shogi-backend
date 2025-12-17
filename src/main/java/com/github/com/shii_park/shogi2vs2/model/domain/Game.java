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
     * @param gameId ゲームID
     * @param playersList プレイヤーのリスト
     * @param board 盤面
     * @param firstTeam 先攻チーム
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

    /**
     * プレイヤーの移動を盤面に適用する
     * 
     * NOTE: 現在はモックデータを使用している
     * NOTE: 本来は引数として(PlayerMove m1,PlayerMove m2,Piece piece)を受け取る必要がある
     * 
     * @param piece 移動対象の駒
     */
    public void applyMoves(Piece piece) {
        if (turnManager.isTimeout()) {
            handleTimeout();
            return;
        }
        if (!board.isTop(piece)) {
            return;
        }
        // モックデータ: p1 飛車 上に3マス / p2 飛車 右に2マス
        Player p1 = players.get("p1");
        Player p2 = players.get("p2");

        PlayerMove m1 = new PlayerMove(p1, piece, List.of(Direction.UP, Direction.UP, Direction.UP),
                Instant.now());

        PlayerMove m2 = new PlayerMove(p2, piece, List.of(Direction.RIGHT, Direction.RIGHT), Instant.now());

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
                board.stackPiece(board.find(piece), piece);
                break;
            }
            capturedPieces.getWinnerTeam().ifPresent(team -> {
                winnerTeam = team;
                status = GameStatus.FINISHED;
            });
        }

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
                board.stackPiece(board.find(piece), piece);
                break;
            }
            capturedPieces.getWinnerTeam().ifPresent(team -> {
                winnerTeam = team;
                status = GameStatus.FINISHED;
            });
        }
        turnManager.nextTurn();

    }
}

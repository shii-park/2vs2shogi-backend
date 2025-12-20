package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.Map;
import java.util.ArrayList;
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
    private final List<PlayerDropPiece> pendingDrops;
    private final List<Piece> pendingPromote;
    private TurnManager turnManager;

    public Game(String gameId, List<Player> playersList, Board board, Team firstTeam) {
        this.gameId = gameId;
        playersList.forEach(p -> players.put(p.getId(), p));
        this.board = board;
        this.status = GameStatus.IN_PROGRESS;
        this.capturedPieces = board.getCapturedPieces();
        this.pendingDrops = new ArrayList<>();
        this.pendingPromote = new ArrayList<>();
        this.turnManager = new TurnManager(firstTeam);
    }

    /**
     * プレイヤーが投了したときのゲーム終了処理を行う
     * 
     * @param t 投了したプレイヤーのチーム
     */
    public void handleResign(Team t) {
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
     * プレイヤーの駒の移動を盤面に適用する
     * 
     * @param move プレイヤーの駒の移動
     */
    public void applyMove(PlayerMove move) {
        if (!board.isTop(move.piece())) {
            return;
        }

        // 移動可能かチェック
        if (!isMovable(move.direction(), move.piece())) {
            return;
        }

        // 飛車、角行など、複数マス移動する駒は繰り返し実行する
        for (Direction dir : move.direction()) {
            MoveResult res = board.moveOneStep(move.piece(), dir);
            if (res == MoveResult.DROPPED) {
                switch (move.player().getTeam()) {
                    case FIRST:
                        capturedPieces.capturedPiece(Team.SECOND, move.piece());
                        break;
                    case SECOND:
                        capturedPieces.capturedPiece(Team.FIRST, move.piece());
                }
            } else if (res == MoveResult.CAPTURED) {
                break;
            } else if (res == MoveResult.STACKED) {
                board.stackPiece(board.find(move.piece()), move.piece());
                break;
            }
        }
        if (move.promote()) {
            pendingPromote.add(move.piece());
        }
    }

    /**
     * プレイヤーの手駒の配置予約をする
     * 
     * @param drop プレイヤーの手駒から盤面に打つ操作
     */
    public void applyDrop(PlayerDropPiece drop) {
        if (board.getTopPiece(drop.position()) != null) {
            return;
        }
        pendingDrops.add(drop);
    }

    /**
     * ターン終了時の処理を行う<br>
     * 王将が捕獲されていた場合、ゲーム終了処理を行う<br>
     * 待機中の成りを処理する<br>
     * 待機中の手駒からの配置を処理する<br>
     * 
     */
    public void handleTurnEnd() {
        capturedPieces.getWinnerTeam().ifPresent(team -> {
            winnerTeam = team;
            status = GameStatus.FINISHED;
        });
        pendingPromote.forEach(piece -> {
            if (board.isInPromotionZone(board.find(piece), piece.getTeam())) {
                board.promotePiece(piece);
            }
        });
        pendingDrops.forEach(drop -> {
            Piece piece = capturedPieces.getCapturedPiece(drop.player().getTeam(), drop.piece());
            if (piece == null) {
                return;
            }
            board.stackPiece(drop.position(), piece);
        });

        pendingDrops.clear();
        pendingPromote.clear();
        // 次のターンに進む
    }

    /**
     * ゲームの状態を返す
     * 
     * @return status
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * 勝利チームを返す
     * 
     * @return 勝利チーム(勝利チームが確定していないときは{@code null})
     */
    public Team getWinnerTeam() {
        return winnerTeam;
    }

    /**
     * 現在のターンのチームを返す
     * 
     * @return Team
     */
    public Team getCurrentTurn() {
        return turnManager.getCurrentTurn();
    }

    /**
     * 現在の盤面を返す
     * 
     * @return Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 駒が指定された方向リストに移動可能かチェックする
     * 
     * @param directions 移動方向のリスト
     * @param piece      移動させたい駒
     * @return {@code true}:全ての移動が可能
     */
    private boolean isMovable(List<Direction> directions, Piece piece) {
        if (directions == null || directions.isEmpty()) {
            return false;
        }

        // 1. 各方向が移動可能な方向かチェック
        for (Direction dir : directions) {
            if (!piece.canMoveToDirection(dir)) {
                return false;
            }
        }

        // 2. 連続移動のチェック
        if (directions.size() > 1) {
            // 連続移動可能な駒かチェック
            if (!piece.canMoveMultipleSteps()) {
                return false;
            }

            // 3. 各方向が連続移動可能な方向かチェック
            for (Direction dir : directions) {
                if (!piece.canMoveMultipleStepsInDirection(dir)) {
                    return false;
                }
            }

            // 同じ方向への連続移動かチェック（飛車・角・香は同じ方向にのみ連続移動可）
            Direction firstDir = directions.get(0);
            for (Direction dir : directions) {
                if (dir != firstDir) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * プレイヤーのIdからPlayerインスタンスを返す
     * 
     * @param playerId 取得したいプレイヤーのid
     * @return Player(存在しないときは{@code null})
     */
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }
}

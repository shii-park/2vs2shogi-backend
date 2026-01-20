package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.MoveResult;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.GameStatus;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Gameクラスは2vs2将棋のゲーム全体を管理するクラスです<br>
 * ゲームの進行、プレイヤー管理、盤面管理、勝敗判定などの主要なロジックを担当します
 */
public class Game {
    /** ゲームid */
    private final String gameId;
    /** プレイヤーのマップ(key:プレイヤーid, value:プレイヤー) */
    private final Map<String, Player> players = new HashMap<>();
    /** 盤面 */
    private final Board board;
    /** ゲームの状態 */
    private volatile GameStatus status = GameStatus.WAITING;
    /** 勝利チーム */
    private Team winnerTeam;
    /** 捕獲された駒の管理 */
    private CapturedPieces capturedPieces;
    /** 待機中の手駒配置リスト */
    private final List<PlayerDropPiece> pendingDrops;
    /** 待機中の成り駒リスト */
    private final List<Piece> pendingPromote;
    /** ターン管理 */
    private TurnManager turnManager;
    /** ターン内の駒移動結果リスト */
    private final List<ApplyMoveResult> turnMoveResults;
    /** ターン内の手駒配置結果リスト */
    private final List<ApplyDropResult> turnDropResults;

    /**
     * Gameクラスのコンストラクタ
     * 
     * @param gameId ゲームid
     * @param playersList プレイヤーのリスト
     * @param board 盤面
     * @param firstTeam 最初のターンのチーム
     */
    public Game(String gameId, List<Player> playersList, Board board, Team firstTeam) {
        this.gameId = gameId;
        playersList.forEach(p -> players.put(p.getId(), p));
        this.board = board;
        this.status = GameStatus.IN_PROGRESS;
        this.capturedPieces = board.getCapturedPieces();
        this.pendingDrops = new ArrayList<>();
        this.pendingPromote = new ArrayList<>();
        this.turnManager = new TurnManager(firstTeam);
        this.turnMoveResults = new ArrayList<>();
        this.turnDropResults = new ArrayList<>();
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
     * @return 適用結果(移動が無効な場合は{@code null})
     */
    public ApplyMoveResult applyMove(PlayerMove move) {
        // 盤面の一番上の駒かチェック
        if (!board.isTop(move.piece())) {
            return null;
        }

        // 移動可能かチェック
        if (!isMovable(move.direction(), move.piece())) {
            return null;
        }

        List<Direction> appliedDirections = new ArrayList<>();
        List<Piece> capturedPiecesList = new ArrayList<>();

        // 飛車、角行など、複数マス移動する駒は各方向への移動を繰り返し実行する
        for (Direction dir : move.direction()) {
            MoveStepResult stepResult = board.moveOneStepWithCapture(move.piece(), dir);
            appliedDirections.add(dir);

            if (stepResult.result() == MoveResult.FELL) {
                // 盤面外に落ちた場合、相手チームの捕獲駒として登録
                switch (move.player().getTeam()) {
                    case FIRST:
                        capturedPieces.capturedPiece(Team.SECOND, move.piece());
                        break;
                    case SECOND:
                        capturedPieces.capturedPiece(Team.FIRST, move.piece());
                }
                capturedPiecesList.add(move.piece());
                break;
            } else if (stepResult.result() == MoveResult.CAPTURED) {
                // 敵の駒を捕獲した場合
                capturedPiecesList.addAll(stepResult.capturedPieces());
                break;
            } else if (stepResult.result() == MoveResult.STACKED) {
                // 味方の駒の上に乗った場合、積む
                board.stackPiece(board.find(move.piece()), move.piece());
                break;
            }
        }

        // 成る指定がある場合、待機リストに追加
        if (move.promote()) {
            pendingPromote.add(move.piece());
        }

        ApplyMoveResult result = new ApplyMoveResult(appliedDirections, capturedPiecesList);
        turnMoveResults.add(result);
        return result;
    }

    /**
     * プレイヤーの手駒の配置予約をする
     * 
     * @param drop プレイヤーの手駒から盤面に打つ操作
     * @return 配置予約の結果
     */
    public ApplyDropResult applyDrop(PlayerDropPiece drop) {
        // 指定位置に既に駒がある場合は配置できない
        if (board.getTopPiece(drop.position()) != null) {
            ApplyDropResult result = new ApplyDropResult(false, drop.position(), drop.piece());
            turnDropResults.add(result);
            return result;
        }
        // 配置可能な場合は待機リストに追加
        pendingDrops.add(drop);
        ApplyDropResult result = new ApplyDropResult(true, drop.position(), drop.piece());
        turnDropResults.add(result);
        return result;
    }

    /**
     * ターン終了時の処理を行う<br>
     * 王将が捕獲されていた場合、ゲーム終了処理を行う<br>
     * 待機中の成りを処理する<br>
     * 待機中の手駒からの配置を処理する<br>
     * 
     * @return ターン終了時のアクション結果
     */
    public ApplyActionResult handleTurnEnd() {
        List<Piece> promotedPieces = new ArrayList<>();
        List<Piece> placedPieces = new ArrayList<>();

        // 王将が捕獲されていた場合、勝者を決定してゲームを終了
        capturedPieces.getWinnerTeam().ifPresent(team -> {
            winnerTeam = team;
            status = GameStatus.FINISHED;
        });

        // 待機中の成りを処理(成れるゾーンにいる場合のみ成る)
        pendingPromote.forEach(piece -> {
            if (board.isInPromotionZone(board.find(piece), piece.getTeam())) {
                board.promotePiece(piece);
                promotedPieces.add(piece);
            }
        });

        // 待機中の手駒配置を処理
        pendingDrops.forEach(drop -> {
            Piece piece = capturedPieces.getCapturedPiece(drop.player().getTeam(), drop.piece());
            if (piece == null) {
                return;
            }
            board.stackPiece(drop.position(), piece);
            placedPieces.add(piece);
        });

        // ターン中に蓄積された結果をコピーしてクリア
        List<ApplyMoveResult> moveResults = new ArrayList<>(turnMoveResults);
        List<ApplyDropResult> dropResults = new ArrayList<>(turnDropResults);

        pendingDrops.clear();
        pendingPromote.clear();
        turnMoveResults.clear();
        turnDropResults.clear();

        return new ApplyActionResult(moveResults, dropResults, promotedPieces, placedPieces);
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

        // 各方向が移動可能な方向かチェック
        for (Direction dir : directions) {
            if (!piece.canMoveToDirection(dir)) {
                return false;
            }
        }

        // 連続移動のチェック
        if (directions.size() > 1) {
            // 連続移動可能な駒かチェック
            if (!piece.canMoveMultipleSteps()) {
                return false;
            }

            // 各方向が連続移動可能な方向かチェック
            for (Direction dir : directions) {
                if (!piece.canMoveMultipleStepsInDirection(dir)) {
                    return false;
                }
            }

            // 同じ方向への連続移動かチェック(飛車・角・香は同じ方向にのみ連続移動可)
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

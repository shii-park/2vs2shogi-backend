package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.MoveResult;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class Board {
    private final Map<Position, Stack<Piece>> pieces;
    private final Map<Piece, Position> index;
    private final CapturedPieces capturedPieces;

    // 盤面の左下が(0,0)、右上が(8,8)
    private static final int BOARD_MIN = 1;
    private static final int BOARD_MAX = 9;

    public Board(Map<Piece, Position> initialPieces) {
        this.pieces = new ConcurrentHashMap<>();
        this.index = new ConcurrentHashMap<>(initialPieces);

        for (Map.Entry<Piece, Position> entry : initialPieces.entrySet()) {
            Piece piece = entry.getKey();
            Position pos = entry.getValue();

            pieces.computeIfAbsent(pos, p -> new Stack<>()).push(piece);
        }
        this.capturedPieces = new CapturedPieces();
    }

    /**
     * 捕獲された駒の管理オブジェクトを取得
     * 
     * @return
     */
    public CapturedPieces getCapturedPieces() {
        return capturedPieces;
    }

    /**
     * Positionにある駒のスタックを返す
     * 
     * @param pos
     * @return not {@code null}
     */
    private Stack<Piece> getStack(Position pos) {
        return pieces.computeIfAbsent(pos, k -> new Stack<>());
    }

    /**
     * Positionにあるスタックの一番上の駒{@code Piece}を返す
     *
     * @param pos
     * @return スタックが空のときに{@code null}を返す
     */
    public Piece getTopPiece(Position pos) {
        Stack<Piece> s = pieces.get(pos);
        return (s == null || s.isEmpty()) ? null : s.peek();
    }

    /**
     * Positionに積まれている駒のリストを返す
     * 
     * @param pos
     * @return スタックがないときは空のリストを返却する
     */
    public List<Piece> getAllPiecesAt(Position pos) {
        Stack<Piece> s = pieces.get(pos);
        return (s == null) ? List.of() : new ArrayList<>(s);
    }

    /**
     * Positionのスタックの一番上ににPieceを積む
     * 
     * @param pos
     * @param piece
     */
    public void stackPiece(Position pos, Piece piece) {
        getStack(pos).push(piece);
    }

    /**
     * Positionにある駒のスタックを捕獲する
     * capturingTeamの駒として登録する
     * 
     * @param pos
     * @param capturingTeam
     * @return 捕獲した駒のリスト
     */
    public List<Piece> captureAll(Position pos, Team capturingTeam) {
        Stack<Piece> stack = pieces.get(pos);
        if (stack == null)
            return List.of();

        List<Piece> captured = new ArrayList<>(stack);
        pieces.remove(pos);

        for (Piece p : captured) {
            capturedPieces.capturedPiece(capturingTeam, p);
            index.remove(p);
        }
        return captured;
    }

    /**
     * 駒がスタックの一番上かを判定する
     * 
     * @param piece
     * @return
     */
    public boolean isTop(Piece piece) {
        Stack<Piece> pieces = this.pieces.get(find(piece));
        if (pieces != null && !pieces.empty()) {
            return piece == pieces.peek();
        }
        return false;
    }

    /**
     * Pieceを新しい位置newPosに移動させる
     * 
     * @param piece
     * @param newPos
     */
    public void movePiece(Piece piece, Position newPos) {
        Position old = index.get(piece);
        Stack<Piece> stack = getStack(old);
        if (stack.isEmpty()) {
            return;
        }
        stack.remove(piece);
        getStack(newPos).push(piece);
        index.put(piece, newPos);
    }

    /**
     * 駒が盤面の中にいるか判定する
     * 
     * @param pos
     * @return true:盤面の中, false:盤面の外
     */
    public boolean isInsideBoard(Position pos) {
        if (pos.x() > BOARD_MAX || pos.x() < BOARD_MIN) {
            return false;
        } else if (pos.y() > BOARD_MAX || pos.y() < BOARD_MIN) {
            return false;
        }
        return true;
    }

    /**
     * 駒の場所を特定する
     * 
     * @param p
     * @return Position, 駒がなければ{@code null}
     */
    public Position find(Piece p) {
        return index.get(p);
    }

    /**
     * コマの所属チームを反転させる
     * 
     * @param p
     */
    public void changeTeam(Piece p) {
        switch (p.getTeam()) {
            case Team.FIRST:
                p.setTeam(Team.SECOND);
                break;
            case Team.SECOND:
                p.setTeam(Team.FIRST);
                break;
        }
    }

    /**
     * 駒が成りが可能なエリア内にいるかを返す
     * FIRSTチームは7行以上、SECONDチームは3行以下が成りエリア
     * 
     * @param pos
     * @param team
     * @return true:成りが可能なエリア内
     */
    public boolean isInPromotionZone(Position pos, Team team) {
        switch (team) {
            case FIRST:
                if (pos.y() >= 7) {
                    return true;
                }
                return false;

            case SECOND:
                if (pos.y() <= 3) {
                    return true;
                }
                return false;
        }
        return false;
    }

    /**
     * 受け取ったPieceを成り状態に変える
     * 
     * @param piece
     */
    public void promotePiece(Piece piece) {
        piece.setPromoted(true);
    }

    /**
     * {@code dir}の方向に駒を進める
     * 
     * @param piece
     * @param dir
     * @return 移動した結果を返す(DROPPED,STACKED,CAPTURED)
     */
    public MoveResult moveOneStep(Piece piece, Direction dir) {
        Position newPos = find(piece).add(dir);
        if (!isInsideBoard(newPos)) {
            return MoveResult.DROPPED;
        }
        Piece top = getTopPiece(newPos);
        if (top != null && top.getTeam() == piece.getTeam()) {
            movePiece(piece, newPos);
            return MoveResult.STACKED;
        }
        if (top != null && top.getTeam() != piece.getTeam()) {
            captureAll(newPos, piece.getTeam());
            movePiece(piece, newPos);
            return MoveResult.CAPTURED;
        }
        // ピース移動
        movePiece(piece, newPos);

        return MoveResult.MOVED;
    }
}

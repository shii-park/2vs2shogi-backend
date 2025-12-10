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

    }

    private Stack<Piece> getStack(Position pos) {
        return pieces.computeIfAbsent(pos, k -> new Stack<>());
    }

    public Piece getTopPiece(Position pos) {
        Stack<Piece> s = pieces.get(pos);
        return (s == null || s.isEmpty()) ? null : s.peek();

    }

    public List<Piece> getAllPiecesAt(Position pos) {
        Stack<Piece> s = pieces.get(pos);
        return (s == null) ? List.of() : new ArrayList<>(s);
    }

    public void stackPiece(Position pos, Piece piece) {
        getStack(pos).push(piece);
    }

    public List<Piece> captureAll(Position pos) {
        Stack<Piece> stack = pieces.get(pos);
        if (stack == null)
            return List.of();

        List<Piece> captured = new ArrayList<>(stack);
        pieces.remove(pos);

        return captured;
    }

    public boolean isTop(Piece piece) {
        Stack<Piece> pieces = this.pieces.get(find(piece));
        if (pieces != null || !pieces.empty()) {
            return piece == pieces.peek();
        }
        return false;
    }

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

    public boolean isInsideBoard(Position pos) {
        if (pos.x() > BOARD_MAX || pos.x() < BOARD_MIN) {
            return false;
        } else if (pos.y() > BOARD_MAX || pos.y() < BOARD_MIN) {
            return false;
        }
        return true;
    }

    public Position find(Piece p) {
        return index.get(p);
    }

    public MoveResult moveOneStep(Piece piece, Direction dir) {
        Position newPos = find(piece).add(dir);
        if (!isInsideBoard(newPos)) {
            return MoveResult.DROPPED_PIECE;
        }
        Piece top = getTopPiece(newPos);
        if (top != null && top.getTeam() == piece.getTeam()) {
            return MoveResult.STACKED;
        }
        if (top != null && top.getTeam() != piece.getTeam()) {
            captureAll(newPos);
            movePiece(piece, newPos);
            return MoveResult.CAPTURED;
        }
        // ピース移動
        movePiece(piece, newPos);

        return MoveResult.MOVED;
    }

    public boolean isKingCaptured(Team team) {
        // TODO:盤面上から team の王将が消えているか判定する
        return false;
    }
}

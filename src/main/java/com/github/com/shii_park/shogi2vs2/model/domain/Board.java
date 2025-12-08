package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class Board {
    private final Map<Position, Stack<Piece>> pieces;

    public Board(List<Piece> initialPieces) {
        this.pieces = new ConcurrentHashMap<>();

        for (Piece p : initialPieces) {
            pieces.computeIfAbsent(p.getPosition(), pos -> new Stack<>()).push(p);
        }
    }

    private Stack<Piece> getStack(Position pos) {
        return pieces.computeIfAbsent(pos, k -> new Stack<>());
    }

    public Piece getToPiece(Position pos) {
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
}

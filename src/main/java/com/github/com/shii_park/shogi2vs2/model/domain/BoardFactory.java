package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.HashMap;
import java.util.Map;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * BoardFactoryクラスは標準的な初期盤面を生成するメソッドを提供します
 * 
 * @author Suiren91
 */
public class BoardFactory {
    private int pawnNum = 1;
    private int lanceNum = 1;
    private int knightNum = 1;
    private int silverNum = 1;
    private int goldNum = 1;
    private int bishopNum = 1;
    private int rookNum = 1;
    private int kingNum = 1;

    /**
     * 標準の初期盤面を生成
     * 
     * @return 生成した盤面(not {@code null})
     */
    public Board createBoard() {
        Map<Piece, Position> initialPieces = new HashMap<>();

        // FIRST_1段目
        initialPieces.put(new Piece(lanceNum++, PieceType.LANCE, Team.FIRST, true), new Position(1, 1));
        initialPieces.put(new Piece(knightNum++, PieceType.KNIGHT, Team.FIRST, true), new Position(2, 1));
        initialPieces.put(new Piece(silverNum++, PieceType.SILVER, Team.FIRST, true), new Position(3, 1));
        initialPieces.put(new Piece(goldNum++, PieceType.GOLD, Team.FIRST, false), new Position(4, 1));
        initialPieces.put(new Piece(kingNum++, PieceType.KING, Team.FIRST, false), new Position(5, 1));
        initialPieces.put(new Piece(goldNum++, PieceType.GOLD, Team.FIRST, false), new Position(6, 1));
        initialPieces.put(new Piece(silverNum++, PieceType.SILVER, Team.FIRST, true), new Position(7, 1));
        initialPieces.put(new Piece(knightNum++, PieceType.KNIGHT, Team.FIRST, true), new Position(8, 1));
        initialPieces.put(new Piece(lanceNum++, PieceType.LANCE, Team.FIRST, true), new Position(9, 1));

        // FIRST_2段目
        initialPieces.put(new Piece(rookNum++, PieceType.ROOK, Team.FIRST, true), new Position(2, 2));
        initialPieces.put(new Piece(bishopNum++, PieceType.BISHOP, Team.FIRST, true), new Position(8, 2));

        // FIRST_3段目
        for (int i = 1; i <= 9; i++) {
            initialPieces.put(new Piece(pawnNum++, PieceType.PAWN, Team.FIRST, true), new Position(i, 3));
        }

        // SECOND_7段目
        for (int i = 1; i <= 9; i++) {
            initialPieces.put(new Piece(pawnNum++, PieceType.PAWN, Team.SECOND, true), new Position(i, 7));
        }

        // SECOND_8段目
        initialPieces.put(new Piece(bishopNum++, PieceType.BISHOP, Team.SECOND, true), new Position(2, 8));
        initialPieces.put(new Piece(rookNum++, PieceType.ROOK, Team.SECOND, true), new Position(8, 8));

        // SECOND_9段目
        initialPieces.put(new Piece(lanceNum++, PieceType.LANCE, Team.SECOND, true), new Position(1, 9));
        initialPieces.put(new Piece(knightNum++, PieceType.KNIGHT, Team.SECOND, true), new Position(2, 9));
        initialPieces.put(new Piece(silverNum++, PieceType.SILVER, Team.SECOND, true), new Position(3, 9));
        initialPieces.put(new Piece(goldNum++, PieceType.GOLD, Team.SECOND, false), new Position(4, 9));
        initialPieces.put(new Piece(kingNum++, PieceType.KING, Team.SECOND, false), new Position(5, 9));
        initialPieces.put(new Piece(goldNum++, PieceType.GOLD, Team.SECOND, false), new Position(6, 9));
        initialPieces.put(new Piece(silverNum++, PieceType.SILVER, Team.SECOND, true), new Position(7, 9));
        initialPieces.put(new Piece(knightNum++, PieceType.KNIGHT, Team.SECOND, true), new Position(8, 9));
        initialPieces.put(new Piece(lanceNum++, PieceType.LANCE, Team.SECOND, true), new Position(9, 9));

        return new Board(initialPieces);
    }
}

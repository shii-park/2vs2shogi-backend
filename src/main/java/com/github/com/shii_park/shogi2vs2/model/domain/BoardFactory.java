package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.HashMap;
import java.util.Map;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * BoardFactoryクラスは標準的な初期盤面を生成するメソッドを提供します
 * 駒の番号は1起点で駒の種類ごとに割り当てられ、チーム間で通し番号です
 * 例: 歩兵(1~18),金将(1~4)
 * 
 * @author Suiren91
 */
public class BoardFactory {
    
    // ユーティリティクラスなのでインスタンス化を防ぐ
    private BoardFactory() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * 標準の初期盤面を生成
     *
     * @return 生成した盤面(not {@code null})
     */
    public static Board createBoard() {
        Map<Piece, Position> initialPieces = new HashMap<>();
        int pawnNum = 1;
        int lanceNum = 1;
        int knightNum = 1;
        int silverNum = 1;
        int goldNum = 1;
        int bishopNum = 1;
        int rookNum = 1;
        int kingNum = 1;

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

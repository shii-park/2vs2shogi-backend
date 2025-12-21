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
        Map<Position, Piece> initialPieces = new HashMap<>();
        int pawnNum = 1;
        int lanceNum = 1;
        int knightNum = 1;
        int silverNum = 1;
        int goldNum = 1;
        int bishopNum = 1;
        int rookNum = 1;
        int kingNum = 1;

        // FIRST_1段目
        initialPieces.put(new Position(1, 1), new Piece(lanceNum++, PieceType.LANCE, Team.FIRST, true));
        initialPieces.put(new Position(2, 1), new Piece(knightNum++, PieceType.KNIGHT, Team.FIRST, true));
        initialPieces.put(new Position(3, 1), new Piece(silverNum++, PieceType.SILVER, Team.FIRST, true));
        initialPieces.put(new Position(4, 1), new Piece(goldNum++, PieceType.GOLD, Team.FIRST, false));
        initialPieces.put(new Position(5, 1), new Piece(kingNum++, PieceType.KING, Team.FIRST, false));
        initialPieces.put(new Position(6, 1), new Piece(goldNum++, PieceType.GOLD, Team.FIRST, false));
        initialPieces.put(new Position(7, 1), new Piece(silverNum++, PieceType.SILVER, Team.FIRST, true));
        initialPieces.put(new Position(8, 1), new Piece(knightNum++, PieceType.KNIGHT, Team.FIRST, true));
        initialPieces.put(new Position(9, 1), new Piece(lanceNum++, PieceType.LANCE, Team.FIRST, true));

        // FIRST_2段目
        initialPieces.put(new Position(2, 2), new Piece(rookNum++, PieceType.ROOK, Team.FIRST, true));
        initialPieces.put(new Position(8, 2), new Piece(bishopNum++, PieceType.BISHOP, Team.FIRST, true));

        // FIRST_3段目
        for (int i = 1; i <= 9; i++) {
            initialPieces.put(new Position(i, 3), new Piece(pawnNum++, PieceType.PAWN, Team.FIRST, true));
        }

        // SECOND_7段目
        for (int i = 1; i <= 9; i++) {
            initialPieces.put(new Position(i, 7), new Piece(pawnNum++, PieceType.PAWN, Team.SECOND, true));
        }

        // SECOND_8段目
        initialPieces.put(new Position(2, 8), new Piece(bishopNum++, PieceType.BISHOP, Team.SECOND, true));
        initialPieces.put(new Position(8, 8), new Piece(rookNum++, PieceType.ROOK, Team.SECOND, true));

        // SECOND_9段目
        initialPieces.put(new Position(1, 9), new Piece(lanceNum++, PieceType.LANCE, Team.SECOND, true));
        initialPieces.put(new Position(2, 9), new Piece(knightNum++, PieceType.KNIGHT, Team.SECOND, true));
        initialPieces.put(new Position(3, 9), new Piece(silverNum++, PieceType.SILVER, Team.SECOND, true));
        initialPieces.put(new Position(4, 9), new Piece(goldNum++, PieceType.GOLD, Team.SECOND, false));
        initialPieces.put(new Position(5, 9), new Piece(kingNum++, PieceType.KING, Team.SECOND, false));
        initialPieces.put(new Position(6, 9), new Piece(goldNum++, PieceType.GOLD, Team.SECOND, false));
        initialPieces.put(new Position(7, 9), new Piece(silverNum++, PieceType.SILVER, Team.SECOND, true));
        initialPieces.put(new Position(8, 9), new Piece(knightNum++, PieceType.KNIGHT, Team.SECOND, true));
        initialPieces.put(new Position(9, 9), new Piece(lanceNum++, PieceType.LANCE, Team.SECOND, true));

        // Boardが要求する向きに変換（Piece -> Position）
        Map<Piece, Position> pieceToPos = new HashMap<>();
        for (Map.Entry<Position, Piece> e : initialPieces.entrySet()) {
            pieceToPos.put(e.getValue(), e.getKey());
        }
        return new Board(pieceToPos);
    }
}

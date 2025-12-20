package com.github.com.shii_park.shogi2vs2.model.enums;

import java.util.List;

/**
 * PAWN:歩兵
 * LANCE:香車
 * KNIGHT:桂馬
 * SILVER:銀将
 * GOLD:金将
 * BISHOP:角行
 * ROOK:飛車
 * KING:王将
 */
public enum PieceType {
    PAWN, LANCE, KNIGHT, SILVER, GOLD, BISHOP, ROOK, KING;

    /**
     * この駒の移動可能な方向のリストを返す
     * 
     * @param isPromoted 成っているかどうか
     * @return 移動可能な方向リスト
     */
    public List<Direction> getMovableDirections(boolean isPromoted) {
        if (isPromoted && this != GOLD && this != KING) {
            return GOLD.getMovableDirections(false);
        }
        return switch (this) {
            case PAWN -> List.of(Direction.UP);
            case LANCE -> List.of(Direction.UP);
            case KNIGHT -> List.of(Direction.KNIGHT_LEFT, Direction.KNIGHT_RIGHT);
            case SILVER ->
                List.of(Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT);
            case GOLD -> List.of(Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT, Direction.LEFT, Direction.RIGHT,
                    Direction.DOWN);
            case BISHOP -> List.of(Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT);
            case ROOK -> List.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
            case KING -> List.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP_LEFT,
                    Direction.UP_RIGHT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT);
        };
    }

    public boolean canMoveMultipleSteps() {
        return this == ROOK || this == BISHOP || this == LANCE;
    }
}

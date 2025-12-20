package com.github.com.shii_park.shogi2vs2.model.enums;

import java.util.ArrayList;
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
        return switch (this) {
            case PAWN -> isPromoted ? getGoldMovement() : List.of(Direction.UP);
            case LANCE -> isPromoted ? getGoldMovement() : List.of(Direction.UP);
            case KNIGHT -> isPromoted ? getGoldMovement() : List.of(Direction.KNIGHT_LEFT, Direction.KNIGHT_RIGHT);
            case SILVER -> isPromoted ? getGoldMovement()
                    : List.of(Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT,
                            Direction.DOWN_LEFT, Direction.DOWN_RIGHT);
            case GOLD -> getGoldMovement();
            case BISHOP -> isPromoted ? getPromotedBishopMovement() : getBishopMovement();
            case ROOK -> isPromoted ? getPromotedRookMovement() : getRookMovement();
            case KING -> List.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT,
                    Direction.UP_LEFT, Direction.UP_RIGHT,
                    Direction.DOWN_LEFT, Direction.DOWN_RIGHT);
        };
    }

    /**
     * 駒が連続移動可能かを判定する
     * 
     * @param isPromoted 成っているかどうか
     * @return {@code true}:連続移動可能
     */
    public boolean canMoveMultipleSteps(boolean isPromoted) {
        return switch (this) {
            case LANCE -> !isPromoted;
            case BISHOP, ROOK -> true;
            default -> false;
        };
    }

    /**
     * 駒が指定した方向に連続移動可能か判定する
     * 
     * @param direction  方向
     * @param isPromoted 成っているか
     * @return {@code true}:連続移動可能
     */
    public boolean canMoveMultipleStepsInDirection(Direction direction, boolean isPromoted) {
        return switch (this) {
            case LANCE -> !isPromoted && direction == Direction.UP;
            case BISHOP -> getBishopMovement().contains(direction);
            case ROOK -> getRookMovement().contains(direction);
            default -> false;
        };
    }

    // ヘルパーメソッド(Private)

    /**
     * 金将
     * 
     * @return 上3方向、横、下
     */
    private static List<Direction> getGoldMovement() {
        return List.of(Direction.UP, Direction.UP_LEFT, Direction.UP_RIGHT,
                Direction.LEFT, Direction.RIGHT, Direction.DOWN);
    }

    /**
     * 角行
     * 
     * @return 斜め
     */
    private static List<Direction> getBishopMovement() {
        return List.of(Direction.UP_LEFT, Direction.UP_RIGHT,
                Direction.DOWN_LEFT, Direction.DOWN_RIGHT);
    }

    /**
     * 飛車
     * 
     * @return 縦横
     */
    private static List<Direction> getRookMovement() {
        return List.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
    }

    /**
     * 龍馬
     * 
     * @return 斜め+縦横1マス
     */
    private static List<Direction> getPromotedBishopMovement() {
        List<Direction> directions = new ArrayList<>(getBishopMovement());
        directions.addAll(getRookMovement()); // 縦横も追加
        return directions;
    }

    /**
     * 竜王
     * 
     * @return 縦横+斜め1マス
     */
    private static List<Direction> getPromotedRookMovement() {
        List<Direction> directions = new ArrayList<>(getRookMovement());
        directions.addAll(getBishopMovement()); // 斜めも追加
        return directions;
    }
}

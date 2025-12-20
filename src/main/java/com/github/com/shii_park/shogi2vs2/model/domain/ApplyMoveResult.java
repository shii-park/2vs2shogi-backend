package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * ApplyMoveResultは実際に成功した移動を表すレコードクラスです
 * 
 * @param applyDirections 成功した移動のリスト
 * @param capturedPieces  捕獲した駒のリスト
 * @param promoted        成ったかどうか
 */
public record ApplyMoveResult(List<Direction> applyDirections, List<Piece> capturedPieces, boolean promoted) {

}

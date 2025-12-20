package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * ApplyMoveResultは実際に成功した移動を表すレコードクラスです
 * 
 * @param appliedDirections 成功した移動のリスト
 * @param capturedPieces    移動中に捕獲した駒のリスト
 * 
 * @author Suiren91
 */
public record ApplyMoveResult(List<Direction> appliedDirections, List<Piece> capturedPieces) {

}

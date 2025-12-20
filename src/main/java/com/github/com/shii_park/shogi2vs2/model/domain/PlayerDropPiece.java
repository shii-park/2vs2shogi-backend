package com.github.com.shii_park.shogi2vs2.model.domain;

/**
 * PlayerDropPieceクラスは手駒から盤面に駒を打つ操作を管理し、それらのゲッターを提供します
 * 
 * @param player   行動するプレイヤー
 * @param piece    盤面に置く駒
 * @param position 盤面の置く位置
 * 
 * @author Suiren91
 */
public record PlayerDropPiece(Player player, Piece piece, Position position) {
}

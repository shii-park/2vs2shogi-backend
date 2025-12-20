package com.github.com.shii_park.shogi2vs2.model.domain;

/**
 * ApplyDropResultは手駒から駒を配置する操作の結果を表すレコードクラスです
 * 
 * @param success  配置予約が成功したかどうか
 * @param position 配置しようとした位置
 * @param piece    配置しようとした駒
 * 
 * @author Suiren91
 */
public record ApplyDropResult(boolean success, Position position, Piece piece) {
}

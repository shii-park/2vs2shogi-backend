package com.github.com.shii_park.shogi2vs2.model.domain;

import java.time.Instant;

/**
 * PendingDropクラスは手駒を打つ予約を管理します
 * 
 * @param player      行動するプレイヤー
 * @param piece       盤面に置く駒
 * @param position    盤面の置く位置
 * @param submittedAt 操作の確定時刻
 */
public record PendingDrop(Player player, Piece piece, Position position, Instant submittedAt) {
    public PendingDrop(Player player, Piece piece, Position position) {
        this(player, piece, position, Instant.now());
    }
}

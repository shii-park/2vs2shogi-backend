package com.github.com.shii_park.shogi2vs2.model.domain;

/**
 * PlayerResignクラスはプレイヤーが投了するときの操作を表現したドメインオブジェクトです
 * 
 * @param player     投了するプレイヤー
 * @param turnNumber 投了時のターン数
 */
public record PlayerResign(Player player, int turnNumber) {
}

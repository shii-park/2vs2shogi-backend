package com.github.com.shii_park.shogi2vs2.model.domain;

import java.time.Instant;
import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

//TODO: resignを手に含める
/**
 * PlayerMoveクラスは各プレイヤーの1ターンでの行動を管理し、それらのゲッターを提供します
 * 
 * @param player    行動するプレイヤー
 * @param piece     操作する駒
 * @param direction 移動する方向(のリスト)
 * @param promote   成るかどうか
 * @param at        行動を決定した時間
 * 
 * @author Suiren91
 */
public record PlayerMove(Player player, Piece piece, List<Direction> direction, boolean promote, Instant at) {
}

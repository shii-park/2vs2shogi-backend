package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * PlayerMoveクラスは各プレイヤーの1ターンでの行動を管理し、それらのゲッターを提供します
 * 
 * @param player    行動するプレイヤー
 * @param piece     操作する駒
 * @param direction 移動する方向(のリスト)
 * @param promote   成るかどうか
 * 
 * @author Suiren91
 */
public record PlayerMove(Player player, Piece piece, List<Direction> direction, boolean promote) {
}

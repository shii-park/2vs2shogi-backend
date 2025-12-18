package com.github.com.shii_park.shogi2vs2.model.domain;

import java.time.Instant;
import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

//TODO: resignを手に含める
public record PlayerMove(Player player, Piece piece, List<Direction> direction, boolean promote, Instant at) {
}

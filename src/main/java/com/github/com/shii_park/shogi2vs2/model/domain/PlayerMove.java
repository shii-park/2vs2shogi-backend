package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;

public record PlayerMove(Player player, Piece piece, List<Direction> direction) {
}

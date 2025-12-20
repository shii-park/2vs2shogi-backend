package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

public record ApplyActionResult(List<Direction> applyDirections, List<Piece> capturedPieces, boolean promoted,
        boolean dropped) {

}

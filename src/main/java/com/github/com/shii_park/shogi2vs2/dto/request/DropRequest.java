package com.github.com.shii_park.shogi2vs2.dto.request;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;

public class DropRequest {
    private final String playerId;
    private final PieceType pieceType;
    private final Position position;

    public DropRequest(String playerId, PieceType pieceType, Position position) {
        this.playerId = playerId;
        this.pieceType = pieceType;
        this.position = position;
    }

    public String getPlayerId() {
        return playerId;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Position getPosition() {
        return position;
    }
}

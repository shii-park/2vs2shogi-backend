package com.github.com.shii_park.shogi2vs2.dto.request;

import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

public class MoveRequest {
    private final String playerId;
    private final int pieceId;
    private final List<Direction> direction;
    private final boolean promote;

    public MoveRequest(String playerId, int pieceId, List<Direction> direction, boolean promote) {
        this.playerId = playerId;
        this.pieceId = pieceId;
        this.direction = direction;
        this.promote = promote;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getPieceId() {
        return pieceId;
    }

    public List<Direction> getDirection() {
        return direction;
    }

    public boolean isPromote() {
        return promote;
    }
}

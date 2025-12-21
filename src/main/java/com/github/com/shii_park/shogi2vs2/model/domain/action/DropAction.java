package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;
import com.github.com.shii_park.shogi2vs2.model.domain.Position;

public record DropAction(
    String userId,
    String teamId,
    String pieceType,
    Position position,
    Instant at
) implements GameAction {

    @Override
    public String getUserId() { return userId(); }
    @Override
    public String getTeamId() { return teamId(); }
    @Override
    public Instant at() { return at(); }
}
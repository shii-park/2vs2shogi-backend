package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;
import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

public record MoveAction(
    String userId, 
    String teamId, 
    int pieceId, 
    String pieceType, 
    List<Direction> directions, 
    boolean promote, 
    Instant at
) implements GameAction {

    @Override
    public String getUserId() { return userId(); }
    @Override
    public String getTeamId() { return teamId(); }
    @Override
    public Instant at() { return at(); }
}
package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "actionType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MoveAction.class, name = "MOVE"),
    @JsonSubTypes.Type(value = DropAction.class, name = "DROP")
})
public interface GameAction {
    String getUserId();
    String getTeamId();
    Instant getAt();
}
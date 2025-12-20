package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

public record TurnExecutionResult (
    String type,
    String pieceId,
    List<String> derections,
    String team,
    boolean promoted
){}

package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

public record TurnExecutionResult(
    String type,        // "moveResult" or "dropResult"
    int pieceId,        // 駒のID
    String pieceType,   // 追加: "FU", "HI" など
    List<String> directions, 
    String teamId,
    boolean promote,
    String Condition,    ///"TAKEN","FALLEN"
    Position position
) {}
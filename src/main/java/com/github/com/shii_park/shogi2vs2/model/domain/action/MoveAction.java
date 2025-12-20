package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;
import java.util.List;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

public record MoveAction(
    String userId,
    String teamId,
    int pieceId,                // 動かす駒のID
    List<Direction> directions, // 移動方向リスト (例: [UP, UP])
    boolean promote,            // 成るかどうか
    Instant at                  // 時刻
) implements GameAction {
    
    // インターフェースの実装メソッド
    @Override public String getUserId() { return userId; }
    @Override public String getTeamId() { return teamId; }
    @Override public Instant at() { return at; }
}
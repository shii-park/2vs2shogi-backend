package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;

public record DropAction(
    String userId,
    String teamId,
    String pieceType,  // 打つ駒の種類 (例: "FU", "KIN")
    Position position, // 打つ場所 (x, y)
    Instant at         // 時刻
) implements GameAction {

    // インターフェースの実装メソッド
    @Override public String getUserId() { return userId; }
    @Override public String getTeamId() { return teamId; }
    @Override public Instant at() { return at; }
}
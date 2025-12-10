package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class CapturedPieces {
    Map<Team, List<Piece>> piecesByTeam;

    public CapturedPieces() {
        piecesByTeam = new ConcurrentHashMap<>();
        piecesByTeam.put(Team.FIRST, null);
        piecesByTeam.put(Team.SECOND, null);
    }

    public void Captured(Team team, Piece piece) {
        piecesByTeam.get(team).add(piece);
    }
}

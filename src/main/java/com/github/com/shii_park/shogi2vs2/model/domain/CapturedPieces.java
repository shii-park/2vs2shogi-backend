package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class CapturedPieces {
    Map<Team, List<Piece>> capturedPieces;
    Team winnerTeam;

    public CapturedPieces() {
        capturedPieces = new ConcurrentHashMap<>();
        capturedPieces.put(Team.FIRST, Collections.synchronizedList(new ArrayList<>()));
        capturedPieces.put(Team.SECOND, Collections.synchronizedList(new ArrayList<>()));
    }

    public List<Piece> getCapturedPieces(Team team) {
        return capturedPieces.get(team);
    }

    public boolean captured(Team team, Piece piece) {
        if (piece.getType() == PieceType.KING) {
            winnerTeam = team;
            return true;
        }
        capturedPieces.get(team).add(piece);
        piece.setPromoted(false);
        piece.setTeam(team);
        return false;
    }
}

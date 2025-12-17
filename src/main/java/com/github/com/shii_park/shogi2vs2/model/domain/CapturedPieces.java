package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class CapturedPieces {
    private final Map<Team, List<Piece>> capturedPieces;
    private Optional<Team> winnerTeam;

    public CapturedPieces() {
        capturedPieces = new ConcurrentHashMap<>();
        capturedPieces.put(Team.FIRST, Collections.synchronizedList(new ArrayList<>()));
        capturedPieces.put(Team.SECOND, Collections.synchronizedList(new ArrayList<>()));
        winnerTeam = Optional.empty();
    }

    /**
     * 捕獲済みの駒リストを返す
     * 
     * @param team
     * @return 駒のリスト(not {@code null})
     */
    public List<Piece> getCapturedPieces(Team team) {
        return capturedPieces.get(team);
    }

    /**
     * 勝利チームを返す
     * 
     * @return
     */
    public Optional<Team> getWinnerTeam() {
        return winnerTeam;
    }

    // TODO: winnerTeamを決める処理を別途追加
    /**
     * PieceをTeamのものにする(捕獲する)
     * 
     * @param team
     * @param piece
     */
    public void capturedPiece(Team team, Piece piece) {
        if (piece.getType() == PieceType.KING) {
            winnerTeam = Optional.of(team);
        }
        capturedPieces.get(team).add(piece);
        piece.setPromoted(false);
        piece.setTeam(team);
    }
}

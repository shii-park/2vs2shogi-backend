package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * CapturedPiecesクラスは各チームの手駒を管理します。<br>
 * 手駒の操作や捕獲に関するメソッドを提供します。
 * また、王将が捕獲されたときは勝利チームを決定します。
 * 
 * @author Suiren91
 */
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
     * チームの手駒リストを返す
     * 
     * @param team 手駒を確認したいチーム
     * @return List 手駒リスト(not {@code null})
     */
    public List<Piece> getCapturedPieces(Team team) {
        return capturedPieces.get(team);
    }

    /**
     * 勝利チームを返す
     * 
     * @return 勝利チーム(確定していないときは{@code Optional.empty()})
     */
    public Optional<Team> getWinnerTeam() {
        return winnerTeam;
    }

    /**
     * 手駒から駒を取り出す。駒のidは考慮しない
     * 
     * @param team  駒を取り出したいチーム
     * @param piece 取り出す駒
     * @return 取り出した駒を返す。手駒に存在しなかった場合は{@code null}
     */
    public Piece getCapturedPiece(Team team, Piece piece) {
        List<Piece> pieces = capturedPieces.get(team);
        if (pieces == null) {
            return null;
        }

        synchronized (pieces) {
            for (int i = 0; i < pieces.size(); i++) {
                Piece captured = pieces.get(i);
                if (captured.getType() == piece.getType()) {
                    pieces.remove(i);
                    return captured;
                }
            }
        }
        return null;
    }

    // TODO: winnerTeamを決める処理を別途追加
    /**
     * 駒を捕獲する
     * 
     * @param team  捕獲するチーム
     * @param piece 捕獲する駒
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

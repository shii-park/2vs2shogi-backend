package com.github.com.shii_park.shogi2vs2.service;

import org.springframework.stereotype.Service;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;

@Service
public class BoardCoordinateService {
    // 将棋盤の最大インデックス (9マスなので 0 〜 8)
    private static final int BOARD_SIZE_INDEX = 8;

    /**
     * チームIDに応じて座標を正規化（反転）する
     * * @param pos 元の座標
     * @param teamId チームID ("FIRST" or "SECOND")
     * @return 正規化された座標
     */
    public Position normalize(Position pos, String teamId) {
        // nullチェック
        if (pos == null) return null;

        // Team2 (SECOND) の場合のみ、盤面を180度回転させる
        if ("SECOND".equals(teamId)) {
            // 例: (0, 0) -> (8, 8)
            // 例: (8, 8) -> (0, 0)
            // 例: (4, 4) -> (4, 4)
            return new Position(BOARD_SIZE_INDEX - pos.x(), BOARD_SIZE_INDEX - pos.y());
        }

        // Team1 (FIRST) なら何もしない
        return pos;
    }
}

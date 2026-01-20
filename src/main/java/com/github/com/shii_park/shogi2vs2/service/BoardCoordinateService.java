package com.github.com.shii_park.shogi2vs2.service;

import org.springframework.stereotype.Service;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;

/**
 * 将棋盤の座標変換サービス
 * チームに応じた座標の正規化（反転）を行います。
 */
@Service
public class BoardCoordinateService {
    /**
     * 座標反転用の定数
     * 座標を1~9で実装しているため10から引く
     */
    private static final int BOARD_INDEX = 10;

    /**
     * チームIDに応じて座標を正規化（反転）します。
     * SECONDチームの場合、盤面を180度回転させた座標を返します。
     * 
     * @param pos 元の座標
     * @param teamId チームID ("FIRST" or "SECOND")
     * @return 正規化された座標。posがnullの場合はnull
     */
    public Position normalize(Position pos, String teamId) {
        // nullチェック
        if (pos == null) return null;

        // Team2 (SECOND) の場合のみ、盤面を180度回転させる
        if ("SECOND".equals(teamId)) {
            // 例: (0, 0) -> (8, 8)
            // 例: (8, 8) -> (0, 0)
            // 例: (4, 4) -> (4, 4)
            return new Position(BOARD_INDEX - pos.x(), BOARD_INDEX - pos.y());
        }

        // Team1 (FIRST) なら何もしない
        return pos;
    }
}

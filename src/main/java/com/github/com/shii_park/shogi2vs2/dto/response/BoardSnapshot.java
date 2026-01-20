package com.github.com.shii_park.shogi2vs2.dto.response;

import com.github.com.shii_park.shogi2vs2.model.domain.*;
import java.util.Map;
import java.util.HashMap;

/**
 * 盤面スナップショットDTO
 * 特定時点の盤面の状態を保持します。
 */
public class BoardSnapshot {
    // 仮実装
    // TODO: Boardクラスのスタック構造に対応させる
    /**
     * 盤面の駒配置マップ
     * 位置と駒の対応関係を保持します。
     */
    private final Map<Position, Piece> board;

    /**
     * BoardSnapshotを構築します。
     * 
     * @param board 盤面の駒配置マップ
     */
    public BoardSnapshot(Map<Position, Piece> board) {
        this.board = new HashMap<>(board);
    }
}

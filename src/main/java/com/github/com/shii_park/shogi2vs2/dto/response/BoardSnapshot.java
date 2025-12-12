package com.github.com.shii_park.shogi2vs2.dto.response;

import com.github.com.shii_park.shogi2vs2.model.domain.*;
import java.util.Map;
import java.util.HashMap;

public class BoardSnapshot {
    // 仮実装
    // TODO: Boardクラスのスタック構造に対応させる
    private final Map<Position, Piece> board;

    public BoardSnapshot(Map<Position, Piece> board) {
        this.board = new HashMap<>(board);
    }
}

package com.github.com.shii_park.shogi2vs2.dto.request;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;

/**
 * 駒打ちリクエストDTO
 * 持ち駒を盤面に配置するリクエスト情報を保持します。
 */
public class DropRequest {
    /**
     * プレイヤーID
     */
    private final String playerId;
    
    /**
     * 駒の種類
     */
    private final PieceType pieceType;
    
    /**
     * 配置位置
     */
    private final Position position;

    /**
     * DropRequestを構築します。
     * 
     * @param playerId プレイヤーID
     * @param pieceType 駒の種類
     * @param position 配置位置
     */
    public DropRequest(String playerId, PieceType pieceType, Position position) {
        this.playerId = playerId;
        this.pieceType = pieceType;
        this.position = position;
    }

    /**
     * プレイヤーIDを取得します。
     * 
     * @return プレイヤーID
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * 駒の種類を取得します。
     * 
     * @return 駒の種類
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * 配置位置を取得します。
     * 
     * @return 配置位置
     */
    public Position getPosition() {
        return position;
    }
}

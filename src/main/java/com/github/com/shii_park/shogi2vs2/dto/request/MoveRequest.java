package com.github.com.shii_park.shogi2vs2.dto.request;

import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * 駒移動リクエストDTO
 * 盤面上の駒を移動させるリクエスト情報を保持します。
 */
public class MoveRequest {
    /**
     * プレイヤーID
     */
    private final String playerId;
    
    /**
     * 駒ID
     */
    private final int pieceId;
    
    /**
     * 移動方向リスト
     */
    private final List<Direction> direction;
    
    /**
     * 成りフラグ
     */
    private final boolean promote;

    /**
     * MoveRequestを構築します。
     * 
     * @param playerId プレイヤーID
     * @param pieceId 駒ID
     * @param direction 移動方向リスト
     * @param promote 成りフラグ
     */
    public MoveRequest(String playerId, int pieceId, List<Direction> direction, boolean promote) {
        this.playerId = playerId;
        this.pieceId = pieceId;
        this.direction = direction;
        this.promote = promote;
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
     * 駒IDを取得します。
     * 
     * @return 駒ID
     */
    public int getPieceId() {
        return pieceId;
    }

    /**
     * 移動方向リストを取得します。
     * 
     * @return 移動方向リスト
     */
    public List<Direction> getDirection() {
        return direction;
    }

    /**
     * 成りフラグを取得します。
     * 
     * @return 成りフラグ
     */
    public boolean isPromote() {
        return promote;
    }
}

package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;
import java.util.List;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * MoveActionは盤面上の駒を移動させるアクションを表すレコードクラスです。
 * GameActionインターフェースを実装し、移動操作に必要な情報を保持します。
 * 
 * @param userId     アクションを実行するユーザーID
 * @param teamId     アクションを実行するチームID
 * @param pieceId    移動させる駒のID
 * @param pieceType  移動させる駒の種類("FU", "HI"など)
 * @param directions 移動方向のリスト(複数ステップの移動をサポート)
 * @param promote    移動後に成るかどうか
 * @param at         アクションが実行された時刻
 * 
 * @author Suiren91
 */
public record MoveAction(
    String userId, 
    String teamId, 
    int pieceId, 
    String pieceType, 
    List<Direction> directions, 
    boolean promote, 
    Instant at
) implements GameAction {

    /**
     * アクションを実行するユーザーIDを取得します。
     * 
     * @return ユーザーID
     */
    @Override
    public String getUserId() { return userId(); }
    
    /**
     * アクションを実行するチームIDを取得します。
     * 
     * @return チームID
     */
    @Override
    public String getTeamId() { return teamId(); }
    
    /**
     * アクションが実行された時刻を取得します。
     * 
     * @return 実行時刻
     */
    @Override
    public Instant at() { return at(); }
}
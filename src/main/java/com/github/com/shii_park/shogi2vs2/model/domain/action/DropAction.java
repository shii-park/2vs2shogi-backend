package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;
import com.github.com.shii_park.shogi2vs2.model.domain.Position;

/**
 * DropActionは手駒から盤面に駒を配置するアクションを表すレコードクラスです。
 * GameActionインターフェースを実装し、配置操作に必要な情報を保持します。
 * 
 * @param userId    アクションを実行するユーザーID
 * @param teamId    アクションを実行するチームID
 * @param pieceType 配置する駒の種類("FU", "HI"など)
 * @param position  配置先の位置
 * @param at        アクションが実行された時刻
 * 
 * @author Suiren91
 */
public record DropAction(
    String userId,
    String teamId,
    String pieceType,
    Position position,
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
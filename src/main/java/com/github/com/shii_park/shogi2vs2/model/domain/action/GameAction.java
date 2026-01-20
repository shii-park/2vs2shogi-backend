package com.github.com.shii_park.shogi2vs2.model.domain.action;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * GameActionはゲーム内のアクション(移動・配置)を表す基底インターフェースです。
 * このインターフェースを実装することで、JSONのシリアライズ・デシリアライズ時に
 * actionTypeプロパティに基づいて適切な実装クラス(MoveAction/DropAction)に変換されます。
 * 
 * @author Suiren91
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "actionType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MoveAction.class, name = "MOVE"),
    @JsonSubTypes.Type(value = DropAction.class, name = "DROP")
})
public interface GameAction {
    /**
     * アクションを実行するユーザーIDを取得します。
     * 
     * @return ユーザーID
     */
    String getUserId();
    
    /**
     * アクションを実行するチームIDを取得します。
     * 
     * @return チームID
     */
    String getTeamId();
    
    /**
     * アクションが実行された時刻を取得します。
     * 
     * @return 実行時刻
     */
    Instant at();
}
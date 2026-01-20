package com.github.com.shii_park.shogi2vs2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ゲームステータスレスポンスDTO
 * マッチングおよびゲームの状態をクライアントに返すレスポンス情報を保持します。
 */
@Data
@AllArgsConstructor
public class GameStatusResponse {
    /**
     * ステータス
     * 取りうる値: WAITING（待機中）、MATCHED（マッチング成立）、NOT_QUEUED（キュー未登録）
     */
    private String status;//WAITING  MATCHED  NOT_QUEUED
    
    /**
     * マッチID
     * マッチングが成立した場合のgameIdが格納されます。
     */
    private String matchId;//マッチングが成立した場合のgameIdが入る
}

package com.github.com.shii_park.shogi2vs2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameStatusResponse {
    private String status;//WAITING  MATCHED  NOT_QUEUED
    private String matchId;//マッチングが成立した場合のgameIdが入る
}

package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

/**
 * TurnExecutionResultはターン実行の結果を表すレコードクラスです。
 * 移動結果(moveResult)または配置結果(dropResult)のいずれかの情報を保持します。
 * 
 * @param type       結果のタイプ("moveResult" または "dropResult")
 * @param pieceId    実行された駒のID
 * @param pieceType  駒の種類("FU", "HI"など)
 * @param directions 移動方向のリスト(移動の場合)
 * @param teamId     実行したチームID
 * @param promote    成りが発生したかどうか
 */
public record TurnExecutionResult(
    String type,
    int pieceId,
    String pieceType,
    List<String> directions, 
    String teamId,
    boolean promote
) {}
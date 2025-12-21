package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

/**
 * ApplyActionResultはターン終了時の最終的なアクション結果を表すレコードクラスです
 * 
 * @param moveResults    実行された移動結果のリスト
 * @param dropResults    実行された配置結果のリスト
 * @param promotedPieces 成った駒のリスト
 * @param placedPieces   実際に盤面に配置された駒のリスト(dropから)
 * 
 * @author Suiren91
 */
public record ApplyActionResult(
        List<ApplyMoveResult> moveResults,
        List<ApplyDropResult> dropResults,
        List<Piece> promotedPieces,
        List<Piece> placedPieces) {

}

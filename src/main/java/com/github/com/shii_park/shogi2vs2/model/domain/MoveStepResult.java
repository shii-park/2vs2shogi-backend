package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.List;

import com.github.com.shii_park.shogi2vs2.model.enums.MoveResult;

/**
 * MoveStepResultは1ステップの移動結果と捕獲した駒の情報を保持するレコードクラスです
 * 
 * @param result         移動結果(FELL, STACKED, CAPTURED, MOVED)
 * @param capturedPieces その移動ステップで捕獲した駒のリスト
 * 
 * @author Suiren91
 */
public record MoveStepResult(MoveResult result, List<Piece> capturedPieces) {
    
    /**
     * 捕獲なしの移動結果を生成
     */
    public static MoveStepResult of(MoveResult result) {
        return new MoveStepResult(result, List.of());
    }
    
    /**
     * 捕獲ありの移動結果を生成
     */
    public static MoveStepResult withCapture(MoveResult result, List<Piece> capturedPieces) {
        return new MoveStepResult(result, capturedPieces);
    }
}

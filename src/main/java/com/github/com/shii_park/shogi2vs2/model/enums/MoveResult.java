package com.github.com.shii_park.shogi2vs2.model.enums;

/**
 * 駒の移動結果を表すEnum
 */
public enum MoveResult {
    /** 駒が盤面の外に出た */
    FELL,
    /** 駒が自チームの駒の上に積まれた */
    STACKED,
    /** 駒が相手チームの駒を捕獲した */
    CAPTURED,
    /** 駒が移動した */
    MOVED,
    /** 手駒から駒を配置した */
    DROPPED;
}
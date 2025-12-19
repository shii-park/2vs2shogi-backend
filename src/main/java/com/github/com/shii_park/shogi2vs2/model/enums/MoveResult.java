package com.github.com.shii_park.shogi2vs2.model.enums;

/**
 * FELL: 駒が盤面の外に出た
 * STACKED: 駒が自チームの駒の上に積まれた
 * CAPTURED: 駒が相手チームの駒を捕獲した
 * MOVED: 駒が移動した
 * DROPPED: 手駒から駒を配置した
 */
public enum MoveResult {
    FELL, STACKED, CAPTURED, MOVED, DROPPED;
}
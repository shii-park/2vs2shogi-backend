package com.github.com.shii_park.shogi2vs2.model.enums;

/**
 * 将棋のチームを表すEnum
 */
public enum Team {
    /** 先手チーム */
    FIRST,
    /** 後手チーム */
    SECOND;

    /**
     * チームを交代する
     * 
     * @return 交代後のチーム
     */
    public Team switchTeam() {
        return (this == Team.FIRST) ? Team.SECOND : Team.FIRST;

    }
}

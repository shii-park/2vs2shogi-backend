package com.github.com.shii_park.shogi2vs2.model.enums;

public enum Team {
    FIRST, SECOND;

    /**
     * チームを交代する
     * 
     * @return 交代後のチーム
     */
    public Team switchTeam() {
        return (this == Team.FIRST) ? Team.SECOND : Team.FIRST;

    }
}

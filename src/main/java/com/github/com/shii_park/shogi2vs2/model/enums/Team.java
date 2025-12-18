package com.github.com.shii_park.shogi2vs2.model.enums;

public enum Team {
    FIRST, SECOND;

    public Team switchTeam() {
        return (this == Team.FIRST) ? Team.SECOND : Team.FIRST;

    }
}

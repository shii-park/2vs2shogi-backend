package com.github.com.shii_park.shogi2vs2.model.domain;

import com.github.com.shii_park.shogi2vs2.model.enums.Team;

public class Turn {
    private Team team;

    public Team changeTeam(Team t) {
        if (t == team.A) {
            return team.B;
        } else
            return team.A;
    }
}

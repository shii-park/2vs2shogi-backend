package com.github.com.shii_park.shogi2vs2.dto.request;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;

public class MoveRequest {
    private final String playerId;
    private final Position from;
    private final Position to;
    private final boolean promote;
    //public final long clientTurnId; //必要であれば追加

    public MoveRequest(String playerId,Position from,Position to,boolean promote){
        this.playerId=playerId;
        this.from=from;
        this.to=to;
        this.promote=promote;
        //clientTurnId使うなら追加
    }

    public String getPlayerId() {return playerId;}
    public Position getFrom() {return from;}
    public Position getTo() {return to;}
    public boolean isPromote() {return promote;}
}

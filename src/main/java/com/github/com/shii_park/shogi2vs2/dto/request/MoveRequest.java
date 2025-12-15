package com.github.com.shii_park.shogi2vs2.dto.request;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;

import jakarta.validation.constraints.NotNull;

public class MoveRequest {
    @NotNull
    private final String userId;
    private final Position from;
    @NotNull
    private final Position to;
    private final boolean promote;
    //public final long clientTurnId; //必要であれば追加

    public MoveRequest(String userId,Position from,Position to,boolean promote){
        this.userId=userId;
        this.from=from;
        this.to=to;
        this.promote=promote;
        //clientTurnId使うなら追加
    }

    public String getuserId() {return userId;}
    public Position getFrom() {return from;}
    public Position getTo() {return to;}
    public boolean isPromote() {return promote;}
}

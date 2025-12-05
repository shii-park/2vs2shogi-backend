package com.github.com.shii_park.shogi2vs2.model.domain;

public class ComposedMove {
    private final Piece piece;
    private final Position dest;
    private final boolean promote;

    public ComposedMove(Piece piece,Position dest,boolean promote){
        this.piece=piece;
        this.dest=dest;
        this.promote=promote;
    }

    public Piece getPiece(){return piece;}
    public Position getPosition(){return dest;}
    public boolean getPromote(){return promote;}

    //デバック用
    @Override
    public String toString(){
        return "ComposedMove{" + piece.getId() + "->" + dest + (promote?"(prom)":"") + "}";
    }
}

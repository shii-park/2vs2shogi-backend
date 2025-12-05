package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;

import com.github.com.shii_park.shogi2vs2.model.enums.GameStatus;

public class Game {
    private final String gameId;
    private final Map<Position,Piece>board=new ConcurrentHashMap<>(); //分離
    private final Map<String,Player>players=new HashMap<>();
    private volatile int turnNumber=0;
    private volatile GameStatus status=GameStatus.WAITING;
    private final List<MoveRecord>history=Collections.synchronizedList(new ArrayList<>());
    
    private static final int BOARD_MIN=1;
    private static final int BOARD_MAX_X=9;
    private static final int BOARD_MAX_Y=9;

    public Game(String gameId,List<Player>playersList,List<Piece>initialPieces){
        this.gameId=gameId;
        playersList.forEach(p->players.put(p.getId(), p));
        initialPieces.forEach(pc->board.put(pc.getPosition(),pc));
        this.status=GameStatus.IN_PROGRESS;
    }

    public Optional<Piece>getPieceAt(Position pos){
        return Optional.ofNullable(board.get(pos));
    }
    
    //分割
    public void applyMoves(List<ComposedMove>applied){
        synchronized(this){
            for(ComposedMove cm:applied){
                Piece p=cm.getPiece();
                Position prev=p.getPosition();
                Piece captured=board.remove(cm.getPosition());
                if(captured!=null){
                    //持ち駒ルールなど
                }
                //盤面をアップデート
                board.remove(prev);
                p.setPosition(cm.getPosition());
                if(cm.getPromote())p.setPromoted(true);
                board.put(cm.getPosition(),p);
               history.add(new MoveRecord(p.getTeam(), prev, cm.getPosition(), cm.getPromote()));
            }
            turnNumber++;
        }
    }
    //盤面からはみ出ないか判定(分離)
    public boolean isInsideBoard(Position p){
        return p.getX() >=BOARD_MIN && p.getX()<=BOARD_MAX_X && p.getY() >= BOARD_MIN && p.getY() <= BOARD_MAX_Y;
    }

}

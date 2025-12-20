package com.github.com.shii_park.shogi2vs2.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import com.github.com.shii_park.shogi2vs2.model.enums.Direction;
import com.github.com.shii_park.shogi2vs2.model.enums.MoveResult;
import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

/**
 * Boardクラスは盤面、駒の逆引きインデックス、盤面の操作に関するメソッドを提供する
 * 
 * @author Suiren91
 */
public class Board {
    private final Map<Position, Stack<Piece>> pieces;
    private final Map<Piece, Position> index;
    private final CapturedPieces capturedPieces;

    // 盤面の左下が(0,0)、右上が(8,8)
    private static final int BOARD_MIN = 1;
    private static final int BOARD_MAX = 9;
    private static final int FIRST_PROMOTABLE_ZONE = 7;
    private static final int SECOND_PROMOTABLE_ZONE = 3;

    public Board(Map<Piece, Position> initialPieces) {
        this.pieces = new ConcurrentHashMap<>();
        this.index = new ConcurrentHashMap<>(initialPieces);

        for (Map.Entry<Piece, Position> entry : initialPieces.entrySet()) {
            Piece piece = entry.getKey();
            Position pos = entry.getValue();

            pieces.computeIfAbsent(pos, p -> new Stack<>()).push(piece);
        }
        this.capturedPieces = new CapturedPieces();
    }

    /**
     * 内部ヘルパー:
     * マスにある駒のスタックを返す
     * 
     * @param pos スタックを取得したいマス
     * @return not {@code null}
     */
    private Stack<Piece> getStack(Position pos) {
        return pieces.computeIfAbsent(pos, k -> new Stack<>());
    }

    /**
     * 捕獲された駒の管理オブジェクトを取得
     * 
     * @return capturedPieces
     */
    public CapturedPieces getCapturedPieces() {
        return capturedPieces;
    }

    /**
     * マスにあるスタックの一番上の駒{@code Piece}を返す
     *
     * @param pos 一番上の駒を取得したいマス
     * @return Piece スタックが空のときに{@code null}を返す
     */
    public Piece getTopPiece(Position pos) {
        Stack<Piece> s = pieces.get(pos);
        return (s == null || s.isEmpty()) ? null : s.peek();
    }

    /**
     * マスに積まれている駒のリストを返す
     * 
     * @param pos 駒のリストを取得したいマス
     * @return スタックがないときは空のリストを返却する
     */
    public List<Piece> getAllPiecesAt(Position pos) {
        Stack<Piece> s = pieces.get(pos);
        return (s == null) ? List.of() : new ArrayList<>(s);
    }

    /**
     * マスのスタックの一番上に駒を積む
     * indexを更新する
     * 
     * @param pos   駒を積むマス
     * @param piece 積む駒
     */
    public void stackPiece(Position pos, Piece piece) {
        getStack(pos).push(piece);
        index.put(piece, pos);
    }

    /**
     * マスにある駒を全て捕獲する<br>
     * capturingTeamの駒として登録する
     * 
     * @param pos           捕獲したいマス
     * @param capturingTeam 駒を捕獲するチーム
     * @return 捕獲した駒のリスト
     */
    public List<Piece> captureAll(Position pos, Team capturingTeam) {
        Stack<Piece> stack = pieces.get(pos);
        if (stack == null)
            return List.of();

        List<Piece> captured = new ArrayList<>(stack);
        pieces.remove(pos);

        for (Piece p : captured) {
            capturedPieces.capturedPiece(capturingTeam, p);
            index.remove(p);
        }
        return captured;
    }

    /**
     * 駒がスタックの一番上かを判定する
     * 
     * @param piece 一番上か確認したい駒
     * @return {@code true}:一番上
     */
    public boolean isTop(Piece piece) {
        Stack<Piece> pieces = this.pieces.get(find(piece));
        if (pieces != null && !pieces.empty()) {
            return piece == pieces.peek();
        }
        return false;
    }

    /**
     * 駒を別のマスnewPosに移動させる
     * 
     * @param piece  移動させたい駒
     * @param newPos 移動先のマス
     */
    public void movePiece(Piece piece, Position newPos) {
        Position old = index.get(piece);
        Stack<Piece> stack = getStack(old);
        if (stack.isEmpty()) {
            return;
        }
        stack.remove(piece);
        getStack(newPos).push(piece);
        index.put(piece, newPos);
    }

    /**
     * 駒が盤面の中にいるか判定する
     * 
     * @param pos 判定したい駒の位置、マス
     * @return {@code true}:盤面の中, {@code false}:盤面の外
     */
    public boolean isInsideBoard(Position pos) {
        if (pos.x() > BOARD_MAX || pos.x() < BOARD_MIN) {
            return false;
        } else if (pos.y() > BOARD_MAX || pos.y() < BOARD_MIN) {
            return false;
        }
        return true;
    }

    /**
     * 駒の場所を特定する
     * 
     * @param p 場所を特定したい駒
     * @return 駒のあるマス, 駒がなければ{@code null}
     */
    public Position find(Piece p) {
        return index.get(p);
    }

    /**
     * 駒の所属チームを反転させる
     * 
     * @param p チームを反転させたい駒
     */
    public void changeTeam(Piece p) {
        p.setTeam(p.getTeam().switchTeam());
    }

    /**
     * 駒が成りが可能なエリア内にいるかを返す
     * FIRSTチームは7行以上、SECONDチームは3行以下が成りエリア
     * 
     * @param pos  駒の位置、マス
     * @param team 駒を保有するチーム
     * @return {@code true}:成りが可能なエリア内
     */
    public boolean isInPromotionZone(Position pos, Team team) {
        switch (team) {
            case FIRST:
                if (pos.y() >= FIRST_PROMOTABLE_ZONE) {
                    return true;
                }
                return false;

            case SECOND:
                if (pos.y() <= SECOND_PROMOTABLE_ZONE) {
                    return true;
                }
                return false;
        }
        return false;
    }

    /**
     * 受け取った駒を成り状態に変える
     * 
     * @param piece 成りたい駒
     */
    public void promotePiece(Piece piece) {
        piece.setPromoted(true); // TODO: isPromotableがfalseのときの処理を追加
    }

    /**
     * {@code dir}の方向に駒を進める
     * 
     * @param piece 移動させる駒
     * @param dir   移動させたい方向
     * @return 移動した結果を返す({@code DROPPED},{@code STACKED},{@code CAPTURED},{@code MOVED})
     */
    public MoveResult moveOneStep(Piece piece, Direction dir) {
        Position newPos = find(piece).add(dir);
        if (!isInsideBoard(newPos)) {
            return MoveResult.FELL;
        }
        Piece top = getTopPiece(newPos);
        if (top != null && top.getTeam() == piece.getTeam()) {
            movePiece(piece, newPos);
            return MoveResult.STACKED;
        }
        if (top != null && top.getTeam() != piece.getTeam()) {
            captureAll(newPos, piece.getTeam());
            movePiece(piece, newPos);
            return MoveResult.CAPTURED;
        }
        // ピース移動
        movePiece(piece, newPos);

        return MoveResult.MOVED;
    }

    /**
     * IDと種類から、盤上（または管理下）にある駒の実体を探す
     * GameRoomServiceのMoveAction処理で使用
     * * @param id   駒のID
     * @param type 駒の種類
     * @return 見つかったPiece (なければnull)
     */
    public Piece getPiece(int id, PieceType type) {
        // indexキーセット（盤上の全駒）から検索
        for (Piece p : index.keySet()) {
            if (p.getId() == id && p.getType() == type) {
                return p;
            }
        }
        return null;
    }
    
}

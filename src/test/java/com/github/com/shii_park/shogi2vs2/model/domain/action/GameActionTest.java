package com.github.com.shii_park.shogi2vs2.model.domain.action;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.domain.Position;
import com.github.com.shii_park.shogi2vs2.model.enums.Direction;

/**
 * GameActionインターフェースとその実装クラス（MoveAction、DropAction）のテスト
 */
class GameActionTest {

    /**
     * MoveActionの生成と基本プロパティの確認
     */
    @Test
    void testMoveActionCreation() {
        Instant now = Instant.now();
        MoveAction action = new MoveAction(
            "user1", 
            "FIRST", 
            1, 
            "PAWN", 
            List.of(Direction.UP), 
            false, 
            now
        );

        assertEquals("user1", action.userId());
        assertEquals("FIRST", action.teamId());
        assertEquals(1, action.pieceId());
        assertEquals("PAWN", action.pieceType());
        assertEquals(1, action.directions().size());
        assertEquals(Direction.UP, action.directions().get(0));
        assertFalse(action.promote());
        assertEquals(now, action.at());
    }

    /**
     * MoveActionのGameActionインターフェース実装確認
     */
    @Test
    void testMoveActionImplementsGameAction() {
        Instant now = Instant.now();
        MoveAction moveAction = new MoveAction(
            "user2", 
            "SECOND", 
            5, 
            "ROOK", 
            List.of(Direction.UP, Direction.UP), 
            false, 
            now
        );
        GameAction action = moveAction;

        assertEquals("user2", action.getUserId());
        assertEquals("SECOND", action.getTeamId());
        assertEquals(now, moveAction.at());
    }

    /**
     * MoveActionで複数方向の移動を確認
     */
    @Test
    void testMoveActionMultipleDirections() {
        MoveAction action = new MoveAction(
            "user1", 
            "FIRST", 
            3, 
            "BISHOP", 
            List.of(Direction.UP_RIGHT, Direction.UP_RIGHT, Direction.UP_RIGHT), 
            false, 
            Instant.now()
        );

        assertEquals(3, action.directions().size());
        action.directions().forEach(dir -> 
            assertEquals(Direction.UP_RIGHT, dir)
        );
    }

    /**
     * MoveActionでpromotionフラグの確認
     */
    @Test
    void testMoveActionWithPromotion() {
        MoveAction actionWithPromotion = new MoveAction(
            "user1", 
            "FIRST", 
            1, 
            "PAWN", 
            List.of(Direction.UP), 
            true, 
            Instant.now()
        );

        assertTrue(actionWithPromotion.promote());

        MoveAction actionWithoutPromotion = new MoveAction(
            "user1", 
            "FIRST", 
            1, 
            "PAWN", 
            List.of(Direction.UP), 
            false, 
            Instant.now()
        );

        assertFalse(actionWithoutPromotion.promote());
    }

    /**
     * DropActionの生成と基本プロパティの確認
     */
    @Test
    void testDropActionCreation() {
        Instant now = Instant.now();
        Position pos = new Position(5, 5);
        DropAction action = new DropAction(
            "user3", 
            "SECOND", 
            "PAWN", 
            pos, 
            now
        );

        assertEquals("user3", action.userId());
        assertEquals("SECOND", action.teamId());
        assertEquals("PAWN", action.pieceType());
        assertEquals(pos, action.position());
        assertEquals(now, action.at());
    }

    /**
     * DropActionのGameActionインターフェース実装確認
     */
    @Test
    void testDropActionImplementsGameAction() {
        Instant now = Instant.now();
        DropAction dropAction = new DropAction(
            "user4", 
            "FIRST", 
            "GOLD", 
            new Position(3, 3), 
            now
        );
        GameAction action = dropAction;

        assertEquals("user4", action.getUserId());
        assertEquals("FIRST", action.getTeamId());
        assertEquals(now, dropAction.at());
    }

    /**
     * DropActionで異なる座標の確認
     */
    @Test
    void testDropActionDifferentPositions() {
        Position pos1 = new Position(1, 1);
        Position pos2 = new Position(9, 9);

        DropAction action1 = new DropAction("user1", "FIRST", "PAWN", pos1, Instant.now());
        DropAction action2 = new DropAction("user1", "FIRST", "PAWN", pos2, Instant.now());

        assertEquals(pos1, action1.position());
        assertEquals(pos2, action2.position());
        assertNotEquals(action1.position(), action2.position());
    }

    /**
     * 複数のアクションのタイムスタンプ順序確認
     */
    @Test
    void testActionTimestampOrdering() throws InterruptedException {
        Instant time1 = Instant.now();
        Thread.sleep(10);
        Instant time2 = Instant.now();

        MoveAction action1 = new MoveAction(
            "user1", "FIRST", 1, "PAWN", List.of(Direction.UP), false, time1
        );
        MoveAction action2 = new MoveAction(
            "user2", "SECOND", 2, "PAWN", List.of(Direction.DOWN), false, time2
        );

        assertTrue(action1.at().isBefore(action2.at()));
    }

    /**
     * レコードの等価性確認
     */
    @Test
    void testActionEquality() {
        Instant now = Instant.now();
        Position pos = new Position(5, 5);

        MoveAction move1 = new MoveAction("user1", "FIRST", 1, "PAWN", List.of(Direction.UP), false, now);
        MoveAction move2 = new MoveAction("user1", "FIRST", 1, "PAWN", List.of(Direction.UP), false, now);
        MoveAction move3 = new MoveAction("user2", "FIRST", 1, "PAWN", List.of(Direction.UP), false, now);

        assertEquals(move1, move2);
        assertNotEquals(move1, move3);

        DropAction drop1 = new DropAction("user1", "FIRST", "PAWN", pos, now);
        DropAction drop2 = new DropAction("user1", "FIRST", "PAWN", pos, now);
        DropAction drop3 = new DropAction("user1", "FIRST", "GOLD", pos, now);

        assertEquals(drop1, drop2);
        assertNotEquals(drop1, drop3);
    }
}

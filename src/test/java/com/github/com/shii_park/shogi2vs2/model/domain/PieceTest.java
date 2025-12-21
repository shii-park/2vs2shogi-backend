package com.github.com.shii_park.shogi2vs2.model.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.com.shii_park.shogi2vs2.model.enums.PieceType;
import com.github.com.shii_park.shogi2vs2.model.enums.Team;

class PieceTest {

    @Test
    void testPieceCreation() {
        Piece piece = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        assertEquals(1, piece.getId());
        assertEquals(PieceType.PAWN, piece.getType());
        assertEquals(Team.FIRST, piece.getTeam());
        assertFalse(piece.isPromoted());
        assertTrue(piece.isPromotable());
    }

    @Test
    void testPromoteablePiece() {
        Piece pawn = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        assertFalse(pawn.isPromoted());
        pawn.setPromoted(true);
        assertTrue(pawn.isPromoted());
    }

    @Test
    void testNonPromoteablePiece() {
        Piece king = new Piece(1, PieceType.KING, Team.FIRST, false);
        assertFalse(king.isPromotable());
    }

    @Test
    void testTeamChange() {
        Piece piece = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        assertEquals(Team.FIRST, piece.getTeam());
        piece.setTeam(Team.SECOND);
        assertEquals(Team.SECOND, piece.getTeam());
    }

    @Test
    void testPromotionReset() {
        Piece piece = new Piece(1, PieceType.PAWN, Team.FIRST, true);
        piece.setPromoted(true);
        assertTrue(piece.isPromoted());
        piece.setPromoted(false);
        assertFalse(piece.isPromoted());
    }
}

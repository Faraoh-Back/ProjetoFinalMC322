package org.chess.pieces;

import org.chess.Color;
import org.chess.Move;
import org.chess.exception.*;

import org.mockito.Mock;

import java.util.function.Function;
import java.util.Collection;
import java.util.List;

import org.chess.Pos;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.function.Predicate;


/**
 * My intention is to test all at once
 * we will have 2 kings in a mocked board,
 * one will test king side castling, other will test
 * king side castling, both will test the simple moves,
 * in total, we should have 14 moves
 */
@ExtendWith(MockitoExtension.class)
public class testKing {

    @Mock
    private Function<Pos, Piece> getPiece;
    
    @Mock
    private Function<Piece, Pos> getPos;

    @Mock
    private Function<Color, Predicate<Pos>> getDangerMap;

    @Mock
    private Predicate<Piece> movedBefore;

    @Test
    public void testKingsMoves(){
        Rook greenKingRook = new Rook(Color.GREEN);
        Rook greenQueenRook = new Rook(Color.GREEN);
        Rook redKingRook = new Rook(Color.RED);
        Rook redQueenRook = new Rook(Color.RED);

        King greenKing = new King(Color.GREEN, greenKingRook, greenQueenRook);
        King redKing = new King(Color.RED, redKingRook, redQueenRook);

        Pos greenKingPos = new Pos(14, 8);
        Pos redKingPos = new Pos(1, 7);

        Collection<King> kings = List.of(greenKing, redKing);

        when(getPiece.apply(any(Pos.class))).thenReturn(null);
        when(getPos.apply(eq(redKing))).thenReturn(redKingPos);
        when(getPos.apply(eq(greenKing))).thenReturn(greenKingPos);
        when(getDangerMap.apply(any(Color.class))).thenReturn((Predicate<Pos>) pos -> false);
        when(movedBefore.test(greenKing)).thenReturn(false);
        when(movedBefore.test(redKing)).thenReturn(false);
        when(movedBefore.test(greenKingRook)).thenReturn(false);
        when(movedBefore.test(greenQueenRook)).thenReturn(false);
        when(movedBefore.test(redKingRook)).thenReturn(false);
        when(movedBefore.test(redQueenRook)).thenReturn(false);

        Collection<Move> moves = null;
        try{
            moves = King.calculateMoves(kings, getPiece, getPos, getDangerMap, movedBefore);

            for(Move m : moves){
                System.out.println(m.toPos());
            }

            assertEquals(14, moves.size());
        }catch(InvalidPosition iPos){

        }catch(PieceNotInBoard pnb){}

    }

}

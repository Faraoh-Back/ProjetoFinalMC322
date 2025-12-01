package org.chess.pieces;

import org.chess.Color;
import org.chess.Move;
import org.chess.exception.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;
import java.util.Collection;

import org.chess.Pos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class testKnight {
    

    @Mock
    private Function<Piece, Pos> getPos;

    @Mock
    private Function<Pos, Piece> getPiece;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testknightCalculateMoves(){
        Knight knight = new Knight(Color.GREEN);
        Piece enemyPiece = new Pawn(Color.RED);
        Piece allyPiece = new Pawn(knight.color);

        Pos knightPos = new Pos(4, 4);
        Pos enemyPos = new Pos(6, 5);
        Pos allyPos = new Pos(6, 3);

        //Dictates the behaviour of getPos and getPiece functions
        when(getPos.apply(eq(knight))).thenReturn(knightPos);
        when(getPiece.apply(eq(enemyPos))).thenReturn(enemyPiece);
        when(getPiece.apply(eq(allyPos))).thenReturn(allyPiece);
        when(getPiece.apply(any(Pos.class))).thenReturn(null);


        Collection<Move> moves = null;
        try{
            moves = knight.calculateMoves(getPiece, getPos);

            for(Move m : moves){
                System.out.println(m.toPos());
            }

            assertEquals(6, moves.size());

        }catch(InvalidPosition iPos){

        }catch(PieceNotInBoard pnb){}
    }
}

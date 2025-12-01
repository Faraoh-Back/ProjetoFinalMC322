package org.chess.pieces;

import org.chess.Color;
import org.chess.Move;
import org.chess.exception.*;

import org.mockito.Mock;

import java.util.function.Function;
import java.util.Collection;

import org.chess.Pos;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class testQueen {
    
    private Queen queen = new Queen(Color.GREEN);

    @Mock
    private Function<Piece, Pos> getPos;

    @Mock
    private Function<Pos, Piece> getPiece;

    @Test
    public void testQueenCalculateMoves(){
        
        Piece enemyPiece = new Pawn(Color.RED);
        Piece allyPiece = new Pawn(queen.color);

        Pos queenPos = new Pos(7, 7);
        Pos enemyPos = new Pos(7, 9);
        Pos allyPos = new Pos(9, 7);

        //Dictates the behaviour of getPos and getPiece functions
        when(getPos.apply(eq(queen))).thenReturn(queenPos);
        when(getPiece.apply(any(Pos.class))).thenReturn(null);
        when(getPiece.apply(eq(enemyPos))).thenReturn(enemyPiece);
        when(getPiece.apply(eq(allyPos))).thenReturn(allyPiece);


        Collection<Move> moves = null;
        try{
            moves = queen.calculateMoves(getPiece, getPos);

            for(Move m : moves){
                System.out.println(m.toPos());
            }

            assertEquals(30, moves.size());
        }catch(InvalidPosition iPos){

        }catch(PieceNotInBoard pnb){}
    }
}



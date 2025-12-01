package org.chess.pieces;

import org.chess.Color;
import org.chess.Move;
import org.chess.exception.*;

import org.mockito.Mock;

import java.util.function.Function;
import java.util.Collection;

import org.chess.Pos;
import org.chess.Move.MoveType;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class testPawn {
    
    private Pawn pawn = new Pawn(Color.GREEN);

    @Mock
    private Function<Piece, Pos> getPos;

    @Mock
    private Function<Pos, Piece> getPiece;

    @Mock
    private Function<Color, Move> getLastMove;


    @Test
    public void frontEnPassant(){
        Pawn enemyPawn = new Pawn(Color.RED);

        Pos pawnPos = new Pos(4, 7);
        Pos enemyPos = new Pos(4, 8);

        when(getPiece.apply(any(Pos.class))).thenReturn(null);
        when(getPiece.apply(eq(enemyPos))).thenReturn(enemyPawn);
        when(getPos.apply(eq(pawn))).thenReturn(pawnPos);
        when(getLastMove.apply(any(Color.class))).thenReturn(null);
        when(getLastMove.apply(Color.RED)).thenReturn(new Move(enemyPawn, MoveType.PAWN_DOUBLE, enemyPos));

        Collection<Move> moves = null;
        try{
            moves = pawn.calculateMoves(getPiece, getPos, getLastMove);

            for(Move m : moves){
                System.out.println(m.toPos());
            }

            assertEquals(2, moves.size());
        }catch(InvalidPosition iPos){

        }catch(PieceNotInBoard pnb){}
    }

    @Test
    public void lateralEnPassant(){
        Pawn enemyPawn = new Pawn(Color.YELLOW);

        Pos pawnPos = new Pos(11,4);
        Pos enemyPos = new Pos(10, 4);

        when(getPiece.apply(any(Pos.class))).thenReturn(null);
        when(getPiece.apply(eq(enemyPos))).thenReturn(enemyPawn);
        when(getPos.apply(eq(pawn))).thenReturn(pawnPos);
        when(getLastMove.apply(any(Color.class))).thenReturn(null);
        when(getLastMove.apply(Color.YELLOW)).thenReturn(new Move(enemyPawn, MoveType.PAWN_DOUBLE, enemyPos));

        Collection<Move> moves = null;
        try{
            moves = pawn.calculateMoves(getPiece, getPos, getLastMove);

            for(Move m : moves){
                System.out.println(m.toPos());
            }

            assertEquals(1, moves.size());
        }catch(InvalidPosition iPos){

        }catch(PieceNotInBoard pnb){}
    }

    @Test
    public void SimpleAttack(){
        Pawn enemyPawn = new Pawn(Color.YELLOW);

        Pos pawnPos = new Pos(11,4);
        Pos enemyPos = new Pos(10, 3);

        when(getPiece.apply(any(Pos.class))).thenReturn(null);
        when(getPiece.apply(eq(enemyPos))).thenReturn(enemyPawn);
        when(getPos.apply(eq(pawn))).thenReturn(pawnPos);

        Collection<Move> moves = null;
        try{
            moves = pawn.calculateMoves(getPiece, getPos, getLastMove);

            for(Move m : moves){
                System.out.println(m.toPos());
            }

            assertEquals(2, moves.size());
        }catch(InvalidPosition iPos){

        }catch(PieceNotInBoard pnb){}
    }


}
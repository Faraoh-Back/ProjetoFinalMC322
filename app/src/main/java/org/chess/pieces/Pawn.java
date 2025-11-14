package org.chess.pieces;

import java.util.ArrayList;
import java.util.List;

import org.chess.Color;
import org.chess.board.Board;
import org.chess.Move;
import org.chess.Pos;
import org.chess.Move.MoveType;


public class Pawn extends Piece{
    
    public Pawn(Color color, Board board){
        super(color, board);
    }

    public MovesCalcResult calculateMoves(){
        //This will be the MovesCalcResult atributes
        ArrayList<Move> validMoves = new ArrayList<Move>();
        ArrayList<Piece> piecesBlockingMoves = new ArrayList<Piece>();

        //getting this piece's position
        Pos thisPos = super.board.getPos(this);
        int row = thisPos.row();
        int column = thisPos.column();

        //En-Passsant check
        //Gets the last move made
        List<Move> history = super.board.history.getMovesView();
        Move lastMove = null;
        if(!history.isEmpty()){
            lastMove = history.get(history.size() - 1);
        }

        //switch to help dealing with four possible directions of the pawns
        //Ficou meio feio, mas outras opções que pesquisei tornaria o código menos legível
        int[] directionHelper = new int[10];
        switch(super.color){
            case GREEN:
                directionHelper[0] = 0;
                directionHelper[1] = -1;
                directionHelper[2] = 0;
                directionHelper[3] = 1;
                directionHelper[4] = 1;
                directionHelper[5] = -1;
                directionHelper[6] = 1;
                directionHelper[7] = 1;
                directionHelper[8] = -1;
                directionHelper[9] = 0;
                break;
            case YELLOW:
                directionHelper[0] = -1;
                directionHelper[1] = 0;
                directionHelper[2] = 1;
                directionHelper[3] = 0;
                directionHelper[4] = -1;
                directionHelper[5] = 1;
                directionHelper[6] = 1;
                directionHelper[7] = 1;
                directionHelper[8] = 0;
                directionHelper[9] = 1;
                break;
            case RED:
                directionHelper[0] = 0;
                directionHelper[1] = 1;
                directionHelper[2] = 0;
                directionHelper[3] = -1;
                directionHelper[4] = 1;
                directionHelper[5] = 1;
                directionHelper[6] = 1;
                directionHelper[7] = -1;
                directionHelper[8] = 1;
                directionHelper[9] = 0;
                break;
            case BLUE:
                directionHelper[0] = 1;
                directionHelper[1] = 0;
                directionHelper[2] = -1;
                directionHelper[3] = 0;
                directionHelper[4] = 1;
                directionHelper[5] = -1;
                directionHelper[6] = -1;
                directionHelper[7] = -1;
                directionHelper[8] = 0;
                directionHelper[9] = -1;
                break;
            default:
                throw new IllegalArgumentException();
        }
        //Checks if last move was a double pawn move
        if(lastMove.type() == MoveType.PAWN_DOUBLE){
            //Checks if that pawn is in this pawn's side
            Piece checkPiece = lastMove.piece();
            Piece leftSidePiece = super.board.getPiece(new Pos(row + directionHelper[0], column + directionHelper[1]));
            Piece rightSidePiece = super.board.getPiece(new Pos(row + directionHelper[2], column + directionHelper[3]));
            if(checkPiece == leftSidePiece){
                //Checks if there is a piece blocking en-passant
                Pos tempPos = new Pos(row + directionHelper[4], column + directionHelper[5]);
                if(super.board.getPiece(tempPos) == null){
                    validMoves.add(new Move(this, MoveType.EN_PASSANT, tempPos));
                }
            }
            if(checkPiece == rightSidePiece){
                //Checks if there is a piece blocking en-passant
                Pos tempPos = new Pos(row + directionHelper[6], column + directionHelper[7]);
                if(super.board.getPiece(tempPos) == null){
                    validMoves.add(new Move(this, MoveType.EN_PASSANT, tempPos));
                }
            }
            //There is no need to add pieces blocking, since en-passant can only be done in the current turn
        }
        //Double-step check
        List<Move> thisMoves = super.board.history.getMovesView(this);
        if(thisMoves.isEmpty()){
            Pos tempPos = new Pos(row + 2 * directionHelper[8], column + 2 * directionHelper[9]);
            Piece pieceInPos = super.board.getPiece(tempPos);
            if(pieceInPos == null){
                validMoves.add(new Move(this, MoveType.PAWN_DOUBLE, tempPos));
            }else{
                piecesBlockingMoves.add(pieceInPos);
            }
        }
        

        //Simple Move check
        Pos tempPos = new Pos(row + directionHelper[8], column + directionHelper[9]);
        Piece pieceInPos = super.board.getPiece(tempPos);
        if(pieceInPos == null){
            validMoves.add(new Move(this, MoveType.PAWN_DOUBLE, tempPos));
        }else{
            piecesBlockingMoves.add(pieceInPos);
        }


        return new MovesCalcResult(validMoves, piecesBlockingMoves);
    }
}

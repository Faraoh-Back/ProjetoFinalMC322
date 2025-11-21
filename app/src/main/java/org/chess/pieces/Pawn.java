package org.chess.pieces;

import java.util.ArrayList;
import java.util.List;

import org.chess.Color;

import com.google.common.collect.BiMap;

import org.chess.Move;
import org.chess.PieceNotInBoard;
import org.chess.Pos;
import org.chess.Move.MoveType;
import org.chess.board.History;


public class Pawn extends Piece{
    boolean thisMovedYet = false;
    
    public Pawn(Color color){
        super(color);
    }

    public MovesCalcResult calculateMoves(BiMap<Pos, Piece> boardState, History gameHistory) throws PieceNotInBoard{

        //Checks if piece is on the board
        Pos thisPos = boardState.inverse().get(this);
        if(thisPos == null){
            throw new PieceNotInBoard();
        }
        
        //This will be the MovesCalcResult atributes
        ArrayList<Move> validMoves = new ArrayList<Move>();
        ArrayList<Pos> dependencies = new ArrayList<Pos>();

        //getting this piece's position
        int row = thisPos.row();
        int column = thisPos.column();

        //Verifies if this pawn has moved
        if(!thisMovedYet){
            List<Move> thisHistory = gameHistory.getMovesView(this);
            if(!thisHistory.isEmpty()){
                thisMovedYet = true;
            }
        }

        //En-passant check
        List<Move> LastMoves = gameHistory.getMovesView();
        Move lastMove = LastMoves.get(LastMoves.size() - 1);

        //checks if last move was a double pawn move
        if(lastMove.type() == MoveType.PAWN_DOUBLE){
            Piece movedPiece = lastMove.piece();
            Pos tempPos;

            // checks if the moved piece is the color in the front
            if(getFrontColor(super.color) == movedPiece.color){
                //checking right-side en-passant
                tempPos = new Pos(row, column + 1);
                if(boardState.get(tempPos) == movedPiece){
                    validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row-1, column+1)));
                }

                //checking left-side en-passant
                tempPos = new Pos(row, column - 1);
                if(boardState.get(tempPos) == movedPiece){
                    validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row-1, column-1)));
                }
            //checking en-passant on the side colors
            }else{
                tempPos = new Pos(row - 1, column);
                if(boardState.get(tempPos) == movedPiece){
                    if(getLeftColor(super.color) == movedPiece.color){
                        validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row-1, column-1)));
                    }else{
                        validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row-1, column+1)));
                    }
                }
            }
        }

        
        //Double-step check
        if(!thisMovedYet){
            Pos movementPos = new Pos(row-2, column);
            dependencies.add(movementPos);
            if(boardState.get(movementPos) == null){
                validMoves.add(new Move(this, MoveType.PAWN_DOUBLE, movementPos));
            }
        }
        

        //Simple Move check
        Pos movementPos = new Pos(row - 1, column);
        Piece pieceInPos = boardState.get(movementPos);
        dependencies.add(movementPos);
        if(pieceInPos == null){
            if(row+1 == 14){
                validMoves.add(new Move(this, MoveType.QUEEN_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.KNIGHT_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.ROOK_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.BISHOP_PROMOTION, movementPos));
            }else{
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, movementPos));
            }  
        }

        //Left Attack-Move check
        try{
            movementPos = new Pos(row - 1, column - 1);
            dependencies.add(movementPos);
            pieceInPos = boardState.get(movementPos);
            
            if(pieceInPos != null && pieceInPos.color != super.color){
                if(row+1 == 14){
                validMoves.add(new Move(this, MoveType.QUEEN_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.KNIGHT_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.ROOK_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.BISHOP_PROMOTION, movementPos));
                }else{
                    validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, movementPos));
                }  
            }
        }catch(Exception e){}

        //Right Attack-Move check
        try{
            movementPos = new Pos(row - 1, column + 1);
            dependencies.add(movementPos);
            pieceInPos = boardState.get(movementPos);
            
            if(pieceInPos != null && pieceInPos.color != super.color){
                if(row+1 == 14){
                validMoves.add(new Move(this, MoveType.QUEEN_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.KNIGHT_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.ROOK_PROMOTION, movementPos));
                validMoves.add(new Move(this, MoveType.BISHOP_PROMOTION, movementPos));
                }else{
                    validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, movementPos));
                }  
            }
        }catch(Exception e){}

        //Add final dependencies that will be needed anyways for checking en-passant
        dependencies.add(new Pos(row, column+1));
        dependencies.add(new Pos(row, column-1));

        return new MovesCalcResult(validMoves, dependencies);
    }

//returns the color in the front
private Color getFrontColor(Color thisColor){
    Color frontColor = Color.RED;
    switch(thisColor){
        case Color.GREEN:
            frontColor = Color.RED;
            break;
        case Color.YELLOW:
            frontColor = Color.BLUE;
            break;
        case Color.RED:
            frontColor = Color.GREEN;
            break;
        case Color.BLUE:
            frontColor = Color.YELLOW;
            break;
    }
    return frontColor;
}

//returns the color in the left
private Color getLeftColor(Color thisColor){
    Color frontColor = Color.RED;
    switch(thisColor){
        case Color.GREEN:
            frontColor = Color.YELLOW;
            break;
        case Color.YELLOW:
            frontColor = Color.RED;
            break;
        case Color.RED:
            frontColor = Color.BLUE;
            break;
        case Color.BLUE:
            frontColor = Color.GREEN;
            break;
    }
    return frontColor;
}
}

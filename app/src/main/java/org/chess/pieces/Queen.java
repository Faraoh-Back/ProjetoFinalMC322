package org.chess.pieces;

import java.util.ArrayList;

import org.chess.Color;
import org.chess.board.Board;
import org.chess.Move;
import org.chess.Pos;
import org.chess.Move.MoveType;


public class Queen extends Piece{
    
    public Queen(Color color, Board board){
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

        //filling up validMoves and piecesBlockingMoves
        //It checks each corner and side of the Queen independently

        //South
        int counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row + counter, column);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
        //North
        counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row - counter, column);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
        //East
        counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row, column + counter);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
        //West
        counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row, column - counter);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
        //Southeast
        counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row + counter, column + counter);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
        //Southwest
        counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row + counter, column - counter);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
        //Northwest
        counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row - counter, column + counter);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
        //Northeast
        counter = 1;
        while(true){
            try{
                Pos pos = new Pos(row - counter, column - counter);
                counter++;
                Piece pieceInPos = super.board.getPiece(pos);
                if(pieceInPos != null){
                    piecesBlockingMoves.add(pieceInPos);
                    if(pieceInPos.color == super.color){
                        break;
                    }
                }
                validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }

        return new MovesCalcResult(validMoves, piecesBlockingMoves);

    }
}


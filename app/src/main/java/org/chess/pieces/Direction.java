package org.chess.pieces;

import java.util.List;
import com.google.common.collect.BiMap;

import org.chess.Move;
import org.chess.Move.MoveType;
import org.chess.Pos;

/**
 * enum to help rook, bishop and queen type pieces to check
 * an entire direction until the end of the board or another 
 * piece is found.
 */

public enum Direction {
  SOUTHEAST(1, 1),
  SOUTH(1, 0),
  SOUTHWEST(1, -1),
  WEST(0, -1),
  NORTHWEST(-1, -1),
  NORTH(-1, 0),
  NORTHEAST(-1, 1),
  EAST(0, 1);

  public final int rowDirection;
  public final int columnDirection;

    private Direction(int rD, int cD){
        rowDirection = rD;
        columnDirection = cD;
    }
    
    public void checkDirection(List<Move> validMoves, List<Pos> dependencies, BiMap<Pos, Piece> boardState, Piece piece, int row, int column){
        int rowCounter = rowDirection;
        int columnConter = columnDirection;
        while(true){
            try{
                Pos pos = new Pos(row + rowCounter, column + columnConter);
                dependencies.add(pos);
                rowCounter += rowDirection;
                columnConter += columnDirection;
                Piece pieceInPos = boardState.get(pos);
                if(pieceInPos != null){
                    if(pieceInPos.color != piece.color){
                        validMoves.add(new Move(piece, MoveType.SIMPLE_MOVE, pos));
                    }
                    break;
                }
                validMoves.add(new Move(piece, MoveType.SIMPLE_MOVE, pos));
            }catch(IllegalArgumentException e){
                break;
            }
        }
      }
    
  
}


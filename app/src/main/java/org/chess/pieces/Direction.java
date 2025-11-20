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

    public int rowDirection;
    public int columnDirection;

    private Direction(int rD, int cD){
        rowDirection = rD;
        columnDirection = cD;
    }
    
    public static void checkDirection(List<Move> validMoves, List<Pos> dependencies, Piece piece, int row, int column, BiMap<Pos, Piece> boardState, Direction direction){
        int rowCounter = direction.rowDirection;
        int columnConter = direction.columnDirection;
        while(true){
            try{
                Pos pos = new Pos(row + rowCounter, column + columnConter);
                dependencies.add(pos);
                rowCounter += direction.rowDirection;
                columnConter += direction.columnDirection;
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

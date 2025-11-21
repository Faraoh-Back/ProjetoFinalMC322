package org.chess.pieces;

import java.util.ArrayList;
import java.util.function.Function;

import org.chess.Color;
import org.chess.Move;
import org.chess.PieceNotInBoard;
import org.chess.Pos;


public class Rook extends NonKing{
    
    public Rook(Color color){
        super(color);
    }

    @Override
    public MovesCalcResult calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos) throws PieceNotInBoard{

        //Checks if piece is on the board
        Pos thisPos = getPos.apply(this);
        if(thisPos == null){
            throw new PieceNotInBoard();
        }

        //This will be the MovesCalcResult atributes
        ArrayList<Move> validMoves = new ArrayList<Move>();
        ArrayList<Pos> dependencies = new ArrayList<Pos>();

        //getting this piece's position
        int row = thisPos.row();
        int column = thisPos.column();

        //Checks directions in wich a Rook can move, filling up the arguments for MovesCalcResult
        Direction[] possiblDirections = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for(Direction direction : possiblDirections){
            direction.checkDirection(validMoves, dependencies, getPiece,  this, row, column);
        }
        
        return new MovesCalcResult(validMoves, dependencies);

    }
}


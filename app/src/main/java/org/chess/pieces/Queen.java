package org.chess.pieces;

import java.util.ArrayList;
import java.util.function.Function;

import org.chess.Color;
import org.chess.Move;
import org.chess.PieceNotInBoard;
import org.chess.Pos;



public class Queen extends NonKing{
    
    public Queen(Color color){
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

    
        //Checks every direction for possible moves
        for(Direction direction : Direction.values()){
            direction.checkDirection(validMoves, dependencies, getPiece,  this, row, column);
        }
        return new MovesCalcResult(validMoves, dependencies);

    }
}


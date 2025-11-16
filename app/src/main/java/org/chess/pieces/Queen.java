package org.chess.pieces;

import java.util.ArrayList;
import com.google.common.collect.BiMap;

import org.chess.Color;
import org.chess.Move;
import org.chess.PieceNotInBoard;
import org.chess.Pos;



public class Queen extends Piece{
    
    public Queen(Color color){
        super(color);
    }

    public MovesCalcResult calculateMoves(BiMap<Pos, Piece> boardState) throws PieceNotInBoard{

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

    
        //Checks every direction for possible moves
        for(Direction direction : Direction.values()){
            checkDirection(validMoves, dependencies, this, row, column, boardState);
        }
        return new MovesCalcResult(validMoves, dependencies);

    }
}


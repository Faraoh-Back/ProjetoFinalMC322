package org.chess.pieces;

import java.util.ArrayList;
import com.google.common.collect.BiMap;

import org.chess.Color;
import org.chess.Move;
import org.chess.PieceNotInBoard;
import org.chess.Pos;


public class Bishop extends NonKing{
    
    public Bishop(Color color){
        super(color);
    }

    @Override
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

        //Checks directions in wich a Bishop can move, filling up the arguments for MovesCalcResult
        Direction[] possibleDirections = {Direction.SOUTHEAST, Direction.SOUTHWEST, Direction.NORTHEAST, Direction.NORTHWEST};
        for(Direction direction : possibleDirections){
            direction.checkDirection(validMoves, dependencies, this, row, column, boardState);
        }

        return new MovesCalcResult(validMoves, dependencies);

    }
}


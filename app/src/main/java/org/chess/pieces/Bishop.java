package org.chess.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.chess.Color;
import org.chess.Move;
import org.chess.Pos;
import org.chess.exception.PieceNotInBoard;

public class Bishop extends NonKing {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public Collection<Move> calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos)
            throws PieceNotInBoard {
        // Checks if piece is on the board
        Pos thisPos = getPos.apply(this);
        if (thisPos == null)
            throw new PieceNotInBoard();

        // This will be the MovesCalcResult atributes
        ArrayList<Move> validMoves = new ArrayList<Move>();

        // getting this piece's position
        int row = thisPos.row();
        int column = thisPos.column();

        // Checks directions in wich a Bishop can move, filling up the arguments for
        // MovesCalcResult
        Direction[] possibleDirections = { Direction.SOUTHEAST, Direction.SOUTHWEST, Direction.NORTHEAST,
                Direction.NORTHWEST };
        for (Direction direction : possibleDirections)
            direction.checkDirection(validMoves, getPiece, this, row, column);

        return validMoves;

    }
}

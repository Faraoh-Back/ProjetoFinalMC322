package org.chess.pieces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.chess.Color;
import org.chess.Move;
import org.chess.Pos;
import org.chess.exception.PieceNotInBoard;

public class Queen extends NonKing implements Serializable {
    public Queen(Color color) {
        super(color);
    }

    @Override
    public Collection<Move> calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos)
            throws PieceNotInBoard {
        // Checks if piece is on the board
        Pos thisPos = getPos.apply(this);
        if (thisPos == null) {
            throw new PieceNotInBoard();
        }

        // This will be the MovesCalcResult atributes
        ArrayList<Move> validMoves = new ArrayList<Move>();

        // getting this piece's position
        int row = thisPos.row();
        int column = thisPos.column();

        // Checks every direction for possible moves
        for (Direction direction : Direction.values())
            direction.checkDirection(validMoves, getPiece, this, row, column);

        return validMoves;
    }
}

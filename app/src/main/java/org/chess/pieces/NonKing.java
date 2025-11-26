package org.chess.pieces;

import java.util.Collection;
import java.util.function.Function;

import org.chess.Color;
import org.chess.Move;
import org.chess.Pos;
import org.chess.exception.PieceNotInBoard;

public abstract class NonKing extends Piece {

    public NonKing(Color color) {
        super(color);
    }

    public abstract Collection<Move> calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos)
            throws PieceNotInBoard;
}

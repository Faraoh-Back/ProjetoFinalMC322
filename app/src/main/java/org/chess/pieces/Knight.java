package org.chess.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.chess.Color;
import org.chess.Move;
import org.chess.Move.MoveType;
import org.chess.exception.InvalidPosition;
import org.chess.exception.PieceNotInBoard;
import org.chess.Pos;

public class Knight extends NonKing {
	public Knight(Color color) {
		super(color);
	}

	@Override
	public Collection<Move> calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos)
			throws PieceNotInBoard {
		// Checks if piece is on the board
		Pos thisPos = getPos.apply(this);
		if (thisPos == null)
			throw new PieceNotInBoard();

		ArrayList<Move> validMoves = new ArrayList<Move>();

		// getting this piece's position
		int row = thisPos.row();
		int column = thisPos.column();

		int[][] possibleMoves = {
				{ row + 2, column + 1 },
				{ row + 2, column - 1 },
				{ row - 2, column + 1 },
				{ row - 2, column - 1 },
				{ row + 1, column + 2 },
				{ row - 1, column + 2 },
				{ row + 1, column - 2 },
				{ row - 1, column - 2 }
		};

		// Checks if those positions would generate validMoves, then, fills validMoves
		for (int[] pos : possibleMoves) {
			try {
				Pos tempPos = new Pos(pos[0], pos[1]);
				Piece pieceInPos = getPiece.apply(tempPos);
				if (pieceInPos == null || pieceInPos.color != color)
					validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, tempPos));
			} catch (InvalidPosition e) {
			}
		}
		return validMoves;
	}
}

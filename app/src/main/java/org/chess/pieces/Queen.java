package org.chess.pieces;

import org.chess.Color;
import org.chess.board.Board;

public class Queen extends Piece {

	public Queen(Color color, Board board) {
		super(color, board);
	}

	@Override
	public MovesCalcResult calculateMoves() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'calculateMoves'");
	}
}

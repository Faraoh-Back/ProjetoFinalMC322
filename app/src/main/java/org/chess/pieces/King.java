package org.chess.pieces;

import org.chess.Color;
import org.chess.PieceNotInBoard;
import org.chess.board.Board;

public class King extends Piece {

	public King(Color color, Board board) {
		super(color, board);
		//TODO
	}

	@Override
	public MovesCalcResult calculateMoves() throws PieceNotInBoard {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'calculateMoves'");
	}
}

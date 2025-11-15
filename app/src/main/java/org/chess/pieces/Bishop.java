package org.chess.pieces;

import org.chess.Color;
import org.chess.PieceNotInBoard;
import org.chess.Pos;

import com.google.common.collect.BiMap;

public class Bishop extends NonKing {

	public Bishop(Color color) {
		super(color);
		// TODO Auto-generated constructor stub
	}

	@Override
	public MovesCalcResult calculateMoves(BiMap<Pos, Piece> boardState) throws PieceNotInBoard {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'calculateMoves'");
	}

}

package org.chess.pieces;

import java.util.Collection;

import org.chess.Color;
import org.chess.PieceNotInBoard;
import org.chess.Pos;

import com.google.common.collect.BiMap;

public class King extends Piece {

	public King(Color color) {
		super(color);
	}

	public static MovesCalcResult calculateMoves(Collection<King> kings, BiMap<Pos, Piece> boardState)
			throws PieceNotInBoard {
		throw new UnsupportedOperationException("Unimplemented method 'calculateMoves'");
	}
}

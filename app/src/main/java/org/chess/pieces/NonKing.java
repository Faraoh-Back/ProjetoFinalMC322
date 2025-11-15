package org.chess.pieces;

import org.chess.Color;
import org.chess.PieceNotInBoard;
import org.chess.Pos;

import com.google.common.collect.BiMap;

public abstract class NonKing extends Piece {

  public NonKing(Color color) {
    super(color);
    // TODO Auto-generated constructor stub
  }

  public abstract MovesCalcResult calculateMoves(BiMap<Pos, Piece> boardState) throws PieceNotInBoard;
}

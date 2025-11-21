package org.chess.pieces;

import java.util.Collection;

import org.chess.Color;
import org.chess.Move;
import org.chess.Pos;

public abstract class Piece {
  public final Color color;

  public Piece(Color color) {
    this.color = color;
  }

  public static record MovesCalcResult(Collection<Move> moves, Collection<Pos> dependencies) {
  }
}
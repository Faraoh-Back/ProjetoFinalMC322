package org.chess;

import java.util.Collection;

import org.chess.board.Board;

public abstract class Piece {
  public final Color color;
  public final Board board;
  
  public Piece(Color color, Board board) {
    this.color = color;
    this.board = board;
  }

  public static record MovesCalcResult(Collection<Move> moves, Collection<Pos> dependencies) {}
  
  public abstract MovesCalcResult calculateMoves();

}

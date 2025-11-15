package org.chess.pieces;

import java.util.Collection;

import org.chess.Color;
import org.chess.Move;
import org.chess.PieceNotInBoard;
import org.chess.Pos;
import org.chess.board.Board;

public abstract class Piece {
  public final Color color;
  public final Board board;
  
  public Piece(Color color, Board board) {
    this.color = color;
    this.board = board;
  }

  public static record MovesCalcResult(Collection<Move> moves, Collection<Pos> dependencies) {}
  
  public abstract MovesCalcResult calculateMoves() throws PieceNotInBoard;

}

package org.chess.pieces;

import java.util.List;

import org.chess.Color;
import org.chess.board.Board;
import org.chess.Move;

public abstract class Piece {
  public final Color color;
  public final Board board;
  
  public Piece(Color color, Board board) {
    this.color = color;
    this.board = board;
  }

  public static record MovesCalcResult(List<Move> validMoves, List<Piece> piecesBlockingMoves) {}
  
  /**
   * For a specific piece, calculates valid moves and 
   * pieces blocking the way, then, returns a record 
   * (MovesCalcResult) that stores sthose informations.
   */
  public abstract MovesCalcResult calculateMoves();

}

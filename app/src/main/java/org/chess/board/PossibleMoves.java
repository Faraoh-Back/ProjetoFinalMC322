package org.chess.board;

import org.chess.Move;
import org.chess.Piece;
import org.chess.Pos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PossibleMoves {
  private final Map<Pos, Danger> dangerMap = new HashMap<>();
  private final Map<Piece, List<Move>> movesMap = new HashMap<>();

  public PossibleMoves() {
    // initialize dangermap
  }

  public boolean isInDanger(Piece piece) {
  }
  public boolean hasMoves(Piece piece) {
    // check movesMap to see if there is some piece with the right color and with moves
  }

  public void forgetMove(Move move) {
    // get piece that's beeing moved
    // remove the move form the movesMap using the piece as key
    // get move's position
    // get piece's color
    // remove the move from the dangerMap using position and color as keys.
  }

  public void submitMove(Move move) {
    // get piece that's beeing moved
    // add move to the movesMap using the piece as key
    // get move's position
    // get piece's color
    // add move to the dangerMap using position and color as keys.
    
  }
  public List<Move> getMovesView(Piece piece) {
  }
}

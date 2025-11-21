package org.chess.board;

import org.chess.Move;
import org.chess.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
public class History {
  private final Map<Piece, List<Move>> pieceWiseHistory = new HashMap<>();
  private final List<Move> gameHistory = new ArrayList<>();

  public History() {
  }

  public void addMove(Move move) {
    // TODO    
  }

  public List<Move> getMovesView(Piece piece) {
    List<Move> list = pieceWiseHistory.get(piece);
    if (list == null) {
      list = new ArrayList<>();
    }
    return Collections.unmodifiableList(list);
    
  }

  public List<Move> getMovesView() {
    return Collections.unmodifiableList(gameHistory);
  }
  
  
}

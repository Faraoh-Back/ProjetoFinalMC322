package org.chess.board;

import org.chess.Color;
import org.chess.Move;
import org.chess.pieces.Piece;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History {
  private final Map<Color, List<Move>> colorWiseHistory = new EnumMap<>(Color.class);
  private final Map<Piece, List<Move>> pieceWiseHistory = new HashMap<>();
  private final List<Move> gameHistory = new ArrayList<>();

  public History() {
  }

  public void addMove(Move move) {
    if (move == null) return;
    gameHistory.add(move); 
    pieceWiseHistory.computeIfAbsent(move.piece(), k -> new ArrayList<>()).add(move);
    colorWiseHistory.computeIfAbsent(move.piece().color, k -> new ArrayList<>()).add(move);
  }

  public List<Move> getMoves(Piece piece) {
    List<Move> list = pieceWiseHistory.computeIfAbsent(piece, k -> new ArrayList<>());
    return new ArrayList<>(list);
  }

  public List<Move> getMoves(Color color) {
    List<Move> list = colorWiseHistory.computeIfAbsent(color, k -> new ArrayList<>());
    return new ArrayList<>(list);
  }

  public List<Move> getMoves() {
    return new ArrayList<>(gameHistory);
  }
  /**
  * Retorna uma visão imutável do histórico completo do jogo.
  * 
  * @return lista imutável de todos os movimentos realizados no jogo
  */

  public Move getLastMove() {
    return gameHistory.getLast();
  }

  public Move getLastMove(Color color) {
    return colorWiseHistory.computeIfAbsent(color, k-> new ArrayList<>()).getLast();
  }

    public Move getLastMove(Piece piece) {
    return pieceWiseHistory.computeIfAbsent(piece, k-> new ArrayList<>()).getLast();
  }

  public boolean movedBefore(Piece piece) {
    return getLastMove(piece) != null;
  }
  
}

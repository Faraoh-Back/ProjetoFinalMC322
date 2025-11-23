package org.chess.board;

import org.chess.Move;
import org.chess.pieces.Piece;

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
    // verifiicar se movimento eh null
    gameHistory.add(move); // Adiciona movimento ao historico
    // Adicao do do ultimo movimento (push back)
    Piece p = move.piece();
    List<Move> piece_moves = pieceWiseHistory.get(p);
    piece_moves.add(move);


    
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
  /**
  * Retorna uma visão imutável do histórico completo do jogo.
  * 
  * @return lista imutável de todos os movimentos realizados no jogo
  */

  public Move getLastMove() {
    if (gameHistory.isEmpty()){
      return null;

    }
    return gameHistory.get(gameHistory.size() - 1); // Retorna ultimo movimento realizado no jogo
  }

  public boolean movedBefore(Piece piece) {
    List<Move> lista_moves = pieceWiseHistory.get(piece);
    if (lista_moves != null && !lista_moves.isEmpty() && lista_moves != null && !lista_moves.isEmpty()){
      return false;
    }
    return true;
  }
  
}

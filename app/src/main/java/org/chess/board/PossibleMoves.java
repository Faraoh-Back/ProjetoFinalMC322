package org.chess.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.chess.Color;
import org.chess.Move;
import org.chess.Pos;
import org.chess.pieces.Piece;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

/* Class to store pieces' moves */
class PossibleMoves {

  // ###########################################################################
  // Data Structures
  // ###########################################################################

  /*
   * Data structure to organize moves by position and player. Is used to check
   * which pieces are in danger.
   */
  private final Map<Pos, Multimap<Color, Move>> posColorMovesMap = new HashMap<>();

  /* Data structure to organize moves piece. Is used to check a piece's moves */
  private final Multimap<Piece, Move> pieceMovesMap = HashMultimap.create();

  // ###########################################################################
  // Package interface
  // ###########################################################################

  PossibleMoves() {
    // Initializing internal mappings
    var builder = MultimapBuilder.enumKeys(Color.class).hashSetValues();
    for (Pos pos : Pos.getValidPositions()) {
      posColorMovesMap.put(pos, builder.build());
    }
  }

  boolean isDangerous(Pos pos, Color color) {
    for (Color otherColor : Color.values())
      if (otherColor != color && !posColorMovesMap.get(pos).get(otherColor).isEmpty())
        return true;
    return false;
  }

  void remove(Move move) {
    pieceMovesMap.remove(move.piece(), move);
    posColorMovesMap.get(move.toPos()).remove(move.piece().color, move);
  }

  void remove(Collection<Move> moves) {
    moves.forEach(m -> remove(m));
  }

  void remove(Piece piece) {
    remove(get(piece));
  }

  void add(Move move) {
    pieceMovesMap.put(move.piece(), move);
    posColorMovesMap.get(move.toPos()).put(move.piece().color, move);
  }

  Collection<Move> get(Piece piece) {
    return new ArrayList<>(pieceMovesMap.get(piece));
  }
}

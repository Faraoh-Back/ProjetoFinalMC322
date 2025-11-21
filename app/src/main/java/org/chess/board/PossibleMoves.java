package org.chess.board;

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

class PossibleMoves {
  private final Map<Pos, Multimap<Color, Move>> posColorMovesMap = new HashMap<>();
  private final Multimap<Piece, Move> pieceMovesMap = HashMultimap.create();

  PossibleMoves() {
    var builder = MultimapBuilder.enumKeys(Color.class).hashSetValues();
    for (Pos pos: Pos.getValidPositions()) {
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

  void removeAll(Collection<Move> moves) {
    moves.forEach(m -> remove(m));
  }

  void removeAll(Piece piece) {
    removeAll(pieceMovesMap.get(piece));
  }

  void add(Move move) {
    pieceMovesMap.put(move.piece(), move);
    posColorMovesMap.get(move.toPos()).put(move.piece().color, move);
  }

  Collection<Move> getReadonly(Piece piece) {
    return Collections.unmodifiableCollection(pieceMovesMap.get(piece));
  }

}

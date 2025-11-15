package org.chess.board;

import java.util.Collection;

import org.chess.Piece;
import org.chess.Pos;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Data structure to store the dependencies between pieces and positions.
 * We say a piece A is dependent on a postition B if A's possible moves could
 * change when a piece on B moves.
 */
class Dependencies {
  private final Multimap<Piece, Pos> piecePosMap = HashMultimap.create();
  private final Multimap<Pos, Piece> posPieceMap = HashMultimap.create();

  Dependencies() {
  }

  public void add(Piece piece, Pos pos) {
    piecePosMap.put(piece, pos);
    posPieceMap.put(pos, piece);
  }

  public void addAllDependencies(Piece piece, Collection<Pos> dependencies) {
    piecePosMap.putAll(piece, dependencies);
    for (Pos pos : dependencies) {
      posPieceMap.put(pos, piece);
    }
  }

  public void remove(Piece piece, Pos pos) {
    piecePosMap.remove(piece, pos);
    posPieceMap.remove(pos, piece);
  }

  public Collection<Pos> removeAllDependencies(Piece piece) {
    var positions = piecePosMap.removeAll(piece);
    for (Pos pos : positions) {
      posPieceMap.remove(pos, piece);
    }
    return positions;
  }

  public Collection<Pos> getDependencies(Piece piece) {
    return piecePosMap.get(piece);
  }

  public Collection<Piece> getDependents(Pos pos) {
    return posPieceMap.get(pos);
  }

}

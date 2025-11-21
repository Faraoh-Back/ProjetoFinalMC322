package org.chess.board;

import java.util.Collection;

import org.chess.pieces.NonKing;
import org.chess.pieces.Piece;
import org.chess.Pos;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Data structure to store the dependencies between pieces and positions.
 * We say a piece A is dependent on a postition B if A's possible moves could
 * change when:
 * - A piece moves to B.
 * - A piece on B is removed.
 * - A piece is added to B.
 */
class Dependencies {
  private final Multimap<Piece, Pos> piecePosMap = HashMultimap.create();
  private final Multimap<Pos, Piece> posPieceMap = HashMultimap.create();

  Dependencies() {
  }

  public void add(NonKing piece, Pos pos) {
    piecePosMap.put(piece, pos);
    posPieceMap.put(pos, piece);
  }

  public void addAll(NonKing piece, Collection<Pos> dependencies) {
    piecePosMap.putAll(piece, dependencies);
    for (Pos pos : dependencies) {
      posPieceMap.put(pos, piece);
    }
  }

  public void remove(Piece piece, Pos pos) {
    piecePosMap.remove(piece, pos);
    posPieceMap.remove(pos, piece);
  }

  public Collection<Pos> removeAll(Piece piece) {
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

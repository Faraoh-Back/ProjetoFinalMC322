package org.chess.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.chess.Color;
import org.chess.King;
import org.chess.Piece;
import org.chess.PieceNotInBoard;
import org.chess.Pos;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Manages the relation between each piece and its position.
 * Also calculates which pieces are in danger.
 */
class BoardState {
  // ##################################################
  // Data structures
  // ##################################################

  /**
   * A bi-directional map between Piece and its Pos.
   */
  private final BiMap<Pos, Piece> boardState = HashBiMap.create();

  /**
   * It is used to find which pieces' moves need to be recalculated after a change
   * in the board's state. It should only be mutated within the `reevaluate`
   * method. Read the method's docstring to know when it must be called.
   */
  private final Dependencies dependencies = new Dependencies();

  /**
   * It is used to store pieces' moves. It should only be mutated within the
   * `reevaluate` method. Read the method's docstring to know when it must be
   * called.
   */
  private final PossibleMoves moves = new PossibleMoves(); 

  private boolean canCalculateDanger = true;

  // ##################################################
  // Constructor
  // ##################################################

  BoardState() {
    // TODO: when starting a new game, all pieces are added. But the `add` method reevaluates for every piece. which is not necessary. Maybe the constructor should receice the initial state??
  }

  // ##################################################
  // Read only operations
  // ##################################################

  /**
   * @param piece
   * @return The position of piece.
   * @throws IllegalArgumentException if piece is not on the board.
   */
  Pos getPos(Piece piece) {
    Pos pos = boardState.inverse().get(piece);
    if (pos == null) {
      throw new IllegalArgumentException("Invalid piece: This piece is not on the board.");
    }
    return pos;
  }

  /**
   * @param pos
   * @return piece at pos or null if the position is empty.
   */
  Piece getPiece(Pos pos) {
    return boardState.get(pos);
  }

  /**
   * @param pos
   * @param color
   * @return true if color would be in danget at pos. Else, false.
   */
  boolean isDangerous(Pos pos, Color color) {
    return moves.isDangerous(pos, color);
  }

  // ##################################################
  // Mutating operations
  // ##################################################

  /**
   * @param piece
   * @param pos
   * @throws IllegalArgumentException if parameters are null, or if there's already a
   *                                  piece at pos, or if the piece is already on
   *                                  the board.
   */
  void addPiece(Piece piece, Pos pos) {
    Objects.requireNonNull(piece, "piece should not be null.");
    Objects.requireNonNull(pos, "pos should not be null.");

    if (boardState.get(pos) != null) {
      throw new IllegalArgumentException("Invalid Position: There's already a piece at this position.");
    }

    try {
      boardState.put(pos, piece);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid Piece: This piece is already at another position. Use .move instead.");
    }

    Collection<Piece> needReevaluation = dependencies.getDependents(pos);
    needReevaluation.add(piece);
    reevaluateAll(needReevaluation);
  }

  /**
   * @param piece
   * @throws IllegalArgumentException if piece is not on the board
   */
  void removePiece(Piece piece) {
    Pos pos = boardState.inverse().remove(piece);
    if (pos == null) {
      throw new IllegalArgumentException("Invalid Piece: This piece is not on the board.");
    }
    Collection<Piece> needReevaluation = dependencies.getDependents(pos);
    needReevaluation.add(piece);
    reevaluateAll(needReevaluation);
  }

  /**
   * @param piece
   * @param toPos
   * @return The piece that was taken, if any.
   * @throws IllegalArgumentException if piece is not on the board
   */
  Piece movePiece(Piece piece, Pos toPos) {
    Pos fromPos = boardState.inverse().get(piece);
    if (fromPos == null) {
      throw new IllegalArgumentException("Invalid piece: This piece is not on the board.");
    }

    Collection<Piece> needReevaluation = dependencies.getDependents(fromPos);
    needReevaluation.addAll(dependencies.getDependents(toPos));
    needReevaluation.add(piece);

    Piece capturedPiece = getPiece(toPos);
    if (capturedPiece != null) {
      boardState.inverse().remove(capturedPiece);
      needReevaluation.add(capturedPiece);
    }

    boardState.forcePut(toPos, piece);

    reevaluateAll(needReevaluation);

    return capturedPiece;
  }

  // ##################################################
  // Private helper functions
  // ##################################################

  /**
   * This is a function to mutate the `dependencies` and `moves` data structures.
   * Every time a change happens to a position (i.e.: a piece moves to it, or a
   * piece is added to it, or a piece is removed from it) all pieces that depend
   * on that position must be reevaluated, as well as the piece that got
   * added/removed/moved.
   * 
   * @param pieces that need to be reevaluated
   */
  private void reevaluateAll(Collection<Piece> pieces) {
    // Reevaluation consists of:
    // - Removing from the data structures all stored moves and dependencies from a
    // piece.
    // - Calculating all dependencies and possible moves.
    // - Adding them to the data structures.
    // Calculation is made by the piece based on the board's state.
    // Since kings can't checkmate themselves, they need to know every move from
    // every piece.
    // Therefore their calculation must be deferred.

    Collection<King> kings = new ArrayList<>();

    // TODO: the `moves` data structure is beeing changed. Pieces cannot try to call `isDangerous`, because result may be wrong.
    // TODO: The order in which kings are evaluated matters. THIS MUST BE FIXED

    // Both problems are the same. I think they can be solved if we prohibit pieces from calling `isDangerous`, and then we just filter kings' moves. 
    
    for (Piece piece : pieces) {
      moves.removeAll(piece);
      dependencies.removeAllDependencies(piece);
      if (piece instanceof King king) {
        kings.add(king);
      } else {
        try {
          var calcResult = piece.calculateMoves();
          dependencies.addAllDependencies(piece, calcResult.dependencies());
          moves.addAll(calcResult.moves());
        } catch (PieceNotInBoard e) {
          continue;
        }
      }
    }

    for (King king : kings) {
      try {
        var calcResult = king.calculateMoves();
        dependencies.addAllDependencies(king, calcResult.dependencies());
        moves.addAll(calcResult.moves());
      } catch (PieceNotInBoard e) {
        continue;
      }
    }
  }
}

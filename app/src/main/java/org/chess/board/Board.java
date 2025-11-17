package org.chess.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.chess.Color;
import org.chess.Move;
import org.chess.Move.MoveType;
import org.chess.PieceNotInBoard;
import org.chess.PieceType;
import org.chess.Pos;
import org.chess.pieces.Bishop;
import org.chess.pieces.King;
import org.chess.pieces.NonKing;
import org.chess.pieces.Piece;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Manages the relation between each piece and its position.
 */
class Board {
  // ###########################################################################
  // Data structures
  // ###########################################################################

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

  private final Map<Color, King> kingsMap = new EnumMap<>(Color.class);

  /** Match's history. */
  public final History history = new History();

  // ###########################################################################
  // Public interface
  // ###########################################################################

  public Board(Map<Pos, Piece> initialState) {
    Collection<Piece> needReevaluation = new ArrayList<>();
    for (Entry<Pos, Piece> entrySet : initialState.entrySet()) {
      needReevaluation.addAll(addPieceHelper(entrySet.getKey(), entrySet.getValue()));
    }
    reevaluate(needReevaluation);
  }

  public Collection<Move> getReadonlyMoves(Piece piece) {
    return moves.getReadonly(piece);
  }

  public Piece doMove(Move move) {
    MoveType moveType = move.type();
    Piece piece = move.piece();
    Pos toPos = move.toPos();
    Color color = piece.color;

    history.addMove(move);

    switch (moveType) {
      case SIMPLE_MOVE:
        return movePiece(piece, toPos);

      case BISHOP_PROMOTION, QUEEN_PROMOTION, ROOK_PROMOTION, KNIGHT_PROMOTION:
        Piece promotionPiece = switch (moveType) {
          case BISHOP_PROMOTION -> new Bishop(color);
          case QUEEN_PROMOTION -> new Bishop(color);
          case ROOK_PROMOTION -> new Bishop(color);
          case KNIGHT_PROMOTION -> new Bishop(color);
          default -> throw new IllegalStateException("Unexpected Enum.");
        };
        Piece takenPiece = movePiece(piece, toPos);
        removePiece(piece);
        addPiece(promotionPiece, toPos);
        return takenPiece;

      case KINGSIDE_CASTLING, QUEENSIDE_CASTLING:
        Pos rookInitialPos = switch (moveType) {
          case KINGSIDE_CASTLING -> rookInitialPos = PieceType.KINGSIDE_ROOK.initialPos(color);
          case QUEENSIDE_CASTLING -> rookInitialPos = PieceType.QUEENSIDE_ROOK.initialPos(color);
          default -> throw new IllegalStateException("Unexpected Enum.");
        };
        Pos rookCastlingPos = switch (moveType) {
          case KINGSIDE_CASTLING -> rookCastlingPos = PieceType.KINGSIDE_BISHOP.initialPos(color);
          case QUEENSIDE_CASTLING -> rookCastlingPos = PieceType.QUEEN.initialPos(color);
          default -> throw new IllegalStateException("Unexpected Enum.");
        };
        movePiece(getPiece(rookInitialPos), rookCastlingPos);
        return movePiece(piece, toPos);

      case EN_PASSANT:
        Move lastMove = history.getLastMove();
        Piece eatenPiece = getPiece(lastMove.toPos());
        removePiece(eatenPiece);
        movePiece(piece, toPos);
        return eatenPiece;

      default:
        throw new IllegalStateException("Unexpected Enum.");
    }
  }

  // ###########################################################################
  // Private read-only operations
  // ###########################################################################

  private Pos getPos(Piece piece) {
    return boardState.inverse().get(piece);
  }

  private Piece getPiece(Pos pos) {
    return boardState.get(pos);
  }

  private Function<Piece, Pos> makeGetPos(Color color) {
    return (piece) -> getPos(piece).toPerspective(color);
  }

  private Function<Piece, Pos> makeGetPos() {
    return (piece) -> getPos(piece);
  }

  private Function<Pos, Piece> makeGetPiece(Color color) {
    return (pos) -> getPiece(pos.fromPerspective(color));
  }

  private Function<Pos, Piece> makeGetPiece() {
    return (pos) -> getPiece(pos);
  }

  private Function<Color, Predicate<Pos>> makeDangerMap() {
    return color -> pos -> moves.isDangerous(pos, color);
  }

  // ###########################################################################
  // Private mutating operations
  // ###########################################################################

  /**
   * @param piece
   * @param pos
   * @throws IllegalArgumentException if parameters are null, or if there's
   *                                  already a
   *                                  piece at pos, or if the piece is already on
   *                                  the board.
   */
  private void addPiece(Piece piece, Pos pos) {
    reevaluate(addPieceHelper(pos, piece));

  }

  private Collection<Piece> addPieceHelper(Pos pos, Piece piece) {
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

    if (piece instanceof King king) {
      if (kingsMap.get(king.color) != null)
        throw new IllegalArgumentException("Invalid King: cannot add two kings with the same color.");
      kingsMap.put(king.color, king);
    }

    Collection<Piece> needReevaluation = dependencies.getDependents(pos);
    needReevaluation.add(piece);
    return needReevaluation;
  }

  /**
   * @param piece
   * @throws IllegalArgumentException if piece is not on the board
   */
  private void removePiece(Piece piece) {
    reevaluate(removePieceHelper(piece));
  }

  private Collection<Piece> removePieceHelper(Piece piece) {
    Pos pos = boardState.inverse().remove(piece);
    if (pos == null) {
      throw new IllegalArgumentException("Invalid Piece: This piece is not on the board.");
    }
    if (piece instanceof King king) {
      kingsMap.remove(king.color);
    }
    Collection<Piece> needReevaluation = dependencies.getDependents(pos);
    needReevaluation.add(piece);
    return needReevaluation;
  }

  /**
   * @param piece
   * @param toPos
   * @return The piece that was taken, if any.
   * @throws IllegalArgumentException if piece is not on the board
   */
  private Piece movePiece(Piece piece, Pos toPos) {
    Pos fromPos = boardState.inverse().get(piece);
    if (fromPos == null) {
      throw new IllegalArgumentException("Invalid piece: This piece is not on the board.");
    }

    Collection<Piece> needReevaluation = dependencies.getDependents(fromPos);
    needReevaluation.addAll(dependencies.getDependents(toPos));
    needReevaluation.add(piece);

    Piece capturedPiece = getPiece(toPos);
    if (capturedPiece != null) {
      needReevaluation.addAll(removePieceHelper(piece));
    }

    boardState.forcePut(toPos, piece);

    reevaluate(needReevaluation);

    return capturedPiece;
  }

  /**
   * This is a function to mutate the `dependencies` and `moves` data structures.
   * Every time a change happens to a position (i.e.: a piece moves to it, or a
   * piece is added to it, or a piece is removed from it) all pieces that depend
   * on that position must be reevaluated, as well as the piece that got
   * added/removed/moved.
   * 
   * @param pieces that need to be reevaluated
   */
  private void reevaluate(Collection<Piece> pieces) {
    for (Piece piece : pieces) {
      if (piece instanceof NonKing nonKing) {
        reevaluate(nonKing);
      }
    }
    // Since kings can't checkmate themselves, they need to know every move from
    // every piece. Therefore their calculation must be deferred.
    reevaluateKings();
  }

  /**
   * Reevaluation consists of:
   * - Removing from the data structures all stored moves and dependencies from a
   * piece.
   * - Calculating all dependencies and possible moves.
   * - Adding them to the data structures.
   * 
   * Since `calculateMoves` assumes the piece is from the player in the bottom,
   * the board must be rotated before sending it to the piece, and the result must
   * be rotated back.
   */
  private void reevaluate(NonKing piece) {
    moves.removeAll(piece);
    dependencies.removeAll(piece);
    try {
      var calcResult = piece.calculateMoves(
          makeGetPiece(piece.color),
          makeGetPos(piece.color));

      for (Pos pos : calcResult.dependencies()) {
        dependencies.add(piece, pos.fromPerspective(piece.color));
      }

      for (Move m : calcResult.moves()) {
        moves.add(new Move(
            m.piece(),
            m.type(),
            m.toPos().fromPerspective(piece.color)));
      }
    } catch (PieceNotInBoard e) {
      throw new IllegalStateException("Tried to reevaluate piece that's not on the board");
    }
  }

  private void reevaluateKings() {
    var kings = kingsMap.values();
    for (King king : kings) {
      moves.removeAll(king);
    }
    try {
      King.calculateMoves(kings, makeGetPiece(), makeGetPos(), makeDangerMap()).forEach(moves::add);
    } catch (PieceNotInBoard e) {
      throw new IllegalStateException("Tried to reevaluate piece that's not on the board");
    }
  }
}

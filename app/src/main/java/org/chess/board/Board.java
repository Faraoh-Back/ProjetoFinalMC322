package org.chess.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.chess.Color;
import org.chess.Move;
import org.chess.Move.MoveType;
import org.chess.exception.PieceNotInBoard;
import org.chess.PieceType;
import org.chess.Pos;
import org.chess.pieces.Bishop;
import org.chess.pieces.King;
import org.chess.pieces.NonKing;
import org.chess.pieces.Pawn;
import org.chess.pieces.Piece;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Manages the relation between each piece and its position.
 */
public class Board {
    // ###########################################################################
    // Data structures
    // ###########################################################################

    /**
     * A bi-directional map between Piece and its Pos.
     */
    private final BiMap<Pos, Piece> boardState = HashBiMap.create();

    /**
     * It is used to store pieces' moves. It should only be mutated within the
     * `reevaluate` method. Read the method's docstring to know when it must be
     * called.
     */
    private PossibleMoves moves;

    private final Map<Color, King> kingsMap = new EnumMap<>(Color.class);

    /** Match's history. */
    public final History history = new History();

    private boolean recursiveReevaluate = true;

    // ###########################################################################
    // Public interface
    // ###########################################################################

    public Board(Map<Pos, Piece> state) {
        for (Entry<Pos, Piece> entrySet : state.entrySet())
            addPiece(entrySet.getKey(), entrySet.getValue());
        reevaluate();
    }

    public Collection<Move> getReadonlyMoves(Piece piece) {
        return moves.get(piece);
    }

    public void doMove(Move move) {
        MoveType moveType = move.type();
        Piece piece = move.piece();
        Pos toPos = move.toPos();
        Color color = piece.color;

        history.addMove(move);

        switch (moveType) {
            case SIMPLE_MOVE:
                movePiece(piece, toPos);
                break;

            case PAWN_DOUBLE:
                movePiece(piece, toPos);
                break;

            case BISHOP_PROMOTION, QUEEN_PROMOTION, ROOK_PROMOTION, KNIGHT_PROMOTION:
                Piece promotionPiece = switch (moveType) {
                    case BISHOP_PROMOTION -> new Bishop(color);
                    case QUEEN_PROMOTION -> new Bishop(color);
                    case ROOK_PROMOTION -> new Bishop(color);
                    case KNIGHT_PROMOTION -> new Bishop(color);
                    default -> throw new IllegalStateException("Unexpected Enum.");
                };
                movePiece(piece, toPos);
                removePiece(piece);
                addPiece(toPos, promotionPiece);
                break;

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
                movePiece(piece, toPos);
                break;

            case EN_PASSANT:
                removePiece(move.enPassantVictim());
                movePiece(piece, toPos);
                break;

            default:
                throw new IllegalStateException("Unexpected Enum.");
        }
        reevaluate();
    }

    public Pos getPos(Piece piece) {
        return boardState.inverse().get(piece);
    }

    public Piece getPiece(Pos pos) {
        return boardState.get(pos);
    }

    // ###########################################################################
    // Private read-only operations
    // ###########################################################################

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

    private Predicate<Piece> makeMovedBefore() {
        return piece -> history.movedBefore(piece);
    }

    private Function<Color, Move> makeGetLastMove() {
        return color -> history.getLastMove(color);
    }

    private Board(Map<Pos, Piece> state, boolean recursiveReevaluate) {
        this.recursiveReevaluate = recursiveReevaluate;
        for (Entry<Pos, Piece> entrySet : state.entrySet())
            addPiece(entrySet.getKey(), entrySet.getValue());
        reevaluate();
    }

    // ###########################################################################
    // Private mutating operations
    // ###########################################################################

    /**
     * @param piece
     * @param pos
     * @throws IllegalArgumentException if there is already a piece at pos, or if
     *                                  the piece is already on the board, or if
     *                                  there is already a king with the same color.
     * @throws NullPointerException     if parameters are null.
     */
    private void addPiece(Pos pos, Piece piece) {
        Objects.requireNonNull(piece, "piece should not be null.");
        Objects.requireNonNull(pos, "pos should not be null.");

        if (boardState.get(pos) != null)
            throw new IllegalArgumentException("Invalid Position: There's already a piece at this position.");

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
    }

    /**
     * @param piece
     * @throws IllegalArgumentException if piece is not on the board.
     * @throws NullPointerException     if parameter is null.
     */
    private void removePiece(Piece piece) {
        Objects.requireNonNull(piece, "piece should not be null.");
        Pos pos = boardState.inverse().remove(piece);
        if (pos == null)
            throw new IllegalArgumentException("Invalid Piece: This piece is not on the board.");
        if (piece instanceof King king)
            kingsMap.remove(king.color);
    }

    /**
     * @param piece
     * @param toPos
     * @return The piece that was taken, if any.
     * @throws IllegalArgumentException if piece is not on the board
     */
    private Piece movePiece(Piece piece, Pos toPos) {
        Pos fromPos = boardState.inverse().get(piece);
        if (fromPos == null)
            throw new IllegalArgumentException("Invalid piece: This piece is not on the board.");
        Piece capturedPiece = getPiece(toPos);
        boardState.forcePut(toPos, piece);
        return capturedPiece;
    }

    /* This is a function to mutate the `moves` data structure. */
    private void reevaluate() {
        // Reevaluation consists of:
        // - Removing all stored moves
        // - Calculating all possible moves.
        // - Adding them to the data structures.
        // Since `calculateMoves` assumes the piece is from the player in the bottom,
        // the board must be rotated before sending it to the piece, and the result must
        // be rotated back.

        moves = new PossibleMoves();
        try {
            for (Piece piece : boardState.values()) {
                Collection<Move> newMoves;
                if (piece instanceof NonKing nonKing)
                    newMoves = nonKing.calculateMoves(makeGetPiece(piece.color), makeGetPos(piece.color));
                else if (piece instanceof Pawn pawn)
                    newMoves = pawn.calculateMoves(makeGetPiece(piece.color), makeGetPos(piece.color),
                            makeGetLastMove());
                else
                    continue;

                for (Move m : newMoves)
                    moves.add(
                            new Move(m.piece(), m.type(), m.toPos().fromPerspective(piece.color), m.enPassantVictim()));
            }
            // Since kings can't checkmate themselves, they need to know every move from
            // every piece. Therefore their calculation must be deferred.
            King.calculateMoves(kingsMap.values(), makeGetPiece(), makeGetPos(), makeDangerMap(), makeMovedBefore())
                    .forEach(moves::add);

            if (recursiveReevaluate)
                preventMovesIfInCheck();

            for (King king : kingsMap.values()) {
                Color color = king.color;
                if (moves.hasNoMoves(color)) {
                    remove(color);
                }
            }

        } catch (PieceNotInBoard e) {
            throw new IllegalStateException("This should not run. Tried to reevaluate piece that's not on the board");
        }
    }

    private void preventMovesIfInCheck() {
        for (King king : kingsMap.values()) {
            Color color = king.color;
            Pos kingPos = getPos(king);
            if (moves.isDangerous(kingPos, color)) {
                Collection<Move> playerMoves = moves.getAllMoves(color);
                for (Move move : playerMoves) {
                    Board hypotheticalBoard = new Board(boardState, false);
                    hypotheticalBoard.doMove(move);
                    Pos hypotheticalKingPos = hypotheticalBoard.getPos(king);
                    if (hypotheticalBoard.moves.isDangerous(hypotheticalKingPos, color)) {
                        moves.remove(move);
                    }
                }
            }
        }
    }

    public boolean isCheckmate(Color currentTurn) {
        return moves.hasNoMoves(currentTurn);
    }

    public void remove(Color color) {
        for (Piece piece : getPieces(color)) {
            if (piece.color == color) {
                removePiece(piece);
            }
        }
        kingsMap.remove(color);
    }

    private Collection<Piece> getPieces(Color color) {
        Collection<Piece> pieces = new ArrayList<>();
        for (Piece piece : boardState.values()) {
            if (piece.color == color) {
                pieces.add(piece);
            }
        }
        return pieces;
    }
}

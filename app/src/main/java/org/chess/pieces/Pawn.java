package org.chess.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.chess.Color;

import org.chess.Move;
import org.chess.Pos;
import org.chess.Move.MoveType;
import org.chess.exception.InvalidPosition;
import org.chess.exception.PieceNotInBoard;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    public Collection<Move> calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos,
            Function<Color, Move> getLastMove) throws PieceNotInBoard {

        // Checks if piece is on the board
        Pos thisPos = getPos.apply(this);
        if (thisPos == null)
            throw new PieceNotInBoard();

        // This will be the MovesCalcResult atributes
        ArrayList<Move> validMoves = new ArrayList<Move>();

        enPassantMove(getPiece, getLastMove, validMoves, thisPos);
        doubleMove(getPiece, validMoves, thisPos);
        addForwardMove(getPiece, validMoves, thisPos);
        addCaptureMove(getPiece, validMoves, thisPos);

        return validMoves;
    }

    // ###########################################################################
    // En Passant logic
    // ###########################################################################

    private void enPassantMove(Function<Pos, Piece> getPiece, Function<Color, Move> getLastMove,
            ArrayList<Move> validMoves, Pos thisPos) {

        for (var disposition : getEnPassantDisposition(thisPos)) {
            Move enPassantMove = checkDisposition(thisPos, getPiece, getLastMove, disposition);
            if (enPassantMove == null)
                continue;
            validMoves.add(enPassantMove);
        }
    }

    record EnPassantDisposition(Pos victimPos, Color victimColor, Pos movePos) {
    }

    private Collection<EnPassantDisposition> getEnPassantDisposition(Pos thisPos) {
        Collection<EnPassantDisposition> positions = new ArrayList<>();
        try {
            positions.add(
                    new EnPassantDisposition(thisPos.top(), this.color.getLeftColor(), thisPos.topLeft()));
        } catch (InvalidPosition e) {
        }
        try {
            positions.add(
                    new EnPassantDisposition(thisPos.top(), this.color.getRightColor(), thisPos.topRight()));
        } catch (InvalidPosition e) {
        }
        try {
            positions.add(
                    new EnPassantDisposition(thisPos.left(), this.color.getFrontColor(), thisPos.topLeft()));
        } catch (InvalidPosition e) {
        }
        try {
            positions.add(
                    new EnPassantDisposition(thisPos.right(), this.color.getFrontColor(), thisPos.topRight()));
        } catch (InvalidPosition e) {
        }
        return positions;
    }

    private Move checkDisposition(Pos thisPos, Function<Pos, Piece> getPiece,
            Function<Color, Move> getLastMove, EnPassantDisposition disposition) {
        Piece victimPiece = getPiece.apply(disposition.victimPos);

        if (victimPiece == null
                || victimPiece.color != disposition.victimColor
                || !(victimPiece instanceof Pawn victimPawn))
            return null;

        Move lastMove = getLastMove.apply(victimPiece.color);
        if (lastMove == null || lastMove.type() != MoveType.PAWN_DOUBLE || lastMove.piece() != victimPawn)
            return null;

        if (getPiece.apply(disposition.movePos) == null)
            return new Move(this, MoveType.EN_PASSANT, disposition.movePos, victimPawn);

        return null;

        // TODO: Although extremely rare, an en-passant move could be a promotion one,
        // and we are ignoring this case.
    }

    // ###########################################################################
    // Other Moves
    // ###########################################################################

    private void doubleMove(Function<Pos, Piece> getPiece, ArrayList<Move> validMoves, Pos thisPos) {
        if (thisPos.row() == 13) {
            Pos movementPos = thisPos.top().top();
            if (getPiece.apply(movementPos) == null)
                validMoves.add(new Move(this, MoveType.PAWN_DOUBLE, movementPos));
        }
    }

    private void addCaptureMove(Function<Pos, Piece> getPiece, Collection<Move> validMoves, Pos thisPos) {
        Collection<Pos> positions = new ArrayList<>(2);
        try {
            positions.add(thisPos.topLeft());
        } catch (InvalidPosition e) {
        }
        try {
            positions.add(thisPos.topRight());
        } catch (InvalidPosition e) {
        }

        for (Pos movePos : positions) {
            Piece pieceInPos = getPiece.apply(movePos);
            if (pieceInPos != null && pieceInPos.color != this.color)
                checkPromotionAndAddMove(getPiece, validMoves, movePos);
        }
    }

    private void addForwardMove(Function<Pos, Piece> getPiece, Collection<Move> validMoves, Pos thisPos) {
        try {
            Piece pieceInPos = getPiece.apply(thisPos.top());
            if (pieceInPos == null)
                checkPromotionAndAddMove(getPiece, validMoves, thisPos.top());
        } catch (InvalidPosition e) {
        }
    }

    private void checkPromotionAndAddMove(Function<Pos, Piece> getPiece, Collection<Move> validMoves, Pos movePos) {
        if (movePos.row() == 1) {
            validMoves.add(new Move(this, MoveType.QUEEN_PROMOTION, movePos));
            validMoves.add(new Move(this, MoveType.KNIGHT_PROMOTION, movePos));
            validMoves.add(new Move(this, MoveType.ROOK_PROMOTION, movePos));
            validMoves.add(new Move(this, MoveType.BISHOP_PROMOTION, movePos));
        } else {
            validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, movePos));
        }
    }
}

package org.chess.pieces;

import java.util.ArrayList;
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

  public MovesCalcResult calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos, Move lastMove)
      throws PieceNotInBoard {

    // Checks if piece is on the board
    Pos thisPos = getPos.apply(this);
    if (thisPos == null) {
      throw new PieceNotInBoard();
    }

    // This will be the MovesCalcResult atributes
    ArrayList<Move> validMoves = new ArrayList<Move>();
    ArrayList<Pos> dependencies = new ArrayList<Pos>();

    // getting this piece's positionu
    int row = thisPos.row();
    int column = thisPos.column();

    // En-passant check
    enPassantMove(getPiece, lastMove, validMoves, dependencies, row, column);

    // Double-step check
    doubleMove(getPiece, validMoves, dependencies, row, column);

    // Simple Move check
    simpleMove(getPiece, validMoves, dependencies, row, column);

    // Left Attack-Move check
    leftMove(getPiece, validMoves, dependencies, row, column);

    // Right Attack-Move check
    rightMove(getPiece, validMoves, dependencies, row, column);

    return new MovesCalcResult(validMoves, dependencies);
  }

  private void enPassantMove(Function<Pos, Piece> getPiece, Move lastMove, ArrayList<Move> validMoves,
      ArrayList<Pos> dependencies, int row, int column) {

    // checks if last move was a double pawn move
    if (lastMove.type() != MoveType.PAWN_DOUBLE)
      return;
    Piece movedPiece = lastMove.piece();

    // checks if the moved piece is the color in the front
    if (color.getFrontColor() == movedPiece.color) {
      // checking right-side en-passant
      try {
        Pos rightPos = new Pos(row, column + 1);
        if (getPiece.apply(rightPos) == movedPiece) {
          Pos movePos = new Pos(row - 1, column + 1);
          if (getPiece.apply(movePos) == null)
            validMoves.add(new Move(this, MoveType.EN_PASSANT, movePos, movedPiece));
        }
      } catch (InvalidPosition e) {
      }

      // checking left-side en-passant
      try {
        Pos rightPos = new Pos(row, column - 1);
        if (getPiece.apply(rightPos) == movedPiece) {
          Pos movePos = new Pos(row - 1, column - 1);
          if (getPiece.apply(movePos) == null)
            validMoves.add(new Move(this, MoveType.EN_PASSANT, movePos, movedPiece));
        }
      } catch (InvalidPosition e) {
      }

      // checking en-passant on the side colors
      // TODO: check null Pos
    } else {
      Pos frontPos = new Pos(row - 1, column);
      if (getPiece.apply(frontPos) == movedPiece) {
        if (color.getLeftColor() == movedPiece.color) { // checking left-side en-passant
          Pos movePos = new Pos(row - 1, column - 1);
          if (getPiece.apply(movePos) == null)
            validMoves.add(new Move(this, MoveType.EN_PASSANT, movePos, movedPiece));
        } else if (color.getRightColor() == movedPiece.color) { // checking right-side en-passant
          Pos movePos = new Pos(row - 1, column + 1);
          if (getPiece.apply(movePos) == null)
            validMoves.add(new Move(this, MoveType.EN_PASSANT, movePos, movedPiece));
        }
      }
    }
    // Add final dependencies that will be needed anyways for checking en-passant
    try {
        dependencies.add(new Pos(row, column + 1));
    } catch (InvalidPosition e) {}
    
    try {
        dependencies.add(new Pos(row, column - 1));
    } catch (InvalidPosition e) {}
  }

  private void doubleMove(Function<Pos, Piece> getPiece, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    if (row == 13) {
      Pos movementPos = new Pos(row - 2, column);
      dependencies.add(movementPos);
      if (getPiece.apply(movementPos) == null) {
        validMoves.add(new Move(this, MoveType.PAWN_DOUBLE, movementPos));
      }
    }
  }

  private void rightMove(Function<Pos, Piece> getPiece, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    try {
      Pos movementPos = new Pos(row - 1, column + 1);
      dependencies.add(movementPos);
      Piece pieceInPos = getPiece.apply(movementPos);

      if (pieceInPos != null && pieceInPos.color != super.color) {
        if (row == 2) {
          validMoves.add(new Move(this, MoveType.QUEEN_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.KNIGHT_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.ROOK_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.BISHOP_PROMOTION, movementPos));
        } else {
          validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, movementPos));
        }
      }
    } catch (InvalidPosition e) {
    }
  }

  private void leftMove(Function<Pos, Piece> getPiece, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    try {
      Pos movementPos = new Pos(row - 1, column - 1);
      dependencies.add(movementPos);
      Piece pieceInPos = getPiece.apply(movementPos);

      if (pieceInPos != null && pieceInPos.color != super.color) {
        if (row == 2) {
          validMoves.add(new Move(this, MoveType.QUEEN_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.KNIGHT_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.ROOK_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.BISHOP_PROMOTION, movementPos));
        } else {
          validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, movementPos));
        }
      }
    } catch (InvalidPosition e) {
    }
  }

  private void simpleMove(Function<Pos, Piece> getPiece, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    try {
      Pos movementPos = new Pos(row - 1, column);
      Piece pieceInPos = getPiece.apply(movementPos);
      dependencies.add(movementPos);
      if (pieceInPos == null) {
        if (row == 2) {
          validMoves.add(new Move(this, MoveType.QUEEN_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.KNIGHT_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.ROOK_PROMOTION, movementPos));
          validMoves.add(new Move(this, MoveType.BISHOP_PROMOTION, movementPos));
        } else {
          validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, movementPos));
        }
      }
    } catch (InvalidPosition e) {
    }
  }
}

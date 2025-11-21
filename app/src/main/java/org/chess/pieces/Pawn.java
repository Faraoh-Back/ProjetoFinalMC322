package org.chess.pieces;

import java.util.ArrayList;
import java.util.List;

import org.chess.Color;

import com.google.common.collect.BiMap;

import org.chess.Move;
import org.chess.PieceNotInBoard;
import org.chess.Pos;
import org.chess.Move.MoveType;
import org.chess.board.History;

public class Pawn extends Piece {
  public Pawn(Color color) {
    super(color);
  }

  public MovesCalcResult calculateMoves(BiMap<Pos, Piece> boardState, History gameHistory) throws PieceNotInBoard {

    // Checks if piece is on the board
    Pos thisPos = boardState.inverse().get(this);
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
    enPassantMove(boardState, gameHistory, validMoves, dependencies, row, column);

    // Double-step check
    doubleMove(boardState, validMoves, dependencies, row, column);

    // Simple Move check
    simpleMove(boardState, validMoves, dependencies, row, column);

    // Left Attack-Move check
    leftMove(boardState, validMoves, dependencies, row, column);

    // Right Attack-Move check
    rightMove(boardState, validMoves, dependencies, row, column);

    return new MovesCalcResult(validMoves, dependencies);
  }

  private void enPassantMove(BiMap<Pos, Piece> boardState, History gameHistory, ArrayList<Move> validMoves,
      ArrayList<Pos> dependencies, int row, int column) {

    List<Move> LastMoves = gameHistory.getMovesView();
    Move lastMove = LastMoves.get(LastMoves.size() - 1);

    // checks if last move was a double pawn move
    if (lastMove.type() == MoveType.PAWN_DOUBLE) {
      Piece movedPiece = lastMove.piece();
      Pos tempPos;

      // checks if the moved piece is the color in the front
      if (super.color.getFrontColor() == movedPiece.color) {
        // checking right-side en-passant
        tempPos = new Pos(row, column + 1);
        if (boardState.get(tempPos) == movedPiece) {
          validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row - 1, column + 1)));
        }

        // checking left-side en-passant
        tempPos = new Pos(row, column - 1);
        if (boardState.get(tempPos) == movedPiece) {
          validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row - 1, column - 1)));
        }
        // checking en-passant on the side colors
      } else {
        tempPos = new Pos(row - 1, column);
        if (boardState.get(tempPos) == movedPiece) {
          if (super.color.getLeftColor() == movedPiece.color) {
            validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row - 1, column - 1)));
          } else {
            validMoves.add(new Move(this, MoveType.EN_PASSANT, new Pos(row - 1, column + 1)));
          }
        }
      }
    }
    // Add final dependencies that will be needed anyways for checking en-passant
    dependencies.add(new Pos(row, column + 1));
    dependencies.add(new Pos(row, column - 1));
  }

  private void doubleMove(BiMap<Pos, Piece> boardState, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    if (row == 13) {
      Pos movementPos = new Pos(row - 2, column);
      dependencies.add(movementPos);
      if (boardState.get(movementPos) == null) {
        validMoves.add(new Move(this, MoveType.PAWN_DOUBLE, movementPos));
      }
    }
  }

  private void rightMove(BiMap<Pos, Piece> boardState, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    try {
      Pos movementPos = new Pos(row - 1, column + 1);
      dependencies.add(movementPos);
      Piece pieceInPos = boardState.get(movementPos);

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
    } catch (IllegalArgumentException e) {
    }
  }

  private void leftMove(BiMap<Pos, Piece> boardState, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    try {
      Pos movementPos = new Pos(row - 1, column - 1);
      dependencies.add(movementPos);
      Piece pieceInPos = boardState.get(movementPos);

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
    } catch (IllegalArgumentException e) {
    }
  }

  private void simpleMove(BiMap<Pos, Piece> boardState, ArrayList<Move> validMoves, ArrayList<Pos> dependencies,
      int row, int column) {
    try {
      Pos movementPos = new Pos(row - 1, column);
      Piece pieceInPos = boardState.get(movementPos);
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
    } catch (IllegalArgumentException e) {
    }
  }
}

package org.chess.pieces;

import java.util.ArrayList;
import java.util.function.Function;

import org.chess.Color;
import org.chess.Move;
import org.chess.Move.MoveType;
import org.chess.PieceNotInBoard;
import org.chess.Pos;

public class Knight extends NonKing {

  public Knight(Color color) {
    super(color);
  }

public MovesCalcResult calculateMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos) throws PieceNotInBoard {
  // Checks if piece is on the board
    Pos thisPos = getPos.apply(this);
    if (thisPos == null) {
      throw new PieceNotInBoard();
    }

    //This will be the MovesCalcResult atributes
    ArrayList<Move> validMoves = new ArrayList<Move>();
    ArrayList<Pos> dependencies = new ArrayList<Pos>();

    //getting this piece's position
    int row = thisPos.row();
    int column = thisPos.column();
    
    int[][] possibleMoves = {
          {row + 3, column + 1},
          {row + 3, column - 1},
          {row - 3, column + 1},
          {row - 3, column - 1},
          {row + 1, column + 3},
          {row - 1, column + 3},
          {row + 1, column - 3},
          {row - 1, column - 3}
      };

      //creating a list of valid positions in the board

    // Checks if those positions would generate validMoves,
    // then, fills validMoves and piecesBlockingMoves
    for (int[] pos : possibleMoves) {
      try {
        Pos tempPos = new Pos(pos[0], pos[1]);
        dependencies.add(tempPos);
        Piece pieceInPos = getPiece.apply(tempPos);
        if (pieceInPos == null || pieceInPos.color != super.color) {
          validMoves.add(new Move(this, MoveType.SIMPLE_MOVE, tempPos));
        }
      } catch (IllegalArgumentException e) {
      }
    }

      return new MovesCalcResult(validMoves, dependencies);
      
  }
}

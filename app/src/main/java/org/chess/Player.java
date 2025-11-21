package org.chess;

import java.util.EnumMap;
import java.util.Map;

import org.chess.pieces.Piece;
import org.chess.pieces.Queen;
import org.chess.pieces.Rook;
import org.chess.pieces.Bishop;
import org.chess.pieces.King;
import org.chess.pieces.Knight;
import org.chess.pieces.Pawn;

public class Player{
  public final Map<PieceType, Piece> pieces = new EnumMap<>(PieceType.class);
  public final Clock clock;
  public final Color color;

  public Player(Clock clock, Color color) {
    this.clock = clock;
    this.color = color;

    for (PieceType pieceType: PieceType.values()) {
      Rook kingsideRook = new Rook(color);
      Rook queensideRook = new Rook(color);
      pieces.put(pieceType, switch (pieceType) {
        case QUEENSIDE_BISHOP, KINGSIDE_BISHOP -> new Bishop(color);
        case QUEENSIDE_ROOK -> queensideRook;
        case KINGSIDE_ROOK -> kingsideRook;
        case QUEENSIDE_KNIGHT, KINGSIDE_KNIGHT -> new Knight(color);
        case QUEEN -> new Queen(color);
        case KING -> new King(color, kingsideRook, queensideRook);
        default -> new Pawn(color);
      });
    }
  }

}

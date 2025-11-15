package org.chess;

import org.chess.pieces.Piece;

/**
 * Move
 */
public record Move(Piece piece, MoveType type, Pos movingTo) {
  public enum MoveType {
    EN_PASSANT,
    KINGSIDE_CASTLING,
    QUEENSIDE_CASTLING,
    QUEEN_PROMOTION,
    ROOK_PROMOTION,
    BISHOP_PROMOTION,
    KNIGHT_PROMOTION,
    SIMPLE_MOVE,
  }
}

package org.chess;

import org.chess.pieces.Piece;

/**
 * Move agora contém a referência explícita para a peça capturada via En Passant.
 */
public record Move(Piece piece, MoveType type, Pos toPos, Piece enPassantVictim) {
  public Move(Piece piece, MoveType type, Pos toPos) {
    this(piece, type, toPos, null);
  }

  public enum MoveType {
    EN_PASSANT,
    PAWN_DOUBLE,
    KINGSIDE_CASTLING,
    QUEENSIDE_CASTLING,
    QUEEN_PROMOTION,
    ROOK_PROMOTION,
    BISHOP_PROMOTION,
    KNIGHT_PROMOTION,
    SIMPLE_MOVE,
  }
}
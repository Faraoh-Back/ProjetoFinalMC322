package org.chess;

public enum PieceType {
  QUEENSIDE_ROOK(new Pos(14, 4)),
  QUEENSIDE_KNIGHT(new Pos(14, 5)),
  QUEENSIDE_BISHOP(new Pos(14, 6)),
  QUEEN(new Pos(14, 7)),
  KING(new Pos(14, 8)),
  KINGSIDE_BISHOP(new Pos(14, 9)),
  KINGSIDE_KNIGHT(new Pos(14, 10)),
  KINGSIDE_ROOK(new Pos(14, 11)),

  QUEENSIDE_ROOK_PAWN(new Pos(13, 4)),
  QUEENSIDE_KNIGHT_PAWN(new Pos(13, 5)),
  QUEENSIDE_BISHOP_PAWN(new Pos(13, 6)),
  QUEEN_PAWN(new Pos(13, 7)),
  KING_PAWN(new Pos(13, 8)),
  KINGSIDE_BISHOP_PAWN(new Pos(13, 9)),
  KINGSIDE_KNIGHT_PAWN(new Pos(13, 10)),
  KINGSIDE_ROOK_PAWN(new Pos(13, 11));

  private Pos initialPos;

  private PieceType(Pos initialPos) {
    this.initialPos = initialPos;
  }

  public Pos initialPos(Color color) {
    return initialPos.fromPerspective(color);
  }
}

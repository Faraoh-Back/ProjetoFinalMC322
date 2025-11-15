package org.chess;

import java.util.List;

import org.chess.pieces.Piece;
import org.chess.pieces.King;

public record Player(List<Piece> pieces, King king, Clock clock, Color color){
}

package org.chess;

import java.util.List;

import org.chess.pieces.King;
import org.chess.pieces.Piece;

public record Player(List<Piece> pieces, King king, Clock clock, Color color){
}

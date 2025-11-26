package org.chess.pieces;

import org.chess.Color;

public abstract class Piece {
    public final Color color;

    public Piece(Color color) {
        this.color = color;
    }
}

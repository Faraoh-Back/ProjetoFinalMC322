package org.chess.pieces;

import java.io.Serializable;
import org.chess.Color;

public abstract class Piece implements Serializable {
    public final Color color;

    public Piece(Color color) {
        this.color = color;
    }
}

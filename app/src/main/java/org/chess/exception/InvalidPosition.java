package org.chess.exception;

public class InvalidPosition extends IllegalArgumentException {

    public InvalidPosition(String string) {
        super(string);
    }
}

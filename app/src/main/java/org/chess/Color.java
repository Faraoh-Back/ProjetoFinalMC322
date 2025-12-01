package org.chess;

public enum Color implements java.io.Serializable {
  GREEN(false),
  RED(false),
  YELLOW(true),
  BLUE(true);

  public final boolean queenToTheLeftOfKing;

  private Color(boolean queenToTheLeftOfKing) {
    this.queenToTheLeftOfKing = queenToTheLeftOfKing;
  }

  // returns the color in the front
  public Color getFrontColor() {
    return switch (this) {
      case GREEN -> RED;
      case YELLOW -> BLUE;
      case RED -> GREEN;
      case BLUE -> YELLOW;
    };
  }

  // returns the color to the left
  public Color getLeftColor() {
    return switch (this) {
      case GREEN -> YELLOW;
      case YELLOW -> RED;
      case RED -> BLUE;
      case BLUE -> GREEN;
    };
  }


  // returns the color to the right
  public Color getRightColor() {
    return switch (this) {
      case GREEN -> BLUE;
      case YELLOW -> GREEN;
      case RED -> YELLOW;
      case BLUE -> RED;
    };
  }
}

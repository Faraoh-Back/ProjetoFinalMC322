package org.chess;

public enum Color {
  GREEN,
  RED,
  YELLOW,
  BLUE;

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

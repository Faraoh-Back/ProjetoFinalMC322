package org.chess;

import java.util.ArrayList;
import java.util.List;

public record Pos(int row, int column) {
  public Pos {
    if (row < 1 || column < 1 || row > 14 || column > 14 || (row < 4 || row > 11) && (column < 4 || column > 11)) {
      throw new IllegalArgumentException("Invalid position");
    }
  }

  public static List<Pos> getValidPositions() {
    List<Pos> list = new ArrayList<Pos>();
    for (int i = 1; i <= 14; i++) {
      for (int j = 1; j <= 14; j++) {
        try {
          list.add(new Pos(i, j));
        } catch (IllegalArgumentException e1) {
        }
      }
    }
    return list;
  }

  /**
   * Transforms a position from the board's perspective to the equivalent position
   * from the color's perspective.
   *
   * Board Layout Context:
   * The board is a 14x14 grid with a 3x3 square removed from each of the four
   * corners.
   * The sides are associated with player colors as follows:
   * - RED: Top side (Row 1)
   * - YELLOW: Left side (Column 1)
   * - BLUE: Right side (Column 14)
   * - GREEN: Bottom side (Row 14)
   *
   * @param color whose perspective is being converted to.
   * @return The transformed position as if color were at the bottom (Row 14).
   */
  public Pos relativeTo(Color color) {
    return rotateClockwise(
        switch (color) {
          case RED -> 2;
          case YELLOW -> 3;
          case GREEN -> 0;
          case BLUE -> 1;
          default -> throw new IllegalStateException("Unexpected enum.");
        });
  }

  /**
   * Transforms a position from the color's perspective to the equivalent position
   * from the board's perspective.
   *
   * Board Layout Context:
   * The board is a 14x14 grid with a 3x3 square removed from each of the four
   * corners.
   * The sides are associated with player colors as follows:
   * - RED: Top side (Row 1)
   * - YELLOW: Left side (Column 1)
   * - BLUE: Right side (Column 14)
   * - GREEN: Bottom side (Row 14)
   *
   * @param color The color whose perspective is being converted from.
   * @return The transformed position form the board's perpective.
   */
  public Pos fromPerspective(Color color) {
    return rotateClockwise(
        switch (color) {
          case RED -> 2;
          case YELLOW -> 1;
          case GREEN -> 0;
          case BLUE -> 3;
          default -> throw new IllegalStateException("Unexpected enum.");
        });
  }

  private Pos rotateClockwise(int nOfRotations) {
    nOfRotations %= 4;
    return switch (nOfRotations) {
      case 0 -> this;
      case 1 -> new Pos(this.column, 15 - this.row);
      case 2 -> new Pos(15 - this.row, 15 - this.column);
      case 3 -> new Pos(15 - this.column, this.row);
      default -> throw new IllegalStateException("Unexpected remainder.");
    };
  }

}

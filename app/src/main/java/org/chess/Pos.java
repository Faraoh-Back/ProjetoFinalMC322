package org.chess;

import java.util.ArrayList;
import java.util.List;

public record Pos(int row, int column) {
  public Pos {
    if (row < 1
        || column < 1
        || row > 14
        || column > 14
        || (row < 4 || row > 11) && (column < 4 || column > 11)) {
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
}

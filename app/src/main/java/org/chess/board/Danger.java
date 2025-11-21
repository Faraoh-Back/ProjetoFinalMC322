package org.chess.board;

import org.chess.Move;
import org.chess.Color;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

class Danger extends EnumMap<Color, List<Move>> {
	public Danger() {
		super(Color.class);
    for (Color color: Color.values()) {
      put(color, new ArrayList<Move>()); // map initialization with default values
    }
	}

	public boolean isInDanger(Color color) {
	  for (Color color2: Color.values()) {
	    if (color != color2 && get(color2).size() > 0) {
	      return true;
	    }
	  }
	  return false;
	}
}

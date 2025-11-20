package org.chess;

import java.util.ArrayList;
import java.util.List;

import org.chess.board.Board;

public class King extends Piece {

	public King(Color color, Board board) {
		super(color, board);
		//TODO
	}

	@Override
	public MovesCalcResult calculateMoves() {
        List<Move> moves = new ArrayList<>();
        List<Piece> blocking = new ArrayList<>();

        Pos current;
        try {
            current = board.getPos(this);
        } catch (Exception e) {
            // posição desconhecida -> nenhum movimento
            return new MovesCalcResult(moves, blocking);
        }

        int[] delta = {-1, 0, 1};
        for (int dr : delta) {
            for (int dc : delta) {
                if (dr == 0 && dc == 0) continue;
                int new_row = current.row() + dr;
                int new_column = current.column() + dc;
                try {
                    Pos p = new Pos(new_row, new_column);
                    Piece dest = null;
                    try {
                        dest = board.getPiece(p);
                    } catch (Exception ignored) {
						// Board piece ainda nao implementado
                    }
                    if (dest == null) {
                        moves.add(new Move(this, Move.MoveType.SIMPLE_MOVE, p));
                    } else if (dest.color != this.color) {
                        // captura possível
                        moves.add(new Move(this, Move.MoveType.SIMPLE_MOVE, p));
                    } else {
						// movimento bloqueado -> mesma cor
                        blocking.add(dest);
                    }
                } catch (IllegalArgumentException ex) {
                    // posição inválida (fora do tabuleiro) -> ignorar
                }
            }
        }

        return new MovesCalcResult(moves, blocking);
    }

}

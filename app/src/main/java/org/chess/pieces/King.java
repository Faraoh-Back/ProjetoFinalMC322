package org.chess.pieces;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.Map;

import com.google.common.collect.BiMap;

import org.chess.Color;
import org.chess.board.Board;
import org.chess.Pos;
import org.chess.Move.MoveType;
import org.chess.PieceNotInBoard;
import org.chess.Move;

public class King extends Piece {
	private boolean castlingEnable = true;
	

	public King(Color color) {
		super(color);
	}

	public static MovesCalcResult calculateMoves(Collection<King> kings, BiMap<Pos, Piece> boardState, Map<Color, Predicate<Pos>> dangerMap) throws PieceNotInBoard{
		Piece kingSideRook;
		Piece queenSideRook;
		int row;
		int column;
		int[] directionHelper;

		//Normal King Move
		int[][] possibleMoves = {
			{row+1, column},
			{row+1, column+1},
			{row, column+1},
			{row-1, column+1},
			{row-1, column},
			{row-1, column-1},
			{row, column-1},
			{row+1, column-1}
		};
		for(int[] pos : possibleMoves){
			try{
				Pos tempPos = new Pos(pos[0], pos[1]);
				Piece pieceInPos = boardState.getPiece(tempPos);
				if(pieceInPos != null){
					if(pieceInPos.color == .color){
						piecesBlockingMoves.add(pieceInPos);
					}else{
						checkForDanger(MoveType.SIMPLE_MOVE, tempPos);
					}
				}else{
					checkForDanger(MoveType.SIMPLE_MOVE, tempPos);
				}
				
			}catch(Exception e){}
		}


		return new MovesCalcResult(validMoves, piecesBlockingMoves);
	}
	/**
	 * Verifies if the king would be in danger if moved
	 * to a position, if not, add valid move to that position,
	 * if yes, adds pieces that put king in danger to piecesBlockingMove
	 * @param type
	 * @param pos
	 */
	private void checkForDanger(MoveType type, Pos pos){
		
	}

	private boolean checkCastling(){

	}

}

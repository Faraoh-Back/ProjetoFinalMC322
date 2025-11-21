package org.chess.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.Map;
import java.util.List;

import com.google.common.collect.BiMap;

import org.chess.Color;
import org.chess.board.History;
import org.chess.Pos;
import org.chess.Move.MoveType;
import org.chess.PieceNotInBoard;
import org.chess.Move;

public class King extends Piece {
	public boolean castlingEnable = true;
	public boolean inCheck = false;
	public Piece kingSideRook;
	public Piece queenSideRook;
	

	public King(Color color) {
		super(color);
	}

	public static Collection<Move> calculateMoves(Collection<King> kings, BiMap<Pos, Piece> boardState, Map<Color, Predicate<Pos>> dangerMap, History gameHistory) throws PieceNotInBoard{
		Collection<Move> validMoves = new ArrayList<Move>();
		
		Pos thisPos;
		int row;
		int column;
		
		//Loop to go through the kings
		for(King king : kings){

			//verify if king is in the board
			thisPos = boardState.inverse().get(king);
			if(thisPos == null){
				continue;
			}

			//Get king's position
			row = thisPos.row();
			column = thisPos.column();

			//Verify if king is in check
			//TODO: verificar se esta verificação está de acordo coma lógica do programa
			if(dangerMap.get(king.color).test(thisPos)){
				king.inCheck = true;
			}else{
				king.inCheck = false;
			}

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
					Pos movementPos = new Pos(pos[0], pos[1]);
					Piece pieceInPos = boardState.get(movementPos);
					if(pieceInPos == null || pieceInPos.color != king.color){
						//TODO: verificar se esta verificação está de acordo coma lógica do programa
						if(!dangerMap.get(king.color).test(movementPos)){ 
							//entraria aqui se a posição fosse segura
							validMoves.add(new Move(king, MoveType.SIMPLE_MOVE, movementPos));
						}
					}
				}catch(Exception e){}
			}

			//Castling
			if(king.castlingEnable && !king.inCheck){
				List<Move> kingMoves = gameHistory.getMovesView(king);
				//verifies if king moved
				if(kingMoves.isEmpty()){
					List<Move> kingSideMoves = gameHistory.getMovesView(king.kingSideRook);
					List<Move> queenSideMoves = gameHistory.getMovesView(king.queenSideRook);
					//verifies if both rooks moved
					if(!kingSideMoves.isEmpty() && !queenSideMoves.isEmpty()){
						king.castlingEnable = false;
						continue;
					}
					//TODO: improve castling verification implementation
					if(kingSideMoves.isEmpty()){
						//verifies poorly if there is no pieces between king and rook and if there is danger
						Pos firstSquare = new Pos(row, column+1);
						Pos secondSquare = new Pos(row, column+2);
						if(boardState.get(firstSquare) == null && boardState.get(secondSquare) == null){
							if(!dangerMap.get(king.color).test(firstSquare) && !dangerMap.get(king.color).test(secondSquare)){
								validMoves.add(new Move(king, MoveType.KINGSIDE_CASTLING, secondSquare));
							}
						}
					}
					if(queenSideMoves.isEmpty()){
						//verifies poorly if there is no pieces between king and rook and if there is danger
						Pos firstSquare = new Pos(row, column-1);
						Pos secondSquare = new Pos(row, column-2);
						Pos thirdSquare = new Pos(row, column -3);
						if(boardState.get(firstSquare) == null && boardState.get(secondSquare) == null && boardState.get(thirdSquare) == null){
							if(!dangerMap.get(king.color).test(firstSquare) && !dangerMap.get(king.color).test(secondSquare) && !dangerMap.get(king.color).test(thirdSquare)){
								validMoves.add(new Move(king, MoveType.QUEENSIDE_CASTLING, secondSquare));
							}
						}
					}
				}else{
					king.castlingEnable = false;
				}
			}

		}
		return validMoves;
	}

	//Atributes to King his rooks, this facilitates castling implementation
	public void setKingRooks(Rook kingSide, Rook queenSide){
		this.kingSideRook = kingSide;
		this.queenSideRook = queenSide;
	}
}

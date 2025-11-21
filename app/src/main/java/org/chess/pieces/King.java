package org.chess.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.chess.Color;
import org.chess.Move;
import org.chess.Move.MoveType;
import org.chess.PieceNotInBoard;
import org.chess.Pos;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class King extends Piece {
    private final Rook kingSideRook;
    private final Rook queenSideRook;

    public King(Color color, Rook kingsideRook, Rook queensideRook) {
        super(color);
        this.kingSideRook = kingsideRook;
        this.queenSideRook = queensideRook;
    }

    public static Collection<Move> calculateMoves(Collection<King> kings, Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos,
            Map<Color, Predicate<Pos>> getDangerMap, Predicate<Piece> hasMoves) throws PieceNotInBoard {
        Multimap<Pos, King> simpleMoves = HashMultimap.create();
        for (King king : kings) {
            for (Pos pos : king.getSimpleMoves(getPiece, getPos, getDangerMap.get(king.color))) {
                simpleMoves.put(pos, king);
            }
        }
        Collection<Move> moves = new ArrayList<>();
        Map<Pos, King> validSimpleMoves = filterIntersectingSimpleMoves(simpleMoves);
        validSimpleMoves.forEach((pos, king) -> moves.add(new Move(king, MoveType.SIMPLE_MOVE, pos)));
        for (King king : kings) {
            Predicate<Pos> dangerMap = getDangerMap.get(king.color);
            Predicate<Pos> patchedDangerMap = pos -> dangerMap.test(pos) || validSimpleMoves.get(pos) != king;
            moves.addAll(king.getCastlingMoves(getPiece, getPos, patchedDangerMap, hasMoves));
        }
        return moves;
    }

    private Collection<Pos> getSimpleMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos, Predicate<Pos> dangerMap) throws PieceNotInBoard {
            Pos thisPos = getPos.apply(this);
            if (thisPos == null)
                throw new PieceNotInBoard();
            int row = thisPos.row();
            int column = thisPos.column();

            // Normal King Move
            int[][] possibleMoves = { 
                { row + 1, column }, 
                { row + 1, column + 1 }, 
                { row, column + 1 },
                { row - 1, column + 1 },
                { row - 1, column },
                { row - 1, column - 1 },
                { row, column - 1 },
                { row + 1, column - 1 }
            };
            Collection<Pos> simpleMoves = new ArrayList<>();
            for (int[] pos : possibleMoves) {
                try {
                    Pos movementPos = new Pos(pos[0], pos[1]);
                    Piece pieceInPos = getPiece.apply(movementPos);
                    if ((pieceInPos == null || pieceInPos.color != color)
                            && !dangerMap.test(movementPos)) {
                        simpleMoves.add(movementPos);
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        return simpleMoves;
    }

    private static Map<Pos, King> filterIntersectingSimpleMoves(Multimap<Pos, King> simpleMoves) {
        Map<Pos, King> moves = new HashMap<>();
        for (Pos pos : simpleMoves.keySet()) {
            Collection<King> kingList = simpleMoves.get(pos);
            if (kingList.size() == 1) {
                for (King king : kingList) {
                    moves.put(pos, king);
                }
            }
        }
        return moves;
    }

    private Collection<Move> getCastlingMoves(Function<Pos, Piece> getPiece, Function<Piece, Pos> getPos, Predicate<Pos> dangerMap,
            Predicate<Piece> hasMoved) throws PieceNotInBoard {
        if (hasMoved.test(this))
            return null;

        Pos thisPos = getPos.apply(this);
        if (thisPos == null)
            throw new PieceNotInBoard();
        int row = thisPos.row();
        int column = thisPos.column();

        if (dangerMap.test(thisPos))
            return null;

        Collection<Move> moves = new ArrayList<>();

        if (!hasMoved.test(kingSideRook))
            moves.add(kingsideCaslte(row, column, dangerMap, getPiece));

        if (!hasMoved.test(queenSideRook))
            moves.add(queensideCaslte(row, column, dangerMap, getPiece));

        return moves;
    }

    private Move kingsideCaslte(int row, int col, Predicate<Pos> dangerMap, Function<Pos, Piece> getPiece) {
        int i = color.queenToTheLeftOfKing ? 1 : -1;
        Pos firstSquare = new Pos(row, col + i);
        Pos secondSquare = new Pos(row, col + 2 * i);
        if (getPiece.apply(firstSquare) != null || getPiece.apply(secondSquare) != null || dangerMap.test(firstSquare)
                || dangerMap.test(secondSquare))
            return null;
        return new Move(this, MoveType.KINGSIDE_CASTLING, secondSquare);
    }

    private Move queensideCaslte(int row, int col, Predicate<Pos> dangerMap, Function<Pos, Piece> getPiece) {
        int i = color.queenToTheLeftOfKing ? -1 : 1;
        Pos firstSquare = new Pos(row, col + i);
        Pos secondSquare = new Pos(row, col + 2 * i);
        Pos thirdSquare = new Pos(row, col + 3 * i);
        if (getPiece.apply(firstSquare) != null || getPiece.apply(secondSquare) != null
                || getPiece.apply(thirdSquare) != null || dangerMap.test(firstSquare) || dangerMap.test(secondSquare))
            return null;
        return new Move(this, MoveType.QUEENSIDE_CASTLING, secondSquare);
    }
}

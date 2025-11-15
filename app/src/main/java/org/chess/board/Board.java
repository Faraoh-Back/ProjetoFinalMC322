package org.chess.board;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chess.Clock;
import org.chess.Color;
import org.chess.King;
import org.chess.Move;
import org.chess.Piece;
import org.chess.Player;
import org.chess.Pos;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

/**
 * Board
 * TODO
 */
public class Board {

  /** Data structure to store all pieces, and their positions. */
  private final BoardState boardState = new BoardState();

  /** Data structure to store all players. */
  private final Map<Color, Player> players = new EnumMap<>(Color.class);

  /** Match's history. */
  public final History history = new History();

  /**
   * @param clockTimeNanosec
   */
  public Board(long clockTimeNanosec) {
    EnumMap<Color, List<Piece>> playersPieces = new EnumMap<>(Color.class);

    for (Color color : Color.values())
      playersPieces.put(color, new ArrayList<>());

    for (var pieceAndPos : constructorHelper()) {
      Color color = pieceAndPos.piece.color;
      if (pieceAndPos.piece instanceof King king) {
        players.put(color, new Player(playersPieces.get(color), king, new Clock(clockTimeNanosec), color));
      }
      playersPieces.get(color).add(pieceAndPos.piece);
      boardState.addPiece(pieceAndPos.piece, pieceAndPos.pos);
    } // TODO: refactor everything.
  }

  public int getNOfPlayers() {
    return players.size();
  }

  public Player getPlayer(Color color) {
    return players.get(color);
  }

  public List<King> getEndangeredKings() {
    return players.values()
        .stream()
        .map(p -> p.king())
        .filter(p -> boardState.isDangerous(boardState.getPos(p), p.color))
        .toList();
  }

  public void doMove(Move move) {
    // TODO: check player clock to see if it still has time?
    // TODO: add to hystory
    switch (move.type()) {
      case SIMPLE_MOVE:
        boardState.movePiece(move.piece(), move.movingTo());
        break;
      case BISHOP_PROMOTION:
        boardState.movePiece(move.piece(), move.movingTo());
        boardState.removePiece(move.piece());
        Piece bishop = new Bishop(move.piece().color, this);
        boardState.addPiece(bishop, move.movingTo());
        break;
      case QUEEN_PROMOTION:
        boardState.movePiece(move.piece(), move.movingTo());
        boardState.removePiece(move.piece());
        Piece queen = new Queen(move.piece().color, this);
        boardState.addPiece(queen, move.movingTo());
        break;
      case ROOK_PROMOTION:
        boardState.movePiece(move.piece(), move.movingTo());
        boardState.removePiece(move.piece());
        Piece rook = new Rook(move.piece().color, this);
        boardState.addPiece(rook, move.movingTo());
        break;
      case KNIGHT_PROMOTION:
        boardState.movePiece(move.piece(), move.movingTo());
        boardState.removePiece(move.piece());
        Piece knight = new Knight(move.piece().color, this);
        boardState.addPiece(knight, move.movingTo());
        break;
      case KINGSIDE_CASTLING:
        boardState.movePiece(move.piece(), move.movingTo());
        Pos rookPos = switch (move.piece().color) {
          case RED -> new Pos(1,6);
          case BLUE -> new Pos (9, 14);
          case YELLOW -> new Pos(6, 1);
          case GREEN -> new Pos(14, 9);
        };
        // TODO: get player's rook
        // boardState.movePiece(players, rookPos);
        break;
      case QUEENSIDE_CASTLING:
        break;
      default:
        throw new IllegalStateException("Unexpected enum.");
    }
  }

  /**
   * Returns true if a player has valid moves. false otherwise
   * 
   * @param player's color
   * @return
   */
  public boolean hasMoves(Color color) {
    for (Piece piece : players.get(color).pieces()) {
      if (moves.getMovesView(piece).size() > 0) // TODO: Find a way to not look through all pieces.
        return true;
    }
    return false;
  }

  /**
   * @param piece
   * @return A view of all moves that `piece` can make.
   */
  public List<Move> getMovesView(Piece piece) {
    // just a public wrapper.
    return moves.getMovesView(piece);
  }

  /**
   * A helper record.
   */
  private static record ConstructorHelper(Piece piece, Pos pos) {
  }

  /**
   * A helper function to construct all pieces. The pieces follow the layout
   * below, with red on top, yellow on the left side, blue on the right side and
   * green on the bottom.
   Top left corner is (1,1).
   *
   * ___rkbKqbkr___
   * ___pppppppp___
   * ___@@@@@@@@___
   * rp@@@@@@@@@@pr
   * kp@@@@@@@@@@pk
   * bp@@@@@@@@@@pb
   * Kp@@@@@@@@@@pq
   * qp@@@@@@@@@@pK
   * bp@@@@@@@@@@pb
   * kp@@@@@@@@@@pk
   * rp@@@@@@@@@@pr
   * ___@@@@@@@@___
   * ___pppppppp___
   * ___rkbqKbkr___
   * 
   * @return All pieces and their positions.
   */
  private ConstructorHelper[] constructorHelper() {
    ConstructorHelper[] array = {
        new ConstructorHelper(new Rook(Color.RED, this), new Pos(1, 4)),
        new ConstructorHelper(new Knight(Color.RED, this), new Pos(1, 5)),
        new ConstructorHelper(new Bishop(Color.RED, this), new Pos(1, 6)),
        new ConstructorHelper(new King(Color.RED, this), new Pos(1, 7)),
        new ConstructorHelper(new Queen(Color.RED, this), new Pos(1, 8)),
        new ConstructorHelper(new Bishop(Color.RED, this), new Pos(1, 9)),
        new ConstructorHelper(new Knight(Color.RED, this), new Pos(1, 10)),
        new ConstructorHelper(new Rook(Color.RED, this), new Pos(1, 11)),

        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 4)),
        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 5)),
        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 6)),
        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 7)),
        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 8)),
        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 9)),
        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 10)),
        new ConstructorHelper(new Pawn(Color.RED, this), new Pos(2, 11)),

        new ConstructorHelper(new Rook(Color.YELLOW, this), new Pos(4, 14)),
        new ConstructorHelper(new Knight(Color.YELLOW, this), new Pos(5, 14)),
        new ConstructorHelper(new Bishop(Color.YELLOW, this), new Pos(6, 14)),
        new ConstructorHelper(new Queen(Color.YELLOW, this), new Pos(7, 14)),
        new ConstructorHelper(new King(Color.YELLOW, this), new Pos(8, 14)),
        new ConstructorHelper(new Bishop(Color.YELLOW, this), new Pos(9, 14)),
        new ConstructorHelper(new Knight(Color.YELLOW, this), new Pos(10, 14)),
        new ConstructorHelper(new Rook(Color.YELLOW, this), new Pos(11, 14)),

        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(4, 13)),
        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(5, 13)),
        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(6, 13)),
        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(7, 13)),
        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(8, 13)),
        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(9, 13)),
        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(10, 13)),
        new ConstructorHelper(new Pawn(Color.YELLOW, this), new Pos(11, 13)),

        new ConstructorHelper(new Rook(Color.BLUE, this), new Pos(4, 1)),
        new ConstructorHelper(new Knight(Color.BLUE, this), new Pos(5, 1)),
        new ConstructorHelper(new Bishop(Color.BLUE, this), new Pos(6, 1)),
        new ConstructorHelper(new King(Color.BLUE, this), new Pos(7, 1)),
        new ConstructorHelper(new Queen(Color.BLUE, this), new Pos(8, 1)),
        new ConstructorHelper(new Bishop(Color.BLUE, this), new Pos(9, 1)),
        new ConstructorHelper(new Knight(Color.BLUE, this), new Pos(10, 1)),
        new ConstructorHelper(new Rook(Color.BLUE, this), new Pos(11, 1)),

        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(4, 2)),
        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(5, 2)),
        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(6, 2)),
        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(7, 2)),
        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(8, 2)),
        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(9, 2)),
        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(10, 2)),
        new ConstructorHelper(new Pawn(Color.BLUE, this), new Pos(11, 2)),

        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 4)),
        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 5)),
        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 6)),
        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 7)),
        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 8)),
        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 9)),
        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 10)),
        new ConstructorHelper(new Pawn(Color.GREEN, this), new Pos(13, 11)),

        new ConstructorHelper(new Rook(Color.GREEN, this), new Pos(14, 4)),
        new ConstructorHelper(new Knight(Color.GREEN, this), new Pos(14, 5)),
        new ConstructorHelper(new Bishop(Color.GREEN, this), new Pos(14, 6)),
        new ConstructorHelper(new Queen(Color.GREEN, this), new Pos(14, 7)),
        new ConstructorHelper(new King(Color.GREEN, this), new Pos(14, 8)),
        new ConstructorHelper(new Bishop(Color.GREEN, this), new Pos(14, 9)),
        new ConstructorHelper(new Knight(Color.GREEN, this), new Pos(14, 10)),
        new ConstructorHelper(new Rook(Color.GREEN, this), new Pos(14, 11)),
    };
    return array;
  }
}

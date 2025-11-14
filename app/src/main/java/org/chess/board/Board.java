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
  /** Data structure to store all currently possible moves */
  private final PossibleMoves moves = new PossibleMoves();

  /** Data structure to store all pieces, and their positions. */
  private final BiMap<Pos, Piece> pieces = HashBiMap.create();

  /**
   *
   * Data structure to store which piece is blocking some piece's movement.
   * A -> B means A is blocking B
   */
  private final MutableGraph<Piece> isBlockingGraph = GraphBuilder.directed().build();

  /** Data structure to store all players. */
  private final Map<Color, Player> players = new EnumMap<>(Color.class);

  /** Match's history. */
  public final History history = new History();

  /**
   * @param clockTimeNanosec
   */
  public Board(long clockTimeNanosec) {
    // Get pieces from helper method and:
    // - adds them to players' pieces list
    // - add them to `Pieces` BiMap.
    // Once all are added, they are reevaluated.
    EnumMap<Color, List<Piece>> playersPieces = new EnumMap<>(Color.class);
    List<Piece> allPieces = new ArrayList<>();

    for (Color color : Color.values())
      playersPieces.put(color, new ArrayList<>());

    for (var pieceAndPos : constructorHelper()) {
      Color color = pieceAndPos.piece.color;
      allPieces.add(pieceAndPos.piece);
      if (pieceAndPos.piece instanceof King king) {
        players.put(color, new Player(playersPieces.get(color), king, new Clock(clockTimeNanosec), color));
      }
      playersPieces.get(color).add(pieceAndPos.piece);
      pieces.put(pieceAndPos.pos, pieceAndPos.piece);
    }

    for (Piece piece : allPieces) {
      reevaluate(piece);
    }
  }

  public int getNOfPlayers() {
    return players.size();
  }

  public Player getPlayer(Color color) {
    return players.get(color);
  }

  public Pos getPos(Piece piece) {
    return pieces.inverse().get(piece);
  }

  public Piece getPiece(Pos pos) {
    return pieces.get(pos);
  }

  /**
   * @return all kings that can be taken by some piece.
   */
  public List<King> getEndangeredKings() {
    return players.values()
        .stream()
        .map(p -> p.king())
        .filter(p -> moves.isInDanger(p))
        .toList();
  }

  public void doMove(Move move) {
    // check player clock to see if it still has time.
    // switch-case for every type of move
    // add move to history
    // if pieces were taken, add them to history.:w
    // save all edges from the moving piece in a temp variable.
    // re-evaluate the moving piece if it is not a king
    // re-evaluate all pieces poited by edges in the temp variable.
    // re-evaluate the moving piece if it is a king

    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'doMove'");
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
   * Updates `isBlockingGraph` with all pieces that are blocking `piece`.
   * Updates `moves` with piece's valid moves.
   * 
   * @param piece
   */
  private void reevaluate(Piece piece) {
    var calcResult = piece.calculateMoves();
    isBlockingGraph.predecessors(piece).forEach(p -> isBlockingGraph.removeEdge(p, piece));
    calcResult.piecesBlockingMoves().forEach(p -> isBlockingGraph.putEdge(p, piece));
    moves.getMovesView(piece).forEach(m -> moves.forgetMove(m));
    calcResult.validMoves().forEach(m -> moves.submitMove(m));
  }

  /**
   * removes it from the `pieces` BiMap
   * removes it's moves from `moves` data structure.
   * reevaluates all pieces that were blocked by it.
   * Removes it's player if it was the king.
   * *
   * 
   * @param player's color
   */
  private void removePiece(Piece piece) {
    Player player = players.get(piece.color);
    if (player.king() == piece) {
      // could just remove from the `players` hash.
      // But if any extra logic is added to removePlayer() it would need to be added
      // here as well.
      removePlayer(player.color());
      return;
    }

    pieces.inverse().remove(piece);
    moves.forgetPiece(piece);
    Set<Piece> wereBlocked = isBlockingGraph.successors(piece);
    isBlockingGraph.removeNode(piece);
    wereBlocked.forEach(p -> reevaluate(p));
    player.pieces().remove(piece);
  }

  /**
   * Find a player by their color.
   * removes their pieces from the `pieces` BiMap
   * removes their pieces' moves from `moves` data structure.
   * reevaluates all pieces that were blocked by their pieces.
   * 
   * @param player's color
   */
  private void removePlayer(Color color) {
    // It is better to reevaluate once all pieces have been removed from
    // `pieces` and their moves have been removed from `moves`. otherwise a
    // piece could be reevaluated twice, which is wastefull.
    // That's why we're not calling removePiece for each piece.

    Set<Piece> wereBlocked = new HashSet<>();
    players.get(color)
        .pieces()
        .forEach(piece -> {
          pieces.inverse().remove(piece);
          moves.forgetPiece(piece);
          wereBlocked.addAll(isBlockingGraph.successors(piece));
          isBlockingGraph.removeNode(piece);
        });
    wereBlocked.forEach(p -> reevaluate(p));
    players.remove(color);
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
   * Bp@@@@@@@@@@pB
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

package org.chess.board;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import org.chess.Move;
import org.chess.Pos;
import org.chess.Color;
import org.chess.King;
import org.chess.Piece;
import org.chess.Player;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Board {
  private final PossibleMoves moves = new PossibleMoves();
  private final BiMap<Pos, Piece> pieces = HashBiMap.create();
  private final MutableGraph<Piece> reevaluationGraph = GraphBuilder.directed().build();
  private final Map<Color, Player> players = new EnumMap<>(Color.class);
  public final History history = new History();

   
  public Board() {
    // initialize all data structures above
    // create a temporary list of pieces
    // create and add pieces to pieces map and to the temporary list.
    // create a player with that list of pieces
    // add player to platers.
    // repeat for every player
    //
    // reavaluate all pieces.
  }

  public int getNOfPlayers() {
  }

  public Player getPlayer(Color color) {
  }

  public Pos getPos(Piece piece) {
  }

  public Piece getPiece(Pos pos) {
  }


  public List<King> getEndangeredKings() {
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
  }

  public boolean hasMoves(Color color) {
    // get player using color as key
    // get player pieces
    // for each piece, use moves.hasMoves(piece)
    // return true if some piece has moves.
  }

  public List<Move> getMovesView(Piece piece) {
    return moves.getMovesView(piece);
  }

  private void reevaluate(Piece piece) {
    // use piece.calculateMoves() to get movesList and blockingPieces
    // in reevaluationGraph, add edges from the blockingPieces to piece
    // moves.submiMove(move)
    }


  private void removePlayer(Color color) {
    // get player instance
    // get list of pieces
    // remove them from pieces BiMap
    // remove them from graph, but store, in a temp variable, all pieces poited by them.
    // reevaluate all pieces in the temp variable
  }


  
}

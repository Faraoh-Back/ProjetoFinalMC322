package org.chess;

import org.chess.board.Board;
import org.chess.board.JsonGameSerializer;
import org.chess.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe principal do jogo de xadrez 4 jogadores.
 * 
 * Configuração do tabuleiro (14x14 com cantos cortados):
 * - Vermelho (RED): Topo - linhas 1-2, colunas 4-11
 * - Amarelo (YELLOW): Esquerda - linhas 4-11, colunas 1-2
 * - Verde (GREEN): Baixo - linhas 13-14, colunas 4-11
 * - Azul (BLUE): Direita - linhas 4-11, colunas 13-14
 * 
 * Verde começa o jogo.
 */
public class App {
    private Board board;
    private Map<Color, Player> players;
    private Color currentTurn;
    private boolean gameOver;

    public App() {
        initializeGame();
    }

    /**
     * Inicializa o jogo: cria jogadores, posiciona peças e cria o tabuleiro.
     */
    private void initializeGame() {
        // Tempo padrão: 10 minutos por jogador (em nanosegundos)
        long defaultTime = 10L * 60L * 1000000000L;

        // Criar estado inicial do tabuleiro
        Map<Pos, Piece> initialState = new HashMap<>();
        players = new EnumMap<>(Color.class);

        // Criar cada jogador e posicionar suas peças
        for (Color color : Color.values()) {
            Clock clock = new Clock(defaultTime);
            Player player = new Player(clock, color);
            players.put(color, player);

            // Posicionar cada peça do jogador no tabuleiro
            for (PieceType pieceType : PieceType.values()) {
                Pos pos = pieceType.initialPos(color);
                Piece piece = player.pieces.get(pieceType);
                initialState.put(pos, piece);
            }
        }

        // Criar o tabuleiro com o estado inicial
        board = new Board(initialState);

        // Verde começa
        currentTurn = Color.GREEN;
        gameOver = false;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Collection<Move> getPossibleMoves(Pos pos) {
        Piece piece = board.getPiece(pos);
        if (piece != null)
            return board.getReadonlyMoves(piece);
        return new ArrayList<Move>();
    }

    public Piece getPiece(Pos pos) {
        return board.getPiece(pos);
    }

    public void resetGame() {
        initializeGame();
    }

    public void doMove(Move move) {
        if (currentTurn != move.piece().color)
            throw new IllegalArgumentException("It's not your turn.");
        Clock clock = players.get(currentTurn).clock;
        if (clock.getTimeLeftNanosecs() < 0)
            throw new IllegalArgumentException("Time is over.");
        board.doMove(move);
        clock.pause();
        currentTurn = currentTurn.getLeftColor();
        if (board.isCheckmate(currentTurn)) {
            board.remove(currentTurn);
            currentTurn = currentTurn.getLeftColor();
        }
        players.get(currentTurn).clock.resume();
    }

    public Player getPlayer(Color color) {
        return players.get(color);
    }
    public void saveGame(String filePath) {
    try {
            String json = JsonGameSerializer.toJson(this);
            java.nio.file.Files.writeString(java.nio.file.Path.of(filePath), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadGame(String filePath) {
        try {
            String json = java.nio.file.Files.readString(java.nio.file.Path.of(filePath));
            JsonGameSerializer.fromJson(this, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
package org.chess;

import org.chess.board.Board;
import org.chess.pieces.Piece;

import java.util.*;
import java.io.*;

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
    private List<Move> gameHistory;

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
        gameHistory = new ArrayList<>();
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
        gameHistory.add(move); // Adicionar ao histórico
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

    public List<Move> getGameHistory() {
        return new ArrayList<>(gameHistory);
    }

    // ===== MÉTODOS DE PERSISTÊNCIA =====

    /**
     * Salva o estado atual do jogo em um arquivo
     */
    public boolean saveGame(String gameName) {
        try {
            File saveDir = new File("saved_games");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            File saveFile = new File(saveDir, gameName + ".chess");
            
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(saveFile))) {
                
                // Salvar estado do tabuleiro
                oos.writeObject(board);
                
                // Salvar estado dos jogadores
                oos.writeObject(new HashMap<>(players));
                
                // Salvar turno atual
                oos.writeObject(currentTurn);
                
                // Salvar estado do jogo
                oos.writeBoolean(gameOver);
                
                // Salvar histórico
                oos.writeObject(gameHistory);
                
                System.out.println("Jogo salvo com sucesso: " + gameName);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar jogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carrega um jogo salvo de um arquivo
     */
    public boolean loadGame(String gameName) {
        try {
            File saveFile = new File("saved_games", gameName + ".chess");
            
            if (!saveFile.exists()) {
                System.err.println("Arquivo de jogo não encontrado: " + gameName);
                return false;
            }

            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(saveFile))) {
                
                // Carregar estado do tabuleiro
                board = (Board) ois.readObject();
                
                // Carregar estado dos jogadores
                players = (Map<Color, Player>) ois.readObject();
                
                // Carregar turno atual
                currentTurn = (Color) ois.readObject();
                
                // Carregar estado do jogo
                gameOver = ois.readBoolean();
                
                // Carregar histórico
                gameHistory = (List<Move>) ois.readObject();
                
                System.out.println("Jogo carregado com sucesso: " + gameName);
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar jogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lista todos os jogos salvos disponíveis
     */
    public List<String> getSavedGames() {
        List<String> savedGames = new ArrayList<>();
        File saveDir = new File("saved_games");
        
        if (!saveDir.exists()) {
            return savedGames;
        }
        
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".chess"));
        if (files != null) {
            for (File file : files) {
                // Remove a extensão .chess do nome do arquivo
                String gameName = file.getName().replace(".chess", "");
                savedGames.add(gameName);
            }
        }
        
        return savedGames;
    }

    /**
     * Remove um jogo salvo
     */
    public boolean deleteSavedGame(String gameName) {
        File saveFile = new File("saved_games", gameName + ".chess");
        return saveFile.delete();
    }
}
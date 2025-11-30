package org.chess;

import org.chess.board.Board;
import org.chess.pieces.Piece;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    // Diretório para salvar jogos
    private static final String SAVE_DIR = "saved_games";
    private static final String SAVE_EXTENSION = ".chess";

    public App() {
        initializeGame();
        ensureSaveDirectoryExists();
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

    // ========================================================================
    // MÉTODOS DE PERSISTÊNCIA
    // ========================================================================

    /**
     * Garante que o diretório de salvamentos existe.
     */
    private void ensureSaveDirectoryExists() {
        try {
            Path savePath = Paths.get(SAVE_DIR);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de salvamentos: " + e.getMessage());
        }
    }

    /**
     * Salva o estado atual do jogo em um arquivo.
     * 
     * @param gameName Nome do jogo a ser salvo (sem extensão)
     * @return true se o salvamento foi bem-sucedido, false caso contrário
     */
    public boolean saveGame(String gameName) {
        if (gameName == null || gameName.trim().isEmpty()) {
            System.err.println("Nome do jogo não pode ser vazio");
            return false;
        }

        // Sanitizar o nome do arquivo
        String sanitizedName = gameName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String fileName = SAVE_DIR + File.separator + sanitizedName + SAVE_EXTENSION;

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(fileName))) {
            
            // Criar objeto de estado serializável
            GameState state = new GameState(
                board,
                players,
                currentTurn,
                gameOver
            );
            
            oos.writeObject(state);
            System.out.println("Jogo salvo com sucesso: " + fileName);
            return true;
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar jogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carrega um jogo salvo anteriormente.
     * 
     * @param gameName Nome do jogo a ser carregado (sem extensão)
     * @return true se o carregamento foi bem-sucedido, false caso contrário
     */
    public boolean loadGame(String gameName) {
        if (gameName == null || gameName.trim().isEmpty()) {
            System.err.println("Nome do jogo não pode ser vazio");
            return false;
        }

        String sanitizedName = gameName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String fileName = SAVE_DIR + File.separator + sanitizedName + SAVE_EXTENSION;

        if (!Files.exists(Paths.get(fileName))) {
            System.err.println("Jogo não encontrado: " + fileName);
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(fileName))) {
            
            GameState state = (GameState) ois.readObject();
            
            // Restaurar o estado do jogo
            this.board = state.board;
            this.players = state.players;
            this.currentTurn = state.currentTurn;
            this.gameOver = state.gameOver;
            
            System.out.println("Jogo carregado com sucesso: " + fileName);
            return true;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar jogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lista todos os jogos salvos disponíveis.
     * 
     * @return Lista com os nomes dos jogos salvos (sem extensão)
     */
    public List<String> getSavedGames() {
        List<String> savedGames = new ArrayList<>();
        
        try {
            Path savePath = Paths.get(SAVE_DIR);
            
            if (!Files.exists(savePath)) {
                return savedGames;
            }

            savedGames = Files.list(savePath)
                .filter(path -> path.toString().endsWith(SAVE_EXTENSION))
                .map(path -> {
                    String fileName = path.getFileName().toString();
                    return fileName.substring(0, fileName.length() - SAVE_EXTENSION.length());
                })
                .sorted()
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            System.err.println("Erro ao listar jogos salvos: " + e.getMessage());
        }
        
        return savedGames;
    }

    /**
     * Deleta um jogo salvo.
     * 
     * @param gameName Nome do jogo a ser deletado (sem extensão)
     * @return true se a exclusão foi bem-sucedida, false caso contrário
     */
    public boolean deleteSavedGame(String gameName) {
        if (gameName == null || gameName.trim().isEmpty()) {
            System.err.println("Nome do jogo não pode ser vazio");
            return false;
        }

        String sanitizedName = gameName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String fileName = SAVE_DIR + File.separator + sanitizedName + SAVE_EXTENSION;

        try {
            Path filePath = Paths.get(fileName);
            
            if (!Files.exists(filePath)) {
                System.err.println("Jogo não encontrado: " + fileName);
                return false;
            }
            
            Files.delete(filePath);
            System.out.println("Jogo deletado com sucesso: " + fileName);
            return true;
            
        } catch (IOException e) {
            System.err.println("Erro ao deletar jogo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================================
    // CLASSE INTERNA PARA SERIALIZAÇÃO
    // ========================================================================

    /**
     * Classe interna para encapsular o estado do jogo de forma serializável.
     * Nota: Para que esta implementação funcione completamente, as classes
     * Board, Player, Clock, Piece e suas subclasses devem implementar Serializable.
     */
    private static class GameState implements Serializable {
        private static final long serialVersionUID = 1L;
        
        final Board board;
        final Map<Color, Player> players;
        final Color currentTurn;
        final boolean gameOver;

        GameState(Board board, Map<Color, Player> players, Color currentTurn, boolean gameOver) {
            this.board = board;
            this.players = players;
            this.currentTurn = currentTurn;
            this.gameOver = gameOver;
        }
    }
}
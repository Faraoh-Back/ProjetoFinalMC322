package org.chess;

import org.chess.board.Board;
import org.chess.pieces.Piece;

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
        players = new HashMap<>();

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

    /**
     * Obtém a cor do jogador atual.
     */
    public Color getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Verifica se o jogo acabou.
     */
    public boolean isGameOver() {
        return gameOver;
    }


    /**
     * Obtém todos os movimentos possíveis de uma peça em determinada posição.
     * 
     * @param row linha da peça
     * @param col coluna da peça
     * @return lista de movimentos possíveis, ou null se não houver peça
     */
    public java.util.Collection<Move> getPossibleMoves(int row, int col) {
        try {
            Pos pos = new Pos(row, col);
            Piece piece = board.getPiece(pos);
            if (piece == null) {
                return null;
            }
            return board.getReadonlyMoves(piece);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Player getPlayer(Color color) {
        return players.get(color);
    }

    /**
     * Obtém o histórico completo de movimentos do jogo.
     */
    public java.util.List<Move> getGameHistory() {
        return board.history.getMovesView();
    }

    /**
     * Calcula o próximo turno na rotação.
     * Ordem: Verde → Amarelo → Vermelho → Azul → Verde
     */
    private Color getNextTurn() {
        return switch (currentTurn) {
            case GREEN -> Color.YELLOW;
            case YELLOW -> Color.RED;
            case RED -> Color.BLUE;
            case BLUE -> Color.GREEN;
        };
    }

    /**
     * Reinicia o jogo com um novo tabuleiro.
     */
    public void resetGame() {
        initializeGame();
    }



    public static void main(String[] args) {
        // Exemplo de uso
        App app = new App();
        
        // - app.getPossibleMoves(13, 5) para ver movimentos
        // - app.executeMove(13, 5, 11, 5) para mover
        // - app.getCurrentTurn() para saber de quem é a vez
        // - app.getGameState() para obter estado completo
    }
}
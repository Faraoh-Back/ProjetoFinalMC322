package org.chess;

import org.chess.board.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.EnumMap;
import org.chess.pieces.*;
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
    private Color currentTurn;
    private boolean gameOver;
    
    public App() {
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
     * Obtém o tabuleiro atual.
     */
    public Board getBoard() {
        return board;
    }
    
    
    /**
     * Executa um movimento no tabuleiro.
     * 
     * @param fromRow linha de origem
     * @param fromCol coluna de origem
     * @param toRow linha de destino
     * @param toCol coluna de destino
     * @return true se o movimento foi executado com sucesso, false caso contrário
     * @throws IllegalArgumentException se as posições forem inválidas
     * @throws IllegalStateException se o jogo já acabou
     */
    public boolean executeMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (gameOver) {
            throw new IllegalStateException("O jogo já acabou");
        }
        
        Pos fromPos = new Pos(fromRow, fromCol);
        Pos toPos = new Pos(toRow, toCol);
        Piece piece = board.getPiece(fromPos);
        if (piece == null) {
            return false; // Nenhuma peça na posição de origem
        }
        
        if (piece.color != currentTurn) {
            return false; 
        }
        
        // Procurar movimento válido
        List<Move> possibleMoves = board.history.getMovesView(piece);
        Move selectedMove = null;
        
        for (Move move : possibleMoves) {
            if (move.toPos().equals(toPos)) {
                selectedMove = move;
                break;
            }
        }
        
        if (selectedMove == null) {
            return false; // Movimento inválido
        }
        
        // Executar movimento
        board.doMove(selectedMove);
        
        // Avançar turno
        currentTurn = getNextTurn();
        
        
        return true;
    }
    
    
    
    /**
     * Obtém o histórico completo de movimentos do jogo.
     * 
     * @return lista de movimentos realizados
     */
    public List<Move> getGameHistory() {
        return board.history.getMovesView();
    }
    
    /**
     * Obtém o histórico de movimentos de uma peça específica.
     * 
     * @param piece a peça
     * @return lista de movimentos da peça
     */
    public List<Move> getPieceHistory(Piece piece) {
        return board.history.getMovesView(piece);
    }
    
    
    /**
     * Obtém o último movimento realizado no jogo.
     * 
     * @return o último movimento, ou null se nenhum movimento foi feito
     */
    public Move getLastMove() {
        return board.history.getLastMove();
    }
    
    /**
     * Calcula o próximo turno na rotação.
     * Ordem: Verde → Vermelho → Amarelo → Azul → Verde
     */
    private Color getNextTurn() {
        return switch (currentTurn) {
            // Verde, Amarelo, Vermelho, Azul
            case GREEN -> Color.YELLOW;
            case RED -> Color.BLUE;
            case YELLOW -> Color.RED;
            case BLUE -> Color.GREEN;
        };
    }
    
    /**
     * Reinicia o jogo com um novo tabuleiro.
     */
    public void resetGame() {
        // this.board = new Board();
        // Resetar initialState
        this.currentTurn = Color.GREEN;
        this.gameOver = false;
    }

    public static void main(String[] args) {
        // Ponto de entrada - frontend irá instanciar App e usar seus métodos

        Map<Pos, Piece> initialState = new HashMap<>();
        
        Map<Color, Player> players = new EnumMap<>(Color.class);
        Clock clock = new Clock(100000);
        for (Color color : Color.values()) {
          Player player = new Player(clock,  color);
          for (PieceType pieceType : PieceType.values()) {
            initialState.put(pieceType.initialPos(color), player.pieces.get(pieceType));
          }
          players.put(color, player);
        }
      

        Board tmp = new Board(initialState);
        App app = new App();
        
        // List<Move> moves = app.getPossibleMoves(13, 5);
        // boolean success = app.executeMove(13, 5, 11, 5);
        // Color turn = app.getCurrentTurn();
    }
}
  

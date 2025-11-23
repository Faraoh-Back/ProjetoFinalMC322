package org.chess.api;

import org.chess.*;
import org.chess.board.Board;
import org.chess.pieces.Piece;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller Spring Boot para integração com a classe App.java real
 * 
 * Esta classe acts como ponte entre o frontend HTMX/JavaScript e o backend Java.
 * Ela recebe requisições HTTP, processa usando a lógica de xadrez existente, e retorna
 * respostas JSON para o frontend.
 * 
 * FUNCIONALIDADES PRINCIPAIS:
 * - Gerenciar estado do tabuleiro (movimentos, jogadores, turnos)
 * - Fornecer API REST para o frontend HTMX
 * - Validar movimentos de peças
 * - Controlar histórico de jogos
 * - Gerenciar informações dos jogadores
 * 
 * CADA MÉTODO É DESIGNADO PARA:
 * - GET: Recuperar informações (estado do jogo, jogadores, histórico)
 * - POST: Executar ações (fazer movimentos, iniciar novo jogo)
 * - Validar dados de entrada antes de processar
 * - Retornar respostas estruturadas em formato JSON
 */
@RestController
@RequestMapping("/api")
public class ChessAPIController {
    
    /**
     * Instância principal do jogo de xadrez
     * Esta é a classe que contém toda a lógica de negócios do jogo:
     * - Movimentos das peças
     * - Validação de regras
     * - Controle de turnos
     * - Estado dos jogadores
     */
    private final App chessGame;
    
    /**
     * Construtor do controller
     * Inicializa uma nova instância do jogo quando o controller é criado
     */
    public ChessAPIController() {
        this.chessGame = new App();
    }
    
    /**
     * ===== ENDPOINTS RELACIONADOS AO TABULEIRO =====
     */
    
    /**
     * Obtém o estado completo do tabuleiro de xadrez
     * 
     * ENDEREÇO: GET /api/board
     * 
     * RETORNA:
     * - currentTurn: Qual jogador está jogando agora
     * - gameOver: Se o jogo terminou
     * - size: Tamanho do tabuleiro (14x14 para 4 jogadores)
     * - players: Status de todos os jogadores (eliminação, tempo restante)
     * 
     * USADO POR: Frontend para atualizar visualmente o estado do jogo
     */
    @GetMapping("/board")
    public ResponseEntity<Map<String, Object>> getBoard() {
        try {
            // Criar mapa para armazenar dados do tabuleiro
            Map<String, Object> boardData = new HashMap<>();
            
            // Informações básicas do jogo
            boardData.put("currentTurn", chessGame.getCurrentTurn().name());
            boardData.put("gameOver", chessGame.isGameOver());
            boardData.put("size", 14);
            
            // Informações detalhadas de cada jogador
            Map<String, Object> playersData = new HashMap<>();
            for (Color color : Color.values()) {
                Player player = chessGame.getPlayer(color);
                if (player != null) {
                    Map<String, Object> playerInfo = new HashMap<>();
                    playerInfo.put("color", color.name());
                    playerInfo.put("eliminated", player.isEliminated());
                    // Formatar tempo restante do relógio em formato legível (MM:SS)
                    playerInfo.put("timeRemaining", formatTime(player.getClock().getTimeRemaining()));
                    playersData.put(color.name(), playerInfo);
                }
            }
            boardData.put("players", playersData);
            
            return ResponseEntity.ok(boardData);
            
        } catch (Exception e) {
            // Tratamento de erro - retornar informação útil para o frontend
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro ao obter estado do tabuleiro: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * ===== ENDPOINTS RELACIONADOS AOS MOVIMENTOS =====
     */
    
    /**
     * Obtém todos os movimentos possíveis para uma peça específica
     * 
     * ENDEREÇO: GET /api/moves/{row}/{col}
     * 
     * PARÂMETROS:
     * - row: Linha da peça (0-13)
     * - col: Coluna da peça (0-13)
     * 
     * RETORNA: Lista de movimentos possíveis com:
     * - row, col: Destino do movimento
     * - capture: Se é um movimento de captura
     * - pieceType: Tipo da peça que pode fazer o movimento
     * 
     * USADO POR: Frontend para mostrar moves possíveis quando uma peça é selecionada
     */
    @GetMapping("/moves/{row}/{col}")
    public ResponseEntity<List<Map<String, Object>>> getPossibleMoves(
            @PathVariable int row, @PathVariable int col) {
        
        try {
            // Validar se as coordenadas estão dentro do tabuleiro
            if (row < 0 || row >= 14 || col < 0 || col >= 14) {
                return ResponseEntity.badRequest().build();
            }
            
            // Obter movimentos possíveis do backend Java
            java.util.Collection<Move> moves = chessGame.getPossibleMoves(row, col);
            
            // Se não há movimentos, retornar lista vazia
            if (moves == null || moves.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            // Converter movimentos para formato JSON (usado pelo frontend)
            List<Map<String, Object>> movesList = moves.stream()
                .map(move -> {
                    Map<String, Object> moveMap = new HashMap<>();
                    moveMap.put("row", move.getTo().getRow());
                    moveMap.put("col", move.getTo().getCol());
                    moveMap.put("capture", move.isCapture());
                    moveMap.put("pieceType", move.getPiece().getType().toString());
                    return moveMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(movesList);
            
        } catch (Exception e) {
            // Tratar erros de forma graciosa
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erro ao obter movimentos possíveis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Executa um movimento no jogo
     * 
     * ENDEREÇO: POST /api/move
     * 
     * CORPO DA REQUISIÇÃO:
     * - fromRow, fromCol: Posição atual da peça
     * - toRow, toCol: Posição de destino
     * 
     * RETORNA:
     * - success: Se o movimento foi bem-sucedido
     * - error: Mensagem de erro (se houver)
     * - message: Confirmção de sucesso
     * 
     * LÓGICA:
     * 1. Valida coordenadas
     * 2. Verifica se o movimento é possível usando o backend
     * 3. Retorna resultado da validação
     * 
     * NOTA: Esta é uma versão de validação - a implementação completa
     *        do movimento real será adicionada posteriormente
     */
    @PostMapping("/move")
    public ResponseEntity<Map<String, Object>> makeMove(@RequestBody MoveRequest request) {
        try {
            // Validação rigorosa das coordenadas de entrada
            if (request.getFromRow() < 0 || request.getFromRow() >= 14 ||
                request.getFromCol() < 0 || request.getFromCol() >= 14 ||
                request.getToRow() < 0 || request.getToRow() >= 14 ||
                request.getToCol() < 0 || request.getToCol() >= 14) {
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Posições inválidas (devem estar entre 0-13)");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // PASSO 1: Obter movimentos possíveis para a posição de origem
            java.util.Collection<Move> possibleMoves = chessGame.getPossibleMoves(
                request.getFromRow(), request.getFromCol());
            
            // PASSO 2: Verificar se o movimento solicitado está na lista de movimentos válidos
            boolean isValidMove = false;
            if (possibleMoves != null) {
                isValidMove = possibleMoves.stream()
                    .anyMatch(move -> 
                        move.getTo().getRow() == request.getToRow() && 
                        move.getTo().getCol() == request.getToCol());
            }
            
            // PASSO 3: Preparar resposta baseada no resultado da validação
            Map<String, Object> response = new HashMap<>();
            response.put("success", isValidMove);
            
            if (!isValidMove) {
                response.put("error", "Movimento não é válido");
            } else {
                response.put("message", "Movimento válido! (implementação completa em desenvolvimento)");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Tratamento de erro interno
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * ===== ENDPOINTS RELACIONADOS AO JOGO =====
     */
    
    /**
     * Obtém qual jogador está jogando agora
     * 
     * ENDEREÇO: GET /api/turn
     * 
     * RETORNA:
     * - currentTurn: Nome técnico da cor (RED, BLUE, GREEN, YELLOW)
     * - color: Nome em português para exibição
     * 
     * USADO POR: Frontend para mostrar de quem é a vez
     */
    @GetMapping("/turn")
    public ResponseEntity<Map<String, String>> getCurrentTurn() {
        try {
            Color currentTurn = chessGame.getCurrentTurn();
            
            Map<String, String> response = new HashMap<>();
            response.put("currentTurn", currentTurn.name());
            response.put("color", getColorDisplayName(currentTurn));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtém histórico completo de movimentos do jogo atual
     * 
     * ENDEREÇO: GET /api/history
     * 
     * RETORNA: Lista de movimentos com:
     * - fromRow, fromCol: Posição de origem
     * - toRow, toCol: Posição de destino
     * - pieceType: Tipo da peça movida
     * - color: Cor do jogador
     * - capture: Se foi um movimento de captura
     * 
     * USADO POR: Frontend para mostrar histórico de jogadas
     */
    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getGameHistory() {
        try {
            java.util.List<Move> history = chessGame.getGameHistory();
            
            // Converter histórico para formato legível pelo frontend
            List<Map<String, Object>> historyList = history.stream()
                .map(move -> {
                    Map<String, Object> moveInfo = new HashMap<>();
                    moveInfo.put("fromRow", move.getFrom().getRow());
                    moveInfo.put("fromCol", move.getFrom().getCol());
                    moveInfo.put("toRow", move.getTo().getRow());
                    moveInfo.put("toCol", move.getTo().getCol());
                    moveInfo.put("pieceType", move.getPiece().getType().toString());
                    moveInfo.put("color", move.getPiece().getColor().name());
                    moveInfo.put("capture", move.isCapture());
                    return moveInfo;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(historyList);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Inicia um novo jogo, reiniciando todas as peças e estados
     * 
     * ENDEREÇO: POST /api/new-game
     * 
     * RETORNA:
     * - success: Se a operação foi bem-sucedida
     * - message: Mensagem de confirmação
     * - currentTurn: Quem começa o novo jogo
     * 
     * USADO POR: Botão "Novo Jogo" no frontend
     */
    @PostMapping("/new-game")
    public ResponseEntity<Map<String, Object>> newGame() {
        try {
            // Chamar método de reset do backend
            chessGame.resetGame();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Novo jogo iniciado!");
            response.put("currentTurn", chessGame.getCurrentTurn().name());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erro ao iniciar novo jogo: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Obtém estado resumido do jogo para atualizações rápidas
     * 
     * ENDEREÇO: GET /api/state
     * 
     * VERSÃO SIMPLIFICADA do endpoint /board, usada para polling frequente
     * 
     * RETORNA: Informações essenciais sobre o estado atual do jogo
     */
    @GetMapping("/state")
    public ResponseEntity<Map<String, Object>> getGameState() {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("currentTurn", chessGame.getCurrentTurn().name());
            state.put("gameOver", chessGame.isGameOver());
            state.put("size", 14);
            
            // Adicionar informações resumidas dos jogadores
            Map<String, Map<String, Object>> playersInfo = new HashMap<>();
            for (Color color : Color.values()) {
                Player player = chessGame.getPlayer(color);
                if (player != null) {
                    Map<String, Object> playerData = new HashMap<>();
                    playerData.put("eliminated", player.isEliminated());
                    playerData.put("timeRemaining", formatTime(player.getClock().getTimeRemaining()));
                    playersInfo.put(color.name(), playerData);
                }
            }
            state.put("players", playersInfo);
            
            return ResponseEntity.ok(state);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * ===== ENDPOINTS RELACIONADOS AOS JOGADORES =====
     */
    
    /**
     * Obtém status de todos os jogadores
     * 
     * ENDEREÇO: GET /api/players
     * 
     * RETORNA: Informações detalhadas sobre cada jogador:
     * - color: Cor técnica
     * - displayName: Nome em português
     * - eliminated: Se foi eliminado
     * - active: Se é o jogador atual
     * - timeRemaining: Tempo restante do relógio
     * 
     * USADO POR: Sidebar de jogadores no frontend
     */
    @GetMapping("/players")
    public ResponseEntity<Map<String, Map<String, Object>>> getPlayersStatus() {
        try {
            Map<String, Map<String, Object>> playersData = new HashMap<>();
            
            // Coletar informações de cada jogador
            for (Color color : Color.values()) {
                Player player = chessGame.getPlayer(color);
                if (player != null) {
                    Map<String, Object> playerInfo = new HashMap<>();
                    playerInfo.put("color", color.name());
                    playerInfo.put("displayName", getColorDisplayName(color));
                    playerInfo.put("eliminated", player.isEliminated());
                    playerInfo.put("active", chessGame.getCurrentTurn() == color);
                    playerInfo.put("timeRemaining", formatTime(player.getClock().getTimeRemaining()));
                    playersData.put(color.name(), playerInfo);
                }
            }
            
            return ResponseEntity.ok(playersData);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtém informações de um jogador específico
     * 
     * ENDEREÇO: GET /api/player/{color}
     * 
     * PARÂMETRO:
     * - color: Cor do jogador (red, blue, green, yellow)
     * 
     * RETORNA: Informações detalhadas do jogador específico
     */
    @GetMapping("/player/{color}")
    public ResponseEntity<Map<String, Object>> getPlayer(@PathVariable String color) {
        try {
            // Converter nome da cor para enum
            Color playerColor = Color.valueOf(color.toUpperCase());
            Player player = chessGame.getPlayer(playerColor);
            
            if (player == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Preparar dados do jogador
            Map<String, Object> playerData = new HashMap<>();
            playerData.put("color", playerColor.name());
            playerData.put("displayName", getColorDisplayName(playerColor));
            playerData.put("eliminated", player.isEliminated());
            playerData.put("timeRemaining", player.getClock().getTimeRemaining());
            playerData.put("timeRemainingFormatted", formatTime(player.getClock().getTimeRemaining()));
            playerData.put("isCurrentTurn", chessGame.getCurrentTurn() == playerColor);
            
            return ResponseEntity.ok(playerData);
            
        } catch (IllegalArgumentException e) {
            // Cor inválida foi fornecida
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Cor inválida: " + color);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * ===== MÉTODOS AUXILIARES PRIVADOS =====
     */
    
    /**
     * Converte enum Color para nome em português para exibição
     * 
     * @param color Enum da cor
     * @return Nome da cor em português
     */
    private String getColorDisplayName(Color color) {
        return switch (color) {
            case RED -> "Vermelho";
            case BLUE -> "Azul";
            case GREEN -> "Verde";
            case YELLOW -> "Amarelo";
        };
    }
    
    /**
     * Converte tempo em nanosegundos para formato legível (MM:SS)
     * 
     * @param timeInNanos Tempo em nanosegundos
     * @return String formatada como "MM:SS"
     */
    private String formatTime(long timeInNanos) {
        // Converter nanosegundos para segundos
        long timeInSeconds = timeInNanos / 1_000_000_000;
        
        // Calcular minutos e segundos
        long minutes = timeInSeconds / 60;
        long seconds = timeInSeconds % 60;
        
        // Formatar com zero à esquerda se necessário
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * ===== CLASSES DE REQUISIÇÃO =====
     */
    
    /**
     * Classe que define a estrutura de dados para requisições de movimento
     * 
     * Esta classe é usada para接收er dados do frontend quando um jogador
     * tenta fazer um movimento. O Spring Boot automaticamente converte
     * o JSON da requisição para objetos desta classe.
     */
    public static class MoveRequest {
        // Posição atual da peça (origem)
        private int fromRow;
        private int fromCol;
        
        // Posição de destino
        private int toRow;
        private int toCol;
        
        // Métodos getter/setter (obrigatórios para Spring Boot)
        public int getFromRow() { return fromRow; }
        public void setFromRow(int fromRow) { this.fromRow = fromRow; }
        
        public int getFromCol() { return fromCol; }
        public void setFromCol(int fromCol) { this.fromCol = fromCol; }
        
        public int getToRow() { return toRow; }
        public void setToRow(int toRow) { this.toRow = toRow; }
        
        public int getToCol() { return toCol; }
        public void setToCol(int toCol) { this.toCol = toCol; }
    }
}
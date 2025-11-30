package org.chess.board.persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.chess.*;
import org.chess.board.Board;
import org.chess.board.History;
import org.chess.pieces.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Serializa e deserializa o jogo COMPLETO incluindo todo o histórico.
 * Captura o estado atual sem modificar classes existentes.
 */
public class JsonGameSerializer {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Converte o estado COMPLETO do jogo para JSON.
     * Reconstrói as informações de origem dos movimentos através de simulação reversa.
     */
    public static String toJson(App app) throws Exception {
        JsonGameState state = new JsonGameState();
        
        // Acessar campos privados
        Field boardField = App.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Board board = (Board) boardField.get(app);

        Field playersField = App.class.getDeclaredField("players");
        playersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Color, Player> players = (Map<Color, Player>) playersField.get(app);

        // Acessar histórico do Board
        History history = board.history;

        // Criar mapeamento de peças para IDs
        Map<Piece, String> pieceToIdMap = createPieceIdMap(players);
        Map<String, Piece> idToPieceMap = createReversePieceMap(pieceToIdMap);

        // === INFORMAÇÕES DO JOGO ===
        JsonGameState.GameInfo gameInfo = new JsonGameState.GameInfo();
        gameInfo.setCurrentTurn(app.getCurrentTurn().name());
        gameInfo.setGameOver(app.isGameOver());
        
        List<Move> allMoves = history.getMoves();
        gameInfo.setTotalMoves(allMoves.size());
        
        // Detectar jogadores eliminados
        List<String> eliminated = new ArrayList<>();
        for (Color color : Color.values()) {
            Player player = players.get(color);
            if (player != null) {
                Piece king = player.pieces.get(PieceType.KING);
                if (board.getPos(king) == null) {
                    eliminated.add(color.name());
                }
            }
        }
        gameInfo.setEliminatedPlayers(eliminated);
        state.setGameInfo(gameInfo);

        // === ESTADO DO TABULEIRO ===
        JsonGameState.BoardState boardState = new JsonGameState.BoardState();
        List<JsonGameState.PiecePosition> pieces = new ArrayList<>();
        Map<String, JsonGameState.PieceMetadata> pieceMetadata = new HashMap<>();
        
        for (Pos pos : Pos.getValidPositions()) {
            Piece piece = board.getPiece(pos);
            if (piece != null) {
                String pieceId = pieceToIdMap.get(piece);
                
                // Adicionar posição da peça
                JsonGameState.Position position = new JsonGameState.Position(pos.row(), pos.column());
                String pieceType = getPieceTypeName(piece);
                
                JsonGameState.Piece jsonPiece = new JsonGameState.Piece(
                    pieceType,
                    piece.color.name(),
                    pieceId
                );
                
                pieces.add(new JsonGameState.PiecePosition(position, jsonPiece));
                
                // Adicionar metadados da peça
                JsonGameState.PieceMetadata metadata = new JsonGameState.PieceMetadata();
                metadata.setPieceId(pieceId);
                metadata.setHasMoved(history.movedBefore(piece));
                
                List<Move> pieceMoves = history.getMoves(piece);
                metadata.setMoveCount(pieceMoves.size());
                
                if (!pieceMoves.isEmpty()) {
                    Move lastMove = pieceMoves.get(pieceMoves.size() - 1);
                    metadata.setLastMoveType(lastMove.type().name());
                }
                
                pieceMetadata.put(pieceId, metadata);
            }
        }
        
        boardState.setPieces(pieces);
        boardState.setPieceMetadata(pieceMetadata);
        state.setBoardState(boardState);

        // === ESTADO DOS JOGADORES ===
        Map<String, JsonGameState.PlayerState> playerStates = new HashMap<>();
        for (Color color : Color.values()) {
            Player player = players.get(color);
            if (player != null) {
                JsonGameState.PlayerState playerState = new JsonGameState.PlayerState();
                playerState.setColor(color.name());
                playerState.setTimeLeftNanos(player.clock.getTimeLeftNanosecs());
                
                // Acessar estado do Clock
                Field pausedField = Clock.class.getDeclaredField("paused");
                pausedField.setAccessible(true);
                boolean isPaused = (boolean) pausedField.get(player.clock);
                playerState.setClockPaused(isPaused);
                
                Field resumedTimestampField = Clock.class.getDeclaredField("resumedTimestamp");
                resumedTimestampField.setAccessible(true);
                long resumedTimestamp = (long) resumedTimestampField.get(player.clock);
                playerState.setLastResumedTimestamp(resumedTimestamp);
                
                playerState.setEliminated(eliminated.contains(color.name()));
                playerState.setMovesCount(history.getMoves(color).size());
                
                // Lista de peças capturadas
                List<String> capturedPieces = new ArrayList<>();
                for (Map.Entry<PieceType, Piece> entry : player.pieces.entrySet()) {
                    Piece piece = entry.getValue();
                    if (board.getPos(piece) == null) {
                        capturedPieces.add(pieceToIdMap.get(piece));
                    }
                }
                playerState.setCapturedPieces(capturedPieces);
                
                playerStates.put(color.name(), playerState);
            }
        }
        state.setPlayers(playerStates);

        // === HISTÓRICO COMPLETO COM RECONSTRUÇÃO ===
        JsonGameState.HistoryState historyState = buildCompleteHistory(
            allMoves, board, players, pieceToIdMap
        );
        state.setHistory(historyState);

        return gson.toJson(state);
    }

    /**
     * Reconstrói o histórico completo através de simulação do jogo.
     * Cria um jogo inicial e replica cada movimento para capturar as posições de origem.
     */
    private static JsonGameState.HistoryState buildCompleteHistory(
            List<Move> originalMoves, Board currentBoard, 
            Map<Color, Player> currentPlayers, Map<Piece, String> pieceToIdMap) {
        
        JsonGameState.HistoryState historyState = new JsonGameState.HistoryState();
        List<JsonGameState.MoveRecord> moveRecords = new ArrayList<>();
        
        // Criar um jogo simulado para rastrear posições
        Map<Piece, Pos> simulatedPositions = new HashMap<>();
        
        // Inicializar posições iniciais
        for (Color color : Color.values()) {
            Player player = currentPlayers.get(color);
            if (player != null) {
                for (PieceType pieceType : PieceType.values()) {
                    Piece piece = player.pieces.get(pieceType);
                    Pos initialPos = pieceType.initialPos(color);
                    simulatedPositions.put(piece, initialPos);
                }
            }
        }
        
        // Processar cada movimento
        for (int i = 0; i < originalMoves.size(); i++) {
            Move move = originalMoves.get(i);
            JsonGameState.MoveRecord record = new JsonGameState.MoveRecord();
            
            record.setMoveNumber(i + 1);
            record.setTimestamp(System.currentTimeMillis()); // Aproximação
            
            Piece movingPiece = move.piece();
            record.setPieceId(pieceToIdMap.get(movingPiece));
            record.setPieceType(getPieceTypeName(movingPiece));
            record.setPieceColor(movingPiece.color.name());
            
            // Capturar posição de origem da simulação
            Pos fromPos = simulatedPositions.get(movingPiece);
            if (fromPos != null) {
                record.setFromPosition(new JsonGameState.Position(fromPos.row(), fromPos.column()));
            }
            
            Pos toPos = move.toPos();
            record.setToPosition(new JsonGameState.Position(toPos.row(), toPos.column()));
            record.setMoveType(move.type().name());
            
            // Verificar se houve captura
            Piece capturedPiece = findPieceAtPosition(simulatedPositions, toPos, movingPiece);
            if (capturedPiece != null) {
                JsonGameState.CaptureInfo captureInfo = new JsonGameState.CaptureInfo();
                captureInfo.setCapturedPieceId(pieceToIdMap.get(capturedPiece));
                captureInfo.setCapturedPieceType(getPieceTypeName(capturedPiece));
                captureInfo.setCapturedPieceColor(capturedPiece.color.name());
                captureInfo.setCapturedAtPosition(new JsonGameState.Position(toPos.row(), toPos.column()));
                record.setCaptureInfo(captureInfo);
                
                // Remover peça capturada da simulação
                simulatedPositions.remove(capturedPiece);
            }
            
            // En passant
            if (move.enPassantVictim() != null) {
                record.setEnPassantVictimId(pieceToIdMap.get(move.enPassantVictim()));
                simulatedPositions.remove(move.enPassantVictim());
            }
            
            // Atualizar posição simulada
            simulatedPositions.put(movingPiece, toPos);
            
            // Tratamento especial para roque
            if (move.type() == Move.MoveType.KINGSIDE_CASTLING || 
                move.type() == Move.MoveType.QUEENSIDE_CASTLING) {
                handleCastlingInSimulation(move, movingPiece.color, simulatedPositions, currentPlayers);
            }
            
            // Notação
            record.setNotation(generateNotation(move, fromPos, toPos, capturedPiece != null));
            
            moveRecords.add(record);
        }
        
        historyState.setAllMoves(moveRecords);
        
        // Índices por cor
        Map<String, List<Integer>> movesByColor = new HashMap<>();
        for (Color color : Color.values()) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < originalMoves.size(); i++) {
                if (originalMoves.get(i).piece().color == color) {
                    indices.add(i);
                }
            }
            movesByColor.put(color.name(), indices);
        }
        historyState.setMovesByColor(movesByColor);
        
        // Índices por peça
        Map<String, List<Integer>> movesByPiece = new HashMap<>();
        for (int i = 0; i < originalMoves.size(); i++) {
            Move move = originalMoves.get(i);
            String pieceId = pieceToIdMap.get(move.piece());
            movesByPiece.computeIfAbsent(pieceId, k -> new ArrayList<>()).add(i);
        }
        historyState.setMovesByPiece(movesByPiece);
        
        return historyState;
    }

    /**
     * Encontra uma peça em uma posição específica na simulação.
     */
    private static Piece findPieceAtPosition(Map<Piece, Pos> positions, Pos targetPos, Piece excludePiece) {
        for (Map.Entry<Piece, Pos> entry : positions.entrySet()) {
            if (entry.getKey() != excludePiece && entry.getValue().equals(targetPos)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Atualiza as posições das torres durante o roque na simulação.
     */
    private static void handleCastlingInSimulation(Move move, Color color, 
                                                   Map<Piece, Pos> positions, 
                                                   Map<Color, Player> players) {
        Player player = players.get(color);
        if (player == null) return;
        
        if (move.type() == Move.MoveType.KINGSIDE_CASTLING) {
            Piece rook = player.pieces.get(PieceType.KINGSIDE_ROOK);
            Pos rookNewPos = PieceType.KINGSIDE_BISHOP.initialPos(color);
            positions.put(rook, rookNewPos);
        } else if (move.type() == Move.MoveType.QUEENSIDE_CASTLING) {
            Piece rook = player.pieces.get(PieceType.QUEENSIDE_ROOK);
            Pos rookNewPos = PieceType.QUEEN.initialPos(color);
            positions.put(rook, rookNewPos);
        }
    }

    /**
     * Gera notação algébrica do movimento.
     */
    private static String generateNotation(Move move, Pos fromPos, Pos toPos, boolean isCapture) {
        StringBuilder notation = new StringBuilder();
        
        Piece piece = move.piece();
        
        // Roque tem notação especial
        if (move.type() == Move.MoveType.KINGSIDE_CASTLING) {
            return "O-O";
        } else if (move.type() == Move.MoveType.QUEENSIDE_CASTLING) {
            return "O-O-O";
        }
        
        // Tipo da peça (exceto peão)
        if (!(piece instanceof Pawn)) {
            notation.append(getPieceTypeName(piece).charAt(0));
        }
        
        // Para peão em captura, adicionar coluna de origem
        if (piece instanceof Pawn && isCapture && fromPos != null) {
            notation.append((char)('a' + fromPos.column() - 1));
        }
        
        // Captura
        if (isCapture || move.type() == Move.MoveType.EN_PASSANT) {
            notation.append("x");
        }
        
        // Posição de destino
        notation.append((char)('a' + toPos.column() - 1));
        notation.append(15 - toPos.row());
        
        // Promoção
        switch (move.type()) {
            case QUEEN_PROMOTION -> notation.append("=Q");
            case ROOK_PROMOTION -> notation.append("=R");
            case BISHOP_PROMOTION -> notation.append("=B");
            case KNIGHT_PROMOTION -> notation.append("=N");
            default -> {}
        }
        
        return notation.toString();
    }

    /**
     * Reconstrói o jogo COMPLETO a partir de JSON.
     */
    public static void fromJson(App app, String json) throws Exception {
        JsonGameState state = gson.fromJson(json, JsonGameState.class);

        Field boardField = App.class.getDeclaredField("board");
        boardField.setAccessible(true);

        Field playersField = App.class.getDeclaredField("players");
        playersField.setAccessible(true);

        Field currentTurnField = App.class.getDeclaredField("currentTurn");
        currentTurnField.setAccessible(true);

        Field gameOverField = App.class.getDeclaredField("gameOver");
        gameOverField.setAccessible(true);

        // Criar jogadores com tempos corretos
        Map<Color, Player> newPlayers = new EnumMap<>(Color.class);
        for (Color color : Color.values()) {
            JsonGameState.PlayerState playerState = state.getPlayers().get(color.name());
            long timeLeft = (playerState != null) ? playerState.getTimeLeftNanos() : 10L * 60L * 1000000000L;
            
            Clock clock = new Clock(timeLeft);
            
            // Restaurar estado do clock
            if (playerState != null) {
                Field pausedField = Clock.class.getDeclaredField("paused");
                pausedField.setAccessible(true);
                pausedField.set(clock, playerState.isClockPaused());
                
                Field resumedTimestampField = Clock.class.getDeclaredField("resumedTimestamp");
                resumedTimestampField.setAccessible(true);
                resumedTimestampField.set(clock, playerState.getLastResumedTimestamp());
            }
            
            Player player = new Player(clock, color);
            newPlayers.put(color, player);
        }

        // Mapear peças
        Map<String, Piece> pieceMap = createPieceMap(newPlayers);
        Map<Pos, Piece> initialState = new HashMap<>();

        for (JsonGameState.PiecePosition piecePos : state.getBoardState().getPieces()) {
            Pos pos = new Pos(piecePos.getPosition().getRow(), piecePos.getPosition().getColumn());
            String pieceId = piecePos.getPiece().getId();
            
            Piece piece = pieceMap.get(pieceId);
            if (piece != null) {
                initialState.put(pos, piece);
            }
        }

        // Criar tabuleiro
        Board newBoard = new Board(initialState);
        
        // RESTAURAR O HISTÓRICO através de reflexão
        restoreHistoryViaReflection(newBoard, state.getHistory(), pieceMap);

        // Atualizar App
        boardField.set(app, newBoard);
        playersField.set(app, newPlayers);
        currentTurnField.set(app, Color.valueOf(state.getGameInfo().getCurrentTurn()));
        gameOverField.set(app, state.getGameInfo().isGameOver());

        // Gerenciar relógios
        for (Player player : newPlayers.values()) {
            Field pausedField = Clock.class.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(player.clock)) {
                player.clock.pause();
            }
        }
        
        if (!state.getGameInfo().isGameOver()) {
            Color currentTurn = Color.valueOf(state.getGameInfo().getCurrentTurn());
            newPlayers.get(currentTurn).clock.resume();
        }
    }

    /**
     * Restaura o histórico usando reflexão (sem modificar Board).
     */
    private static void restoreHistoryViaReflection(Board board, JsonGameState.HistoryState historyState, 
                                                    Map<String, Piece> pieceMap) throws Exception {
        History history = board.history;

        Field gameHistoryField = History.class.getDeclaredField("gameHistory");
        gameHistoryField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Move> gameHistory = (List<Move>) gameHistoryField.get(history);

        // Reconstruir movimentos
        for (JsonGameState.MoveRecord record : historyState.getAllMoves()) {
            Piece piece = pieceMap.get(record.getPieceId());
            if (piece == null) continue;
            
            Pos toPos = new Pos(record.getToPosition().getRow(), record.getToPosition().getColumn());
            Move.MoveType moveType = Move.MoveType.valueOf(record.getMoveType());
            
            Pawn enPassantVictim = null;
            if (record.getEnPassantVictimId() != null) {
                Piece victim = pieceMap.get(record.getEnPassantVictimId());
                if (victim instanceof Pawn) {
                    enPassantVictim = (Pawn) victim;
                }
            }
            
            Move move = new Move(piece, moveType, toPos, enPassantVictim);
            gameHistory.add(move);
        }
    }

    // Métodos auxiliares
    private static Map<Piece, String> createPieceIdMap(Map<Color, Player> players) {
        Map<Piece, String> map = new HashMap<>();
        for (Color color : Color.values()) {
            Player player = players.get(color);
            if (player == null) continue;
            
            for (Map.Entry<PieceType, Piece> entry : player.pieces.entrySet()) {
                map.put(entry.getValue(), color.name() + "_" + entry.getKey().name());
            }
        }
        return map;
    }

    private static Map<String, Piece> createReversePieceMap(Map<Piece, String> pieceToIdMap) {
        Map<String, Piece> map = new HashMap<>();
        for (Map.Entry<Piece, String> entry : pieceToIdMap.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        return map;
    }

    private static Map<String, Piece> createPieceMap(Map<Color, Player> players) {
        Map<String, Piece> map = new HashMap<>();
        for (Color color : Color.values()) {
            Player player = players.get(color);
            if (player == null) continue;
            
            for (Map.Entry<PieceType, Piece> entry : player.pieces.entrySet()) {
                map.put(color.name() + "_" + entry.getKey().name(), entry.getValue());
            }
        }
        return map;
    }

    private static String getPieceTypeName(Piece piece) {
        if (piece instanceof Pawn) return "PAWN";
        if (piece instanceof Rook) return "ROOK";
        if (piece instanceof Knight) return "KNIGHT";
        if (piece instanceof Bishop) return "BISHOP";
        if (piece instanceof Queen) return "QUEEN";
        if (piece instanceof King) return "KING";
        return "UNKNOWN";
    }
}
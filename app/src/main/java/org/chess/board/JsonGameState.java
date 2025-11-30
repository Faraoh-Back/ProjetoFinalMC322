package org.chess.board;

import java.util.List;
import java.util.Map;

/**
 * Classe que representa o estado completo do jogo em formato serializável.
 * Contém todas as informações necessárias para salvar e carregar uma partida.
 */
public class JsonGameState {
    private GameInfo gameInfo;
    private BoardState boardState;
    private Map<String, PlayerState> players;
    private HistoryState history;

    // Getters e Setters principais
    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    public Map<String, PlayerState> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, PlayerState> players) {
        this.players = players;
    }

    public HistoryState getHistory() {
        return history;
    }

    public void setHistory(HistoryState history) {
        this.history = history;
    }

    // ===== CLASSES INTERNAS =====

    /**
     * Informações gerais do jogo
     */
    public static class GameInfo {
        private String currentTurn;
        private boolean gameOver;
        private int totalMoves;
        private List<String> eliminatedPlayers;

        public String getCurrentTurn() {
            return currentTurn;
        }

        public void setCurrentTurn(String currentTurn) {
            this.currentTurn = currentTurn;
        }

        public boolean isGameOver() {
            return gameOver;
        }

        public void setGameOver(boolean gameOver) {
            this.gameOver = gameOver;
        }

        public int getTotalMoves() {
            return totalMoves;
        }

        public void setTotalMoves(int totalMoves) {
            this.totalMoves = totalMoves;
        }

        public List<String> getEliminatedPlayers() {
            return eliminatedPlayers;
        }

        public void setEliminatedPlayers(List<String> eliminatedPlayers) {
            this.eliminatedPlayers = eliminatedPlayers;
        }
    }

    /**
     * Estado do tabuleiro
     */
    public static class BoardState {
        private List<PiecePosition> pieces;
        private Map<String, PieceMetadata> pieceMetadata;

        public List<PiecePosition> getPieces() {
            return pieces;
        }

        public void setPieces(List<PiecePosition> pieces) {
            this.pieces = pieces;
        }

        public Map<String, PieceMetadata> getPieceMetadata() {
            return pieceMetadata;
        }

        public void setPieceMetadata(Map<String, PieceMetadata> pieceMetadata) {
            this.pieceMetadata = pieceMetadata;
        }
    }

    /**
     * Posição de uma peça no tabuleiro
     */
    public static class PiecePosition {
        private Position position;
        private Piece piece;

        public PiecePosition(Position position, Piece piece) {
            this.position = position;
            this.piece = piece;
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Piece getPiece() {
            return piece;
        }

        public void setPiece(Piece piece) {
            this.piece = piece;
        }
    }

    /**
     * Coordenadas de posição
     */
    public static class Position {
        private int row;
        private int column;

        public Position(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }
    }

    /**
     * Informações da peça
     */
    public static class Piece {
        private String type;
        private String color;
        private String id;

        public Piece(String type, String color, String id) {
            this.type = type;
            this.color = color;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    /**
     * Metadados de uma peça
     */
    public static class PieceMetadata {
        private String pieceId;
        private boolean hasMoved;
        private int moveCount;
        private String lastMoveType;

        public String getPieceId() {
            return pieceId;
        }

        public void setPieceId(String pieceId) {
            this.pieceId = pieceId;
        }

        public boolean isHasMoved() {
            return hasMoved;
        }

        public void setHasMoved(boolean hasMoved) {
            this.hasMoved = hasMoved;
        }

        public int getMoveCount() {
            return moveCount;
        }

        public void setMoveCount(int moveCount) {
            this.moveCount = moveCount;
        }

        public String getLastMoveType() {
            return lastMoveType;
        }

        public void setLastMoveType(String lastMoveType) {
            this.lastMoveType = lastMoveType;
        }
    }

    /**
     * Estado de um jogador
     */
    public static class PlayerState {
        private String color;
        private long timeLeftNanos;
        private boolean clockPaused;
        private long lastResumedTimestamp;
        private boolean eliminated;
        private int movesCount;
        private List<String> capturedPieces;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public long getTimeLeftNanos() {
            return timeLeftNanos;
        }

        public void setTimeLeftNanos(long timeLeftNanos) {
            this.timeLeftNanos = timeLeftNanos;
        }

        public boolean isClockPaused() {
            return clockPaused;
        }

        public void setClockPaused(boolean clockPaused) {
            this.clockPaused = clockPaused;
        }

        public long getLastResumedTimestamp() {
            return lastResumedTimestamp;
        }

        public void setLastResumedTimestamp(long lastResumedTimestamp) {
            this.lastResumedTimestamp = lastResumedTimestamp;
        }

        public boolean isEliminated() {
            return eliminated;
        }

        public void setEliminated(boolean eliminated) {
            this.eliminated = eliminated;
        }

        public int getMovesCount() {
            return movesCount;
        }

        public void setMovesCount(int movesCount) {
            this.movesCount = movesCount;
        }

        public List<String> getCapturedPieces() {
            return capturedPieces;
        }

        public void setCapturedPieces(List<String> capturedPieces) {
            this.capturedPieces = capturedPieces;
        }
    }

    /**
     * Estado do histórico de movimentos
     */
    public static class HistoryState {
        private List<MoveRecord> allMoves;
        private Map<String, List<Integer>> movesByColor;
        private Map<String, List<Integer>> movesByPiece;

        public List<MoveRecord> getAllMoves() {
            return allMoves;
        }

        public void setAllMoves(List<MoveRecord> allMoves) {
            this.allMoves = allMoves;
        }

        public Map<String, List<Integer>> getMovesByColor() {
            return movesByColor;
        }

        public void setMovesByColor(Map<String, List<Integer>> movesByColor) {
            this.movesByColor = movesByColor;
        }

        public Map<String, List<Integer>> getMovesByPiece() {
            return movesByPiece;
        }

        public void setMovesByPiece(Map<String, List<Integer>> movesByPiece) {
            this.movesByPiece = movesByPiece;
        }
    }

    /**
     * Registro de um movimento
     */
    public static class MoveRecord {
        private int moveNumber;
        private long timestamp;
        private String pieceId;
        private String pieceType;
        private String pieceColor;
        private Position fromPosition;
        private Position toPosition;
        private String moveType;
        private CaptureInfo captureInfo;
        private String enPassantVictimId;
        private String notation;

        public int getMoveNumber() {
            return moveNumber;
        }

        public void setMoveNumber(int moveNumber) {
            this.moveNumber = moveNumber;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getPieceId() {
            return pieceId;
        }

        public void setPieceId(String pieceId) {
            this.pieceId = pieceId;
        }

        public String getPieceType() {
            return pieceType;
        }

        public void setPieceType(String pieceType) {
            this.pieceType = pieceType;
        }

        public String getPieceColor() {
            return pieceColor;
        }

        public void setPieceColor(String pieceColor) {
            this.pieceColor = pieceColor;
        }

        public Position getFromPosition() {
            return fromPosition;
        }

        public void setFromPosition(Position fromPosition) {
            this.fromPosition = fromPosition;
        }

        public Position getToPosition() {
            return toPosition;
        }

        public void setToPosition(Position toPosition) {
            this.toPosition = toPosition;
        }

        public String getMoveType() {
            return moveType;
        }

        public void setMoveType(String moveType) {
            this.moveType = moveType;
        }

        public CaptureInfo getCaptureInfo() {
            return captureInfo;
        }

        public void setCaptureInfo(CaptureInfo captureInfo) {
            this.captureInfo = captureInfo;
        }

        public String getEnPassantVictimId() {
            return enPassantVictimId;
        }

        public void setEnPassantVictimId(String enPassantVictimId) {
            this.enPassantVictimId = enPassantVictimId;
        }

        public String getNotation() {
            return notation;
        }

        public void setNotation(String notation) {
            this.notation = notation;
        }
    }

    /**
     * Informações sobre captura
     */
    public static class CaptureInfo {
        private String capturedPieceId;
        private String capturedPieceType;
        private String capturedPieceColor;
        private Position capturedAtPosition;

        public String getCapturedPieceId() {
            return capturedPieceId;
        }

        public void setCapturedPieceId(String capturedPieceId) {
            this.capturedPieceId = capturedPieceId;
        }

        public String getCapturedPieceType() {
            return capturedPieceType;
        }

        public void setCapturedPieceType(String capturedPieceType) {
            this.capturedPieceType = capturedPieceType;
        }

        public String getCapturedPieceColor() {
            return capturedPieceColor;
        }

        public void setCapturedPieceColor(String capturedPieceColor) {
            this.capturedPieceColor = capturedPieceColor;
        }

        public Position getCapturedAtPosition() {
            return capturedAtPosition;
        }

        public void setCapturedAtPosition(Position capturedAtPosition) {
            this.capturedAtPosition = capturedAtPosition;
        }
    }
}
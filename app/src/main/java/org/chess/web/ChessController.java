package org.chess.web;

import org.chess.App;
import org.chess.Color; // Importante: Importar Color
import org.chess.Move;
import org.chess.Pos;
import org.chess.exception.InvalidPosition;
import org.chess.pieces.Bishop;
import org.chess.pieces.King;
import org.chess.pieces.Knight;
import org.chess.pieces.Pawn;
import org.chess.pieces.Piece;
import org.chess.pieces.Queen;
import org.chess.pieces.Rook;

import spark.ModelAndView;
import spark.Request;

import java.util.*;

public class ChessController {
    private final App app;

    // ESTADO DA SESSÃO
    private Pos selectedPos = null;
    private List<Move> currentContextMoves = new ArrayList<>();
    
    // ESTADO VISUAL
    private Color lastTurn = Color.GREEN;
    private int boardRotation = 0; // Acumula o ângulo (ex: 0, -90, -180, -270, -360...)
    private boolean doTransition = false;

    public ChessController(App app) {
        this.app = app;
    }

    public ModelAndView resetGame(Request req) {
        app.resetGame();
        clearSelection();
        // Reseta o estado visual
        lastTurn = Color.GREEN;
        doTransition = false;
        boardRotation = 0;
        return renderBoard(req);
    }

    public ModelAndView renderBoard(Request req) {
        Map<String, Object> model = new HashMap<>();
        
        // --- LÓGICA DE ROTAÇÃO INFINITA ---
        Color currentTurn = app.getCurrentTurn();
        if (currentTurn != lastTurn) {
            // Se o turno avançou para o "próximo" (Esquerda/LeftColor), giramos -90
            if (currentTurn == lastTurn.getLeftColor()) {
                boardRotation -= 90;
            } 
            // Se o jogo foi revertido ou algo do tipo
            else if (currentTurn == lastTurn.getRightColor()) {
                boardRotation += 90;
            }
            // Fallback para reset (ex: jogo reiniciou de forma inesperada sem passar pelo método resetGame)
            else {
                 switch(currentTurn) {
                     case GREEN -> boardRotation = 0;
                     case YELLOW -> boardRotation = -90;
                     case RED -> boardRotation = -180;
                     case BLUE -> boardRotation = -270;
                 }
            }
            lastTurn = currentTurn;
        }
        
        // Passa o ângulo calculado para o template
        model.put("boardRotation", boardRotation);
        model.put("doTransition", doTransition);
        doTransition = false;
        List<List<SquareView>> grid = new ArrayList<>();

        Map<Pos, Integer> moveMap = new HashMap<>();
        for (int i = 0; i < currentContextMoves.size(); i++) {
            Move m = currentContextMoves.get(i);
            if (!moveMap.containsKey(m.toPos())) {
                moveMap.put(m.toPos(), i);
            }
        }

        for (int r = 1; r <= 14; r++) {
            List<SquareView> rowList = new ArrayList<>();
            for (int c = 1; c <= 14; c++) {
                try {
                    Pos currentPos = new Pos(r, c);
                    Piece piece = app.getPiece(currentPos);
                    
                    boolean isSelected = selectedPos != null && selectedPos.equals(currentPos);
                    boolean isTarget = moveMap.containsKey(currentPos);
                    Integer moveIndex = moveMap.get(currentPos);

                    String hxUrl;
                    String hxVerb;

                    if (isTarget) {
                        hxVerb = "post";
                        hxUrl = "/move?moveIndex=" + moveIndex;
                    } else {
                        hxVerb = "get";
                        hxUrl = "/select?row=" + r + "&col=" + c;
                    }

                    rowList.add(new SquareView(r, c, false, piece, hxUrl, hxVerb, isSelected, isTarget));
                } catch (InvalidPosition e) {
                    rowList.add(new SquareView(r, c, true, null, null, "none", false, false));
                    continue;
                }

            }
            grid.add(rowList);
        }

        model.put("grid", grid);
        model.put("currentTurn", app.getCurrentTurn());
        model.put("gameOver", app.isGameOver());

        return new ModelAndView(model, "board");
    }

    public ModelAndView handleSelection(Request req, int row, int col) {
        try {
            Pos currentPos = new Pos(row, col);
            Piece piece = app.getPiece(currentPos);

            if (piece != null && piece.color == app.getCurrentTurn()) {
                this.selectedPos = currentPos; // CORREÇÃO: Faltava atualizar a selectedPos
                this.currentContextMoves = new ArrayList<>(app.getPossibleMoves(currentPos));
            } else {
                clearSelection();
            }

        } catch (InvalidPosition e) {
            clearSelection();
        }
        doTransition = false;
        return renderBoard(req);
    }

    public ModelAndView handleMove(Request req, int moveIndex) {
        try {
            if (moveIndex >= 0 && moveIndex < currentContextMoves.size()) {
                Move move = currentContextMoves.get(moveIndex);
                app.doMove(move);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearSelection();
        }
        doTransition = true;
        return renderBoard(req);
    }

    private void clearSelection() {
        this.selectedPos = null;
        this.currentContextMoves.clear();
    }

    public static class SquareView {
        public int row, col;
        public boolean isVoid;
        public Piece piece;
        public String hxUrl;
        public String hxVerb;
        public boolean isSelected;
        public boolean isTarget;

        public SquareView(int r, int c, boolean isVoid, Piece piece, String url, String verb, boolean sel,
                boolean target) {
            this.row = r;
            this.col = c;
            this.isVoid = isVoid;
            this.piece = piece;
            this.hxUrl = url;
            this.hxVerb = verb;
            this.isSelected = sel;
            this.isTarget = target;
        }

        public String getSymbol() {
            if (piece == null)
                return "";
            if (piece instanceof Pawn)
                return "♙";
            if (piece instanceof Rook)
                return "♖";
            if (piece instanceof Knight)
                return "♘";
            if (piece instanceof Bishop)
                return "♗";
            if (piece instanceof Queen)
                return "♕";
            if (piece instanceof King)
                return "♔";
            else
                return "?";
        }

        public String getCssClass() {
            if (isVoid)
                return "void";
            if (isTarget)
                return "target";
            if (isSelected)
                return "selected";
            if ((row + col) % 2 == 0)
                return "white-cell";
            return "black-cell";
        }

        public String getPieceColorClass() {
            return piece != null ? piece.color.toString().toLowerCase() : "";
        }
    }

    // ===== MÉTODOS DE PERSISTÊNCIA =====

    public ModelAndView saveGame(Request req) {
        String gameName = req.queryParams("name");
        if (gameName == null || gameName.trim().isEmpty()) {
            return renderError("Nome do jogo é obrigatório para salvar.");
        }
        
        boolean success = app.saveGame(gameName.trim());
        
        if (success) {
            return renderSuccess("Jogo salvo com sucesso: " + gameName.trim());
        } else {
            return renderError("Erro ao salvar o jogo: " + gameName.trim());
        }
    }

    public ModelAndView loadGame(Request req) {
        String gameName = req.queryParams("name");
        if (gameName == null || gameName.trim().isEmpty()) {
            return renderError("Nome do jogo é obrigatório para carregar.");
        }
        
        boolean success = app.loadGame(gameName.trim());
        
        if (success) {
            clearSelection(); // Limpar seleção ao carregar novo jogo
            lastTurn = Color.GREEN;
            doTransition = false;
            boardRotation = 0;
            return renderBoard(req);
        } else {
            return renderError("Jogo não encontrado ou erro ao carregar: " + gameName.trim());
        }
    }

    public ModelAndView getSavedGames(Request req) {
        List<String> savedGames = app.getSavedGames();
        Map<String, Object> model = new HashMap<>();
        model.put("savedGames", savedGames);
        model.put("currentTurn", app.getCurrentTurn());
        model.put("gameOver", app.isGameOver());
        
        return new ModelAndView(model, "saved-games");
    }

    public ModelAndView deleteGame(Request req) {
        String gameName = req.queryParams("name");
        if (gameName == null || gameName.trim().isEmpty()) {
            return renderError("Nome do jogo é obrigatório para deletar.");
        }
        
        boolean success = app.deleteSavedGame(gameName.trim());
        
        if (success) {
            return renderSuccess("Jogo deletado com sucesso: " + gameName.trim());
        } else {
            return renderError("Erro ao deletar o jogo: " + gameName.trim());
        }
    }

    private ModelAndView renderSuccess(String message) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", message);
        model.put("boardHtml", "<div class='success' style='padding: 20px; background: #4aff4a; color: black; border-radius: 5px;'>" + message + "</div>");
        model.put("currentTurn", app.getCurrentTurn());
        model.put("gameOver", app.isGameOver());
        model.put("boardRotation", boardRotation);
        model.put("doTransition", doTransition);
        return new ModelAndView(model, "board");
    }

    private ModelAndView renderError(String message) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", message);
        model.put("boardHtml", "<div class='error' style='padding: 20px; background: #ff4a4a; color: white; border-radius: 5px;'>" + message + "</div>");
        model.put("currentTurn", app.getCurrentTurn());
        model.put("gameOver", app.isGameOver());
        model.put("boardRotation", boardRotation);
        model.put("doTransition", doTransition);
        return new ModelAndView(model, "board");
    }
}
package org.chess.web;

import org.chess.App;
import org.chess.Color; // Importante: Importar Color
import org.chess.Move;
import org.chess.Pos;
import org.chess.exception.InvalidPosition;
import org.chess.pieces.Piece;
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

    public ChessController(App app) {
        this.app = app;
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
            // Fallback para reset (ex: jogo reiniciou do zero)
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
                this.currentContextMoves = new ArrayList<>(app.getPossibleMoves(currentPos));
            } else {
                clearSelection();
            }

        } catch (InvalidPosition e) {
            clearSelection();
        }
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
            String name = piece.getClass().getSimpleName();
            if (name.equals("Knight"))
                return "N";
            return name.substring(0, 1);
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
}
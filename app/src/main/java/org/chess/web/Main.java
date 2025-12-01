package org.chess.web;

import static spark.Spark.*;
import org.chess.App;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {
    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/public");

        App gameApp = new App();
        ChessController controller = new ChessController(gameApp);

        // Rota inicial
        get("/", (req, res) -> controller.renderBoard(req), new ThymeleafTemplateEngine());

        // Seleção via GET
        get("/select", (req, res) -> {
            int row = Integer.parseInt(req.queryParams("row"));
            int col = Integer.parseInt(req.queryParams("col"));
            return controller.handleSelection(req, row, col);
        }, new ThymeleafTemplateEngine());

        // Movimento via POST (parâmetro vem na QueryString da URL do POST)
        post("/move", (req, res) -> {
            int moveIndex = Integer.parseInt(req.queryParams("moveIndex"));
            return controller.handleMove(req, moveIndex);
        }, new ThymeleafTemplateEngine());

        // Reiniciar Jogo via POST
        post("/reset", (req, res) -> {
            return controller.resetGame(req);
        }, new ThymeleafTemplateEngine());

        // === ROTAS DE PERSISTÊNCIA ===
        
        // Salvar Jogo via POST (HTML/HTMX)
        post("/save-game", (req, res) -> {
            return controller.saveGame(req);
        }, new ThymeleafTemplateEngine());

        // Salvar Jogo via POST (JSON para JavaScript)
        post("/save-game-json", (req, res) -> {
            res.type("application/json");
            String gameName = req.queryParams("name");
            if (gameName == null || gameName.trim().isEmpty()) {
                return "{\"success\": false, \"error\": \"Nome do jogo é obrigatório para salvar.\"}";
            }
            
            boolean success = gameApp.saveGame(gameName.trim());
            if (success) {
                return "{\"success\": true, \"message\": \"Jogo salvo com sucesso: " + gameName.trim() + "\"}";
            } else {
                return "{\"success\": false, \"error\": \"Erro ao salvar o jogo: " + gameName.trim() + "\"}";
            }
        });

        // Carregar Jogo via POST
        post("/load-game", (req, res) -> {
            return controller.loadGame(req);
        }, new ThymeleafTemplateEngine());

        // Listar Jogos Salvos via GET
        get("/saved-games", (req, res) -> {
            return controller.getSavedGames(req);
        }, new ThymeleafTemplateEngine());

        // Deletar Jogo via POST
        post("/delete-game", (req, res) -> {
            return controller.deleteGame(req);
        }, new ThymeleafTemplateEngine());

        System.out.println("Servidor rodando em http://localhost:8080");
        System.out.println("Rotas configuradas:");
        System.out.println("  GET  /     - Tabuleiro principal");
        System.out.println("  GET  /select?row=X&col=Y - Selecionar peça");
        System.out.println("  POST /move?moveIndex=Z  - Executar movimento");
        System.out.println("  POST /reset              - Reiniciar jogo");
        System.out.println("  POST /save-game?name=X   - Salvar jogo");
        System.out.println("  GET  /load-game?name=X   - Carregar jogo");
        System.out.println("  GET  /saved-games        - Listar jogos salvos");
        System.out.println("  DEL  /delete-game?name=X - Deletar jogo");
    }
}
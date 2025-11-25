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

        System.out.println("Servidor rodando em http://localhost:8080");
    }
}
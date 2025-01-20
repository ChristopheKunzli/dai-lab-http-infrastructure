package ch.dai.http;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(Integer.parseInt(args[0]));
        QuoteController quoteController = new QuoteController();

        app.get("/api/quotes", quoteController::getAll);
        app.get("/api/quotes/{id}", quoteController::getOne);
        app.post("/api/quotes", quoteController::create);
        app.put("/api/quotes/{id}", quoteController::update);
        app.delete("/api/quotes/{id}", quoteController::delete);
    }
}
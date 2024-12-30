package ch.dai.http;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        // Créer une instance du repository pour gérer les citations
        QuoteRepository quoteRepository = new QuoteRepository();

        // CRUD: Create (POST)
        app.post("/quotes", ctx -> {
            String author = ctx.formParam("author");
            String content = ctx.formParam("content");
            Quote createdQuote = quoteRepository.add(author, content);
            ctx.status(201).json(createdQuote);
        });

        // CRUD: Read All (GET)
        app.get("/quotes", ctx -> {
            ctx.json(quoteRepository.getAll());
        });

        // CRUD: Read One (GET)
        app.get("/quotes/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            quoteRepository.getById(id).ifPresentOrElse(
                quote -> ctx.json(quote),
                () -> ctx.status(404).result("Quote not found")
            );
        });

        // CRUD: Update (PUT)
        app.put("/quotes/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            String author = ctx.formParam("author");
            String content = ctx.formParam("content");
            quoteRepository.update(id, author, content).ifPresentOrElse(
                quote -> ctx.json(quote),
                () -> ctx.status(404).result("Quote not found")
            );
        });

        // CRUD: Delete (DELETE)
        app.delete("/quotes/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (quoteRepository.delete(id)) {
                ctx.status(204); // No content
            } else {
                ctx.status(404).result("Quote not found");
            }
        });
    }
}

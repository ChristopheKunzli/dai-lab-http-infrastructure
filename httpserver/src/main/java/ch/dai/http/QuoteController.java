package ch.dai.http;

import io.javalin.http.Context;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class QuoteController {
    private ConcurrentHashMap<Integer, Quote> quotes = new ConcurrentHashMap<>();
    private AtomicInteger lastId = new AtomicInteger(0);

    public QuoteController() {
        // Quelques données de base
        quotes.put(lastId.incrementAndGet(), new Quote("Author1", "Quote1"));
        quotes.put(lastId.incrementAndGet(), new Quote("Author2", "Quote2"));
        quotes.put(lastId.incrementAndGet(), new Quote("Author3", "Quote3"));
    }

    public void getAll(Context ctx) {
        ctx.json(quotes);
    }

    public void getOne(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Quote quote = quotes.get(id);
        if (quote == null) {
            ctx.status(404).result("Quote not found");
        } else {
            ctx.json(quote);
        }
    }

    public void create(Context ctx) {
        Quote quote = ctx.bodyAsClass(Quote.class);
        int id = lastId.incrementAndGet();
        quotes.put(id, quote);
        ctx.status(201).json(quote);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Quote existing = quotes.get(id);
        if (existing == null) {
            ctx.status(404).result("Quote not found");
            return;
        }
        Quote quote = ctx.bodyAsClass(Quote.class);
        quotes.put(id, quote);
        ctx.status(200).json(quote);
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Quote removed = quotes.remove(id);
        if (removed == null) {
            ctx.status(404).result("Quote not found");
        } else {
            ctx.status(204);
        }
    }
}

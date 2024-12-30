package ch.dai.http;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteRepository {
    private List<Quote> quotes = new ArrayList<>();
    private int nextId = 1;

    public List<Quote> getAll() {
        return quotes;
    }

    public Optional<Quote> getById(int id) {
        return quotes.stream().filter(quote -> quote.id == id).findFirst();
    }

    public Quote add(String author, String content) {
        Quote quote = new Quote(nextId++, author, content);
        quotes.add(quote);
        return quote;
    }

    public Optional<Quote> update(int id, String author, String content) {
        Optional<Quote> existingQuote = getById(id);
        existingQuote.ifPresent(quote -> {
            quote.author = author;
            quote.content = content;
        });
        return existingQuote;
    }

    public boolean delete(int id) {
        return quotes.removeIf(quote -> quote.id == id);
    }
}

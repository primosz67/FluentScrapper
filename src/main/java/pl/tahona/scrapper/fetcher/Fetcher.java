package pl.tahona.scrapper.fetcher;

import org.jsoup.nodes.Document;

public interface Fetcher<U> {
    U fetchData(Document document);
}

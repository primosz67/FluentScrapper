package pl.tahona.scrapper.crawler;

import org.jsoup.nodes.Document;

public interface Fetcher<U> {
    U fetchData(Document document);
}

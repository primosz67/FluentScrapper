package pl.tahona.scrapper.crawler;

import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created on 2015-12-30.
 */
public abstract class WebCrawler<T> {
    public abstract String getDomainUrl();

    public Set<String> getLinkUrls(final Document doc) {
        return doc.select("a").stream()
                .map(element->element.attr("href"))
                .collect(Collectors.toSet());
    }

}

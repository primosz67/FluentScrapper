package pl.tahona.scrapper;

import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created on 2015-12-30.
 */
public abstract class WebSite<T> {
    abstract protected String getDomainUrl();

    protected Set<String> getLinkUrls(final Document doc) {
        return doc.select("a").stream()
                .map(element->element.attr("href"))
                .collect(Collectors.toSet());
    }

}

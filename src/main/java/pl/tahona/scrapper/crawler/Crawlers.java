package pl.tahona.scrapper.crawler;


import com.google.common.collect.ImmutableSet;
import org.jsoup.nodes.Document;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Crawlers {


    private Crawlers() {

    }

    public static <T> WebCrawler<T> simple(String domain) {
        return new SimpleCrawler<>(domain);
    }

    public static <T> WebCrawler<T> supplier(String domain, Supplier<String> supplier) {
        return new WebCrawler<T>() {
            @Override
            public String getDomainUrl() {
                return domain;
            }

            @Override
            public Set<String> getLinkUrls(Document doc) {
                return ImmutableSet.of(supplier.get());
            }
        };
    }
}

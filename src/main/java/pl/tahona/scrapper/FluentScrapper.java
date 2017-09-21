package pl.tahona.scrapper;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tahona.scrapper.api.DataStore;
import pl.tahona.scrapper.crawler.Fetcher;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.containsAny;

/**
 * Created on 2015-12-30.
 */
public class FluentScrapper<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(FluentScrapper.class);
    private static final int URLS_PART_SIZE = 50;

    private final WebSite<T> webPage;
    private String startPage;

    private final Set<String> visitedLinks = Sets.newHashSet();
    private final Set<String> toVisitLinks = Sets.newHashSet();
    private final Set<String> allLinks = Sets.newHashSet();

    private List<String> neededUrlParts = ImmutableList.of();
    private Long linksLimit;
    private int maxTreeLevel = 999;
    private boolean omitNullData;

    private DataStore<T> dataStore;
    private Fetcher<List<T>> fetcher;

    private FluentScrapper(final WebSite<T> webPage) {
        this.webPage = webPage;
    }

    public static <T> FluentScrapper<T> of(final WebSite<T> webPage) {
        return new FluentScrapper<T>(webPage);
    }

    public static <T> FluentScrapper<T> of(final String site) {
        return new FluentScrapper<T>(new SimpleSite<T>(site));
    }

    public void crawl() {
        long startMilis = System.currentTimeMillis();

        Objects.requireNonNull(dataStore, "Data store can't be null! ");

        final int level = 0;
        this.<String>crawlLink(fixLink(getStartPage()), level, maxTreeLevel);

        final int toIndex = this.toVisitLinks.size() > linksLimit ? linksLimit.intValue() : toVisitLinks.size();

        final List<String> result = new ArrayList<>(toVisitLinks)
                .subList(0, toIndex);

        LOGGER.info("Urls found on page: {}", allLinks.size());

        Iterables.partition(result, URLS_PART_SIZE)
                .forEach(this::saveUrlsResponses);

        LOGGER.info("Execute Time: {} min", ((System.currentTimeMillis() - startMilis)/1000)/60);
    }

    private String getStartPage() {
        if (StringUtils.isNotBlank(startPage)) {
            return startPage;
        }
        return webPage.getDomainUrl();
    }

    private void saveUrlsResponses(final List<String> urls) {
        final List<ResponseObj<T>> data = urls.parallelStream()
                .flatMap(url -> {
                    List<T> results = fetchData(url);

                    if (isNotEmpty(results)) {
                        LOGGER.info("URL: {} found data: {}", url, results.size());

                        return results.stream().map(x -> new ResponseObj<T>(url, x))
                                .collect(Collectors.toList()).stream();
                    }
                    return ImmutableList.of(new ResponseObj<T>(url, null)).stream();
                })
                .filter(Objects::nonNull)
                .filter(x -> !omitNullData || Objects.nonNull(x.getDataObj()))
                .collect(Collectors.toList());

        dataStore.save(data);
    }

    private List<T> fetchData(final String url) {
        return Optional.of(url)
                .map(SoupHelper::get)
                .map(fetcher::fetchData)
                .orElse(null);
    }

    private void crawlLink(final String newLink, final int level, final int maxLevel) {

        if (level < maxLevel && (linksLimit == null || linksLimit >= getAllLinksSize())) {

            if (!visitedLinks.contains(newLink)) {
                toVisitLinks.remove(newLink);

                final Document document = SoupHelper.get(newLink);

                if (document != null) {
                    visitedLinks.add(newLink);
                    allLinks.add(newLink);

                    saveSingleDoc(newLink, document);
                    crawlSubLinks(newLink, document, level, maxLevel);
                }
            }
        }
    }

    private void saveSingleDoc(final String newLink, final Document document) {
        final List<T> results = this.fetcher.fetchData(document);

        final List<ResponseObj<T>> responseObjs = results.stream()
                .map(x -> new ResponseObj<>(newLink, x))
                .filter(Objects::nonNull)
                .filter(x -> !omitNullData || Objects.nonNull(x.getDataObj()))
                .collect(Collectors.toList());

        dataStore.save(responseObjs);
    }

    private Long getAllLinksSize() {
        return Long.valueOf(allLinks.size());
    }

    private void crawlSubLinks(final String newLink, final Document doc, final int level, final int maxLevel) {
        final String[] arr = neededUrlParts.toArray(new String[]{});

        final Set<String> urls = webPage.getLinkUrls(doc).stream()
                .map(this::fixLink)
                .filter(Objects::nonNull)
                .filter(x -> isNotEmpty(neededUrlParts)
                        && containsAny(x, arr))
                .collect(Collectors.toSet());

        toVisitLinks.addAll(urls);
        allLinks.addAll(urls);

        LOGGER.info("Crawling: {}, found urls: {} (all:{})", newLink, CollectionUtils.size(urls), CollectionUtils.size(allLinks));

        urls.forEach(url -> this.<String>crawlLink(url, level + 1, maxLevel));

    }

    private String fixLink(final String link) {
        if (link.startsWith("/") || !link.startsWith("htt")) {
            return webPage.getDomainUrl() + link;
        }
        if (link.startsWith(webPage.getDomainUrl())) {
            return link;
        }
        return null;
    }

    public FluentScrapper<T> linksLimit(final long limit) {
        this.linksLimit = limit;
        return this;
    }

    public FluentScrapper<T> depthLevel(final int maxLevel) {
        this.maxTreeLevel = maxLevel;
        return this;
    }

    public FluentScrapper<T> omitNullData() {
        this.omitNullData = true;
        return this;
    }

    public FluentScrapper<T> dataStore(DataStore<T> dataStore) {
        this.dataStore = dataStore;
        return this;
    }

    public FluentScrapper<T> fetcher(final Fetcher<List<T>> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public FluentScrapper<T> linksContainsAny(final String... neededUrlParts) {
        this.neededUrlParts = ImmutableList.copyOf(neededUrlParts);
        return this;
    }

    public FluentScrapper<T> startPage(final String startPage) {
        this.startPage = startPage;
        return this;
    }
}


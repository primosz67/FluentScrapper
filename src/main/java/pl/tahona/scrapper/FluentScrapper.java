package pl.tahona.scrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tahona.scrapper.api.DataStore;
import pl.tahona.scrapper.crawler.Crawlers;
import pl.tahona.scrapper.crawler.WebCrawler;
import pl.tahona.scrapper.fetcher.Fetcher;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.containsAny;

/**
 * Created on 2015-12-30.
 */
public class FluentScrapper<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(FluentScrapper.class);
    private static final int URLS_PART_SIZE = 50;

    private final WebCrawler<T> webCrawler;
    private String startPage;

    private final Set<String> visitedLinks = Sets.newHashSet();
    private final Set<String> toVisitLinks = Sets.newHashSet();
    private final Set<String> allLinks = Sets.newHashSet();

    private List<String> neededUrlParts = ImmutableList.of();
    private Long linksLimit = Long.MAX_VALUE;
    private int maxTreeLevel = 999;
    private boolean omitNullData;

    private DataStore<T> dataStore;
    private Fetcher<List<T>> fetcher;

    private FluentScrapper(final WebCrawler<T> webCrawler) {
        this.webCrawler = webCrawler;
    }

    public static <T> FluentScrapper<T> of(final WebCrawler<T> webCrawler) {
        return new FluentScrapper<>(webCrawler);
    }

    public static <T> FluentScrapper<T> of(final String site) {
        return new FluentScrapper<>(Crawlers.simple(site));
    }

    public void crawl() {
        long startMillis = System.currentTimeMillis();

        Objects.requireNonNull(dataStore, "Data store can't be null! ");

        final int level = 0;
        boolean wasCrawlingLinks = this.crawlLink(fixLink(getStartPage()), level, maxTreeLevel);

        if (wasCrawlingLinks) {
            final int toIndex = this.toVisitLinks.size() > linksLimit ?
                    linksLimit.intValue() :
                    toVisitLinks.size();

            final List<String> result = new ArrayList<>(toVisitLinks)
                    .subList(0, toIndex);

            LOGGER.info("All urls found on page: {}", allLinks.size());

            Iterables.partition(result, URLS_PART_SIZE)
                    .forEach(this::saveUrlsResponses);
        }

        LOGGER.info("Execute Time: {} min", ((System.currentTimeMillis() - startMillis) / 1000) / 60);
    }

    private String getStartPage() {
        if (StringUtils.isNotBlank(startPage)) {
            return startPage;
        }
        return webCrawler.getDomainUrl();
    }

    private void saveUrlsResponses(final List<String> urls) {
        final List<ResponseObj<T>> data = urls.parallelStream()
                .flatMap(url -> {
                    List<T> results = fetchData(url);

                    if (isNotEmpty(results)) {
                        LOGGER.info("URL: {} found data: {}", url, results.size());

                        return results.stream().map(x -> new ResponseObj<>(url, x))
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

    private boolean crawlLink(final String newLink, final int level, final int maxLevel) {

        if (level < maxLevel && (linksLimit == null || linksLimit >= getAllLinksSize())) {

            if (!visitedLinks.contains(newLink)) {
                toVisitLinks.remove(newLink);

                final Document document = SoupHelper.get(newLink);

                if (document != null) {
                    visitedLinks.add(newLink);
                    allLinks.add(newLink);

                    saveSingleDoc(newLink, document);
                    crawlSubLinks(newLink, document, level, maxLevel);
                    return true;
                }
            }
        }
        return false;
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

    private void crawlSubLinks(final String newLink, final Document currentDoc, final int level, final int maxLevel) {
        final String[] arr = neededUrlParts.toArray(new String[]{});

        final Set<String> urls = webCrawler.getLinkUrls(currentDoc).stream()
                .map(this::fixLink)
                .filter(Objects::nonNull)
                .filter(fullUrl -> filterNeededParts(arr, fullUrl))
                .collect(Collectors.toSet());

        toVisitLinks.addAll(urls);
        allLinks.addAll(urls);

        LOGGER.info("Crawling: {}, found urls: {} (all:{})", newLink,
                CollectionUtils.size(urls), CollectionUtils.size(allLinks));

        urls.forEach(url -> this.<String>crawlLink(url, level + 1, maxLevel));

    }

    private boolean filterNeededParts(String[] arr, String fullUrl) {
        return isEmpty(neededUrlParts) ||
                (isNotEmpty(neededUrlParts) && containsAny(fullUrl, arr));
    }

    private String fixLink(final String link) {
        if (link.startsWith("/") || !link.startsWith("htt")) {
            return webCrawler.getDomainUrl() + link;
        }
        if (link.startsWith(webCrawler.getDomainUrl())) {
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

    public FluentScrapper<T> dataFetcher(final Fetcher<List<T>> fetcher) {
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


package pl.tahona.scrapper.crawler;

class SimpleCrawler<T> extends WebCrawler<T> {

    private String site;

    SimpleCrawler(final String site) {
        this.site = site;
    }

    @Override
    public String getDomainUrl() {
        return site;
    }
}

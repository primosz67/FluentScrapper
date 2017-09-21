package pl.tahona.scrapper;

class SimpleSite<T> extends WebSite<T> {

    private String site;

    SimpleSite(final String site) {
        this.site = site;
    }

    @Override
    protected String getDomainUrl() {
        return site;
    }
}

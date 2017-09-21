package pl.tahona.scrapper;

import pl.tahona.scrapper.api.DataRow;

class ResponseObj<T> implements DataRow<T> {
    private final String url;
    private final T dataObj;

    ResponseObj(final String url, final T dataObj) {
        this.url = url;
        this.dataObj = dataObj;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public T getDataObj() {
        return dataObj;
    }
}

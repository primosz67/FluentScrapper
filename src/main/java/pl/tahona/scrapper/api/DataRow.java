package pl.tahona.scrapper.api;

public interface DataRow<T> {

    String getUrl();

    T getDataObj();
}

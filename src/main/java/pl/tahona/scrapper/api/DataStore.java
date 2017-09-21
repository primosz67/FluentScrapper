package pl.tahona.scrapper.api;

import java.util.List;

public interface DataStore<T> {
    void save(List<? extends DataRow<T>> data);
}

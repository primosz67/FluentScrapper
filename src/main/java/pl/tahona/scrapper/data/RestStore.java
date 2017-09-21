package pl.tahona.scrapper.data;

import pl.tahona.scrapper.api.DataRow;
import pl.tahona.scrapper.api.DataStore;

import java.util.List;

class RestStore<T> implements DataStore<T> {
    @Override
    public void save(final List<? extends DataRow<T>> data) {
//        return RestHelper.send(url, f);
    }
}

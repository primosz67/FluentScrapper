package pl.tahona.scrapper.data;

import com.google.common.base.Joiner;
import pl.tahona.scrapper.api.Converter;
import pl.tahona.scrapper.api.DataRow;
import pl.tahona.scrapper.api.DataStore;

import java.io.PrintStream;
import java.util.List;

class PrintToStringStore<T> implements DataStore<T> {
    private final PrintStream out;


    PrintToStringStore(final PrintStream out) {
        this.out = out;
    }

    @Override
    public void save(final List<? extends DataRow<T>> data) {
        data.forEach(x -> out.println(x.getDataObj()));
    }
}

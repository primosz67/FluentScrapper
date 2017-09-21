package pl.tahona.scrapper.data;

import com.google.common.base.Joiner;
import pl.tahona.scrapper.api.Converter;
import pl.tahona.scrapper.api.DataRow;
import pl.tahona.scrapper.api.DataStore;

import java.io.PrintStream;
import java.util.List;

class CsvToStreamStore<T> implements DataStore<T> {
    private final Joiner joiner;
    private final PrintStream out;
    private final Converter<T, List<String>> converter;

    CsvToStreamStore(final PrintStream out, final String separator, final Converter<T, List<String>> converter) {
        this.out = out;
        this.converter = converter;
        this.joiner = Joiner.on(separator);
    }

    @Override
    public void save(final List<? extends DataRow<T>> data) {
        data.forEach(x -> out.println(joiner.join(x.getUrl(),
                converter.convert(x.getDataObj()))));
    }
}

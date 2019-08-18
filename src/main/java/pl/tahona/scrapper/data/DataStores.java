package pl.tahona.scrapper.data;


import pl.tahona.scrapper.api.Converter;
import pl.tahona.scrapper.api.DataStore;

import java.io.PrintStream;
import java.util.List;

public class DataStores {
    public static <T> DataStore<T> csvFile(final String fileName, Converter<T, List<String>> converter) {
        return new CsvFileStore<>(fileName, converter);
    }

    public static <T> DataStore<T> printCsv(final PrintStream out, Converter<T, List<String>> converter) {
        return new CsvToStreamStore<>(out, ",", converter);
    }

    public static <T> DataStore<T> printToStream(final PrintStream out) {
        return new PrintToStringStore<>(out);
    }
}

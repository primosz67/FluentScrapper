package pl.tahona.scrapper.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tahona.scrapper.api.Converter;
import pl.tahona.scrapper.api.DataRow;
import pl.tahona.scrapper.api.DataStore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class CsvFileStore<T> implements DataStore<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CsvFileStore.class);
    public static final boolean APPEND = true;

    private final File file;
    private final Converter<T, List<String>> converter;

    CsvFileStore(final String fileName, final Converter<T, List<String>> converter) {
        this.file = new File(fileName);
        this.converter = converter;

    }

    @Override
    public void save(final List<? extends DataRow<T>> data) {
        LOGGER.info("Init CSV save to file: {}", file.getAbsolutePath());

        createFileIfNeeded();

        try {
            final List<String> csvRows = convertDataRowToCsv(data);
            FileUtils.writeLines(file, csvRows, APPEND);
            LOGGER.info("DONE! savedFile: {}", file.getAbsolutePath());

        } catch (final IOException e) {
            LOGGER.error("Cannot export top file: {}", file.getAbsolutePath(), e);
        }
    }

    private List<String> convertDataRowToCsv(final List<? extends DataRow<T>> data) {
        return data.stream()
                .map(x -> converter.convert(x.getDataObj()))
                .map(x -> StringUtil.join(x, ","))
                .collect(Collectors.toList());
    }

    private void createFileIfNeeded() {
        try {
            if (!file.exists()) {
                file.createNewFile();
                LOGGER.info("Created new file: {}", file.getAbsolutePath());
            }
        } catch (final IOException e) {
            LOGGER.error("Cannot create new file: {}", file.getAbsolutePath(), e);
        }
    }

}

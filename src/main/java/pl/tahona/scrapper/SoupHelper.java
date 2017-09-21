package pl.tahona.scrapper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2015-12-30.
 */
class SoupHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoupHelper.class);

    static Document get(final String url) {
        try {
            LOGGER.info("Connecting to: {}", url);

            return Jsoup.connect(url)
                    .userAgent("Mozzila")
                    .timeout(5000)
                    .get();
        } catch (final Exception e) {
            LOGGER.error("Error when connecting to: {} with message: {}", url, e.getMessage());
        }
        return null;
    }
}

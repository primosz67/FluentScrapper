package pl.tahona.scrapper.crawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class RegexpFetcher implements Fetcher<List<String>> {
    private final String regexp;

    public RegexpFetcher(final String regexp) {
        this.regexp = regexp;
    }

    @Override
    public List<String> fetchData(final Document doc) {

        final Element bodyElement = doc.body();
        if (Objects.nonNull(bodyElement)) {

            return Stream.of(bodyElement) //full body search
                    .map(Element::text)
                    .flatMap(x -> findSearched(x).stream())
                    .filter(StringUtils::isNotBlank)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return findSearched(doc.toString());
        }

    }

    private List<String> findSearched(final String rawText) {

        final Matcher m = Pattern.compile(regexp).matcher(rawText);

        final List<String> searched = new ArrayList<>();
        while (m.find()) {
            searched.add(m.group());
        }
        return searched;
    }
}

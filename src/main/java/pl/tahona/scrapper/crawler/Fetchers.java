package pl.tahona.scrapper.crawler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public final class Fetchers {

    public static Fetcher<List<String>> email() {
        return new EmailFetcher(ImmutableSet.of());
    }

    public static Fetcher<List<String>> email(final String... extensions) {
        return new EmailFetcher(ImmutableSet.copyOf(extensions));
    }

    public static Fetcher<List<String>> searchText(final String regexp) {
        return new RegexpFetcher(regexp);
    }

}

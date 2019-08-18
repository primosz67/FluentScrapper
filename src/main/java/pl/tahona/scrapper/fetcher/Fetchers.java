package pl.tahona.scrapper.fetcher;

import com.google.common.collect.ImmutableSet;

import java.util.List;

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

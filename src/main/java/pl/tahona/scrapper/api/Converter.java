package pl.tahona.scrapper.api;

import java.util.Map;

public interface Converter<T, R> {
    R convert(T x) ;
}

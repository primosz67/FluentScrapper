package pl.tahona.scrapper.data;

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;

/**
 * Created on 2015-12-30.
 */
class RestHelper {
    static void send(final String url, final List<NameValuePair> params) {
        try {
            final String formatted = URLEncodedUtils.format(params, Charsets.UTF_8.displayName());

            System.out.println("connecting to:" + url);
            System.out.println(formatted);

            final Content content = Request.Post(url)
                    .bodyString(formatted, ContentType.APPLICATION_FORM_URLENCODED)
                    .execute()
                    .returnContent();

            if (content.toString().length() > 100) {
                System.out.println(content);
            }

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}

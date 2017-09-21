package pl.tahona.scrapper.utils;

import org.jsoup.nodes.Document;

/**
 * Created on 2016-02-10.
 */
public class DocRetriever {
	private final Document doc;

	public DocRetriever(final Document doc) {
		this.doc = doc;
	}

	public String getText(final String s) {
		return doc.select(s).first().text();
	}

	public Double getNumberOnly(final String s) {
		String text = "" + doc.select(s).first().text();
		return new Double(text.replaceAll("[^0-9.,]+", ""));
	}
}

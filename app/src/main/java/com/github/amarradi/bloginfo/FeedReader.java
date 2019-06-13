package com.github.amarradi.bloginfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

public class FeedReader {

	public static final String XML_TAG_LAST_BUILD_DATE = "lastBuildDate";

	public String parseLastBuildDate(InputStream is) {
		try {
			return parseXml(is);
		} catch (XmlPullParserException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String parseXml(InputStream is) throws XmlPullParserException, IOException {
		XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserFactory.newPullParser();

		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(is, null);

		while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
			if (XML_TAG_LAST_BUILD_DATE.equals(parser.getName()))
				return parser.nextText();

			parser.next();
		}
		throw new RuntimeException();
	}

}

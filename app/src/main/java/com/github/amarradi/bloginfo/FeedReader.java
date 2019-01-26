package com.github.amarradi.bloginfo;

import android.util.Log;

import com.github.amarradi.bloginfo.receivers.Channel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FeedReader {

	private static final String TAG = "data_content";

	public String parseLastBuildDate(InputStream is) {
		return parseXML(is);
	}

	private String parseXML(InputStream is) {
		XmlPullParserFactory parserFactory;
		try {

			parserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserFactory.newPullParser();

			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(is, null);

			return processParsing(parser);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}

	private String processParsing(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<Channel> channels = new ArrayList<>();
		int eventType = parser.getEventType();
		Channel currentChannel = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String eltName = null;
			switch (eventType) {
				case XmlPullParser.START_TAG:
					eltName = parser.getName();

					if ("channel".equals(eltName)) {
						currentChannel = new Channel();
						channels.add(currentChannel);
					} else if (currentChannel != null) {
						if ("lastBuildDate".equals(eltName)) {
							currentChannel.lastBuildDate = parser.nextText();
						}
					}
					break;
			}
			eventType = parser.next();
		}
		StringBuilder builder = new StringBuilder();
		for (Channel channel : channels) {
			builder.append(channel.lastBuildDate);
		}
		Log.i(TAG, builder.toString());
		return builder.toString();
	}

}

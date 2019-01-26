package com.github.amarradi.bloginfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@Config(manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class FeedCheckerTest {

	@Test
	public void parseLastBuildDate() throws IOException {
		InputStream is = getClass().getResourceAsStream("/feed.xml");

		String lastBuildDate = new FeedReader().parseLastBuildDate(is);

		assertEquals("Di, 22 Jan 2019 13:05:25 +0000", lastBuildDate);
	}

}

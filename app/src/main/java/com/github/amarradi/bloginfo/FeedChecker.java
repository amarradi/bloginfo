package com.github.amarradi.bloginfo;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.amarradi.bloginfo.receivers.Channel;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class FeedChecker implements Runnable {

    private static final String LAST_FEED_CONTENT_STORAGE_KEY = "LAST_FEED_CONTENT_STORAGE_KEY";


    private final Context context;
    private final boolean showToast;
    private static final String TAG = "data_content";


    public FeedChecker(Context context, boolean showToast) {


        this.context = context;
        this.showToast = showToast;
    }

    public void check() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        String currentFeedContent = readFeedContent();
        if (currentFeedContent != null) {

            String lastFeedContent = getLastFeedContent();
            if (!currentFeedContent.equals(lastFeedContent)) {
                notifyUser();
            } else if (this.showToast) {
                Handler h = new Handler(context.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {

                        Toast toast = Toast.makeText(context, R.string.noUpdate, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            setLastFeedContent(currentFeedContent);
        }
    }

    private String readFeedContent() {
        InputStream in = null;
        try {
            in = new URL(MainActivity.FEED_URL).openStream();
            // Log.i(TAG, IOUtils.toString(in, "utf-8"));
            parseXML();
            return IOUtils.toString(in, "utf-8").trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    private void parseXML() {
        XmlPullParserFactory parserFactory;
        try {

            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = context.getAssets().open("feed.xml");

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processParsing(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processParsing(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList <Channel> channels = new ArrayList <>();
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
    }



    private void notifyUser() {
        String textTitle = this.context.getString(R.string.notificationTitle);
        String textContent = this.context.getString(R.string.notificationText);

        NotificationManager notificationManager = (NotificationManager) this.context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.WEB_URL));
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, browserIntent, 0);

        @SuppressLint("ResourceAsColor")
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.timmy_turtle_logo_transparent)
                .setContentTitle(textTitle)
                .setContentText(textContent)
              //  .setDefaults(DEFAULT_LIGHTS)
                .setVibrate(new long[]{250,250,250})
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.timmy_turtle_logo_transparent))
                .setColorized(true)
                .setColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark))
                .setLights(R.color.colorPrimaryLight, 1000, 1000)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        if (notificationManager != null) {
            notificationManager.notify(0, mBuilder.build());
        }
    }

    private String getLastFeedContent() {
        String prefers;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        prefers = preferences.getString(LAST_FEED_CONTENT_STORAGE_KEY, "");
        //Warning:(116, 73) Method invocation 'trim' may produce 'java.lang.NullPointerException'...
        if (prefers.length() == 0) {
            return prefers;
        } else {
            prefers = prefers.trim();
        }
        return prefers;
    }

    private void setLastFeedContent(String feedContent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        preferences.edit().putString(LAST_FEED_CONTENT_STORAGE_KEY, feedContent).apply();
    }
}

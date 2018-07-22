package com.github.amarradi.blogalert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;

public class FeedChecker implements Runnable {

    private static final String LAST_FEED_CONTENT_STORAGE_KEY = "LAST_FEED_CONTENT_STORAGE_KEY";

    private final Context context;

    public FeedChecker(Context context) {
        this.context = context;
    }

    public void check() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        String currentFeedContent = readFeedContent();
        String lastFeedContent = getLastFeedContent();

        if (currentFeedContent != null && !currentFeedContent.equals(lastFeedContent))
            notifyUser();

        setLastFeedContent(currentFeedContent);
    }

    private String readFeedContent() {
        InputStream in = null;
        try {
            in = new URL(MainActivity.FEED_URL).openStream();
            return IOUtils.toString(in, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    private void notifyUser() {
        String textTitle = this.context.getString(R.string.notificationTitle);
        String textContent = this.context.getString(R.string.notificationText, MainActivity.WEB_URL);

        NotificationManager notificationManager = (NotificationManager) this.context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.WEB_URL));
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, browserIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.turtle_bg_layer)
                .setLargeIcon(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.lilu96))
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(0, mBuilder.build());
        }
        Log.i("Notification", "notify user");
    }

    private String getLastFeedContent() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        return preferences.getString(LAST_FEED_CONTENT_STORAGE_KEY, "");
    }

    private void setLastFeedContent(String feedContent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        preferences.edit().putString(LAST_FEED_CONTENT_STORAGE_KEY, feedContent).apply();
    }
}

package com.github.amarradi.blogalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.amarradi.blogalert.service.BlogBroadcastReceiver;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = MainActivity.class.getName();

    private static final String LAST_FEED_CONTENT_STORAGE_KEY = "LAST_FEED_CONTENT_STORAGE_KEY";

    public static String FEED_URL = "http://www.marcusradisch.de/feed/";
    public static String WEB_URL = "http://www.marcusradisch.de";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel();
        }

        this.preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new FeedChecker());
        BlogBroadcastReceiver.start(getApplicationContext());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    class FeedChecker implements View.OnClickListener, Runnable {


        @Override
        public void onClick(View v) {
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
                in = new URL(FEED_URL).openStream();
                return IOUtils.toString(in, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
            }
            return null;
        }

        private void notifyUser() {
            Context context = getApplicationContext();
            String textTitle = getString(R.string.notificationTitle);
            String textContent = getString(R.string.notificationText, WEB_URL);

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_URL));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, browserIntent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(0, mBuilder.build());
        }

        private String getLastFeedContent() {
            return preferences.getString(LAST_FEED_CONTENT_STORAGE_KEY, "");
        }

        private void setLastFeedContent(String feedContent) {
            preferences.edit().putString(LAST_FEED_CONTENT_STORAGE_KEY, feedContent).apply();
        }
    }


}

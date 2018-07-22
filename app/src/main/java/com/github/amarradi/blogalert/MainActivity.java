package com.github.amarradi.blogalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.amarradi.blogalert.receivers.BlogBroadcastReceiver;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import static java.util.Objects.*;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = MainActivity.class.getName();

    public static final int DEFAULT_ALARM_TIME = 7;

    //public static String FEED_URL = "http://www.marcusradisch.de/feed/";
    //public static String WEB_URL = "http://www.marcusradisch.de";
    public static String FEED_URL = "https://www.presseportal.de/rss/presseportal.rss2";
    public static String WEB_URL = "https://www.presseportal.de";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.turtle_bg_layer);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel();
        }

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedChecker checker = new FeedChecker(getApplicationContext());
                checker.check();
            }
        });

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = "Marcus Radisch"
            //String description = "http://www.marcusradisch.de"
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static class FeedChecker implements Runnable {

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
            String textTitle = this.context.getString(R.string.notificationTitle);
            String textContent = this.context.getString(R.string.notificationText, WEB_URL);

            NotificationManager notificationManager = (NotificationManager) this.context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_URL));
            PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, browserIntent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, CHANNEL_ID)
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


}

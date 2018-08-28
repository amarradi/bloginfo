package com.github.amarradi.blogalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import static java.util.Objects.*;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = MainActivity.class.getName();

    public static final int DEFAULT_ALARM_TIME = 7;

    public static String FEED_URL = "http://www.marcusradisch.de/feed/";
    public static String WEB_URL = "http://www.marcusradisch.de";
    //public static String FEED_URL = "https://www.presseportal.de/rss/presseportal.rss2";
    //public static String WEB_URL = "https://www.presseportal.de";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                return true;
            case R.id.thanks:
                Intent intentThanks = new Intent(this, ThanksActivity.class);
                startActivity(intentThanks);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.turtle_timmy_round);
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


}

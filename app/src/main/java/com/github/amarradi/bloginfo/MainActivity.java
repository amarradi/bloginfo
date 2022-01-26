package com.github.amarradi.bloginfo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.amarradi.bloginfo.receivers.BootReceiver;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = MainActivity.class.getName();

    public static final int DEFAULT_ALARM_TIME = 7;

    public static String FEED_URL = "https://www.marcusradisch.de/feed";
    public static String WEB_URL = "https://www.marcusradisch.de";
/*

   public static String FEED_URL = "https://www.heise.de/rss/heise.rdf";
   public static String WEB_URL = "https://www.heise.de";
*/
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

        BootReceiver.start(getApplicationContext());
        requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setLogo(R.mipmap.timmy_turtle_logo_transparent);
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
                FeedChecker checker = new FeedChecker(getApplicationContext(), true);
                checker.check();
            }
        });

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //int importance = NotificationManager.IMPORTANCE_HIGH;
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

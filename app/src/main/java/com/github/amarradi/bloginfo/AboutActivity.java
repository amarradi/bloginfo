package com.github.amarradi.bloginfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView appVersion = findViewById(R.id.tvVersion);
        Resources resources = getResources();

        String version_text = String.format(resources.getString(R.string.version), BuildConfig.VERSION_NAME);
        appVersion.setText(version_text);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.thanks:
                Intent intentThanks = new Intent(this, ThanksActivity.class);
                startActivity(intentThanks);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
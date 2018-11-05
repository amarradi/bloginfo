package com.github.amarradi.bloginfo.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.sql.Time;

public class Preferences {

    // preference key constants
    private static final String UPDATE_TIME = "updateTime";

    private static Preferences instance = null;

    private SharedPreferences sharedPreferences;

    private Preferences(Context baseContext) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext);
    }

    public static Preferences getInstance(Context baseContext) {
        if (instance == null) {
            instance = new Preferences(baseContext);
        }
        return instance;
    }

    public Time getNotificationTime() {
        return Time.valueOf(this.sharedPreferences.getString(UPDATE_TIME, "07:00") + ":00");
    }


}

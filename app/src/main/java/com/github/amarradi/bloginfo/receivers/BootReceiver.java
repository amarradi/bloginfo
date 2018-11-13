package com.github.amarradi.bloginfo.receivers;

/*
 * Copyright (C) 2015-2016 The Food Restriction Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.github.amarradi.bloginfo.FeedChecker;

import com.github.amarradi.bloginfo.R;
import com.github.amarradi.bloginfo.helpers.AlarmHelper;
import com.github.amarradi.bloginfo.helpers.Preferences;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;


public class BootReceiver extends BroadcastReceiver {

    private static final String TIMED = "timed";
    private final AlarmHelper alarm = new AlarmHelper();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION_CODES.KITKAT <= VERSION.SDK_INT) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if((Objects.isNull(intent.getAction()))) {
                    //Toast toast = Toast.makeText(context, R.string.objisnull, Toast.LENGTH_SHORT);
                    //toast.show();
                    new FeedChecker(context, false).check();
                } else {
                    if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        long toRingAt = prefs.getLong("scan_daily_interval", 0);
                        alarm.cancelAlarm(context);
                        alarm.setAlarm(context, toRingAt);
                    }
                }
            }
            if (AlarmHelper.ACTION_BLOG_NOTIFICATION.equals(intent.getAction())) {
                new FeedChecker(context, false).check();
            }
        }
    }

    public static void start(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context);

        Preferences prefs = Preferences.getInstance(context);
        Time nextNotificationTime = prefs.getNotificationTime();

        Calendar defaultNoteAt = Calendar.getInstance();

        defaultNoteAt.set(Calendar.HOUR_OF_DAY, nextNotificationTime.getHours());
        defaultNoteAt.set(Calendar.MINUTE, nextNotificationTime.getMinutes());
        defaultNoteAt.set(Calendar.SECOND, defaultNoteAt.getActualMinimum(Calendar.SECOND));
        defaultNoteAt.set(Calendar.MILLISECOND, defaultNoteAt.getActualMinimum(Calendar.MILLISECOND));

        if (defaultNoteAt.before(Calendar.getInstance())) {
            defaultNoteAt.add(Calendar.DAY_OF_MONTH,1);
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(defaultNoteAt.getTime());
        Log.i("Alarm", "Setting alarm at in BootReceiver " + formattedDate);
        alarmManager.set(AlarmManager.RTC_WAKEUP, defaultNoteAt.getTimeInMillis(), pendingIntent);

    }

    private static PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, BootReceiver.class);
        intent.putExtra(TIMED, true);

        return PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
}

package com.github.amarradi.bloginfo.helpers;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.github.amarradi.bloginfo.MainActivity;
import com.github.amarradi.bloginfo.receivers.BlogInfoReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmHelper {

    private AlarmManager alarmManager;
    public final static String ACTION_BLOG_NOTIFICATION = "com.github.amarradi.blogalert.NOTIFICATION";

    public void setAlarm (Context context, long setNoteAt) {
        Calendar defaultNoteAt = Calendar.getInstance();

        if (setNoteAt == 0) {
            defaultNoteAt.set(Calendar.HOUR_OF_DAY, MainActivity.DEFAULT_ALARM_TIME);
            defaultNoteAt.set(Calendar.MINUTE, 0);
            defaultNoteAt.set(Calendar.SECOND, 0);
        } else {
            Calendar now = Calendar.getInstance();
            defaultNoteAt.setTimeInMillis(setNoteAt);
            defaultNoteAt.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(defaultNoteAt.getTime());
      //  Log.i("Alarm", "Setting alarm at " + formattedDate);

        setNoteAt = defaultNoteAt.getTimeInMillis();

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, BlogInfoReceiver.class);
        alarmIntent.setAction(ACTION_BLOG_NOTIFICATION);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context,0,alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Repeat it every 24 hours from the configured time */
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                setNoteAt,
                AlarmManager.INTERVAL_DAY,
                pendingAlarmIntent);

        /* Restart if rebooted */
        ComponentName receiver = new ComponentName(context, BlogInfoReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, BlogInfoReceiver.class);
        alarmIntent.setAction(ACTION_BLOG_NOTIFICATION);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context,
                0,
                alarmIntent,
                0);
        alarmManager.cancel(pendingAlarmIntent);

        /* Alarm won't start again if device is rebooted */
        ComponentName receiver = new ComponentName(context, BlogInfoReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}
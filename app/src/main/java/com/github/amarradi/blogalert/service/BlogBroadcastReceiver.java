package com.github.amarradi.blogalert.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;

import java.sql.Time;

public class BlogBroadcastReceiver extends BroadcastReceiver {

    private static final String TIMED = "timed";



    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            start(context);
        }

    }

    private static PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, BlogBroadcastReceiver.class);
        intent.putExtra(TIMED, true);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    //https://developer.android.com/training/scheduling/alarms
    public static void start(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context);

        /* Alarm immer um 07:00Uhr */
        Calendar calendar;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 35);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public  static void stop(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void restart(Context context) {
        stop(context);
        start(context);
    }
}

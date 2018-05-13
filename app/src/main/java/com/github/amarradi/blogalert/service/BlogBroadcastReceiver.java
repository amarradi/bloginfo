package com.github.amarradi.blogalert.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;

public class BlogBroadcastReceiver extends BroadcastReceiver {

    private static final String TIMED = "timed";

    @Override
    public void onReceive(Context context, Intent intent) {
        start(context);

    }

    public static void start(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BlogBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        /* Alarm immer um 07:00Uhr */
        Calendar calendar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, alarmIntent);
            }
        }
    }

    public  static void stop(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

    }

    private static PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, BlogBroadcastReceiver.class);
        intent.putExtra(TIMED, true);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
}

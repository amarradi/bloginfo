package com.github.amarradi.bloginfo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;

import com.github.amarradi.bloginfo.MainActivity;

public class BlogBroadcastReceiver extends BroadcastReceiver {

    private static final String TIMED = "timed";


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MainActivity.class);
        //context.startService(serviceIntent);
        start(context);

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

        /* Alarm und notification immer um 07:07:07'07Uhr */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar firingCal;
            Calendar currentCal = Calendar.getInstance();
            firingCal = Calendar.getInstance();
            firingCal.setTimeInMillis(System.currentTimeMillis());
            firingCal.set(Calendar.HOUR_OF_DAY, 15);
            firingCal.set(Calendar.MINUTE, 41);
            firingCal.set(Calendar.SECOND, 00);
            firingCal.set(Calendar.MILLISECOND,00);
            long intendedTime = firingCal.getTimeInMillis();
            long currentTime = currentCal.getTimeInMillis();
            if (intendedTime >= currentTime ) {
            //if (alarmManager != null && intendedTime >= currentTime ) {
                Log.i("alarmManager <> null ","Logoutput");

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                firingCal.add(Calendar.MINUTE,1);
                Log.i("alarmManager == null ","Logoutput");
                intendedTime = firingCal.getTimeInMillis();
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
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

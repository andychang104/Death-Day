package com.cactime.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.cactime.SplashActivity;

/**
 * Created by andy on 2018/4/3.
 */

public class AlarmInitReceiver extends BroadcastReceiver
{

    private static final String TAG = "BootBroadcastReceiver";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_BOOT)) { //开机启动完成后，要做的事情
            Log.i(TAG, "BootBroadcastReceiver onReceive(), Do thing!");

            Log.d("OPENOPEN", "OPENOPEN");
            Calendar cal = Calendar.getInstance();
            // 設定於 3 分鐘後執行
            cal.add(Calendar.MINUTE, 1);
            intent = new Intent(context, PlayReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }
    }


}

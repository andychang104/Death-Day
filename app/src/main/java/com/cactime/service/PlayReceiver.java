package com.cactime.service;

import static android.content.Context.MODE_PRIVATE;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cactime.MainActivity;
import com.cactime.R;
import com.cactime.api.PastData;
import com.cactime.util.ListDataSave;

/**
 * Created by andy on 2018/4/3.
 */

public class PlayReceiver extends BroadcastReceiver
{

    SharedPreferences sp;
    List<PastData> pastData1;
    ListDataSave dataSave1;
    List<PastData> pastData2;
    ListDataSave dataSave2;
    NotificationManager mNM;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        boolean ischeck = false;
        Log.d("GCM", "GCM");
        sp = context.getSharedPreferences("CacUserData", MODE_PRIVATE);


        boolean isPush = sp.getBoolean("Push", false);
        long lastLaunchTime = sp.getLong("TIME", -1);
        long allday = sp.getLong("AllDay", -1);

        dataSave1 = new ListDataSave(context, "NewDay1");
        pastData1 = dataSave1.getDataList("DataList1");

        dataSave2 = new ListDataSave(context, "NewDay2");
        pastData2 = dataSave2.getDataList("DataList2");

        if(isPush){
            if(lastLaunchTime > 0) {
                sp.edit().putLong("AllDay", (allday-1)).apply();
                ischeck  = putNotification(lastLaunchTime , allday, 1000 * 60 * 60 * 24, context.getString(R.string.index_title_msg), context.getString(R.string.Notification_msg2), context);
                if(ischeck){
                    setDay();
                }
            }
        }

        if(pastData1 != null){
            if(pastData1.size()!=0){
                for(int i=0; i<pastData1.size(); i++){
                    if(pastData1.get(i).getItemIsPush()){
                        if(pastData1.get(i).getItemAllDay() != null){
                            if((pastData1.get(i).getItemAllDay()-1)>=0){
                                ischeck = putNotification(lastLaunchTime , pastData1.get(i).getItemAllDay(), 1000 * 60 * 60 * 24 * 365, pastData1.get(i).getItemName(), context.getString(R.string.Notification_msg2), context);

                                if(ischeck){
                                    pastData1.get(i).setItemAllDay((pastData1.get(i).getItemAllDay()-1));
                                    dataSave1.setDataList("DataList1", pastData1);
                                    setDay();
                                }

                            }
                        }
                    }
                }
            }
        }

        if(pastData2 != null){
            if(pastData2.size()!=0){
                for(int i=0; i<pastData2.size(); i++){
                    if(pastData2.get(i).getItemIsPush()){
                        if((pastData2.get(i).getItemAllDay()-1)>=0){
                            ischeck = putNotification(lastLaunchTime , pastData2.get(i).getItemAllDay(), 1000 * 60 * 60 * 24, pastData2.get(i).getItemName(), context.getString(R.string.Notification_msg2), context);
                            if(ischeck){
                                pastData2.get(i).setItemAllDay((pastData2.get(i).getItemAllDay()-1));
                                dataSave2.setDataList("DataList2", pastData2);
                                setDay();
                            }

                        }

                    }
                }
            }
        }

        onClickSetup(context);

    }

    // 按下設定鈕
    public void onClickSetup(Context context)
    {
        Calendar cal = Calendar.getInstance();
        // 設定於 3 分鐘後執行
        cal.add(Calendar.MINUTE, 1);

        Intent intent = new Intent(context, PlayReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

    public boolean putNotification(long lastLaunchTime, long allday, long pushtime, String title, String msg, Context context){
        boolean ischeck = false;
        long intervalSinceLastLaunch = System.currentTimeMillis() - lastLaunchTime;
        Log.d("10M10M", "10M10M"+lastLaunchTime);

        Log.d("10M10M", "10M10M"+ System.currentTimeMillis());
        //检查距离用户上一次启动app是否过了一定时间

        if(intervalSinceLastLaunch > pushtime) {
            ischeck = true;
            String allmsg = "";

            if((allday-1)>0){
                allmsg = title+context.getString(R.string.Notification_msg1)+(allday-1)+msg;
            }
            else if((allday-1) == 0){
                allmsg = title+context.getString(R.string.Notification_msg3);
            }

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setNotificationChannel(context);
                Notification.BigTextStyle style = new Notification.BigTextStyle();
                style.bigText(allmsg);
                Notification.Builder builder =
                        new Notification.Builder(context)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(allmsg)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                .setStyle(style)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setContentIntent(resultPendingIntent)
                                .setChannelId("123");

                mNM.notify((int) System.currentTimeMillis(), builder.build());
            }
            else{
                Notification.BigTextStyle style = new Notification.BigTextStyle();
                style.bigText(allmsg);
                Notification.Builder mBuilder =
                        new Notification.Builder(context)
                                .setAutoCancel(true)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(allmsg)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                .setStyle(style)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setContentIntent(resultPendingIntent);
                mNM.notify((int) System.currentTimeMillis(), mBuilder.build());
            }
        }

        return  ischeck;
    }

    public void setDay(){
        Calendar c = Calendar.getInstance();              //得到當前日期和時間
        c.set(Calendar.HOUR_OF_DAY,  8 );                 //把當前時間小時變成０
        c.set(Calendar.MINUTE,  0 );                      //把當前時間分鐘變成０
        c.set(Calendar.SECOND,  0 );                      //把當前時間秒數變成０
        c.set(Calendar.MILLISECOND,  0 );                 //把當前時間毫秒變成０
        Date date1 = c.getTime();                         //創建當天的0時0分0秒一個date對象
        sp.edit().putLong("TIME", date1.getTime()).apply();
    }

    protected void setNotificationChannel (Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channelLove = new NotificationChannel(
                    "123",
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channelLove.setDescription(context.getString(R.string.app_name));
            channelLove.enableLights(true);
            channelLove.enableVibration(true);

            mNM.createNotificationChannel(channelLove);



        }
    }

}

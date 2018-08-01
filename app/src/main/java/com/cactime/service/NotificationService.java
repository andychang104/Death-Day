package com.cactime.service;

import static android.content.ContentValues.TAG;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cactime.MainActivity;
import com.cactime.R;
import com.cactime.api.PastData;
import com.cactime.util.ListDataSave;

/**
 * Created by andy on 2018/3/16.
 */

public class NotificationService  extends JobService {
    SharedPreferences sp;
    List<PastData> pastData1;
    ListDataSave dataSave1;
    List<PastData> pastData2;
    ListDataSave dataSave2;
    NotificationManager mNM;


    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            Log.d("GCM", "GCM");
            sp = getSharedPreferences("CacUserData", MODE_PRIVATE);


            boolean isPush = sp.getBoolean("Push", false);
            long lastLaunchTime = sp.getLong("TIME", -1);
            long allday = sp.getLong("AllDay", -1);

            dataSave1 = new ListDataSave (this, "NewDay1");
            pastData1 = dataSave1.getDataList("DataList1");

            dataSave2 = new ListDataSave(this, "NewDay2");
            pastData2 = dataSave2.getDataList("DataList2");

            if(isPush){
                if(lastLaunchTime > 0) {
                    sp.edit().putLong("AllDay", (allday-1)).apply();
                    putNotification(lastLaunchTime , allday, 1000 * 60 * 60 * 24, getString(R.string.index_title_msg), getString(R.string.Notification_msg2));
                    setDay();
                }
            }

            if(pastData1 != null){
                if(pastData1.size()!=0){
                    for(int i=0; i<pastData1.size(); i++){
                        if(pastData1.get(i).getItemIsPush()){
                            if((pastData1.get(i).getItemAllDay()-1)>=0){
                                putNotification(lastLaunchTime , pastData1.get(i).getItemAllDay(), 1000 * 60 * 60 * 24 * 365, pastData1.get(i).getItemName(), getString(R.string.Notification_msg2));
                                pastData1.get(i).setItemAllDay((pastData1.get(i).getItemAllDay()-1));
                                dataSave1.setDataList("DataList1", pastData1);
                            }
                            setDay();
                        }
                    }
                }
            }

            if(pastData2 != null){
                if(pastData2.size()!=0){
                    for(int i=0; i<pastData2.size(); i++){
                        if(pastData2.get(i).getItemIsPush()){
                            if((pastData2.get(i).getItemAllDay()-1)>=0){
                                putNotification(lastLaunchTime , pastData2.get(i).getItemAllDay(), 1000 * 60 * 60 * 24, pastData2.get(i).getItemName(), getString(R.string.Notification_msg2));
                                pastData2.get(i).setItemAllDay((pastData2.get(i).getItemAllDay()-1));
                                dataSave2.setDataList("DataList2", pastData2);
                            }
                            setDay();
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.d("JobSchedulerService", "7.0 handleMessage task running");
                String servicename = params.getExtras().getString("servicename");
                Class service = getClassLoader().loadClass(servicename);
                if (service != null) {
                    Log.d("JobSchedulerService", "7.0 handleMessage task running ~~2~~"+service.hashCode());
                    //判断保活的service是否被杀死
                    if (!isMyServiceRunning(service)) {
                        //重启service
                        startService(new Intent(getApplicationContext(), service));
                    }
                }
                //创建一个新的JobScheduler任务
                scheduleRefresh(servicename);
                jobFinished(params, false);
                Log.d("JobSchedulerService","7.0 handleMessage task end~~"+ System.currentTimeMillis());
                return true;
            }

        } catch (Exception ex) {

        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob");

        return false;
    }


    public void putNotification(long lastLaunchTime, long allday, long pushtime, String title, String msg){
        long intervalSinceLastLaunch = System.currentTimeMillis() - lastLaunchTime;
        Log.d("10M10M", "10M10M"+lastLaunchTime);

        Log.d("10M10M", "10M10M"+ System.currentTimeMillis());
        //检查距离用户上一次启动app是否过了一定时间

        if(intervalSinceLastLaunch > pushtime) {
            String allmsg = "";

            if((allday-1)>0){
                allmsg = title+getString(R.string.Notification_msg1)+(allday-1)+msg;
            }
            else if((allday-1) == 0){
                allmsg = title+getString(R.string.Notification_msg3);
            }

            Intent resultIntent = new Intent(NotificationService.this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(NotificationService.this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setNotificationChannel();
                Notification.BigTextStyle style = new Notification.BigTextStyle();
                style.bigText(allmsg);
                Notification.Builder builder =
                        new Notification.Builder(this)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(allmsg)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
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
                        new Notification.Builder(NotificationService.this)
                                .setAutoCancel(true)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(allmsg)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .setStyle(style)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setContentIntent(resultPendingIntent);
                mNM.notify((int) System.currentTimeMillis(), mBuilder.build());
            }
        }
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

    protected void setNotificationChannel (){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channelLove = new NotificationChannel(
                    "123",
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channelLove.setDescription(getString(R.string.app_name));
            channelLove.enableLights(true);
            channelLove.enableVibration(true);

            mNM.createNotificationChannel(channelLove);



        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void scheduleRefresh(String serviceName) {
        JobScheduler mJobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);
        //jobId可根据实际情况设定
        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(0,
                        new ComponentName(getPackageName(),
                                NotificationService.class.getName()));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobBuilder.setMinimumLatency(2* 60 * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            PersistableBundle persiBundle = new PersistableBundle();
            persiBundle.putString("servicename", serviceName);
            mJobBuilder.setExtras(persiBundle);
        }

        if (mJobScheduler != null && mJobScheduler.schedule(mJobBuilder.build())
                <= JobScheduler.RESULT_FAILURE) {
            //Scheduled Failed/LOG or run fail safe measures
            Log.d("JobSchedulerService", "7.0 Unable to schedule the service FAILURE!");
        }else{
            Log.d("JobSchedulerService", "7.0 schedule the service SUCCESS!");
        }
    }


}


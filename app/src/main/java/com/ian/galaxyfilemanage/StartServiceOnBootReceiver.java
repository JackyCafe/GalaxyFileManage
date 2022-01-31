package com.ian.galaxyfilemanage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class StartServiceOnBootReceiver extends BroadcastReceiver {
    private static final int MY_NOTIFICATION_ID=1;
    String TAG = StartServiceOnBootReceiver.class.getName();
    NotificationManager mgr;
    Notification myNotification;
    NotificationCompat.Builder builder;
    @Override
    public void onReceive(Context context, Intent intent) {
        mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent it = new Intent(context, MainActivity.class);
        builder = new NotificationCompat.Builder(context).setTicker("message");
        Intent appIntent=null;
        appIntent = new Intent(context,MainActivity.class);
        appIntent.setAction(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//關鍵的一步，設定啟動模式
        PendingIntent contentIntent =PendingIntent.getActivity(context, 0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("YH","custom",NotificationManager.IMPORTANCE_HIGH);
            mgr.createNotificationChannel(notificationChannel);
            myNotification = builder.setContentIntent(contentIntent).setContentTitle("管理")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId("YH")
                    .setContentText("download 資料夾").build();
            myNotification.flags = Notification.FLAG_AUTO_CANCEL;
            mgr.notify(10, myNotification);
        }
//        Intent its = new Intent(context,StartService.class);
//        context.startForegroundService(its);
        Log.v(TAG,"OnReceiver");
    }
}

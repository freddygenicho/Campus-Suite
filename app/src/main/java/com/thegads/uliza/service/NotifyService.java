package com.thegads.uliza.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thegads.uliza.MainActivity;
import com.thegads.uliza.R;
import com.thegads.uliza.util.Config;

/**
 * Created by Freddy Genicho on 6/7/2016.
 */
public class NotifyService extends Service{

    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.intent = intent;
         return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        Notification mNotify = new Notification.Builder(this)
                .setContentTitle("Reminder")
                .setContentText("You have a class")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setAutoCancel(true)
                .addAction(0,"View",pendingIntent)
                .build();
        notificationManager.notify(1,mNotify);

        Log.e(Config.TAG,"Background service is running");
    }
}

package com.example.lifeset;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundService();
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, ImportantAlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (this, 0, notificationIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.service_notification);

        NotificationCompat.Builder builder;
//        String CHANNEL_ID = "lifeSet_notification_service_channel";
//        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                "This is LifeSet Channel",
//                NotificationManager.IMPORTANCE_HIGH);
//
//        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                .createNotificationChannel(channel);

//        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
    }
}

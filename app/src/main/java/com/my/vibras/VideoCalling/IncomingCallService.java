package com.my.vibras.VideoCalling;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.my.vibras.R;
public class IncomingCallService extends Service {
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = buildNotification();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    private Notification buildNotification() {
        Intent fullScreenIntent = new Intent(this, IncomingCallActivity.class);
        PendingIntent fullScreenPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fullScreenPendingIntent= PendingIntent.getActivity(this,
                    0, fullScreenIntent, PendingIntent.FLAG_MUTABLE);

        }else {
            fullScreenPendingIntent= PendingIntent.getActivity(this,
                    0, fullScreenIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        }


         NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Incoming call")
                        .setContentText("(919) 555-1234")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setFullScreenIntent(fullScreenPendingIntent, true);
        notificationBuilder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("1", "1"
                    , NotificationManager.IMPORTANCE_MAX));
            notificationBuilder.setChannelId("1");
        }
        Notification incomingCallNotification = notificationBuilder.build();
        return incomingCallNotification;
    }

}
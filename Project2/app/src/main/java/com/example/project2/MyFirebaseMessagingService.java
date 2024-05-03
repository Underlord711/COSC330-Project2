package com.example.project2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);

    getFirebaseMessage(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void getFirebaseMessage(String title, String body) {
    NotificationCompat.Builder builder= new NotificationCompat.Builder(this,"notify" )
            .setSmallIcon(R.drawable.ic_monitor_heart)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true);
        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(this);
        managerCompat.notify(102,builder.build());

    }

}

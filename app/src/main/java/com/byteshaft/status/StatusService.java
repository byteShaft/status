package com.byteshaft.status;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class StatusService extends Service {

    private int mNotificationID = 12;

    @Override
    public int onStartCommand(Intent intent1, int flags, int startId) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, mNotificationID, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setLargeIcon(bm)
                .setTicker("Noty SMS")
                .setContentTitle("Noty SMS")
                .setContentText("Test test test")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationID, notificationBuilder.build());
        Log.e("Service", " OK kr k chal gai service");
        startForeground(mNotificationID, notificationBuilder.build());
        return super.onStartCommand(intent1, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

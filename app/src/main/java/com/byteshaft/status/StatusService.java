package com.byteshaft.status;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.byteshaft.requests.HttpRequest;

import java.net.HttpURLConnection;

public class StatusService extends Service {

    private static final int NOTIFICATION_ID = 12;
    private static StatusService sInstance;
    private static final String CHANGE_STATUS_ONE = "CHANGE_STATUS_ONE";
    private static final String CHANGE_STATUS_TWO = "CHANGE_STATUS_TWO";

    private static final String OPEN_ACTIVITY = "OPEN_ACTIVITY";

    private Bitmap artImage;

    public static StatusService getInstance() {
        return sInstance;
    }
    private NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent1, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sInstance = this;
        if (intent1 != null) {
            String action = intent1.getAction();
            if (action != null && action.equals(OPEN_ACTIVITY)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else if (action != null && action.equals(CHANGE_STATUS_ONE)) {
                int drawable = intent1.getIntExtra("color", -1);
                changeStatus(getStateByDrawable(drawable));

            } else if (action != null && action.equals(CHANGE_STATUS_TWO)) {
                int drawable = intent1.getIntExtra("color", -1);
                changeStatus(getStateByDrawable(drawable));

            }
        }
        getStatus();
        return START_STICKY;
    }

    private int getStateByDrawable(int drawable) {
        switch (drawable) {
            case R.drawable.green:
                Log.i("TAG", "green");
                return 1;
            case R.drawable.yellow:
                Log.i("TAG", "yellow");
                return 2;
            case R.drawable.red:
                Log.i("TAG", "red");
                return 0;
            default: return 0;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void buildNotification(int state) {
        int[] drawableArray = getColorByStatus(state);
        Intent intentUpdateState = new Intent();
        intentUpdateState.putExtra("color", drawableArray[1]);
        intentUpdateState.setAction(CHANGE_STATUS_ONE);
        Intent intentUpdateStateTwo = new Intent();
        intentUpdateStateTwo.putExtra("color", drawableArray[2]);
        intentUpdateStateTwo.setAction(CHANGE_STATUS_TWO);
        Intent intentOpenPlayer = new Intent();
        intentOpenPlayer.setAction(OPEN_ACTIVITY);

        PendingIntent changeStateOne = PendingIntent.getService(this, 0, intentUpdateState, 0);
        PendingIntent changeStateTwo = PendingIntent.getService(this, 0, intentUpdateStateTwo, 0);
        PendingIntent openActivity = PendingIntent.getService(this, 0, intentOpenPlayer, 0);

        RemoteViews mNotificationTemplate = new RemoteViews(this.getPackageName(), R.layout.notification_ui);
        Notification.Builder notificationBuilder = new Notification.Builder(this);

        Log.i("TAG", String.valueOf(drawableArray[0]));
        if (artImage == null)
            artImage = BitmapFactory.decodeResource(getResources(), drawableArray[0]);

        mNotificationTemplate.setTextViewText(R.id.notification_line_one, "Status");
        mNotificationTemplate.setTextViewText(R.id.notification_line_two, "Service Running");
        mNotificationTemplate.setImageViewResource(R.id.one, drawableArray[1]);
        mNotificationTemplate.setImageViewResource(R.id.two, drawableArray[2]);
        mNotificationTemplate.setImageViewBitmap(R.id.notification_image, artImage);

        mNotificationTemplate.setOnClickPendingIntent(R.id.one, changeStateOne);
        mNotificationTemplate.setOnClickPendingIntent(R.id.two, changeStateTwo);

        Notification notification = notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentIntent(openActivity)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContent(mNotificationTemplate)
                .setUsesChronometer(true)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        if (notificationManager != null)
            notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private int[] getColorByStatus(int number) {
        switch (number) {
            case 0:
                return new int[]{R.drawable.red, R.drawable.green, R.drawable.yellow};
            case 1:
                return new int[]{R.drawable.green, R.drawable.yellow, R.drawable.red};
            case 2:
                return new int[]{R.drawable.yellow, R.drawable.red, R.drawable.green};
            default: return new int[]{R.drawable.red, R.drawable.green, R.drawable.yellow};
        }
    }

    private void getStatus() {
        HttpRequest request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                request.getResponseText();
                                buildNotification(2);

                        }
                }

            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("GET", AppGlobals.GET_URL);
        request.send();
    }

    private void changeStatus(int status) {
        HttpRequest request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                request.getResponseText();
                                getStatus();
                        }
                }

            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        String url = String.format("%s%s", AppGlobals.POST_URL, status);
        request.open("GET", url);
        request.send();

    }

}

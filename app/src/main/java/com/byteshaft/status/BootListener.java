package com.byteshaft.status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

/**
 * Created by s9iper1 on 4/22/17.
 */

public class BootListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppGlobals.isDataSaved()) {
            context.startService(new Intent(context, StatusService.class));
            String intervalInSeconds = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SECONDS);
            int seconds = Integer.parseInt(intervalInSeconds);
            AlarmHelpers.setAlarmForInterval(TimeUnit.SECONDS.toMillis(seconds));
        }

    }
}

/*
 *
 *  * (C) Copyright 2015 byteShaft Inc.
 *  *
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the GNU Lesser General Public License
 *  * (LGPL) version 2.1 which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/lgpl-2.1.html
 *  
 */

package com.byteshaft.status;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class AlarmHelpers {

    private static AlarmManager mAlarmManager;
    private static PendingIntent mPendingIntent;


    public static void setAlarmForInterval(long time) {
        mAlarmManager = getAlarmManager(AppGlobals.getContext());
        Log.i("Interval",
                String.format("Setting alarm for: %d", TimeUnit.SECONDS.toMillis(time)));
        Intent intent = new Intent("com.byteshaft.interval");
        mPendingIntent = PendingIntent.getBroadcast(AppGlobals.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time, mPendingIntent);
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    void removePreviousAlarams() {
        try {
            if (mPendingIntent != null) {
                mAlarmManager.cancel(mPendingIntent);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

public class IntervalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (StatusService.getInstance() != null) {
            StatusService.getInstance().getStatus();
            String intervalInSeconds = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SECONDS);
            int seconds = Integer.parseInt(intervalInSeconds);
            AlarmHelpers.setAlarmForInterval(TimeUnit.SECONDS.toMillis(seconds));
        }
    }
}

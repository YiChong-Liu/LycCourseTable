package com.yichongliu.lyccoursetable.ui.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class LycAlarmBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if ((intent.getAction()).equals("startAlarm")) {
            Intent alarmIntent=new Intent(context,LycWakeAlarmActivity.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmIntent);
        }
    }


}


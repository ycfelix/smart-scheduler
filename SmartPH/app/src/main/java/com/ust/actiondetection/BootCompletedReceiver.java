package com.ust.actiondetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //we double check here for only boot complete event
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            //here we start the service

            context.startService(ActivityDetectionService.getStartIntent(context));
        }
        System.out.println("boot successfully");
    }
}

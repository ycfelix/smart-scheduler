package com.ust.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

//Google Location (Update from Background)
public class MyLocationService extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = "edmt.dev.google.location background.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    try {
                    } catch (Exception e) {
                        Log.d("MyLocationService Exception: ", e.toString());
                    }
                }
            }
        }
    }
}

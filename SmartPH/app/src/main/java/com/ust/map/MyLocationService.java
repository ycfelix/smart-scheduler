package com.ust.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;

//Google Location Update from Background
public class MyLocationService extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = "edmt.dev.google.location background.UPDATE_LOCATION";

    @Override
    public void onReceive(Context contexe, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    System.out.println("Updated Location: "+location.getLatitude()+","+location.getLongitude());
                    try {
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }
}

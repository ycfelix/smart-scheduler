package com.example.calendar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

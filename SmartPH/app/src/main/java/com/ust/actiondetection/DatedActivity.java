package com.ust.actiondetection;

import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

public class DatedActivity {

    DetectedActivity detectedActivity;
    Date date;

    public DatedActivity(DetectedActivity detectedActivity) {
        this.detectedActivity = detectedActivity;
        date = new Date();
    }

    public String getType() {
        switch (detectedActivity.getType()){
            case 0: return "IN_VEHICLE";
            case 1: return "N_BICYCLE";
            case 2: return "ON_FOOT";
            case 3: return "STILL";
            case 5: return "TILTING";
            case 7: return "WALKING";
            case 8: return "RUNNING";
            default: return "UNKNOWN";
        }
    }

    public int getConfidence() {
        return detectedActivity.getConfidence();
    }
}

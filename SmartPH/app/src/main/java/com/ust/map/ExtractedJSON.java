package com.ust.map;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

//version: snap route
public class ExtractedJSON {
    ArrayList<LatLng> steps;
    int distance;
    int duration;

    ExtractedJSON(){
        steps = new ArrayList<LatLng>();
        distance=0;
        duration=0;
    }

    public void addStep(LatLng s){
        steps.add(s);
    }
    public void setDistance(int d){
        distance=d;
    }
    public void setDuration(int d){
        duration=d;
    }
    public ArrayList<LatLng> getSteps(){
        return steps;
    }
    public int getDistance(){
        return distance;
    }
    public int getDuration(){
        return duration;
    }

}
package com.ust.map;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class SuggestedPath {
    private String mode;
    private int modeImageDrawable;
    private int distance;
    private int duration;
    private ArrayList<LatLng> path;

    public SuggestedPath() {
        path=new ArrayList<LatLng>();
    }

    public void setMode(String m){
        mode=m;
    }

    public void setModeImageDrawable(int m) {
        modeImageDrawable=m;
    }

    public void setDistance(int d){
        distance=d;
    }

    public void setDuration(int d){
        duration=d;
    }

    public void addPathPoint(LatLng point){
        path.add(point);
    }

    public String getMode(){
        return mode;
    }

    public int getModeImageDrawable() {
        return modeImageDrawable;
    }

    public int getDistance(){
        return distance;
    }

    public int getDuration(){
        return duration;
    }

    public LatLng getPathPoint(int i){
        return path.get(i);
    }
}

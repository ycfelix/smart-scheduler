package com.example.calendar;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


/*
//version: draw direction
public class ExtractedJSON {
    List<List<HashMap<String, String>>> legs;
    int distance;
    int duration;

    ExtractedJSON(){
        legs = new ArrayList<List<HashMap<String, String>>>();
        distance=0;
        duration=0;
    }

    public void addLeg(List<HashMap<String, String>> l){
        legs.add(l);
    }
    public void setDistance(int d){
        distance=d;
    }
    public void setDuration(int d){
        duration=d;
    }
    public List<List<HashMap<String, String>>> getLegs(){
        return legs;
    }
    public int getDistance(){
        return distance;
    }
    public int getDuration(){
        return duration;
    }

}
*/
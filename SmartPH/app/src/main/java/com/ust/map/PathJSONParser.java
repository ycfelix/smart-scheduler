package com.ust.map;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class PathJSONParser {

    public ArrayList<ExtractedJSON> parse(JSONObject jObject) {
        //version: snap route
        ArrayList<ExtractedJSON> routesToSnap = new ArrayList<ExtractedJSON>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jDistance = null;
        JSONObject jDuration = null;
        int distance =0;
        int duration =0;
        ArrayList<LatLng> path = new ArrayList<LatLng>();

        try {
            jRoutes = jObject.getJSONArray("routes");
            // Traversing all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                // Traversing all legs [1 leg = 1 alternative route]
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    distance += ((JSONObject) jDistance).getInt("value");
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    duration += ((JSONObject) jDuration).getInt("value");

                    // Traversing all steps
                    ExtractedJSON e = new ExtractedJSON();
                    e.setDuration(duration);
                    e.setDistance(distance);
                    for (int k = 0; k < jSteps.length(); k++) {
                        int stepDistance=((JSONObject) jSteps.get(k)).getJSONObject("distance").getInt("value");
                        if(stepDistance<100) {
                            Double fromLat = ((JSONObject) jSteps.get(k)).getJSONObject("start_location").getDouble("lat");
                            Double fromLng = ((JSONObject) jSteps.get(k)).getJSONObject("start_location").getDouble("lng");
                            Double toLat = ((JSONObject) jSteps.get(k)).getJSONObject("end_location").getDouble("lat");
                            Double toLng = ((JSONObject) jSteps.get(k)).getJSONObject("end_location").getDouble("lng");
                            path.add(new LatLng(fromLat, fromLng));
                            path.add(new LatLng(toLat, toLng));
                            e.addStep(new LatLng(fromLat, fromLng));
                            e.addStep(new LatLng(toLat, toLng));
                        }
                        else{
                            int newStepDistance=stepDistance;
                            Double startLat = ((JSONObject) jSteps.get(k)).getJSONObject("start_location").getDouble("lat");
                            Double startLng = ((JSONObject) jSteps.get(k)).getJSONObject("start_location").getDouble("lng");
                            Double endLat = ((JSONObject) jSteps.get(k)).getJSONObject("end_location").getDouble("lat");
                            Double endLng = ((JSONObject) jSteps.get(k)).getJSONObject("end_location").getDouble("lng");

                            double c= Math.pow(Math.pow((startLat-endLat),2)+ Math.pow((startLng-endLng),2),0.5);
                            double previousPLat=startLat;
                            double previousPLng=startLng;
                            double ratio=(double)100/(double)stepDistance;
                            for(int a=1; newStepDistance>100; a++) {
                                double pLat=startLat+((c*ratio)/c)*(endLat-startLat);
                                double pLng=startLng+((c*ratio)/c)*(endLng-startLng);
                                e.addStep(new LatLng(previousPLat, previousPLng));
                                e.addStep(new LatLng(pLat, pLng));
                                newStepDistance-=100;
                                previousPLat=pLat;
                                previousPLng=pLng;
                            }
                            e.addStep(new LatLng(previousPLat, previousPLng));
                            e.addStep(new LatLng(endLat, endLng));
                        }
                    }
                    routesToSnap.add(e);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("PathJSONParser JSONException: ", e.toString());
        } catch (Exception e) {

        }
        return routesToSnap;
    }
}
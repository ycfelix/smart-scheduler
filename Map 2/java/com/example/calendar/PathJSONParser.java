package com.example.calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
/*
//version: for direction

public class PathJSONParser {

    public ArrayList<ExtractedJSON> parse(JSONObject jObject) {
        //List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        ArrayList<ExtractedJSON> routes = new ArrayList<ExtractedJSON>();

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject JDistance = null;
        JSONObject JDuration = null;
        int distance =0;
        int duration =0;

        try {
            jRoutes = jObject.getJSONArray("routes");
            // Traversing all routes

            System.out.println("Routes len: "+jRoutes.length());

            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                routes.add(new ExtractedJSON());

                // Traversing all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    System.out.println("Legs i: "+jLegs.get(j));
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    JDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    distance += ((JSONObject) JDistance).getInt("value");
                    JDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    duration += ((JSONObject) JDuration).getInt("value");

                    // Traversing all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                .get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        // Traversing all points
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat",
                                    Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng",
                                    Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.get(routes.size()-1).addLeg(path);
                }
                routes.get(routes.size()-1).setDistance(distance);
                routes.get(routes.size()-1).setDuration(duration);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }


     // Method Courtesy :
     // jeffreysambells.com/2010/05/27
     // /decoding-polylines-from-google-maps-direction-api-with-java

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}*/



//version: for snapped route


public class PathJSONParser {

    public ArrayList<ExtractedJSON> parse(JSONObject jObject) {
        //ArrayList<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        //version: draw direction
        //ArrayList<ExtractedJSON> routes = new ArrayList<ExtractedJSON>();
        //version: snap route
        ArrayList<ExtractedJSON> routesToSnap = new ArrayList<ExtractedJSON>();

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject JDistance = null;
        JSONObject JDuration = null;
        int distance =0;
        int duration =0;
        ArrayList<LatLng> path2 = new ArrayList<LatLng>();

        try {
            jRoutes = jObject.getJSONArray("routes");
            // Traversing all routes

            System.out.println("Routes len: "+jRoutes.length());

            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                //version: draw direction
                //routes.add(new ExtractedJSON());
                System.out.println("jLegs size: "+jLegs.length());

                // Traversing all legs [1 leg = 1 alternative route]
                for (int j = 0; j < jLegs.length(); j++) {
                    System.out.println("Legs i: "+jLegs.get(j));
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    JDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    distance += ((JSONObject) JDistance).getInt("value");
                    JDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    duration += ((JSONObject) JDuration).getInt("value");

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
                            path2.add(new LatLng(fromLat, fromLng));
                            path2.add(new LatLng(toLat, toLng));
                            e.addStep(new LatLng(fromLat, fromLng));
                            e.addStep(new LatLng(toLat, toLng));
                        }
                        else{
                            System.out.println("Entered else");
                            int newStepDistance=stepDistance;
                            Double startLat = ((JSONObject) jSteps.get(k)).getJSONObject("start_location").getDouble("lat");
                            Double startLng = ((JSONObject) jSteps.get(k)).getJSONObject("start_location").getDouble("lng");
                            Double endLat = ((JSONObject) jSteps.get(k)).getJSONObject("end_location").getDouble("lat");
                            Double endLng = ((JSONObject) jSteps.get(k)).getJSONObject("end_location").getDouble("lng");

                            double c=Math.pow(Math.pow((startLat-endLat),2)+Math.pow((startLng-endLng),2),0.5);
                            double previousPLat=startLat;
                            double previousPLng=startLng;
                            double ratio=(double)100/(double)stepDistance;
                            for(int a=1; newStepDistance>100; a++) {
                                System.out.println("Entered else while");
                                double pLat=startLat+((c*ratio)/c)*(endLat-startLat);
                                double pLng=startLng+((c*ratio)/c)*(endLng-startLng);
                                e.addStep(new LatLng(previousPLat, previousPLng));
                                e.addStep(new LatLng(pLat, pLng));
                                System.out.println("added: "+previousPLat+","+previousPLng+"|"+pLat+","+ pLng);
                                newStepDistance-=100;
                                previousPLat=pLat;
                                previousPLng=pLng;
                            }
                            e.addStep(new LatLng(previousPLat, previousPLng));
                            e.addStep(new LatLng(endLat, endLng));
                            System.out.println("added: "+previousPLat+","+previousPLng+"|"+endLat+","+ endLng);
                        }
                    }
                    //version: draw direction
                    //routes.get(routes.size()-1).addLeg(path);
                    //version: snape route
                    routesToSnap.add(e);
                    System.out.println("routesToSnap size: "+routesToSnap.size());
                }
                /*
                //version: draw direction
                routes.get(routes.size()-1).setDistance(distance);
                routes.get(routes.size()-1).setDuration(duration);
                */
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) { }

        //System.out.println("path2: "+path2);
        //return path2;
        return routesToSnap;
    }


 //Method Courtesy :
 //jeffreysambells.com/2010/05/27
 //decoding-polylines-from-google-maps-direction-api-with-java

private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
        int b, shift = 0, result = 0;
        do {
        b = encoded.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
        } while (b >= 0x20);
        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
        lat += dlat;

        shift = 0;
        result = 0;
        do {
        b = encoded.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
        } while (b >= 0x20);
        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
        lng += dlng;

        LatLng p = new LatLng((((double) lat / 1E5)),
        (((double) lng / 1E5)));
        poly.add(p);
        }
        return poly;
        }
        }
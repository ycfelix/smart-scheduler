package com.ust.map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SQLDB {
    private ArrayList<LatLng> currentPathHistory;
    private ArrayList<Integer> walkingPathDateTimeHistory;
    private ArrayList<ArrayList<LatLng>> walkingPathLatLngHistory;
    private ArrayList<Integer> walkingPathDurationHistory;
    private ArrayList<Integer> drivingPathDateTimeHistory;
    private ArrayList<ArrayList<LatLng>> drivingPathLatLngHistory;
    private ArrayList<Integer> drivingPathDurationHistory;
    private String userId;
    private ArrayList<String> fdEmailList;
    private Context context;
    private ArrayList<Double> lat;
    private ArrayList<Double> lng;
    public ArrayList<Double> fdLat;
    public ArrayList<Double> fdLng;
    private String lastLocationHistoryJSON;
    private String lastFriendEmailListResultJSON;
    private String lastFriendLocationResultJSON;
    private String userEmail;
    public boolean fdLocationGot;
    public boolean fdLatLngUpdated;

    // Connect to database.
    public SQLDB(Context context, String userEmail){
        this.context=context;
        this.userEmail=userEmail;
        walkingPathLatLngHistory=new ArrayList<ArrayList<LatLng>>();
        walkingPathDurationHistory=new ArrayList<Integer>();
        drivingPathLatLngHistory=new ArrayList<ArrayList<LatLng>>();
        drivingPathDurationHistory=new ArrayList<Integer>();
        fdEmailList = new ArrayList<>();
        lat = new ArrayList<Double>();
        lng = new ArrayList<Double>();
        fdLat = new ArrayList<>();
        fdLng = new ArrayList<>();
        walkingPathDateTimeHistory = new ArrayList<Integer>();
        userId="0";
        getUserId();
        fdLocationGot=false;
        fdLatLngUpdated=false;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getLocationHistory();
    }

    public ArrayList<ArrayList<LatLng>> getWalkingPathLatLngHistory(){
        return walkingPathLatLngHistory;
    }

    public ArrayList<ArrayList<LatLng>> getDrivingPathLatLngHistory(){
        return drivingPathLatLngHistory;
    }

    public ArrayList<Integer> getWalkingPathDurationHistory(){
        return walkingPathDurationHistory;
    }

    public ArrayList<Integer> getDrivingPathDurationHistory(){
        return drivingPathDurationHistory;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private LatLng originStart;
        private LatLng originEnd;
        private boolean addOriginEnd;

        public HttpAsyncTask(LatLng originStart, LatLng originEnd, boolean addOriginEnd) {
            this.originStart = originStart;
            this.originEnd = originEnd;
            this.addOriginEnd = addOriginEnd;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                if(!((result==null)||(result==""))) {
                    JSONObject json = new JSONObject(result);
                    String warningMessage = "";
                    JSONArray pointList = json.getJSONArray("snappedPoints");
                    //walking version
                    getSnappedPoint(pointList, originStart, originEnd);

                    //driving version
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("SQLDB JSONException: ", e.toString());
            }
        }

        private void getSnappedPoint(JSONArray pointList, LatLng OS, LatLng OE){
            try{
                currentPathHistory = new ArrayList<LatLng>();
                currentPathHistory.add(originStart);    //to correct, because start point get deviated
                LatLng currentPoint;
                for(int i=0; i<pointList.length(); i++){
                    JSONObject location = pointList.getJSONObject(i).getJSONObject("location");
                    currentPoint=new LatLng(location.getDouble("latitude"),location.getDouble("longitude"));
                    if(!(currentPoint.equals(currentPathHistory.get(currentPathHistory.size()-1)))){   //avoid adding duplicated currentPathHistory
                        currentPathHistory.add(currentPoint);
                    }
                    System.out.println("Points "+currentPathHistory.size()+": "+currentPathHistory.get(currentPathHistory.size()-1).latitude+","+currentPathHistory.get(currentPathHistory.size()-1).longitude);
                }
                //if(addOriginEnd) {
                    currentPathHistory.add(originEnd);  //to correct, because destination point get deviated
                //}
                System.out.println(currentPathHistory.get(0)+"|"+currentPathHistory.get(currentPathHistory.size()-1));
                walkingPathLatLngHistory.add(currentPathHistory);
                System.out.println("Data got!!");
            } catch (JSONException e) {
                Log.d("SQLDB JSONException: ", e.toString());
            }
        }

        private String GET(String url){
            String result = "";
            try {
                URL link = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();
                try {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    if(inputStream != null) {
                        result = convertInputStreamToString(inputStream);
                    }
                    else {
                        result = "inputStream == null";
                    }
                    //readStream(inputStream);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                System.out.println("SQLDB Exception: "+e.getLocalizedMessage());
            }

            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            inputStream.close();
            return result;

        }
    }

    public void getUserId(){
        HashMap<String, String> data=new HashMap<>();
        String sqlCommand="select UserId from Accounts where Email="+"'"+userEmail+"'";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);
        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //parse JSON
                            JSONArray result= response.getJSONArray("result");
                            JSONObject currentRecord = ((JSONObject) result.get(0));
                            userId=currentRecord.getString("UserId");
                        } catch (JSONException e) {
                            Log.d("SQLDB JSONException: ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SQLDB onErrorResponse: ", error.toString());
                    }
                }
        );
        queue.add(request);
    }

    public void insertLocationData(LatLng point, double dateTime){
        String url = "http://13.70.2.33/api/sql_db";
        //do a select first, then
        String query = "INSERT INTO user_location_history VALUES ("+"'"+userId+"'"+","+ point.latitude+","+point.longitude+","+dateTime+");";
        List<String> commands= Arrays.asList(query);
        for(int i=0;i<commands.size();i++){
            HashMap<String, String> data=new HashMap<>();
            data.put("db_name","Smart Scheduler");
            data.put("sql_cmd",commands.get(i));
            System.out.println("Query: "+commands.get(i));

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("SQLDB onResponse: ", response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("SQLDB onErrorResponse: ", error.toString());
                        }
                    }
            );
            queue.add(request);
        }

    }

    public void getLocationHistory(){   //return true: have updated, return false: no updates
        HashMap<String, String> data=new HashMap<>();
        String sqlCommand="SELECT * FROM dbo.user_location_history WHERE (userId="+"'"+userId+"'"+")";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //parse JSON
                            JSONArray result= response.getJSONArray("result");
                            boolean needToUpdate=true;
                            if(lastLocationHistoryJSON!=null){
                                if(lastLocationHistoryJSON.equals(result.toString())){
                                    needToUpdate=false;
                                }
                            }

                            ArrayList<Double> temLat = new ArrayList<>();
                            ArrayList<Double> temLng = new ArrayList<>();
                            ArrayList<Integer> temWalkingPathDateTimeHistory = new ArrayList<>();
                            if(needToUpdate) {
                                lastLocationHistoryJSON = result.toString();
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject currentRecord = ((JSONObject) result.get(i));
                                    temLat.add(currentRecord.getDouble("Lat"));
                                    temLng.add(currentRecord.getDouble("Lng"));
                                    temWalkingPathDateTimeHistory.add(currentRecord.getInt("DateTime"));
                                }

                                walkingPathDateTimeHistory.clear();
                                for(int i=0; i<temWalkingPathDateTimeHistory.size(); i++){
                                    walkingPathDateTimeHistory.add(temWalkingPathDateTimeHistory.get(i));
                                }
                                Collections.sort(temWalkingPathDateTimeHistory);
                                lat.clear();
                                lng.clear();
                                for(int i=0; i<temWalkingPathDateTimeHistory.size(); i++){
                                    int oldIdx=walkingPathDateTimeHistory.indexOf(temWalkingPathDateTimeHistory.get(i));
                                    lat.add(temLat.get(oldIdx));
                                    lng.add(temLng.get(oldIdx));
                                }

                                //get path info using google api
                                boolean addOriginEnd = false;
                                for (int i = 0; i < lat.size() - 1; i++) {
                                    System.out.println("history: " + lat.get(i) + "," + lng.get(i) + "|" + lat.get(i + 1) + "," + lng.get(i + 1));
                                    if (i == lat.size() - 2) {
                                        addOriginEnd = true;
                                    }
                                    String url="https://roads.googleapis.com/v1/snapToRoads?path=" + lat.get(i) + "," + lng.get(i) + "|" + lat.get(i + 1) + "," + lng.get(i + 1) + "&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking";
                                    new HttpAsyncTask(new LatLng(lat.get(i), lng.get(i)), new LatLng(lat.get(i + 1), lng.get(i + 1)), addOriginEnd).execute(url);
                                    walkingPathDurationHistory.add(walkingPathDateTimeHistory.get(i + 1) - walkingPathDateTimeHistory.get(i));
                                }
                            }
                        } catch (JSONException e) {
                            Log.d("SQLDB JSONException: ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SQLDB onErrorResponse: ", error.toString());
                    }
                }
        );
        queue.add(request);
    }

    public void getUserFriendEmailListByUserEmail(){
        HashMap<String, String> data=new HashMap<>();
        String sqlCommand="SELECT * FROM dbo.user_friend WHERE (user_email="+"'"+userEmail+"'"+")";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //parse JSON
                            JSONArray result= response.getJSONArray("result");
                            boolean needToUpdate=true;
                            if(lastFriendEmailListResultJSON!=null){
                                if(lastFriendEmailListResultJSON.equals(result.toString())){
                                    needToUpdate=false;
                                }
                            }

                            if(needToUpdate) {
                                lastFriendEmailListResultJSON = result.toString();
                                JSONObject currentRecord = ((JSONObject) result.get(0));
                                String fdEmailStringList = currentRecord.getString("friend_list");
                                String[] fdEmailArray = fdEmailStringList.split(",");
                                if(fdEmailList!=null) {
                                    fdEmailList.clear();
                                }
                                Collections.addAll(fdEmailList, fdEmailArray);
                            }
                            getfdLocationByfdEmail();
                        } catch (JSONException e) {
                            Log.d("SQLDB JSONException: ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SQLDB onErrorResponse: ", error.toString());
                    }
                }
        );
        queue.add(request);
    }

    public void getfdLocationByfdEmail(){   //return true: have updated, return false: no updates
        HashMap<String, String> data=new HashMap<>();
        String sqlCommand="SELECT * FROM dbo.user_current_location WHERE (Online=1) AND (";
        for(int i=0; i<fdEmailList.size(); i++){
            if(i!=0){
                sqlCommand+="OR";
            }
            sqlCommand+="(user_email="+"'"+fdEmailList.get(i)+"'"+")";;
        }
        sqlCommand+=")";

        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //parse JSON
                            JSONArray result= response.getJSONArray("result");
                            boolean needToUpdate=true;
                            if(lastFriendLocationResultJSON!=null){
                                if(lastFriendLocationResultJSON.equals(result.toString())){
                                    needToUpdate=false;
                                }
                            }

                            if(needToUpdate) {
                                lastFriendLocationResultJSON = result.toString();
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject currentRecord = ((JSONObject) result.get(i));
                                    fdLat.add(currentRecord.getDouble("Lat"));
                                    fdLng.add(currentRecord.getDouble("Lng"));
                                }
                                fdLatLngUpdated=true;
                            }
                            fdLocationGot=true;
                        } catch (JSONException e) {
                            Log.d("SQLDB JSONException: ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SQLDB onErrorResponse: ", error.toString());
                    }
                }
        );
        queue.add(request);
    }
}

package com.ust.map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.ust.utility.Utils;
import android.content.SharedPreferences;

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
    private ArrayList<String> fdIDList;
    private Context context;
    private ArrayList<Double> lat;
    private ArrayList<Double> lng;
    public ArrayList<Double> fdLat;
    public ArrayList<Double> fdLng;
    private String strLatlngList = "";
    private String lastLocationHistoryJSON;
    private String lastFriendEmailListResultJSON;
    private String lastFriendIDListResultJSON;
    private String lastFriendLocationResultJSON;
    private String userEmail;
    public boolean fdLocationGot;
    public boolean fdLatLngUpdated;

    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public SQLDB(Context context, String userEmail){
        this.context=context;
        this.userEmail=userEmail;
        walkingPathLatLngHistory=new ArrayList<ArrayList<LatLng>>();
        walkingPathDurationHistory=new ArrayList<Integer>();
        drivingPathLatLngHistory=new ArrayList<ArrayList<LatLng>>();
        drivingPathDurationHistory=new ArrayList<Integer>();
        userId="0";
        getUserId();
        fdEmailList = new ArrayList<>();
        fdIDList = new ArrayList<>();
        fdLocationGot=false;
        fdLatLngUpdated=false;
        System.out.println("started http");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //selectFromDatabase();
        lat = new ArrayList<Double>();
        lng = new ArrayList<Double>();
        fdLat = new ArrayList<>();
        fdLng = new ArrayList<>();
        walkingPathDateTimeHistory = new ArrayList<Integer>();
        getLocationHistory();

        //insertLocationData(new LatLng(22.340685, 114.161366),2);
    }
/*
    public void selectFromDatabase(){
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String url = String.format("jdbc:jtds:sqlserver://"+hostName+":1433/"+dbName);
            Connection conn = null;
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("connection Successful, "+conn);


            walkingPathDateTimeHistory = new ArrayList<Integer>();
            drivingPathDateTimeHistory = new ArrayList<Integer>();
            if (conn != null) {
                //System.out.println("DB: "+conn.getCatalog());
                Statement statement = conn.createStatement();
                //for walking history
                String query = "SELECT * FROM [dbo].[user_location_history] WHERE (userId="+userId+")";
                System.out.println("Query: "+query);

                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    lat.add(rs.getDouble("Lat"));
                    lng.add(rs.getDouble("lng"));
                    walkingPathDateTimeHistory.add(rs.getInt("dateTime"));
                }
                //for driving history
                //for driving history

                //
                //strLatlngList = "";
                //for(int i=0; i<lat.size(); i++){
                //    strLatlngList+=lat.get(i)+","+lng.get(i);
                //    if(i!=lat.size()-1){
                //        strLatlngList+="|";
                //    }
                //}
                strLatlngList = "";
                boolean addOriginEnd=false;
                for(int i=0; i<lat.size()-1; i++){
                    System.out.println("history: "+lat.get(i)+","+lng.get(i)+"|"+lat.get(i+1)+","+lng.get(i+1));
                    if(i==lat.size()-2){
                        addOriginEnd=true;
                    }
                    new HttpAsyncTask(new LatLng(lat.get(i),lng.get(i)),new LatLng(lat.get(i+1),lng.get(i+1)),addOriginEnd).execute("https://roads.googleapis.com/v1/snapToRoads?path="+lat.get(i)+","+lng.get(i)+"|"+lat.get(i+1)+","+lng.get(i+1)+"&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking");
                    walkingPathDurationHistory.add(walkingPathDateTimeHistory.get(i+1)-walkingPathDateTimeHistory.get(i));
                }

                //new HttpAsyncTask().execute("https://roads.googleapis.com/v1/snapToRoads?path="+strLatlngList+"&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE");
            }
            conn.close();
        }
        catch (SQLException e) {
            System.out.println("Query error: "+e);
        }
        catch(ClassNotFoundException e){

        }
    }*/
/*
    public void insertToDatabase(LatLng point, double walkingPathDateTimeHistory){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String url = String.format("jdbc:jtds:sqlserver://"+hostName+":1433/"+dbName);
            Connection conn = null;
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("connection Successful, "+conn);

            if (conn != null) {
                Statement statement = conn.createStatement();
                //for walking
                String query = "INSERT INTO user_location_history VALUES (20,"+ point.latitude+","+point.longitude+","+walkingPathDateTimeHistory+");";
                System.out.println("Query: "+query);

                //for driving history
                //for driving history

                ResultSet rs = statement.executeQuery(query);
            }
            conn.close();
        }
        catch (SQLException e) {
            System.out.println("Query error: "+e);
        }
        catch(ClassNotFoundException e){

        }
    }*/

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
        //private ArrayList<LatLng> newPathPoints;

        public HttpAsyncTask(LatLng originStart, LatLng originEnd, boolean addOriginEnd) {
            this.originStart = originStart;
            this.originEnd = originEnd;
            this.addOriginEnd = addOriginEnd;
            //newPathPoints = new ArrayList<LatLng>();
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.print("HttpAsyncTask result: "+result);
                JSONObject json = new JSONObject(result);
                String warningMessage="";
                //warningMessage= json.getString("warningMessage");
                //System.out.println("warningMessage: "+warningMessage);
                /*
                if(warningMessage.equals("Input path is too sparse. You should provide a path where consecutive currentPathHistory are closer to each other. Refer to the 'path' parameter in Google Roads API documentation.")){
                    System.out.println("entered warningMessage branch");
                    String url = getMapsApiDirectionsUrl(originStart,originEnd);
                    GetDirectionTask getDirectionTask = new GetDirectionTask();
                    getDirectionTask.execute(url);
                }
                else {
                    JSONArray pointList = json.getJSONArray("snappedPoints");
                    getSnappedPoint(pointList,originStart,originEnd);
                }*/

                JSONArray pointList = json.getJSONArray("snappedPoints");
                System.out.println("pointList: "+pointList);
                //walking version
                getSnappedPoint(pointList,originStart,originEnd);

                //driving version
                //driving version

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void getSnappedPoint(JSONArray pointList, LatLng OS, LatLng OE){
            try{
                currentPathHistory = new ArrayList<LatLng>();
                System.out.println("PointList size: "+pointList.length());
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

            }
        }

        private String GET(String url){
            String result = "";
            try {
                URL link = new URL(url);
                System.out.println("URL: "+url);
                HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();
                try {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    if(inputStream != null) {
                        result = convertInputStreamToString(inputStream);
                        System.out.println("Successfully get result");
                    }
                    else {
                        result = "inputStream == null";
                    }
                    //readStream(inputStream);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                System.out.println("InputStream "+e.getLocalizedMessage());
            }

            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

/*
        private class GetDirectionTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... url) {
                String data = "";
                try {
                    HttpConnection http = new HttpConnection();
                    data = http.readUrl(url[0]);
                    System.out.println("url:"+url[0]);
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                System.out.println("data: "+data);
                return data;
            }

            @Override
            protected void onPostExecute(String result) { //result == data
                System.out.println("result: "+result);
                super.onPostExecute(result);
                //System.out.println("Result:"+result);
                ParserTask parserTask = new ParserTask();
                parserTask.execute(result);
            }

            private class ParserTask extends AsyncTask<String, Integer, ArrayList<ExtractedJSON>> {

                @Override
                protected ArrayList<ExtractedJSON> doInBackground(String... jsonData) {

                    System.out.println("jsonData[0]: "+jsonData[0]);

                    JSONObject jObject;
                    ArrayList<ExtractedJSON> routes = null;

                    try {
                        jObject = new JSONObject(jsonData[0]);
                        System.out.println("jObject: "+jObject);
                        PathJSONParser parser = new PathJSONParser();
                        routes = parser.parse(jObject);
                        System.out.println("Routes size in ParserTask:"+routes.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return routes;
                }

                @Override
                protected void onPostExecute(ArrayList<ExtractedJSON> routes) {
                    ArrayList<LatLng> newPathPoints = routes.get(0).getSteps();

                    try {
                        System.out.println("newPathPoints.size(): " + newPathPoints.size());
                        for (int i = 0; i < newPathPoints.size() - 1; i++) {
                            addOriginEnd = false;
                            if (i == newPathPoints.size() - 2) {
                                addOriginEnd = true;
                            }
                            String result = GET("https://roads.googleapis.com/v1/snapToRoads?path=" + newPathPoints.get(i) + "|" + newPathPoints.get(i + 1) + "&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking");
                            System.out.println("Result: "+result);
                            JSONObject json = new JSONObject(result);
                            System.out.println("Json: "+json);
                            JSONArray pointList = json.getJSONArray("snappedPoints");
                            System.out.println("pointList.length(): "+pointList.length());
                            getSnappedPoint(pointList,newPathPoints.get(i),newPathPoints.get(i + 1));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }*/
    }

    private String getMapsApiDirectionsUrl(LatLng pointFrom, LatLng pointTo) {
        String waypoints = "waypoints=optimize:true|"
                + (pointFrom).latitude + "," + (pointFrom).longitude
                + "|" + (pointTo).latitude + ","
                + (pointTo).longitude;
        String OriDest = "origin="+(pointFrom).latitude+","+(pointFrom).longitude+"&destination="+(pointTo).latitude+","+(pointTo).longitude;
        String sensor = "sensor=true";
        String mode = "mode=walking";
        String params = OriDest+"&%20"+waypoints + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + "key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&callback=initialize&" + params;
        //System.out.println("OUTPUT:"+url);
        return url;
    }

    public void getUserId(){
        HashMap<String, String> data=new HashMap<>();

        String sqlCommand="select UserId from Accounts where Email="+"'"+userEmail+"'";
        System.out.println("sql: "+sqlCommand);
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("response: "+response);
                            //parse JSON
                            JSONArray result= response.getJSONArray("result");
                            //System.out.print("user id result: "+ result);

                            JSONObject currentRecord = ((JSONObject) result.get(0));
                            //System.out.print("currentRecord result: "+currentRecord);

                            userId=currentRecord.getString("UserId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
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
                            System.out.println("insert data response: "+response);
                            //response whether success or not
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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
                            System.out.println("getLocationHistory result: "+result);
                            boolean needToUpdate=true;
                            if(lastLocationHistoryJSON!=null){
                                if(lastLocationHistoryJSON.equals(result.toString())){
                                    needToUpdate=false;
                                }
                            }
                            //need to solve!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            ArrayList<Double> temLat = new ArrayList<>();
                            ArrayList<Double> temLng = new ArrayList<>();
                            ArrayList<Integer> temWalkingPathDateTimeHistory = new ArrayList<>();
                            if(needToUpdate) {
                                lastLocationHistoryJSON = result.toString();
                                System.out.println("this is the result:" + result);
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject currentRecord = ((JSONObject) result.get(i));
                                    System.out.println("currentRecord: " + currentRecord);
                                    temLat.add(currentRecord.getDouble("Lat"));
                                    temLng.add(currentRecord.getDouble("Lng"));
                                    temWalkingPathDateTimeHistory.add(currentRecord.getInt("DateTime"));
                                }
                                System.out.println("temWalkingPathDateTimeHistory: "+temWalkingPathDateTimeHistory);
                                System.out.println("temLat: "+temLat);
                                System.out.println("temLng: "+temLng);

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
                                System.out.println("walkingPathDateTimeHistory: "+walkingPathDateTimeHistory);
                                System.out.println("lat: "+lat);
                                System.out.println("lng: "+lng);

                                //get path info using google api
                                boolean addOriginEnd = false;
                                for (int i = 0; i < lat.size() - 1; i++) {
                                    System.out.println("history: " + lat.get(i) + "," + lng.get(i) + "|" + lat.get(i + 1) + "," + lng.get(i + 1));
                                    if (i == lat.size() - 2) {
                                        addOriginEnd = true;
                                    }
                                    String url="https://roads.googleapis.com/v1/snapToRoads?path=" + lat.get(i) + "," + lng.get(i) + "|" + lat.get(i + 1) + "," + lng.get(i + 1) + "&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking";
                                    System.out.println("onResponse url: "+url);
                                    new HttpAsyncTask(new LatLng(lat.get(i), lng.get(i)), new LatLng(lat.get(i + 1), lng.get(i + 1)), addOriginEnd).execute(url);
                                    walkingPathDurationHistory.add(walkingPathDateTimeHistory.get(i + 1) - walkingPathDateTimeHistory.get(i));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }

    public void getUserFriendEmailListByUserEmail(){
        HashMap<String, String> data=new HashMap<>();
        String sqlCommand="SELECT * FROM dbo.user_friend WHERE (user_email="+"'"+userEmail+"'"+")";
        System.out.println("getUserFriendEmailListByUserID query: "+sqlCommand);
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
                            System.out.println("getUserFriendEmailListByUserEmail result: "+result);
                            boolean needToUpdate=true;
                            if(lastFriendEmailListResultJSON!=null){
                                if(lastFriendEmailListResultJSON.equals(result.toString())){
                                    needToUpdate=false;
                                }
                            }

                            if(needToUpdate) {
                                lastFriendEmailListResultJSON = result.toString();
                                JSONObject currentRecord = ((JSONObject) result.get(0));
                                //System.out.print("currentRecord result: "+currentRecord);

                                String fdEmailStringList = currentRecord.getString("friend_list");
                                String[] fdEmailArray = fdEmailStringList.split(",");
                                if(fdEmailList!=null) {
                                    fdEmailList.clear();
                                }
                                Collections.addAll(fdEmailList, fdEmailArray);
                            }
                            getfdLocationByfdEmail();
                            //getUserFriendIDListByfdEmail();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }
/*
    public void getUserFriendIDListByfdEmail(){
        HashMap<String, String> data=new HashMap<>();
        String sqlCommand="SELECT * FROM dbo.Accounts WHERE ";
        for(int i=0; i<fdEmailList.size(); i++){
            if(i!=0){
                sqlCommand+="OR";
            }
            sqlCommand+="(Email="+"'"+fdEmailList.get(i)+"'"+")";
        }
        System.out.println("getUserFriendIDListByfdEmail query: "+sqlCommand);
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
                            System.out.println("getUserFriendIDListByfdEmail result: "+result);
                            boolean needToUpdate=true;
                            if(lastFriendIDListResultJSON!=null){
                                if(lastFriendIDListResultJSON.equals(result.toString())){
                                    needToUpdate=false;
                                }
                            }

                            if(needToUpdate) {
                                lastFriendIDListResultJSON = result.toString();
                                System.out.println("lastFriendIDListResultJSON:" + lastFriendIDListResultJSON);
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject currentRecord = ((JSONObject) result.get(i));
                                    fdIDList.add(currentRecord.getString("UserID"));
                                }
                                System.out.println("fdIDList:" + fdIDList);
                            }

                            getfdLocationByfdID();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);


    }*/

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
        System.out.println("getfdLocationByfdEmail query: "+sqlCommand);

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
                            System.out.println("getfdLocationByfdEmail result: "+result);
                            boolean needToUpdate=true;
                            if(lastFriendLocationResultJSON!=null){
                                if(lastFriendLocationResultJSON.equals(result.toString())){
                                    needToUpdate=false;
                                }
                            }

                            if(needToUpdate) {
                                lastFriendLocationResultJSON = result.toString();
                                System.out.println("this is the result:" + result);
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject currentRecord = ((JSONObject) result.get(i));
                                    System.out.println("currentRecord: " + currentRecord);
                                    fdLat.add(currentRecord.getDouble("Lat"));
                                    fdLng.add(currentRecord.getDouble("Lng"));
                                }
                                fdLatLngUpdated=true;
                            }
                            fdLocationGot=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }
}

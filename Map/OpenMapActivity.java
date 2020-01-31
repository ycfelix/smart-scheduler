package com.example.calendar;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OpenMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> markers;
    private ArrayList<LatLng> points;
    private PolylineOptions polyLineOptions;
    private Polyline polyline;
    private static final float DEFAULT_ZOOM = 15f;
    private TextView tvFrom;
    private TextView tvTo;
    private boolean byClick;
    private boolean byText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        points= new ArrayList();
        markers= new ArrayList<Marker>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvFrom = (TextView) findViewById(R.id.from);
        tvFrom.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            // TODO: the editText has just been left
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    if(!byClick) {
                        if(points!=null) {
                            if (points.size() == 2) {
                                System.out.println("cleaning");
                                if(markers!=null){
                                        markers.get(0).remove();
                                }
                                markers.remove(0);
                                points.remove(0);
                                if(polyline!=null) {
                                    polyline.remove();
                                }
                            }
                        }

                        System.out.println("Changed text");
                        String searchString = tvFrom.getText().toString();

                        Geocoder geocoder = new Geocoder(OpenMapActivity.this);
                        List<Address> list = new ArrayList<>();
                        try {
                            list = geocoder.getFromLocationName(searchString, 1);
                        } catch (IOException e) {
                        }

                        if (list.size() > 0) {
                            Address address = list.get(0);
                            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                                    address.getAddressLine(0));
                        }

                        if (points.size() == 2) {
                            String url = getMapsApiDirectionsUrl();
                            ReadTask downloadTask = new ReadTask();
                            downloadTask.execute(url);

                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13));
                        }
                    }
                }
            }

            private void moveCamera(LatLng latLng, float zoom, String title){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                markers.add(0,mMap.addMarker(options));
                points.add(0,latLng);
            }
        });

        tvTo = (TextView) findViewById(R.id.to);
        tvTo.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            // TODO: the editText has just been left
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    if(!byClick) {
                        if(points!=null) {
                            if (points.size() == 2) {
                                System.out.println("cleaning");
                                if(markers!=null){
                                    markers.get(1).remove();
                                }
                                markers.remove(1);
                                points.remove(1);
                                if(polyline!=null) {
                                    polyline.remove();
                                }
                            }
                        }

                        System.out.println("Changed text");
                        String searchString = tvFrom.getText().toString();

                        Geocoder geocoder = new Geocoder(OpenMapActivity.this);
                        List<Address> list = new ArrayList<>();
                        try {
                            list = geocoder.getFromLocationName(searchString, 1);
                        } catch (IOException e) {
                        }

                        if (list.size() > 0) {
                            Address address = list.get(0);
                            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                                    address.getAddressLine(0));
                        }

                        if (points.size() == 2) {
                            String url = getMapsApiDirectionsUrl();
                            ReadTask downloadTask = new ReadTask();
                            downloadTask.execute(url);

                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13));
                        }
                    }
                }
            }

            private void moveCamera(LatLng latLng, float zoom, String title){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                markers.add(1,mMap.addMarker(options));
                points.add(1,latLng);
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
        //example of add marker
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(points!=null) {
                    if (points.size() == 2) {
                        System.out.println("cleaning");
                        if(markers!=null){
                            for(int i=0;i<markers.size();i++) {
                                markers.get(i).remove();
                            }
                        }
                        tvTo.setText("");
                        markers.clear();
                        points.clear();
                        if(polyline!=null) {
                            polyline.remove();
                        }
                    }
                }
                String _Location="null";

                double longitude = point.longitude;
                double latitude = point.latitude;
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if(null!=listAddresses&&listAddresses.size()>0){
                        _Location = listAddresses.get(0).getAddressLine(0);
                    }
                    System.out.println(_Location);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("exception");
                }

                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(_Location);
                markers.add(mMap.addMarker(markerOption));
                points.add(point);
                //System.out.println(point.latitude+"---"+ point.longitude);
                switch(points.size()){
                    case 1:
                        byClick=true;
                        tvFrom.setText(_Location);
                        byClick=false;
                        break;
                    case 2:
                        byClick=true;
                        tvTo.setText(_Location);
                        byClick=false;
                        break;
                }

                System.out.println("Points size: "+points.size());
                if (points.size() == 2) {
                    String url = getMapsApiDirectionsUrl();
                    ReadTask downloadTask = new ReadTask();
                    downloadTask.execute(url);

                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13));
                }
            }
        });
    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + (points.get(0)).latitude + "," + (points.get(0)).longitude
                + "|" + "|" + (points.get(1)).latitude + ","
                + (points.get(1)).longitude;

        String OriDest = "origin="+(points.get(0)).latitude+","+(points.get(0)).longitude+"&destination="+(points.get(1)).latitude+","+(points.get(1)).longitude;
        String sensor = "sensor=false";
        String params = OriDest+"&%20"+waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + "key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&callback=initialize&" + params;
        //System.out.println("OUTPUT:"+url);
        return url;
    }



    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
                //System.out.println("Data:"+data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //System.out.println("Result:"+result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
                //System.out.println("Routes:"+routes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            //PolylineOptions polyLineOptions = null;
            polyLineOptions = null;

            if(routes.isEmpty()){
                //no routes
                System.out.println("NO ROUTES!!");
            }
            else {
                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(10);
                    polyLineOptions.color(Color.BLUE);
                }

                polyline = mMap.addPolyline(polyLineOptions);
            }
        }
    }
}

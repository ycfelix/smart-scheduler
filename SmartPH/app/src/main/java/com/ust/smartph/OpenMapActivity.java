package com.example.calendar;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Build;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.view.View.OnClickListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import de.hdodenhof.circleimageview.CircleImageView;


public class OpenMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private Marker markerFrom;
    private Marker markerTo;
    private int numOfMarkers;
    private LatLng pointFrom;
    private LatLng pointTo;
    private PolylineOptions polyLineOptions;
    private Polyline polyline;
    //private ArrayList<Polyline> heatmapPolyline;
    private ArrayList<ArrayList<Polyline>> heatmapPolyline;
    private ArrayList<Integer> heatmapPolylineTag;
    private static final int heatmapColor1=Color.rgb(51, 204, 255);
    private static final int heatmapColor2=Color.rgb(255, 195, 77);
    private static final int heatmapColor3=Color.GREEN;
    private static final int heatmapColor4=Color.RED;
    private static final float DEFAULT_ZOOM = 15f;
    private AutoCompleteTextView tvFrom;
    private AutoCompleteTextView tvTo;
    private TextView tvInfo;
    private boolean byClick;
    private boolean byText;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private Button buttHeatmap;
    private SQLDB sqldb;
    private ArrayList<Integer> range1PathIdx;
    private ArrayList<Integer> range2PathIdx;
    private ArrayList<Integer> range3PathIdx;
    private ArrayList<Integer> range4PathIdx;
    private ArrayList<Integer> range5PathIdx;
    private boolean allHeatmapPolylineShown;
    private boolean range1HeatmapPolylineShown;
    private boolean range2HeatmapPolylineShown;
    private boolean range3HeatmapPolylineShown;
    private boolean range4HeatmapPolylineShown;
    private boolean keepUpdateGPS=false;
    private ArrayList<LatLng> GPSPoints;
    private ArrayList<Polyline> GPSPolyline;
    //private ArrayList<LatLng> snappedGPSPoints;
    private SlidingUpPanelLayout mLayout;
    private ListView listView;
    private SuggestedPathAdapter suggestedPathAdapter;
    private ArrayList<SuggestedPath> alternativeSuggestedPathList;
    private SuggestedPath preferedSuggestedPath;
    //private int numOfSuggestedPaths;
    private LinearLayout dragView;
    private LinearLayout item;
    private TextView dragBar;
    private ViewGroup.LayoutParams layoutParams;
    private float dpToPixel;
    private int itemHeight;
    private int dragBarHeight;
    private int shadowHeight;
    private ImageView preferedModeIV;
    private TextView preferedDistance;
    private TextView preferedDuration;
    private String preferedMode;
    private LinearLayout.LayoutParams buttonPanelViewParams;
    private RelativeLayout buttonPanelView;



    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        System.out.println("getting map");
        checkNetworkEnabled();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        System.out.println("creating sqldb");
        sqldb = new SQLDB(getApplicationContext());
    }

//onMapReday
    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("Map Ready");
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.3352301,114.2662848), DEFAULT_ZOOM));

        dragView = (LinearLayout) findViewById(R.id.dragView);
        alternativeSuggestedPathList= new ArrayList<SuggestedPath>();


        checkGPSPermission(); //need to uncomment
        preferedMode="walking"; //either "walking" or "driving"

        //OnMapClickListener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //hid the keyboard to prevent layout error
                hideKeyboard(OpenMapActivity.this);

                //get Location name
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
                //add marker
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(_Location);
                switch(numOfMarkers){
                    case 1:
                        if(markerTo!=null) {
                            System.out.println("markerTo!=null");
                            markerFrom = mMap.addMarker(markerOption);
                            numOfMarkers+=1;
                            pointFrom=point;
                            byClick=true;
                            tvFrom.setText(_Location);
                            System.out.println("debug: start clearFocus");
                            tvFrom.clearFocus();
                            System.out.println("debug: end clearFocus");
                            byClick=false;
                        }
                        else if(markerFrom!=null){
                            System.out.println("markerFrom!=null");
                            markerTo = mMap.addMarker(markerOption);
                            numOfMarkers+=1;
                            pointTo=point;
                            byClick=true;
                            tvTo.setText(_Location);
                            System.out.println("debug: start clearFocus");
                            tvTo.clearFocus();
                            System.out.println("debug: end clearFocus");
                            byClick=false;
                        }
                        System.out.println("numOfMarkers: "+numOfMarkers);
                        System.out.println("polyline 0 start");


                        //Direction
                        clearSuggestedPathList();
                        String url = getMapsApiDirectionsUrl(preferedMode,pointFrom,pointTo);
                        GetDirectionTask getDirectionTask = new GetDirectionTask(false,true);
                        getDirectionTask.execute(url);
                        //Direction
                        System.out.println("in onMapClick");
                        System.out.println("alternativeSuggestedPathList size: "+alternativeSuggestedPathList.size());

                        //new GetSnappedRouteTask(new LatLng(pointFrom.latitude,pointFrom.longitude),new LatLng(pointTo.latitude,pointTo.longitude)).execute("https://roads.googleapis.com/v1/snapToRoads?path="+pointFrom.latitude+","+pointFrom.longitude+"|"+pointTo.latitude+","+pointTo.longitude+"&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking");
                        System.out.println("polyline 0 end");
                        break;
                    case 2:
                        clearInput(0,false);
                    case 0:
                        //hide previous suggested paths
                        System.out.println("debug: hide at mapClick");
                        mLayout.setPanelHeight(0);
                        mLayout.setShadowHeight(0);
                        mLayout.setPanelState(PanelState.COLLAPSED);
                        buttonPanelViewParams.setMargins(10,0,10,15);
                        buttonPanelView.setLayoutParams(buttonPanelViewParams);

                        markerFrom=mMap.addMarker(markerOption);
                        numOfMarkers+=1;
                        pointFrom=point;
                        byClick=true;
                        tvFrom.setText(_Location);
                        tvFrom.clearFocus();
                        byClick=false;
                        break;
                }
            }
        });

        //From: OnFocusChangeListener
        tvFrom = (AutoCompleteTextView) findViewById(R.id.from);
        tvFrom.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            // TODO: the editText has just been left
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    System.out.println("debug: in tvFrom not focus");
                    if(!byClick) {
                        System.out.println("debug: entered tvFrom not byClick");
                        //clear marker From
                        if(markerFrom!=null){
                            clearInput(1,false);
                        }
                        //search the address and add marker
                        System.out.println("Changed text, tvFrom");
                        String strFrom = tvFrom.getText().toString();
                        if(strFrom!="") {
                            MarkerOptions newMarkerOptions = getMarker(strFrom, 1);
                            if (newMarkerOptions != null) {
                                markerFrom = mMap.addMarker(newMarkerOptions);
                                numOfMarkers += 1;
                                System.out.println("numOfMarkers 1, not focusing: " + numOfMarkers);
                                if (markerTo != null) {
                                    System.out.println("polyline 1 start");
                                    clearSuggestedPathList();
                                    String url = getMapsApiDirectionsUrl(preferedMode,pointFrom,pointTo);
                                    GetDirectionTask getDirectionTask = new GetDirectionTask(false,true);
                                    getDirectionTask.execute(url);
                                    System.out.println("polyline 1 end");
                                    System.out.println("in onNotFocustvFrom");
                                    System.out.println("alternativeSuggestedPathList size: "+alternativeSuggestedPathList.size());
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Please type an valid Origin address", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else{
                    //hide previous suggested paths
                    System.out.println("debug: hide at tvFrom");
                    mLayout.setPanelHeight(0);
                    mLayout.setShadowHeight(0);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    buttonPanelViewParams.setMargins(10,0,10,15);
                    buttonPanelView.setLayoutParams(buttonPanelViewParams);
                    //clear input
                    System.out.println("numOfMarkers 1, focusing: "+numOfMarkers);
                    if(markerFrom!=null){
                        clearInput(1,true);
                    }
                }
            }
        });
        tvFrom.setAdapter(new PlaceAutoSuggestAdapter(this,android.R.layout.simple_list_item_1));
        tvFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                //TODO: set focus on next view
                AutoCompleteTextView acTo = (AutoCompleteTextView) findViewById(R.id.to);
                acTo.setFocusableInTouchMode(true);
                acTo.requestFocus();
            }
        });

        //To: OnFocusChangeListener
        tvTo = (AutoCompleteTextView) findViewById(R.id.to);
        tvTo.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            // TODO: the editText has just been left
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    System.out.println("debug: in tvTo not focus");
                    if(!byClick) {
                        System.out.println("debug: entered tvTo not byClick");
                        //clear marker To
                        if(markerTo!=null){
                            clearInput(2,false);
                        }
                        //search the address and add marker
                        System.out.println("Changed text, tvTo");
                        String strTo = tvTo.getText().toString();
                        if(strTo!="") {
                            MarkerOptions newMarkerOptions = getMarker(strTo, 2);
                            if(newMarkerOptions!=null) {
                                markerTo = mMap.addMarker(newMarkerOptions);
                                numOfMarkers+=1;
                                System.out.println("numOfMarkers 2, not focusing: "+numOfMarkers);
                                if(markerFrom!=null){
                                    System.out.println("polyline 2 start");
                                    clearSuggestedPathList();
                                    String url = getMapsApiDirectionsUrl(preferedMode,pointFrom,pointTo);
                                    GetDirectionTask getDirectionTask = new GetDirectionTask(false,true);
                                    getDirectionTask.execute(url);
                                    System.out.println("polyline 2 end");
                                    System.out.println("in onNotFocustvTo");
                                    System.out.println("alternativeSuggestedPathList size: "+alternativeSuggestedPathList.size());
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Please type an valid Destination address", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else{
                    //hide previous suggested paths
                    System.out.println("debug: hide at tvTo");
                    mLayout.setPanelHeight(0);
                    mLayout.setShadowHeight(0);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    buttonPanelViewParams.setMargins(10,0,10,15);
                    buttonPanelView.setLayoutParams(buttonPanelViewParams);
                    //clear input
                    System.out.println("numOfMarkers 2, focusing: "+numOfMarkers);
                    if(markerTo!=null){
                        clearInput(2,true);
                    }
                }
            }
        });
        tvTo.setAdapter(new PlaceAutoSuggestAdapter(this,android.R.layout.simple_list_item_1));
        tvTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                //TODO: set focus on next view
                hideKeyboard(OpenMapActivity.this);
                tvTo.clearFocus();
            }
        });

        //tvInfo = (TextView) findViewById(R.id.routeInfo);
        //tvInfo.setKeyListener(null);

        //heatmap button: OnClickListener
        heatmapPolyline = new ArrayList<ArrayList<Polyline>>();
        heatmapPolylineTag = new ArrayList<Integer>();
        range1PathIdx = new ArrayList<Integer>();
        range2PathIdx = new ArrayList<Integer>();
        range3PathIdx = new ArrayList<Integer>();
        range4PathIdx = new ArrayList<Integer>();
        range5PathIdx = new ArrayList<Integer>();
        allHeatmapPolylineShown=false;
        range1HeatmapPolylineShown = false;
        range2HeatmapPolylineShown = false;
        range3HeatmapPolylineShown = false;
        range4HeatmapPolylineShown = false;




        dpToPixel=getResources().getDisplayMetrics().density;
        itemHeight = (int)(80*dpToPixel);
        dragBar = (TextView) findViewById(R.id.drag_bar);
        dragBarHeight=dragBar.getHeight();
        shadowHeight=(int)(4*dpToPixel);

        buttonPanelViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonPanelView = (RelativeLayout) findViewById(R.id.buttonPanel);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        layoutParams = mLayout.getLayoutParams();
        //layoutParams.height =dragBar.getHeight();
        //mLayout.setLayoutParams(layoutParams);


/*
        //Ontouch
        dragView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {//当前状态
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("Action: down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("Action: move");
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("Action: up");
                        break;
                    default:
                        System.out.println("Action: other");
                        break;
                }
                return true;//还回为true,说明事件已经完成了，不会再被其他事件监听器调用
            }
        });*/

        mLayout.addPanelSlideListener(new PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                System.out.println("onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                System.out.println( "onPanelStateChanged " + newState);
                if(newState==PanelState.COLLAPSED){
                }
                if(newState==PanelState.DRAGGING){
                }
                if(newState==PanelState.ANCHORED){
                }
                if(newState==PanelState.HIDDEN){
                }
            }
        });

        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

        preferedModeIV=(ImageView) findViewById(R.id.preferedMode);
        preferedDistance=(TextView) findViewById(R.id.preferedDistance);
        preferedDuration=(TextView) findViewById(R.id.preferedDuration);
    }

    private void clearSuggestedPathList(){
        if(preferedSuggestedPath!=null){
            preferedSuggestedPath=null;
        }
        if(alternativeSuggestedPathList!=null){
            alternativeSuggestedPathList.clear();
        }
    }

    public void buttonOnClick(View view)
    {
        System.out.println("clicked view: "+view.getId());
        //refresh sqldb data
        sqldb.getLocationHistory();
        //group the same color path by storing th index
        int currentDuration;
        System.out.println("sqldb.getWalkingPathLatLngHistory().size(): "+sqldb.getWalkingPathLatLngHistory().size());
        for(int i=0; i<sqldb.getWalkingPathLatLngHistory().size(); i++) { //1:all, 2:1-5, 3:6-10, 4:11-15, 5:16-...
            currentDuration = sqldb.getWalkingPathDurationHistory().get(i);
            if (currentDuration <= 5) {
                range1PathIdx.add(i);
            } else if (currentDuration <= 10) {
                range2PathIdx.add(i);
            } else if (currentDuration <= 15) {
                range3PathIdx.add(i);
            } else {
                range4PathIdx.add(i);
            }
        }
        switch(view.getId())
        {
            case R.id.show_friends:
                System.out.println("showFriends is clicked");
                LatLng myLocation=new LatLng(22.324017, 114.168779);
                MarkerOptions markerOption = new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                mMap.addMarker(markerOption);

                ArrayList<LatLng> friendLocations = new ArrayList<LatLng>();
                friendLocations.add(new LatLng(22.320097, 114.168508));
                friendLocations.add(new LatLng(22.319640, 114.169935));
                friendLocations.add(new LatLng(22.321863, 114.167542));

                for(int i=0; i<friendLocations.size(); i++){
                    showFriendsLocation(friendLocations.get(i),null);
                    /*
                    ArrayList<LatLng> path = new ArrayList<LatLng>();
                    path.add(myLocation);
                    path.add(friendLocations.get(i));

                    PolylineOptions options = new PolylineOptions();
                    options.addAll(path);
                    options.width(10);
                    options.color(Color.GREEN);
                    polyline=mMap.addPolyline(options);*/

                    String url = getMapsApiDirectionsUrl(preferedMode,myLocation,friendLocations.get(i));
                    GetDirectionTask getDirectionTask = new GetDirectionTask(false,false);
                    getDirectionTask.execute(url);
                }
                break;

            case R.id.gps:
                if (!keepUpdateGPS) {
                    if(checkLocationEnabled()){
                        keepUpdateGPS=true;
                        startUpdateGPS();
                        /*
                        if(mLastLocation!=null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())));
                            Toast.makeText(getApplicationContext(), "Current Location: "+mLastLocation.getLatitude()+","+mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        }*/
                    }
                    else{
                        keepUpdateGPS=false;
                    }
                }
                else{
                    keepUpdateGPS=false;
                }
                break;

            case R.id.showHeatmap:
                System.out.println("showHeatmap is clicked");
                ArrayList<Integer> allPathIdx = new ArrayList<Integer>();
                if(!range1HeatmapPolylineShown){
                    allPathIdx.addAll(range1PathIdx);
                }
                if(!range2HeatmapPolylineShown){
                    allPathIdx.addAll(range2PathIdx);
                }
                if(!range3HeatmapPolylineShown){
                    allPathIdx.addAll(range3PathIdx);
                }
                if(!range4HeatmapPolylineShown){
                    allPathIdx.addAll(range4PathIdx);
                }
                System.out.println("Size of allPathIdx: "+allPathIdx.size());
                if(allHeatmapPolylineShown){
                    showHeatmap(allPathIdx, true);
                    allHeatmapPolylineShown=false;
                    range1HeatmapPolylineShown=false;
                    range2HeatmapPolylineShown=false;
                    range3HeatmapPolylineShown=false;
                    range4HeatmapPolylineShown=false;
                }
                else{
                    showHeatmap(allPathIdx, false);
                    allHeatmapPolylineShown=true;
                    range1HeatmapPolylineShown=false;
                    range2HeatmapPolylineShown=false;
                    range3HeatmapPolylineShown=false;
                    range4HeatmapPolylineShown=false;
                }
                break;

            case R.id.showHeatmap1_5:
                System.out.println("showHeatmap1_5 is clicked");
                if(range1HeatmapPolylineShown){/*
                    showHeatmap(range1PathIdx, true);
                    range1HeatmapPolylineShown=false;*/
                }
                else{
                    showHeatmap(range1PathIdx, false);
                    range1HeatmapPolylineShown=true;
                    if(range2HeatmapPolylineShown
                            &&range3HeatmapPolylineShown
                            &&range4HeatmapPolylineShown){
                        allHeatmapPolylineShown=true;
                    }
                }
                break;

            case R.id.showHeatmap6_10:
                System.out.println("showHeatmap6_10 is clicked");
                if(range2HeatmapPolylineShown){/*
                    showHeatmap(range2PathIdx, true);
                    range2HeatmapPolylineShown=false;*/
                }
                else{
                    showHeatmap(range2PathIdx, false);
                    range2HeatmapPolylineShown=true;
                    if(range1HeatmapPolylineShown
                            &&range3HeatmapPolylineShown
                            &&range4HeatmapPolylineShown){
                        allHeatmapPolylineShown=true;
                    }
                }
                break;

            case R.id.showHeatmap11_15:
                System.out.println("showHeatmap11_15 is clicked");
                if(range3HeatmapPolylineShown){/*
                    showHeatmap(range3PathIdx, true);
                    range3HeatmapPolylineShown=false;*/
                }
                else{
                    showHeatmap(range3PathIdx, false);
                    range3HeatmapPolylineShown=true;
                    if(range1HeatmapPolylineShown
                            &&range2HeatmapPolylineShown
                            &&range4HeatmapPolylineShown){
                        allHeatmapPolylineShown=true;
                    }
                }
                break;

            case R.id.showHeatmap16_:
                System.out.println("showHeatmap16_ is clicked");
                if(range4HeatmapPolylineShown){/*
                    showHeatmap(range4PathIdx, true);
                    range4HeatmapPolylineShown=false;*/
                }
                else{
                    showHeatmap(range4PathIdx, false);
                    range4HeatmapPolylineShown=true;
                    if(range1HeatmapPolylineShown
                            &&range2HeatmapPolylineShown
                            &&range3HeatmapPolylineShown){
                        allHeatmapPolylineShown=true;
                    }
                }
                break;
            default:
                System.out.println("other thing is clicked");
                break;
        }
    }

    private void showHeatmap(ArrayList<Integer> pathIdx, boolean cleanHeatmap){
        if(cleanHeatmap){
            for(int i=0; i<heatmapPolyline.size(); i++){
                for(int j=0; j<heatmapPolyline.get(i).size(); j++){
                    heatmapPolyline.get(i).get(j).remove();
                }
                heatmapPolyline.get(i).clear();
            }
            heatmapPolyline.clear();
            //heatmapPolyline = new ArrayList<ArrayList<Polyline>>();
            heatmapPolylineTag.clear();
            //heatmapPolylineTag = new ArrayList<Integer>();
            //allHeatmapPolylineShown=!allHeatmapPolylineShown;
        }
        else{
            System.out.println("road point size: "+pathIdx.size());
            int currentDuration;
            //ArrayList currentPolyline = new ArrayList<Polyline>();
            for(int i=0; i<pathIdx.size(); i++){
                currentDuration=sqldb.getWalkingPathDurationHistory().get(pathIdx.get(i));
                int currentColor;
                if(currentDuration<=5){
                    currentColor=heatmapColor1;
                }
                else if(currentDuration<=10){
                    currentColor=heatmapColor2;
                }
                else if(currentDuration<=15){
                    currentColor=heatmapColor3;
                }
                else{
                    currentColor=heatmapColor4;
                }
                PolylineOptions options = new PolylineOptions().width(10).color(currentColor).geodesic(true);
                options.addAll(sqldb.getWalkingPathLatLngHistory().get(pathIdx.get(i)));
                //currentPolyline.add(mMap.addPolyline(options));

                if((heatmapPolylineTag.isEmpty())||(!heatmapPolylineTag.contains(currentColor))){
                    //new branch
                    System.out.println("new branch");
                    heatmapPolylineTag.add(currentColor);
                    heatmapPolyline.add(new ArrayList<Polyline>());
                    heatmapPolyline.get(heatmapPolyline.size()-1).add(mMap.addPolyline(options));
                }
                else {
                    System.out.println("exsisting branch");
                    heatmapPolyline.get(heatmapPolylineTag.indexOf(currentColor)).add(mMap.addPolyline(options));
                }
            }
            if(sqldb.getWalkingPathLatLngHistory().size()!=0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sqldb.getWalkingPathLatLngHistory().get(0).get(0), DEFAULT_ZOOM));
                //allHeatmapPolylineShown=!allHeatmapPolylineShown;
            }
            else{
                Toast.makeText(getApplicationContext(), "loading data, try 1 minute later/ no history", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //getOverlappedPolyine
    private void getOverlappedPolyine(ArrayList<ArrayList<LatLng>> suggestedPaths, ArrayList<ArrayList<LatLng>> userPathHistory){
        ArrayList<LatLng> intergratedUserPathHistory = new ArrayList<LatLng>();
        ArrayList<Integer> intergratedUserPathHistoryDurationTags = new ArrayList<Integer>();
        for(int i=0; i<userPathHistory.size(); i++){
            intergratedUserPathHistory.addAll(userPathHistory.get(i));
            //insert duration data
            for(int j=0; j<userPathHistory.get(i).size(); j++){
                intergratedUserPathHistoryDurationTags.add(sqldb.getWalkingPathDurationHistory().get(i));///(userPathHistory.get(i).size()-1));
            }
        }
        System.out.println("intergratedUserPathHistory size:"+intergratedUserPathHistory.size());
        System.out.println("intergratedUserPathHistoryDurationTags size:"+intergratedUserPathHistoryDurationTags.size());
        System.out.println("intergratedUserPathHistory:"+intergratedUserPathHistory);
        System.out.println("intergratedUserPathHistoryDurationTags:"+intergratedUserPathHistoryDurationTags);

        double p1Lat;
        double p2Lat;
        double p1Lng;
        double p2Lng;
        String p1Lat5;
        String p2Lat5;
        String p1Lng5;
        String p2Lng5;
        String p1Lat4;
        String p2Lat4;
        String p1Lng4;
        String p2Lng4;
        String p1Lat3;
        String p2Lat3;
        String p1Lng3;
        String p2Lng3;
        DecimalFormat df5 = new DecimalFormat("#.#####");
        //df5.setRoundingMode(RoundingMode.DOWN);
        DecimalFormat df4 = new DecimalFormat("#.####");
        //df4.setRoundingMode(RoundingMode.DOWN);
        DecimalFormat df3 = new DecimalFormat("#.###");
        //df3.setRoundingMode(RoundingMode.DOWN);
        DecimalFormat dfs = new DecimalFormat("#");
        DecimalFormat dfs1 = new DecimalFormat("#.#");
        //dfs1.setRoundingMode(RoundingMode.DOWN);
        DecimalFormat dfs2 = new DecimalFormat("#.##");
        //dfs2.setRoundingMode(RoundingMode.DOWN);
        boolean lat5;
        boolean lng5;
        boolean lat4;
        boolean lng4;
        boolean lat3;
        boolean lng3;
        boolean pass=false;
        //determined by slope
        boolean pass2=false;
        ArrayList<LatLng> temResult = new ArrayList<LatLng>();
        ArrayList<Integer> temResultIdx = new ArrayList<Integer>();
        ArrayList<Integer> temDurationTag = new ArrayList<Integer>();
        ArrayList<ArrayList<LatLng>> suggestedPathResults = new ArrayList<ArrayList<LatLng>>();
        ArrayList<ArrayList<Integer>> suggestedPathResultsDurationTags = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> suggestedPathResultsIdxs = new ArrayList<ArrayList<Integer>>();
        int maxNumOfCommonPoint=0;
        int maxNumOfCommonPointIdx=0;
        double suggestedSlope;
        double historySlope;

        System.out.println("num of suggested path: "+suggestedPaths.size());
        for(int i=0; i<suggestedPaths.size(); i++){
            System.out.println("num of Point of current suggestedPath: "+suggestedPaths.get(i).size());
            temResult = new ArrayList<LatLng>();
            temResultIdx = new ArrayList<Integer>();
            for(int j=0; j<suggestedPaths.get(i).size(); j++){
                //System.out.println("num of Point of intergratedUserPathHistory: "+intergratedUserPathHistory.size());

                LatLng suggestedSlopePoint1;
                LatLng suggestedSlopePoint2;
                //determined by slope
                if(j==suggestedPaths.get(i).size()-1){
                    //suggestedSlope=Math.abs(suggestedPaths.get(i).get(j-1).longitude-suggestedPaths.get(i).get(j).longitude)/Math.abs(suggestedPaths.get(i).get(j-1).latitude-suggestedPaths.get(i).get(j).latitude);
                    suggestedSlopePoint1=suggestedPaths.get(i).get(j-1);
                    suggestedSlopePoint2=suggestedPaths.get(i).get(j);
                }
                else{
                    //suggestedSlope=Math.abs(suggestedPaths.get(i).get(j).longitude-suggestedPaths.get(i).get(j+1).longitude)/Math.abs(suggestedPaths.get(i).get(j).latitude-suggestedPaths.get(i).get(j+1).latitude);
                    suggestedSlopePoint1=suggestedPaths.get(i).get(j);
                    suggestedSlopePoint2=suggestedPaths.get(i).get(j+1);
                }

                for(int k=0; k<intergratedUserPathHistory.size(); k++){
                    LatLng historySlopePoint1;
                    LatLng historySlopePoint2;
                    p1Lat = suggestedPaths.get(i).get(j).latitude;
                    p2Lat = intergratedUserPathHistory.get(k).latitude;
                    p1Lng = suggestedPaths.get(i).get(j).longitude;
                    p2Lng = intergratedUserPathHistory.get(k).longitude;
                    p1Lat5 = df5.format(p1Lat);
                    p2Lat5 = df5.format(p2Lat);
                    p1Lng5 = df5.format(p1Lng);
                    p2Lng5 = df5.format(p2Lng);
                    p1Lat4 = df4.format(p1Lat);
                    p2Lat4 = df4.format(p2Lat);
                    p1Lng4 = df4.format(p1Lng);
                    p2Lng4 = df4.format(p2Lng);
                    p1Lat3 = df3.format(p1Lat);
                    p2Lat3 = df3.format(p2Lat);
                    p1Lng3 = df3.format(p1Lng);
                    p2Lng3 = df3.format(p2Lng);
                    lat5=p1Lat5.equals(p2Lat5);
                    lng5=p1Lng5.equals(p2Lng5);
                    lat4=p1Lat4.equals(p2Lat4);
                    lng4=p1Lng4.equals(p2Lng4);
                    lat3=p1Lat3.equals(p2Lat3);
                    lng3=p1Lng3.equals(p2Lng3);
                    //pass=(lat3&&lng4)||(lat4&&lng3);
                    pass=(lat3&&lng3);/*
                    System.out.println("Round i:"+i+" j:"+j+" k:"+k);
                    System.out.println("Compare Points: "+suggestedPaths.get(i).get(j).latitude+","+suggestedPaths.get(i).get(j).longitude+"|"+intergratedUserPathHistory.get(k).latitude+","+intergratedUserPathHistory.get(k).longitude);
                    System.out.println("Pass Result: "+pass);*/
                    if(pass){
                        System.out.println("Round i:"+i+" j:"+j+" k:"+k);
                        //determined by slope
                        //System.out.println("intergratedUserPathHistory.size(): "+intergratedUserPathHistory.size()+" k: "+k);
                        if (k == intergratedUserPathHistory.size() - 1) {
                            //historySlope = Math.abs(intergratedUserPathHistory.get(k - 1).longitude - intergratedUserPathHistory.get(k).longitude) / Math.abs(intergratedUserPathHistory.get(k - 1).latitude - intergratedUserPathHistory.get(k).latitude);
                            historySlopePoint1=intergratedUserPathHistory.get(k - 1);
                            historySlopePoint2=intergratedUserPathHistory.get(k);
                            System.out.println("last k");
                        } else {
                            //historySlope = Math.abs(intergratedUserPathHistory.get(k).longitude - intergratedUserPathHistory.get(k + 1).longitude) / Math.abs(intergratedUserPathHistory.get(k).latitude - intergratedUserPathHistory.get(k + 1).latitude);
                            historySlopePoint1=intergratedUserPathHistory.get(k);
                            historySlopePoint2=intergratedUserPathHistory.get(k+1);
                        }
                        suggestedSlope=(suggestedSlopePoint1.longitude-suggestedSlopePoint2.longitude)/(suggestedSlopePoint1.latitude-suggestedSlopePoint2.latitude);
                        historySlope = (historySlopePoint1.longitude - historySlopePoint2.longitude) / (historySlopePoint1.latitude - historySlopePoint2.latitude);
                        System.out.println("suggested line: "+suggestedSlopePoint1.latitude+","+suggestedSlopePoint1.longitude+"|"+suggestedSlopePoint2.latitude+","+suggestedSlopePoint2.longitude);
                        System.out.println("suggestedSlope: " + suggestedSlopePoint1.longitude + "-" +suggestedSlopePoint2.longitude+"/"+suggestedSlopePoint1.latitude+"-"+suggestedSlopePoint2.latitude+"="+suggestedSlope);
                        System.out.println("history line: "+historySlopePoint1.latitude+","+historySlopePoint1.longitude+"|"+historySlopePoint2.latitude+","+historySlopePoint2.longitude);
                        System.out.println("historySlope: " + historySlopePoint1.longitude + "-" +historySlopePoint2.longitude+"/"+historySlopePoint1.latitude+"-"+historySlopePoint2.latitude+"="+historySlope);


                        if(lat4&&lng4){
                            System.out.println("lat4&&lng4: true");
                            pass2=true;
                        }
                        else if((lat3&&lng4)||(lat4&&lng3)){
                            System.out.println("(lat3&&lng4)||(lat4&&lng3): true");
                            pass2=true;
                            //pass2=dfs2.format(suggestedSlope).equals(dfs2.format(historySlope));
                            /*
                            //check whether if it is perpendicular
                            pass2=!(dfs.format(suggestedSlope*historySlope).equals("-1"));
                            if(Double.isNaN(historySlope)&&dfs.format(suggestedSlope).equals("0")){
                                pass2=false;
                            }
                            System.out.println("Check Slope: "+pass2+", "+dfs.format(suggestedSlope*historySlope));
                            */

                            //check degree
                            if(Math.toDegrees(Math.atan(Math.abs((historySlope-suggestedSlope)/(1+suggestedSlope*historySlope))))>=45){
                                pass2=false;
                            }
                            System.out.println("degree between 2 lines: "+Math.toDegrees(Math.atan(Math.abs((historySlope-suggestedSlope)/(1+suggestedSlope*historySlope)))));

                        }
                        else if(lat3&&lng3){    //need to fix!!!!!!!!!!!!!!!!!!
                            System.out.println("lat3&&lng3: true");
                            pass2=true;
                            //check degree
                            if(Math.toDegrees(Math.atan(Math.abs((historySlope-suggestedSlope)/(1+suggestedSlope*historySlope))))>=45){
                                pass2=false;
                            }
                            System.out.println("degree between 2 lines: "+Math.toDegrees(Math.atan(Math.abs((historySlope-suggestedSlope)/(1+suggestedSlope*historySlope)))));
                            //check distance of 2 lines
                            double HS1Distance=Math.sqrt(Math.pow(historySlopePoint1.latitude-suggestedSlopePoint1.latitude,2)+Math.pow(historySlopePoint1.longitude-suggestedSlopePoint1.longitude,2));
                            double HS2Distance=Math.sqrt(Math.pow(historySlopePoint2.latitude-suggestedSlopePoint2.latitude,2)+Math.pow(historySlopePoint2.longitude-suggestedSlopePoint2.longitude,2));
                            double linesDistance=Math.max(HS1Distance,HS2Distance);
                            double HDistance=Math.sqrt(Math.pow(historySlopePoint1.latitude-historySlopePoint2.latitude,2)+Math.pow(historySlopePoint1.longitude-historySlopePoint2.longitude,2));
                            double SDistance=Math.sqrt(Math.pow(suggestedSlopePoint1.latitude-suggestedSlopePoint2.latitude,2)+Math.pow(suggestedSlopePoint1.longitude-suggestedSlopePoint2.longitude,2));
                            double lineLength=Math.min(HDistance,SDistance);
                            if(linesDistance>lineLength){
                                pass2=false;
                            }



                            /*
                            //doesn't work
                            //check degree between hSSlope & sHSlope
                            double hSSlope=(historySlopePoint1.longitude-suggestedSlopePoint2.longitude)/(historySlopePoint1.latitude-suggestedSlopePoint2.latitude);
                            double sHSlope = (suggestedSlopePoint1.longitude - historySlopePoint2.longitude) / (suggestedSlopePoint1.latitude - historySlopePoint2.latitude);
                            double degree = Math.toDegrees(Math.atan(Math.abs((hSSlope-sHSlope)/(1+sHSlope*hSSlope))));
                            System.out.println("hSSlope*sHSlope: "+degree);
                            if(degree>=20){
                                pass2=false;
                            }*/

                            //pass2=false;
                            //pass2=dfs2.format(suggestedSlope).equals(dfs2.format(historySlope));
                        }
                        else{
                            System.out.println("other: true");
                            pass2=false;
                        }
                        System.out.println("Pass2 Result: "+pass2);
                        if(pass2){
                            //System.out.println("added to temResult");
                            if(temResult.size()==0){
                                temResult.add(suggestedPaths.get(i).get(j));
                                temDurationTag.add(intergratedUserPathHistoryDurationTags.get(k));
                                temResultIdx.add(j);
                            }
                            else if(suggestedPaths.get(i).get(j)!=temResult.get(temResult.size()-1)){ //advoid temResult.get(?)==null
                                temResult.add(suggestedPaths.get(i).get(j));
                                temDurationTag.add(intergratedUserPathHistoryDurationTags.get(k));
                                temResultIdx.add(j);
                            }
                        }
                    }
                }
            }
            /*
            System.out.println("temResult size: "+temResult.size());
            System.out.println("temResultIdx size: "+temResultIdx.size());*/
            //System.out.println("suggestedPathResults size before: "+suggestedPathResults.size());
            //System.out.println("suggestedPathResultsDurationTags size before: "+suggestedPathResultsDurationTags.size());
            suggestedPathResults.add(temResult);
            suggestedPathResultsDurationTags.add(temDurationTag);
            //System.out.println("suggestedPathResults size after: "+suggestedPathResults.size());
            //System.out.println("suggestedPathResultsDurationTags size after: "+suggestedPathResultsDurationTags.size());
            suggestedPathResultsIdxs.add(temResultIdx);
            if(temResult.size()>maxNumOfCommonPoint){
                maxNumOfCommonPointIdx=i;
                maxNumOfCommonPoint=temResult.size();
            }
        }
/*
        System.out.println("---out side for loop---");
        System.out.println("maxNumOfCommonPoint: "+maxNumOfCommonPoint);
        System.out.println("maxNumOfCommonPointIdx: "+maxNumOfCommonPointIdx);
        System.out.println("suggestedPathResults size: "+suggestedPathResults.size());
        System.out.println("suggestedPathResultsIdxs size: "+suggestedPathResultsIdxs.size());
        System.out.println("suggestedPathResults.get(maxNumOfCommonPointIdx) size: "+suggestedPathResults.get(maxNumOfCommonPointIdx).size());
        System.out.println("suggestedPathResultsIdxs.get(maxNumOfCommonPointIdx) size: "+suggestedPathResultsIdxs.get(maxNumOfCommonPointIdx).size());
        */
        ArrayList<LatLng> bestPath = (ArrayList<LatLng>) suggestedPathResults.get(maxNumOfCommonPointIdx);
        ArrayList<Integer> bestPathDurationTags = (ArrayList<Integer>) suggestedPathResultsDurationTags.get(maxNumOfCommonPointIdx);
        ArrayList<Integer> bestPathIdx = (ArrayList<Integer>) suggestedPathResultsIdxs.get(maxNumOfCommonPointIdx);
        ArrayList<LatLng> overlappedPath = new ArrayList<LatLng>();
        int overlappedPathDurationTag;
        int prevousColor=0;
        int color=0;
        boolean colorChanged=false;
        System.out.println("size of bestPath: "+bestPath);
        System.out.println("size of bestPathDurationTags: "+bestPathDurationTags);
        System.out.println("size of besPathIdx: "+bestPathIdx);
        if(bestPath.size()!=0) {
            int prevIdx = bestPathIdx.get(0);
            for (int i = 0; i < maxNumOfCommonPoint; i++) {
                /*
                overlappedPathDurationTag=bestPathDurationTags.get(i);
                System.out.println("Color Level:"+overlappedPathDurationTag);
                prevousColor=color;
                color=Color.rgb(122, 0, 153);
                if(overlappedPathDurationTag<=5){
                    color=Color.rgb(184, 0, 230);
                }
                else if(overlappedPathDurationTag<=10){
                    color=Color.rgb(214, 51, 255);
                }
                else if(overlappedPathDurationTag<15){
                    color=Color.rgb(229, 128, 255);
                }
                colorChanged=!((prevousColor==0)||(prevousColor==color));
                System.out.println("(prevousColor==0):"+(prevousColor==0));
                System.out.println("(prevousColor==color):"+(prevousColor==color));
                System.out.println("if(("+(bestPathIdx.get(i) <= prevIdx + 1)+")&&("+(!colorChanged)+"))");
*/
                overlappedPathDurationTag=bestPathDurationTags.get(i);
                System.out.println("Color Level:"+overlappedPathDurationTag);
                prevousColor=color;
                if(overlappedPathDurationTag<=5){
                    color=Color.rgb(184, 0, 230);
                }
                else if(overlappedPathDurationTag<=10){
                    color=Color.rgb(214, 51, 255);
                }
                else if(overlappedPathDurationTag<=15){
                    color=Color.rgb(229, 128, 255);
                }
                else{
                    color=Color.rgb(122, 0, 153);
                }

                colorChanged=!((prevousColor==0)||(prevousColor==color));
                if ((bestPathIdx.get(i) <= prevIdx + 1)&&(!colorChanged)){//&&(!colorChanged)) {
                    System.out.println("if true add point");
                    overlappedPath.add(bestPath.get(i));
                    prevIdx = bestPathIdx.get(i);
                    prevousColor=color;
                } else {
                    System.out.println("if false draw point");
                    System.out.println("color:"+color);
                    PolylineOptions commonOptions = new PolylineOptions().width(15).color(color).geodesic(true);
                    commonOptions.addAll(overlappedPath);
                    mMap.addPolyline(commonOptions);
                    if((bestPathIdx.get(i) <= prevIdx + 1)){
                        LatLng tem=overlappedPath.get(overlappedPath.size()-1);
                        overlappedPath.clear();
                        System.out.println("if false add point");
                        overlappedPath.add(tem);
                        overlappedPath.add(bestPath.get(i));
                    }
                    else{
                        overlappedPath.clear();
                        System.out.println("if false add point");
                        overlappedPath.add(bestPath.get(i));
                    }

                    prevIdx = bestPathIdx.get(i);
                    prevousColor=color;
                }
            }
            if (overlappedPath!=null) {
                System.out.println("draw point");
                System.out.println("color:"+color);
                PolylineOptions commonOptions = new PolylineOptions().width(15).color(color).geodesic(true);
                commonOptions.addAll(overlappedPath);
                mMap.addPolyline(commonOptions);
            }

        }
    }

    //hideKeyboard
    public static void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null && v != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //showKeyboard
    private static void showKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null && v != null;
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    private void checkNetworkEnabled(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifi_enabled = wifiManager.isWifiEnabled();
        boolean wifi_connected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;

        NetworkInfo mobileInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        String reason = mobileInfo.getReason();
        boolean mobile_enabled = !(mobileInfo.getState() == NetworkInfo.State.DISCONNECTED
                && (reason == null || reason.equals("specificDisabled")));
        boolean mobile_connected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;

        if(!wifi_enabled){
            Toast.makeText(getApplicationContext(), "WiFi is disabled", Toast.LENGTH_SHORT).show();
        }
        else if(!wifi_connected){
            Toast.makeText(getApplicationContext(), "WiFi is disconnected", Toast.LENGTH_SHORT).show();
        }

        if(!mobile_enabled){
            Toast.makeText(getApplicationContext(), "Cellar data is disabled", Toast.LENGTH_SHORT).show();
        }
        else if(!mobile_connected){
            Toast.makeText(getApplicationContext(), "No cellar signal", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationEnabled () {
        LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            Toast.makeText(getApplicationContext(), "GPS is disabled", Toast.LENGTH_SHORT).show();
        }
        else{
            return true;
        }
        return false;
    }

    //checkGPSPermission
    private void checkGPSPermission(){
        //check location permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                UiSettings settings = mMap.getUiSettings();
                settings.setMyLocationButtonEnabled(false);
                System.out.println("field 1");
            }
            else {
                //request permission of location
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                System.out.println("field 2");
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            System.out.println("field 3");
        }
    }

    //buildGoogleApiClient
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    //clearInput
    private void clearInput(int mode, boolean focusing){  //mode:0=all, 1=1st, 2=2nd
        System.out.println("cleaning, fun, mode: "+mode);
        System.out.println("numOfMarkers: "+numOfMarkers);
        System.out.println("pointFrom: "+pointFrom);
        System.out.println("markerFrom: "+markerFrom);
        System.out.println("pointTo: "+pointTo);
        System.out.println("markerTo: "+markerTo);

        switch(mode){
            case 0:
                if(markerFrom!=null) {
                    markerFrom.remove();
                    markerFrom = null;
                    numOfMarkers-=1;
                }
                if(markerTo!=null) {
                    markerTo.remove();
                    markerTo = null;
                    numOfMarkers-=1;
                }

                //tvFrom.setText("");
                //tvTo.setText("");
                if (tvFrom.length() > 0) {
                    tvFrom.getText().clear();
                }
                if (tvTo.length() > 0) {
                    tvTo.getText().clear();
                }
                if(polyline!=null) {
                    polyline.remove();
                    System.out.println("removed polyline");
                }
                break;

            case 1:
                if(markerFrom!=null) {
                    markerFrom.remove();
                    markerFrom = null;
                    numOfMarkers-=1;
                }
                if(focusing) {
                    //tvFrom.setText("");
                    if (tvFrom.length() > 0) {
                        tvFrom.getText().clear();
                    }
                }
                if(polyline!=null) {
                    polyline.remove();
                    System.out.println("removed polyline");
                }
                break;

            case 2:
                if(markerTo!=null) {
                    markerTo.remove();
                    markerTo = null;
                    numOfMarkers-=1;
                }
                if(focusing) {
                    //tvTo.setText("");
                    if (tvTo.length() > 0) {
                        tvTo.getText().clear();
                    }
                }
                if(polyline!=null) {
                    polyline.remove();
                    System.out.println("removed polyline");
                }
                break;
        }
        System.out.println("after clearing");
        System.out.println("numOfMarkers: "+numOfMarkers);
        System.out.println("pointFrom: "+pointFrom);
        System.out.println("markerFrom: "+markerFrom);
        System.out.println("pointTo: "+pointTo);
        System.out.println("markerTo: "+markerTo);
    }

    //getMarker
    private MarkerOptions getMarker(String searchString, int mode){ //mode:1 from, 2 to
        Geocoder geocoder = new Geocoder(OpenMapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
            System.out.println("list, "+mode+": "+list);
        } catch (IOException e) {
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            LatLng point=new LatLng(address.getLatitude(), address.getLongitude());
            switch(mode){
                case 1:
                    pointFrom=point;
                    break;
                case 2:
                    pointTo=point;
                    break;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, DEFAULT_ZOOM));
            MarkerOptions markerOption = new MarkerOptions().position(point).title(address.getAddressLine(0));
            return markerOption;
        }
        return null;
    }

    //getMapsApiDirectionsUrl
    private String getMapsApiDirectionsUrl(String preferedMode, LatLng ptF, LatLng ptT) {
        String waypoints = "waypoints=optimize:true|"
                + (ptF).latitude + "," + (ptF).longitude
                + "|" + (ptT).latitude + ","
                + (ptT).longitude;
        String OriDest = "origin="+(ptF).latitude+","+(ptF).longitude+"&destination="+(ptT).latitude+","+(ptT).longitude;
        String sensor = "sensor=true";
        String mode = "mode="+preferedMode;
        String params = OriDest+"&%20"+waypoints + "&" + sensor + "&" + mode + "&alternatives=true";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + "key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&callback=initialize&" + params;
        System.out.println("get direction URL:"+url);
        return url;
    }

    //onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //onConnected
    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("connected");
        //GPS
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10*1000);
        mLocationRequest.setFastestInterval(5*1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void startUpdateGPS(){
        //start location updates
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void stopUpdateGPS(){
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    //getPendingIntent (Google Location Update from Background)
    private PendingIntent getPendingIntent(){
        android.content.Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //onConnectionSuspended
    @Override
    public void onConnectionSuspended(int i) {
    }

    //onConnectionFailed
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //onLocationChanged
    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        if(keepUpdateGPS) {
            //get Location name
            String currentLocation = "null";
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    currentLocation = listAddresses.get(0).getAddressLine(0);
                }
                //System.out.println("Current Location: " + location.getLatitude() + "," + location.getLongitude());
                //Toast.makeText(getApplicationContext(), "Current Location: " + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("exception");
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(currentLocation);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            //mCurrLocationMarker = mMap.addMarker(markerOptions);
            boolean pass=false;
            if(mLastLocation==null){
                pass=true;
            }
            else if((location.getLatitude()!=mLastLocation.getLatitude())||(location.getLongitude()!=mLastLocation.getLongitude())){
                pass=true;
            }
            if(pass) {
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //insert new record to DB
                //method 1
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                //sqldb.insertLocationData(latLng, timestamp.getTime());
            }
            mLastLocation = location;

            /*
            if(snappedGPSPoints==null){
                snappedGPSPoints = new ArrayList<LatLng>();
            }*/
            if(GPSPoints==null){
                GPSPoints = new ArrayList<LatLng>();
            }
            if(GPSPoints.size()<2){
                mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
                GPSPoints.add(latLng);
            }
            else{
                if(GPSPolyline==null){
                    GPSPolyline=new ArrayList<Polyline>();
                }
                GPSPoints.add(latLng);
                //draw route
                //GetSnappedGPSRouteTask getSnappedGPSRouteTask=new GetSnappedGPSRouteTask(GPSPoints);
                //getSnappedGPSRouteTask.execute();
                ArrayList<LatLng> snappedPath = new ArrayList<LatLng>();
                LatLng gpsFrom=new LatLng(GPSPoints.get(GPSPoints.size()-2).latitude,GPSPoints.get(GPSPoints.size()-2).longitude);
                LatLng gpsTo=new LatLng(GPSPoints.get(GPSPoints.size()-1).latitude,GPSPoints.get(GPSPoints.size()-1).longitude);
                if(gpsFrom!=gpsTo) {
                    String url = "https://roads.googleapis.com/v1/snapToRoads?path=" + gpsFrom.latitude + "," + gpsFrom.longitude + "|" + gpsTo.latitude + "," + gpsTo.longitude + "&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking";
                    System.out.println("get snapToRoad URL(GPS): " + url);
                    snappedPath = extractJson(GET(url), gpsFrom, gpsTo);
                    System.out.println("snappedPath: " + snappedPath);
                    if(snappedPath!=null) {
                        if (snappedPath.get(0) != gpsFrom) {
                            snappedPath.add(0, gpsFrom);
                        }
                        if (snappedPath.get(snappedPath.size() - 1) != gpsTo) {
                            snappedPath.add(gpsTo);
                        }
                        drawSnappedGPSRoute(snappedPath);
                    }
                }
                /*
                PolylineOptions options = new PolylineOptions();
                options.addAll(GPSPoints);
                options.width(10);
                options.color(Color.BLUE);
                GPSPolyline=mMap.addPolyline(options);
                System.out.println("GPS polyline plotted");*/
            }
        }
        else{
            stopUpdateGPS();
            if(GPSPolyline!=null) {
                for(int i=0; i<GPSPolyline.size(); i++){
                    GPSPolyline.get(i).remove();
                }
                GPSPolyline=null;
            }
            GPSPoints.clear();
        }
    }


    //-------------------------------AsyncTask Class & Function--------------------------------------------
//class: GetDirectionTask
    private class GetDirectionTask extends AsyncTask<String, Void, String> {
        private boolean getOvelappedPath;
        private boolean showRouteInfo;

        GetDirectionTask(boolean getOvelappedPath, boolean showRouteInfo){
            this.getOvelappedPath=getOvelappedPath;
            this.showRouteInfo=showRouteInfo;
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                System.out.println("url in GetDirectionTask: "+url[0]);
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
            new ParserTask(getOvelappedPath, showRouteInfo).execute(result);
        }
    }

    //class: ParserTask
    //private class ParserTask extends AsyncTask<String, Integer, ArrayList<ExtractedJSON>> {
    //version: snap route
    private class ParserTask extends AsyncTask<String, Integer, ArrayList<ExtractedJSON>> {
        private boolean getOvelappedPath;
        private boolean showRouteInfo;

        ParserTask(boolean getOvelappedPath, boolean showRouteInfo){
            this.getOvelappedPath=getOvelappedPath;
            this.showRouteInfo=showRouteInfo;
        }

        @Override
        //protected ArrayList<ExtractedJSON> doInBackground(String... jsonData) { //jsonData[0] == result == data
        //version: snap route
        protected ArrayList<ExtractedJSON> doInBackground(String... jsonData) {

            System.out.println("jsonData[0]: "+jsonData[0]);

            JSONObject jObject;
            //ArrayList<ExtractedJSON> routes = null;
            //version: snap route
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
        //protected void onPostExecute(ArrayList<ExtractedJSON> routes) {
        //version: snap route
        protected void onPostExecute(ArrayList<ExtractedJSON> routes) {
            //drawDirection(routes);//by direction
            getPaths(routes,getOvelappedPath,showRouteInfo);//by snapping route
        }
    }

    //getPaths
    private void getPaths(ArrayList<ExtractedJSON> routes,boolean getOvelappedPath,boolean showRouteInfo){
        ArrayList<LatLng> pathsPoint = null;
        //ArrayList<ArrayList<LatLng>> allRoutes = new ArrayList<ArrayList<LatLng>>();
        System.out.println("routes: "+routes);
        //[routes] idx1:point 1 -> idx2:point 2 -> idx3:point2 -> idx4:point3
        new GetSnappedRouteTask(routes,getOvelappedPath,showRouteInfo).execute();
    }
/*
//drawDirection
    private void drawDirection(List<ExtractedJSON> routes){
        ArrayList<LatLng> points = null;
        polyLineOptions = null;
        ArrayList<ArrayList<LatLng>> allRoutes = new ArrayList<ArrayList<LatLng>>();

        System.out.println("routes: "+routes);

        int currentRouteDistance = ((int) routes.get(0).getDistance());
        int currentRouteDuration = ((int) routes.get(0).getDuration());
        tvInfo.setText("Distance: "+(currentRouteDistance/1000)+" km"+"\nDuration: "+((int) currentRouteDuration/3600)+" hours"+((int) (currentRouteDuration%3600)/60)+" mins"+(currentRouteDuration%60)+" sec");


        ExtractedJSON currentRoute = routes.get(0);
        if(currentRoute.getLegs().isEmpty()){
            //no routes
            System.out.println("NO ROUTES!!");
        }
        else {
            // traversing through routes
            System.out.println("num of Routes/Legs: "+currentRoute.getLegs().size());
            for (int i = 0; i < currentRoute.getLegs().size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = currentRoute.getLegs().get(i);
                //System.out.println("path.size(): "+path.size());
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    //System.out.println("path.get("+j+"): "+path.get(j));

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                allRoutes.add(points);
                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(Color.BLUE);

                polyline = mMap.addPolyline(polyLineOptions);
                System.out.println("added polyline");
                //solving AsyncTask problem in typing focus: delete marker -> reomve polyline -> add back marker -> add back polyline
                if(numOfMarkers<2)
                    polyline.remove();
            }
            //getOverlappedPolyine(allRoutes, sqldb.getWalkingPathLatLngHistory());
        }
    }*/

    //GetSnappedRouteTask
    private class GetSnappedRouteTask extends AsyncTask<Void, Void, Void> {
        private ArrayList<ExtractedJSON> routes;
        private ArrayList<ArrayList<LatLng>> paths;
        private int duration;
        private ArrayList<Integer> durationList;
        private int distance;
        private ArrayList<Integer> distanceList;
        private ArrayList<LatLng> intergratedPath;
        private ArrayList<ArrayList<LatLng>> intergratedPathList;
        private ArrayList<ArrayList<LatLng>> allRoutes;
        private boolean getOvelappedPath;
        private boolean showRouteInfo;

        public GetSnappedRouteTask(ArrayList<ExtractedJSON> routes, boolean getOvelappedPath,boolean showRouteInfo) {
            this.routes=routes;
            paths=new ArrayList<ArrayList<LatLng>>();
            intergratedPath = new ArrayList<LatLng>();
            intergratedPathList = new ArrayList<ArrayList<LatLng>>();
            allRoutes = new ArrayList<ArrayList<LatLng>>();
            distanceList=new ArrayList<Integer>();
            durationList=new ArrayList<Integer>();
            this.getOvelappedPath=getOvelappedPath;
            this.showRouteInfo=showRouteInfo;
        }

        @Override
        protected Void doInBackground(Void... param) {
            String url="";
            ExtractedJSON currentRoute=null;
            if(routes!=null){
                currentRoute = routes.get(0);
            }
            System.out.println("Start: "+currentRoute.getSteps().size());
            if(currentRoute!=null) {
                for (int i = 0; i < currentRoute.getSteps().size(); ) {
                    url = "https://roads.googleapis.com/v1/snapToRoads?path=" + currentRoute.getSteps().get(i).latitude + "," + currentRoute.getSteps().get(i).longitude + "|" + currentRoute.getSteps().get(i + 1).latitude + "," + currentRoute.getSteps().get(i + 1).longitude + "&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking";
                    System.out.println("get snapToRoad URL: " + url);
                    paths.add(extractJson(GET(url), new LatLng(currentRoute.getSteps().get(i).latitude, currentRoute.getSteps().get(i).longitude), new LatLng(currentRoute.getSteps().get(i + 1).latitude, currentRoute.getSteps().get(i + 1).longitude)));
                    i += 2;
                }
            }

            if(paths!=null) {
                for (int i = 0; i < paths.size(); i++) {
                    intergratedPath.addAll(paths.get(i));
                }
            }
            //System.out.println("intergratedPath: "+intergratedPath);
            distance = ((int) currentRoute.getDuration());
            duration = ((int) currentRoute.getDistance());
            allRoutes.add(intergratedPath);
            distanceList.add(distance);
            durationList.add(duration);




/*
            //clear duplicate points (clear by slope)
            System.out.println("Paths.size(): "+paths.size());
            for(int i=0; i<paths.size()-1; i++){
                for(int j=0; j<paths.get(i).size()-2; j++){
                    if ((paths.get(i).get(j).longitude - paths.get(i).get(j + 1).longitude) / (paths.get(i).get(j).latitude - paths.get(i).get(j + 1).latitude) * (paths.get(i).get(j + 1).longitude - paths.get(i).get(j + 2).longitude) / (paths.get(i).get(j + 1).latitude - paths.get(i).get(j + 2).latitude) < -0.9) {
                        paths.get(i).subList(0, j + 1).clear();
                        break;
                    }
                }
                for(int j=0; j<paths.get(i).size()-2; j++){
                    if ((paths.get(i).get(j).longitude - paths.get(i).get(j + 1).longitude) / (paths.get(i).get(j).latitude - paths.get(i).get(j + 1).latitude) * (paths.get(i).get(j + 1).longitude - paths.get(i).get(j + 2).longitude) / (paths.get(i).get(j + 1).latitude - paths.get(i).get(j + 2).latitude) < -0.9) {
                        paths.get(i).subList(j + 2,paths.get(i).size()).clear();
                        break;
                    }
                }
            }*/
/*
            //clear duplicate points (clear 1st-n points)
            for(int i=0; i<paths.size()-1; i++){
                if(i==0){
                    paths.get(i).subList(0,1).clear();
                    paths.get(i).subList(paths.get(i).size()-1-1,paths.get(i).size()).clear();
                    System.out.println("cleared");
                }
                else if(i==paths.size()-1){
                    //paths.get(i).subList(0,1).clear();
                    paths.get(i).subList(paths.get(i).size()-1-1,paths.get(i).size()).clear();
                    System.out.println("cleared");
                }
                else{
                    paths.get(i).subList(0,1).clear();
                    //paths.get(i).subList(paths.get(i).size()-1-1,paths.get(i).size()).clear();
                    System.out.println("cleared");
                }
            }*/

            /*
            //clear duplicate points (conditional clearing)
            for(int i=0; i<paths.size()-1; i++){
                for(int j=0; j<paths.get(i).size(); j++){
                    for(int k=0; k<paths.get(i+1).size(); k++){
                        DecimalFormat df = new DecimalFormat("#.####");
                        df.setRoundingMode(RoundingMode.DOWN);
                        if((df.format(paths.get(i).get(j).latitude).equals(df.format(paths.get(i+1).get(k).latitude)))&&
                        (df.format(paths.get(i).get(j).longitude).equals(df.format(paths.get(i+1).get(k).longitude)))){
                            System.out.println("cleared after"+j+"/"+paths.get(i).size()+" & before "+k+"/"+paths.get(i+1).size());
                            paths.get(i).subList(j+1,paths.get(i).size()).clear();
                            paths.get(i+1).subList(0,k).clear();
                            break;
                        }
                    }
                }
            }*/

            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void param) {
            drawSnappedRoute(showRouteInfo);
            if(getOvelappedPath) {
                getOverlappedPolyine(allRoutes, sqldb.getWalkingPathLatLngHistory());
            }
        }

        private void drawSnappedRoute(boolean showRouteInfo){
            //test without merge paths, may need to uncomment
            /*
            //merge paths
            ArrayList<LatLng> intergratedPath = new ArrayList<LatLng>();
            for(int i=0; i<paths.size(); i++){
                intergratedPath.addAll(paths.get(i));
            }*/

            //draw route
            PolylineOptions options = new PolylineOptions();
            options.addAll(intergratedPath);
            options.width(10);
            options.color(Color.RED);
            polyline=mMap.addPolyline(options);
            System.out.println("GetSnappedRouteTask added polyline");

            //Toast.makeText(getApplicationContext(), "showRouteInfo: "+showRouteInfo, Toast.LENGTH_SHORT).show();
            if(showRouteInfo) {
                //Add suggested paths info
                //numOfSuggestedPaths=0;
                listView = (ListView) findViewById(R.id.suggestedPaths);
                SuggestedPath currentSuggestedPath;
                for (int i = 0; i < allRoutes.size(); i++) {
                    currentSuggestedPath = new SuggestedPath();
                    currentSuggestedPath.setMode(preferedMode);
                    currentSuggestedPath.setModeImageDrawable(R.drawable.walk);
                    for (int j = 0; j < allRoutes.get(i).size(); j++) {
                        currentSuggestedPath.addPathPoint(allRoutes.get(i).get(j));
                    }
                    //System.out.println("distance: "+distance+" duration: "+duration);
                    currentSuggestedPath.setDistance(distanceList.get(i));
                    currentSuggestedPath.setDuration(durationList.get(i));

                    if (i == 0) {
                        preferedSuggestedPath = currentSuggestedPath;
                        //for test, need to delete
                        //alternativeSuggestedPathList.add(currentSuggestedPath);
                        //for test, need to delete
                    } else {
                        alternativeSuggestedPathList.add(currentSuggestedPath);
                    }
                    //numOfSuggestedPaths++;
                }


                if (preferedSuggestedPath != null) {
                    preferedModeIV.setImageResource(preferedSuggestedPath.getModeImageDrawable());
                    double km = ((double) (preferedSuggestedPath.getDistance())) / 1000;
                    String kmPart = km + " km";
                    String distanceInfo = kmPart;
                    preferedDistance.setText(distanceInfo);
                    int hours = ((int) preferedSuggestedPath.getDuration() / 3600);
                    int mins = ((int) (preferedSuggestedPath.getDuration() % 3600) / 60);
                    String hoursPart;
                    String minsPart;
                    if (hours == 0) {
                        hoursPart = "";
                    } else {
                        hoursPart = hours + " hours";
                    }
                    if (mins == 0) {
                        minsPart = "";
                    } else {
                        minsPart = mins + " mins";
                    }
                    String durationInfo = "";
                    durationInfo += hoursPart;
                    if (minsPart != "") {
                        durationInfo += "\n";
                    }
                    durationInfo += minsPart;
                    preferedDuration.setText(durationInfo);
                }

                System.out.println("in drawSnappedRoute");
                System.out.println("alternativeSuggestedPathList size: " + alternativeSuggestedPathList.size());

                if (alternativeSuggestedPathList != null) {
                    suggestedPathAdapter = new SuggestedPathAdapter(getApplicationContext(), alternativeSuggestedPathList);
                    listView.setAdapter(suggestedPathAdapter);
                }


                System.out.println("debug: show at draw");
                //hideKeyboard(OpenMapActivity.this);
                if (mLayout.getPanelState() == PanelState.HIDDEN) {
                    mLayout.setPanelState(PanelState.COLLAPSED);
                }
                dragBarHeight=dragBar.getHeight();  //to fix the problem: dragBarHeight accidentally become zero
                mLayout.setPanelHeight(itemHeight + dragBarHeight);
                mLayout.setShadowHeight(shadowHeight);
                mLayout.setPanelState(PanelState.COLLAPSED);
                buttonPanelViewParams.setMargins(10,0,10,15+itemHeight + dragBarHeight);
                buttonPanelView.setLayoutParams(buttonPanelViewParams);

                //solving AsyncTask problem in typing focus: delete marker -> reomve polyline -> add back marker -> add back polyline
                System.out.println("numOfMarkers: " + numOfMarkers);
                if (numOfMarkers < 2) {
                    polyline.remove();
                    //hide previous suggested paths
                    System.out.println("debug: hide at draw");
                    mLayout.setPanelHeight(0);
                    mLayout.setShadowHeight(0);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    buttonPanelViewParams.setMargins(10,0,10,15);
                    buttonPanelView.setLayoutParams(buttonPanelViewParams);
                }
            }
        }
/*
        private ArrayList<LatLng> extractJson(String result, LatLng originStart, LatLng originEnd){
            try {
                JSONObject json = new JSONObject(result);
                String str = "";
                JSONArray pointList = json.getJSONArray("snappedPoints");
                ArrayList<LatLng> points = new ArrayList<LatLng>();
                System.out.println("GetSnappedRouteTask PointList size: "+pointList.length());
                //points.add(originStart);    //to correct, because start point get deviated
                for(int i=0; i<pointList.length(); i++){
                    JSONObject location = pointList.getJSONObject(i).getJSONObject("location");
                    LatLng currentPoint=new LatLng(location.getDouble("latitude"),location.getDouble("longitude"));
                    if(points.size()!=0) {
                        if (!(currentPoint.equals(points.get(points.size() - 1)))) {
                            points.add(currentPoint);
                        }
                    }
                    else{
                        points.add(currentPoint);
                    }
                    //System.out.println("Points "+points.size()+": "+points.get(points.size()-1).latitude+","+points.get(points.size()-1).longitude);
                }
                //points.add(originEnd);  //to correct, because destination point get deviated
                System.out.println("GetSnappedRouteTask "+points.get(0)+"|"+points.get(points.size()-1));
                System.out.println("GetSnappedRouteTask Data got!!");

                //ArrayList<ArrayList<LatLng>> allRoutes = new ArrayList<ArrayList<LatLng>>();
                //allRoutes.add(points);
                //getOverlappedPolyine(allRoutes, sqldb.getWalkingPathLatLngHistory());

                return points;

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
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

        private  String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }*/
    }





    private void drawSnappedGPSRoute(ArrayList<LatLng> path){
        //draw route
        PolylineOptions options = new PolylineOptions();
        options.addAll(path);
        options.width(10);
        options.color(Color.BLUE);
        GPSPolyline.add(mMap.addPolyline(options));
        System.out.println("GPS polyline added");
    }

    private ArrayList<LatLng> extractJson(String result, LatLng originStart, LatLng originEnd){
        try {
            JSONObject json = new JSONObject(result);
            String str = "";
            JSONArray pointList = json.getJSONArray("snappedPoints");
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            System.out.println("GetSnappedRouteTask PointList size: "+pointList.length());
            //points.add(originStart);    //to correct, because start point get deviated
            for(int i=0; i<pointList.length(); i++){
                JSONObject location = pointList.getJSONObject(i).getJSONObject("location");
                LatLng currentPoint=new LatLng(location.getDouble("latitude"),location.getDouble("longitude"));
                if(points.size()!=0) {
                    if (!(currentPoint.equals(points.get(points.size() - 1)))) {
                        points.add(currentPoint);
                    }
                }
                else{
                    points.add(currentPoint);
                }
                //System.out.println("Points "+points.size()+": "+points.get(points.size()-1).latitude+","+points.get(points.size()-1).longitude);
            }
            //points.add(originEnd);  //to correct, because destination point get deviated
            System.out.println("GetSnappedRouteTask "+points.get(0)+"|"+points.get(points.size()-1));
            System.out.println("GetSnappedRouteTask Data got!!");
/*
                ArrayList<ArrayList<LatLng>> allRoutes = new ArrayList<ArrayList<LatLng>>();
                allRoutes.add(points);
                getOverlappedPolyine(allRoutes, sqldb.getWalkingPathLatLngHistory());*/

            return points;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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

    private  String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private void showFriendsLocation(LatLng point, Bitmap icon){
        //get Location name
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
        //add marker
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        //markerImage.setImageResource(R.drawable.walk);
        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        //txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(_Location).icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        mMap.addMarker(markerOption);
    }
}
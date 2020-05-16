package com.ust.smartph;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.ust.map.ExtractedJSON;
import com.ust.map.HttpConnection;
import com.ust.map.MyLocationService;
import com.ust.map.PathJSONParser;
import com.ust.map.PlaceAutoSuggestAdapter;
import com.ust.map.SQLDB;
import com.ust.map.SuggestedPath;
import com.ust.map.SuggestedPathAdapter;

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
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import de.hdodenhof.circleimageview.CircleImageView;


public class OpenMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static Context mContext;
    private static GoogleMap mMap;
    private Marker markerFrom;
    private Marker markerTo;
    private static int numOfMarkers;
    private LatLng pointFrom;
    private LatLng pointTo;
    private static Polyline polyline;
    private ArrayList<ArrayList<Polyline>> heatmapPolyline;
    private ArrayList<Integer> heatmapPolylineTag;
    private static final int heatmapColor1=Color.rgb(51, 204, 255);
    private static final int heatmapColor2=Color.rgb(255, 195, 77);
    private static final int heatmapColor3=Color.GREEN;
    private static final int heatmapColor4=Color.RED;
    private static final float DEFAULT_ZOOM = 15f;
    private AutoCompleteTextView tvFrom;
    private AutoCompleteTextView tvTo;
    private boolean byClick;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private SQLDB sqldb;
    private ArrayList<Integer> range1PathIdx;
    private ArrayList<Integer> range2PathIdx;
    private ArrayList<Integer> range3PathIdx;
    private ArrayList<Integer> range4PathIdx;
    private static final int TIME_RANGE1=5*60*1000;
    private static final int TIME_RANGE2=60*60*1000;
    private static final int TIME_RANGE3=3*60*60*1000;
    private boolean allHeatmapPolylineShown;
    private boolean range1HeatmapPolylineShown;
    private boolean range2HeatmapPolylineShown;
    private boolean range3HeatmapPolylineShown;
    private boolean range4HeatmapPolylineShown;
    private ArrayList<Polyline> GPSPolyline;
    private static SlidingUpPanelLayout mLayout;
    private static ListView listView;
    private static SuggestedPathAdapter suggestedPathAdapter;
    private static ArrayList<SuggestedPath> alternativeSuggestedPathList;
    private static SuggestedPath preferedSuggestedPath;
    private static TextView dragBar;
    private float dpToPixel;
    private static int itemHeight;
    private static int dragBarHeight;
    private static int shadowHeight;
    private static ImageView preferedModeIV;
    private static TextView preferedDistance;
    private static TextView preferedDuration;
    private static String preferedMode;
    private static LinearLayout.LayoutParams buttonPanelViewParams;
    private static RelativeLayout buttonPanelView;
    private static int buttonPanelLeftMargin;
    private static int buttonPanelRightMargin;
    private static int buttonPanelTopMargin;
    private static int buttonPanelBottomMargin;
    private FloatingActionButton gpsButton;
    private FloatingActionButton fdsButton;
    private com.github.clans.fab.FloatingActionMenu currentPreferedMode;
    private com.github.clans.fab.FloatingActionButton alternativePreferedMode;
    private static boolean showFriend;
    private boolean onGPS;
    private static ArrayList<Polyline> frinedsPolylines;
    private static ArrayList<Marker> friendsMarkers;
    private ArrayList<LatLng> friendLocations;
    private int updateRequest;




    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_map);
        mContext = getApplicationContext();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        checkNetworkEnabled();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String emailStr = getIntent().getExtras().getString("emailStr");
        sqldb = new SQLDB(getApplicationContext(), emailStr);
    }

//onMapReday
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.3352301,114.2662848), DEFAULT_ZOOM));

        updateRequest=0;
        showFriend=false;
        onGPS=false;
        gpsButton = findViewById(R.id.gps);
        fdsButton = findViewById(R.id.show_friends);
        currentPreferedMode = findViewById(R.id.current_prefered_mode);
        currentPreferedMode.setIconAnimated(false);
        alternativePreferedMode = findViewById(R.id.alternative_prefered_mode);
        listView = findViewById(R.id.suggestedPaths);
        alternativeSuggestedPathList= new ArrayList<>();
        frinedsPolylines = new ArrayList<>();
        friendsMarkers = new ArrayList<>();
        friendLocations = new ArrayList<>();


        checkGPSPermission(); //need to uncomment
        preferedMode="walking"; //either "walking" or "driving"

        //OnMapClickListener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //hid the keyboard to prevent layout error
                hideKeyboard(OpenMapActivity.this);

                //get Location name
                String location="null";
                double longitude = point.longitude;
                double latitude = point.latitude;
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if(null!=listAddresses&&listAddresses.size()>0){
                        location = listAddresses.get(0).getAddressLine(0);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("exception: "+e);
                }
                //add marker
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(location);
                switch(numOfMarkers){
                    case 1:
                        if(markerTo!=null) {
                            markerFrom = mMap.addMarker(markerOption);
                            numOfMarkers+=1;
                            pointFrom=point;
                            byClick=true;
                            tvFrom.setText(location);
                            tvFrom.clearFocus();
                            byClick=false;
                        }
                        else if(markerFrom!=null){
                            markerTo = mMap.addMarker(markerOption);
                            numOfMarkers+=1;
                            pointTo=point;
                            byClick=true;
                            tvTo.setText(location);
                            tvTo.clearFocus();
                            byClick=false;
                        }

                        //Direction
                        clearSuggestedPathList();
                        String url = getMapsApiDirectionsUrl(preferedMode,pointFrom,pointTo);
                        if(url!=null) {
                            GetDirectionTask getDirectionTask = new GetDirectionTask("search");
                            getDirectionTask.execute(url);
                        }
                        break;
                    case 2:
                        clearInput(0,false);
                    case 0:
                        //hide previous suggested paths
                        mLayout.setPanelHeight(0);
                        mLayout.setShadowHeight(0);
                        mLayout.setPanelState(PanelState.COLLAPSED);
                        buttonPanelViewParams.setMargins(buttonPanelLeftMargin,buttonPanelTopMargin,buttonPanelRightMargin,buttonPanelBottomMargin);
                        buttonPanelView.setLayoutParams(buttonPanelViewParams);

                        markerFrom=mMap.addMarker(markerOption);
                        numOfMarkers+=1;
                        pointFrom=point;
                        byClick=true;
                        tvFrom.setText(location);
                        tvFrom.clearFocus();
                        byClick=false;
                        break;
                }
            }
        });

        //From: OnFocusChangeListener
        tvFrom = findViewById(R.id.from);
        tvFrom.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    if(!byClick) {
                        //clear marker From
                        if(markerFrom!=null){
                            clearInput(1,false);
                        }
                        //search the address and add marker
                        String strFrom = tvFrom.getText().toString();
                        if(!strFrom.equals("")) {
                            MarkerOptions newMarkerOptions = getMarker(strFrom, 1);
                            if (newMarkerOptions != null) {
                                markerFrom = mMap.addMarker(newMarkerOptions);
                                numOfMarkers += 1;
                                if (markerTo != null) {
                                    clearSuggestedPathList();
                                    String url = getMapsApiDirectionsUrl(preferedMode,pointFrom,pointTo);
                                    if(url!=null) {
                                        GetDirectionTask getDirectionTask = new GetDirectionTask("search");
                                        getDirectionTask.execute(url);
                                    }
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
                    mLayout.setPanelHeight(0);
                    mLayout.setShadowHeight(0);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    buttonPanelViewParams.setMargins(buttonPanelLeftMargin,buttonPanelTopMargin,buttonPanelRightMargin,buttonPanelBottomMargin);
                    buttonPanelView.setLayoutParams(buttonPanelViewParams);
                    //clear input
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
                AutoCompleteTextView acTo = findViewById(R.id.to);
                acTo.setFocusableInTouchMode(true);
                acTo.requestFocus();
            }
        });

        //To: OnFocusChangeListener
        tvTo = findViewById(R.id.to);
        tvTo.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    if(!byClick) {
                        //clear marker To
                        if(markerTo!=null){
                            clearInput(2,false);
                        }
                        //search the address and add marker
                        String strTo = tvTo.getText().toString();
                        if(!strTo.equals("")) {
                            MarkerOptions newMarkerOptions = getMarker(strTo, 2);
                            if(newMarkerOptions!=null) {
                                markerTo = mMap.addMarker(newMarkerOptions);
                                numOfMarkers+=1;
                                if(markerFrom!=null){
                                    clearSuggestedPathList();
                                    String url = getMapsApiDirectionsUrl(preferedMode,pointFrom,pointTo);
                                    if(url!=null) {
                                        GetDirectionTask getDirectionTask = new GetDirectionTask("search");
                                        getDirectionTask.execute(url);
                                    }
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
                    mLayout.setPanelHeight(0);
                    mLayout.setShadowHeight(0);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    buttonPanelViewParams.setMargins(buttonPanelLeftMargin,buttonPanelTopMargin,buttonPanelRightMargin,buttonPanelBottomMargin);
                    buttonPanelView.setLayoutParams(buttonPanelViewParams);
                    //clear input
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
                hideKeyboard(OpenMapActivity.this);
                tvTo.clearFocus();
            }
        });

        //heatmap button: OnClickListener
        heatmapPolyline = new ArrayList<>();
        heatmapPolylineTag = new ArrayList<>();
        range1PathIdx = new ArrayList<>();
        range2PathIdx = new ArrayList<>();
        range3PathIdx = new ArrayList<>();
        range4PathIdx = new ArrayList<>();
        allHeatmapPolylineShown=false;
        range1HeatmapPolylineShown = false;
        range2HeatmapPolylineShown = false;
        range3HeatmapPolylineShown = false;
        range4HeatmapPolylineShown = false;




        dpToPixel=getResources().getDisplayMetrics().density;
        itemHeight = (int)(80*dpToPixel);
        dragBar = findViewById(R.id.drag_bar);
        dragBarHeight=dragBar.getHeight();
        shadowHeight=(int)(4*dpToPixel);

        buttonPanelViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonPanelView = findViewById(R.id.buttonPanel);
        LinearLayout.LayoutParams temLinearLayoutParams = (LinearLayout.LayoutParams) buttonPanelView.getLayoutParams();
        buttonPanelLeftMargin=temLinearLayoutParams.leftMargin;
        buttonPanelRightMargin=temLinearLayoutParams.rightMargin;
        buttonPanelTopMargin=temLinearLayoutParams.topMargin;
        buttonPanelBottomMargin=temLinearLayoutParams.bottomMargin;

        mLayout = findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                /*
                if(newState==PanelState.COLLAPSED){
                }
                if(newState==PanelState.DRAGGING){
                }
                if(newState==PanelState.ANCHORED){
                }
                if(newState==PanelState.HIDDEN){
                }
                */
            }
        });

        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

        preferedModeIV=findViewById(R.id.preferedMode);
        preferedDistance=findViewById(R.id.preferedDistance);
        preferedDuration=findViewById(R.id.preferedDuration);
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
        //refresh sqldb data
        sqldb.getLocationHistory();
        //group the same color path by storing th index
        int currentDuration;
        for(int i=0; i<sqldb.getWalkingPathLatLngHistory().size(); i++) {
            //for (int j=0; j<sqldb.getWalkingPathLatLngHistory().get(i).size(); j++) {
                currentDuration = sqldb.getWalkingPathDurationHistory().get(i);
                //duration ragnes for test: 5, 10, 15, >15
                if (currentDuration <= TIME_RANGE1) {
                    range1PathIdx.add(i);
                } else if (currentDuration <= TIME_RANGE2) {
                    range2PathIdx.add(i);
                } else if (currentDuration <= TIME_RANGE3) {
                    range3PathIdx.add(i);
                } else {
                    range4PathIdx.add(i);
                }
            //}
        }
        switch(view.getId())
        {
            case R.id.alternative_prefered_mode:
                if(preferedMode.equals("walking")){
                    preferedMode="driving";
                    currentPreferedMode.getMenuIconView().setImageResource(R.drawable.drive_button);
                    alternativePreferedMode.setImageResource(R.drawable.walk_button);
                }
                else if(preferedMode.equals("driving")){
                    preferedMode="walking";
                    currentPreferedMode.getMenuIconView().setImageResource(R.drawable.walk_button);
                    alternativePreferedMode.setImageResource(R.drawable.drive_button);
                }
                //refresh route
                if(polyline!=null) {
                    polyline.remove();
                    String url = getMapsApiDirectionsUrl(preferedMode,pointFrom,pointTo);
                    if(url!=null) {
                        GetDirectionTask getDirectionTask = new GetDirectionTask("search");
                        getDirectionTask.execute(url);
                    }
                }
                break;

            case R.id.show_friends:
                if(checkLocationEnabled()||showFriend) {
                    showFriend = !showFriend;
                    if (showFriend) {
                        //change button image
                        fdsButton.setImageResource(R.drawable.fds_on);
                        //instant show
                        if (mLastLocation != null) {
                            getFriendsLocation();
                            updateRequest+=1;
                        }
                        //onGPSUpdate show
                        //if GPS is not turned on
                        if (!onGPS) {
                            FloatingActionButton gpsButton = findViewById(R.id.gps);
                            gpsButton.performClick();
                        }
                    } else {
                        hideFriendsLocation();
                        //change button image
                        fdsButton.setImageResource(R.drawable.fds_off);
                    }
                }
                break;

            case R.id.gps:
                if(checkLocationEnabled()||onGPS) {
                    onGPS = !onGPS;
                    if (onGPS) {
                        //change button image
                        gpsButton.setImageResource(R.drawable.gps_enabled);
                        //turn on GPS
                        //turnOnGPS();
                        startUpdateGPS();
                    } else {
                        //off show friend if it is on
                        if (showFriend) {
                            FloatingActionButton fdsButton = findViewById(R.id.show_friends);
                            fdsButton.performClick();
                        }
                        //turn off GPS
                        stopUpdateGPS();
                        //change button image
                        gpsButton.setImageResource(R.drawable.gps_disabled);
                    }
                }
                break;

            case R.id.showHeatmap:
                ArrayList<Integer> allPathIdx = new ArrayList<>();
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
                if(allHeatmapPolylineShown){
                    cleanHeatmap();
                    allHeatmapPolylineShown=false;
                    range1HeatmapPolylineShown=false;
                    range2HeatmapPolylineShown=false;
                    range3HeatmapPolylineShown=false;
                    range4HeatmapPolylineShown=false;
                }
                else{
                    if((allPathIdx!=null)&&(allPathIdx.size()>1)) {
                        showHeatmap(allPathIdx);
                        allHeatmapPolylineShown = true;
                        range1HeatmapPolylineShown = false;
                        range2HeatmapPolylineShown = false;
                        range3HeatmapPolylineShown = false;
                        range4HeatmapPolylineShown = false;
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "loading data, try 1 minute later/ no history", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.showHeatmap1_5:
                if(range1HeatmapPolylineShown){
                }
                else{
                    if((range1PathIdx!=null)&&(range1PathIdx.size()>1)) {
                        showHeatmap(range1PathIdx);
                        range1HeatmapPolylineShown = true;
                        if (range2HeatmapPolylineShown
                                && range3HeatmapPolylineShown
                                && range4HeatmapPolylineShown) {
                            allHeatmapPolylineShown = true;
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "loading data, try 1 minute later/ no history", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.showHeatmap6_10:
                if(range2HeatmapPolylineShown){
                }
                else{
                    if((range2PathIdx!=null)&&(range2PathIdx.size()>1)) {
                        showHeatmap(range2PathIdx);
                        range2HeatmapPolylineShown = true;
                        if (range1HeatmapPolylineShown
                                && range3HeatmapPolylineShown
                                && range4HeatmapPolylineShown) {
                            allHeatmapPolylineShown = true;
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "loading data, try 1 minute later/ no history", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.showHeatmap11_15:
                if(range3HeatmapPolylineShown){
                }
                else{
                    if((range3PathIdx!=null)&&(range3PathIdx.size()>1)) {
                        showHeatmap(range3PathIdx);
                        range3HeatmapPolylineShown = true;
                        if (range1HeatmapPolylineShown
                                && range2HeatmapPolylineShown
                                && range4HeatmapPolylineShown) {
                            allHeatmapPolylineShown = true;
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "loading data, try 1 minute later/ no history", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.showHeatmap16_:
                if(range4HeatmapPolylineShown){
                }
                else{
                    if((range4PathIdx!=null)&&(range4PathIdx.size()>1)) {
                        showHeatmap(range4PathIdx);
                        range4HeatmapPolylineShown = true;
                        if (range1HeatmapPolylineShown
                                && range2HeatmapPolylineShown
                                && range3HeatmapPolylineShown) {
                            allHeatmapPolylineShown = true;
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "loading data, try 1 minute later/ no history", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void cleanHeatmap(){
        for(int i=0; i<heatmapPolyline.size(); i++){
            for(int j=0; j<heatmapPolyline.get(i).size(); j++){
                heatmapPolyline.get(i).get(j).remove();
            }
            heatmapPolyline.get(i).clear();
        }
        heatmapPolyline.clear();
        heatmapPolylineTag.clear();
    }

    private void showHeatmap(ArrayList<Integer> pathIdx){
        int currentDuration;
        for(int i=0; i<pathIdx.size(); i++){
            currentDuration=sqldb.getWalkingPathDurationHistory().get(pathIdx.get(i));
            int currentColor;
            if(currentDuration<=TIME_RANGE1){
                currentColor=heatmapColor1;
            }
            else if(currentDuration<=TIME_RANGE2){
                currentColor=heatmapColor2;
            }
            else if(currentDuration<=TIME_RANGE3){
                currentColor=heatmapColor3;
            }
            else{
                currentColor=heatmapColor4;
            }
            PolylineOptions options = new PolylineOptions().width(10).color(currentColor).geodesic(true);
            options.addAll(sqldb.getWalkingPathLatLngHistory().get(pathIdx.get(i)));

            if((heatmapPolylineTag.isEmpty())||(!heatmapPolylineTag.contains(currentColor))){
                //new branch
                heatmapPolylineTag.add(currentColor);
                heatmapPolyline.add(new ArrayList<>());
                heatmapPolyline.get(heatmapPolyline.size()-1).add(mMap.addPolyline(options));
            }
            else {
                heatmapPolyline.get(heatmapPolylineTag.indexOf(currentColor)).add(mMap.addPolyline(options));
            }
        }
        if(sqldb.getWalkingPathLatLngHistory().size()!=0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sqldb.getWalkingPathLatLngHistory().get(0).get(0), DEFAULT_ZOOM));
        }
        else{
            Toast.makeText(getApplicationContext(), "loading data, try 1 minute later/ no history", Toast.LENGTH_SHORT).show();
        }
    }

    //hideKeyboard
    public static void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null && v != null;
        if(v!=null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
        //boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if(!gps_enabled){
            Toast.makeText(getApplicationContext(), "GPS is disabled", Toast.LENGTH_SHORT).show();
        }
        return gps_enabled;
    }

    //checkGPSPermission
    private void checkGPSPermission(){
        //check location permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
                UiSettings settings = mMap.getUiSettings();
                settings.setMyLocationButtonEnabled(false);
            }
            else {
                //request permission of location
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
        else {
            buildGoogleApiClient();
            //mMap.setMyLocationEnabled(true);
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
                if (tvFrom.length() > 0) {
                    tvFrom.getText().clear();
                }
                if (tvTo.length() > 0) {
                    tvTo.getText().clear();
                }
                if(polyline!=null) {
                    polyline.remove();
                }
                break;

            case 1:
                if(markerFrom!=null) {
                    markerFrom.remove();
                    markerFrom = null;
                    numOfMarkers-=1;
                }
                if(focusing) {
                    if (tvFrom.length() > 0) {
                        tvFrom.getText().clear();
                    }
                }
                if(polyline!=null) {
                    polyline.remove();
                }
                break;

            case 2:
                if(markerTo!=null) {
                    markerTo.remove();
                    markerTo = null;
                    numOfMarkers-=1;
                }
                if(focusing) {
                    if (tvTo.length() > 0) {
                        tvTo.getText().clear();
                    }
                }
                if(polyline!=null) {
                    polyline.remove();
                }
                break;
        }
    }

    //getMarker
    private MarkerOptions getMarker(String searchString, int mode){ //mode:1 from, 2 to
        Geocoder geocoder = new Geocoder(OpenMapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d("IOException: ", e.toString());
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
            return new MarkerOptions().position(point).title(address.getAddressLine(0));
        }
        return null;
    }

    //getMapsApiDirectionsUrl
    private String getMapsApiDirectionsUrl(String preferedMode, LatLng ptF, LatLng ptT) {
        if((ptF!=null)&(ptT!=null)) {
            String waypoints = "waypoints=optimize:true|"
                    + (ptF).latitude + "," + (ptF).longitude
                    + "|" + (ptT).latitude + ","
                    + (ptT).longitude;
            String OriDest = "origin=" + (ptF).latitude + "," + (ptF).longitude + "&destination=" + (ptT).latitude + "," + (ptT).longitude;
            String sensor = "sensor=true";
            String mode = "mode=" + preferedMode;
            String params = OriDest + "&%20" + waypoints + "&" + sensor + "&" + mode + "&alternatives=true";
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?" + "key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&callback=initialize&" + params;
            System.out.println("get direction URL:" + url);
            return url;
        }
        return null;
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
                //return;
            }
        }
    }

    //onConnected
    @Override
    public void onConnected(Bundle bundle) {
        //GPS
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60*1000);
        mLocationRequest.setFastestInterval(15*1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getFriendsLocation() {
        sqldb.getUserFriendEmailListByUserEmail();
    }

    private void showFriendsLocation(Location location){
        //clear all past data
        hideFriendsLocation();

        //LatLng myLocation=new LatLng(22.324017, 114.168779);
        LatLng myLocation=new LatLng(location.getLatitude(), location.getLongitude());

        //whether need to update
        if(sqldb.fdLatLngUpdated){
            friendLocations.clear();
            for(int i=0; i<sqldb.fdLat.size(); i++){
                friendLocations.add(new LatLng(sqldb.fdLat.get(i),sqldb.fdLng.get(i)));
            }
        }

        for(int i=0; i<friendLocations.size(); i++){
            //check fd button status
            if(showFriend) {
                //draw markers
                drawFriendsMarkers(friendLocations.get(i), null);
                //draw polylines
                String url = getMapsApiDirectionsUrl(preferedMode, myLocation, friendLocations.get(i));
                if(url!=null) {
                    GetDirectionTask getDirectionTask = new GetDirectionTask("fd");
                    getDirectionTask.execute(url);
                }
            }
            else{
                break;
            }
        }
    }

    private void drawFriendsMarkers(LatLng point, Bitmap icon){
        //get Location name
        String location="null";
        double longitude = point.longitude;
        double latitude = point.latitude;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                location = listAddresses.get(0).getAddressLine(0);
            }
            System.out.println(location);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("exception: "+e);
        }
        //add marker
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = marker.findViewById(R.id.user_dp);
        //markerImage.setImageResource(R.drawable.walk);
        TextView txt_name = marker.findViewById(R.id.name);
        //txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        (this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(location).icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        friendsMarkers.add(mMap.addMarker(markerOption));
    }

    private static void hideFriendsLocation(){
        //remove polylines
        if(frinedsPolylines!=null){
            for(int i=0; i<frinedsPolylines.size(); i++){
                frinedsPolylines.get(i).remove();
            }
            frinedsPolylines.clear();
        }
        //remove mrakers
        if(friendsMarkers!=null){
            for(int i=0; i<friendsMarkers.size(); i++){
                friendsMarkers.get(i).remove();
            }
            friendsMarkers.clear();
        }
    }

    private void startUpdateGPS(){
        //turn on the blue location indicator
        mMap.setMyLocationEnabled(true);
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
        //
        if(showFriend){
            FloatingActionButton fdsButton = findViewById(R.id.show_friends);
            gpsButton.performClick();
        }
        //turn off the blue location indicator
        mMap.setMyLocationEnabled(false);

        if(GPSPolyline!=null) {
            for(int i=0; i<GPSPolyline.size(); i++){
                GPSPolyline.get(i).remove();
            }
            GPSPolyline.clear();
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
        if(sqldb.fdLocationGot){
            if(updateRequest==1){
                showFriendsLocation(location);
            }
            sqldb.fdLocationGot=false;
            updateRequest-=1;
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //insert new record to DB
        //method 1
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        sqldb.insertLocationData(latLng, timestamp.getTime());

        boolean pass=false;
        boolean noRecord=false;
        boolean sameRecord=false;
        DecimalFormat df3 = new DecimalFormat("#.###");
        if(mLastLocation==null){
            noRecord=true;
        }
        //else if((df3.format(location.getLatitude()).equals(df3.format(mLastLocation.getLatitude())))&&(df3.format(location.getLongitude()).equals(df3.format((mLastLocation.getLongitude()))))){
        else if((location.getLatitude()==mLastLocation.getLatitude())&&(location.getLongitude()==(mLastLocation.getLongitude()))){
            sameRecord=true;
        }
        pass=noRecord||!sameRecord;
        //System.out.println("move cam: "+pass);
        if(pass) {
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        //draw location path
        //show friends' location
        if((!(noRecord))&&(!sameRecord)){
            //draw line
            if(GPSPolyline==null){
                GPSPolyline=new ArrayList<>();
            }
            String url = "https://roads.googleapis.com/v1/snapToRoads?path=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "|" + location.getLatitude() + "," + location.getLongitude() + "&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking";
            System.out.println("get snapToRoad URL(GPS): " + url);
            LatLng gpsFrom=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            LatLng gpsTo=new LatLng(location.getLatitude(),location.getLongitude());
            ArrayList<LatLng> snappedPath = extractJson(GET(url), gpsFrom, gpsTo);
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
        //update friend paths
        if(!sameRecord){
            if(showFriend) {
                getFriendsLocation();
                updateRequest+=1;
                //showFriendsLocation(location);
            }
        }
        mLastLocation = location;
    }


    //-------------------------------AsyncTask Class & Function--------------------------------------------
//class: GetDirectionTask
    private static class GetDirectionTask extends AsyncTask<String, Void, String> {
        private String callMode;

        GetDirectionTask(String callMode){
            this.callMode=callMode;
        }

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
            return data;
        }

        @Override
        protected void onPostExecute(String result) { //result == data
            super.onPostExecute(result);
            new ParserTask(callMode).execute(result);
        }
    }

    //class: ParserTask
    //private class ParserTask extends AsyncTask<String, Integer, ArrayList<ExtractedJSON>> {
    //version: snap route
    private static class ParserTask extends AsyncTask<String, Integer, ArrayList<ExtractedJSON>> {
        private String callMode;

        ParserTask(String callMode){
            this.callMode=callMode;
        }

        @Override
        //protected ArrayList<ExtractedJSON> doInBackground(String... jsonData) { //jsonData[0] == result == data
        //version: snap route
        protected ArrayList<ExtractedJSON> doInBackground(String... jsonData) {
            JSONObject jObject;
            //ArrayList<ExtractedJSON> routes = null;
            //version: snap route
            ArrayList<ExtractedJSON> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
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
            getPaths(routes,callMode);//by snapping route
        }
    }

    //getPaths
    private static void getPaths(ArrayList<ExtractedJSON> routes, String callMode){
        ArrayList<LatLng> pathsPoint = null;
        System.out.println("routes: "+routes);
        if((routes!=null)&&(routes.size()!=0)){
            new GetSnappedRouteTask(routes,callMode).execute();
        }
        else{
            Toast.makeText(mContext, "no information of the route", Toast.LENGTH_SHORT).show();
        }
    }

    //GetSnappedRouteTask
    private static class GetSnappedRouteTask extends AsyncTask<Void, Void, Void> {
        private ArrayList<ExtractedJSON> routes;
        private ArrayList<ArrayList<LatLng>> paths;
        private int duration;
        private ArrayList<Integer> durationList;
        private int distance;
        private ArrayList<Integer> distanceList;
        private ArrayList<LatLng> intergratedPath;
        private ArrayList<ArrayList<LatLng>> intergratedPathList;
        private ArrayList<ArrayList<LatLng>> allRoutes;
        private String callMode;

        public GetSnappedRouteTask(ArrayList<ExtractedJSON> routes, String callMode) {
            this.routes=routes;
            paths=new ArrayList<>();
            intergratedPath = new ArrayList<>();
            intergratedPathList = new ArrayList<>();
            allRoutes = new ArrayList<>();
            distanceList=new ArrayList<>();
            durationList=new ArrayList<>();
            this.callMode=callMode;
        }

        @Override
        protected Void doInBackground(Void... param) {
            String url;
            ExtractedJSON currentRoute=null;
            if ((routes != null) && (routes.size() != 0)) {
                currentRoute = routes.get(0);
                if (currentRoute != null) {
                    for (int i = 0; i < currentRoute.getSteps().size(); ) {
                        url = "https://roads.googleapis.com/v1/snapToRoads?path=" + currentRoute.getSteps().get(i).latitude + "," + currentRoute.getSteps().get(i).longitude + "|" + currentRoute.getSteps().get(i + 1).latitude + "," + currentRoute.getSteps().get(i + 1).longitude + "&interpolate=true&key=AIzaSyDl9jmXdHxOZglKI6uZ_Kci5w-mdvMGRmE&travelMode=walking";
                        System.out.println(i+" get snapToRoad URL: " + url);
                        paths.add(extractJson(GET(url), new LatLng(currentRoute.getSteps().get(i).latitude, currentRoute.getSteps().get(i).longitude), new LatLng(currentRoute.getSteps().get(i + 1).latitude, currentRoute.getSteps().get(i + 1).longitude)));
                        i += 2;
                    }

                    distance = currentRoute.getDistance();
                    duration = currentRoute.getDuration();

                    if (paths != null) {
                        for (int i = 0; i < paths.size(); i++) {
                            if(paths.get(i)!=null) {
                                intergratedPath.addAll(paths.get(i));
                            }
                            else{
                                System.out.println(i+" url got nothing");
                            }
                        }
                    }

                    allRoutes.add(intergratedPath);
                    distanceList.add(distance);
                    durationList.add(duration);
                }
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void param) {
            drawSnappedRoute(callMode);
        }

        private void drawSnappedRoute(String callMode){
            //draw route
            PolylineOptions options = new PolylineOptions();
            options.addAll(intergratedPath);
            options.width(10);
            options.color(Color.RED);
            polyline=mMap.addPolyline(options);
            if(callMode.equals("fd")){
                if(showFriend){
                    frinedsPolylines.add(polyline);
                    polyline=null;
                }
                else{
                    polyline.remove();
                    hideFriendsLocation();
                }
            }

            boolean showRouteInfo=false;
            if(callMode.equals("search")){
                showRouteInfo=true;
            }
            if(callMode.equals("fd")){
                showRouteInfo=false;
            }
            if(showRouteInfo) {
                SuggestedPath currentSuggestedPath;
                for (int i = 0; i < allRoutes.size(); i++) {
                    currentSuggestedPath = new SuggestedPath();
                    currentSuggestedPath.setMode(preferedMode);
                    int modeImageDrawable=R.drawable.walk;
                    if(preferedMode.equals("walking")){
                        modeImageDrawable=R.drawable.walk;
                    }
                    else if(preferedMode.equals("driving")){
                        modeImageDrawable=R.drawable.drive;
                    }
                    currentSuggestedPath.setModeImageDrawable(modeImageDrawable);
                    for (int j = 0; j < allRoutes.get(i).size(); j++) {
                        currentSuggestedPath.addPathPoint(allRoutes.get(i).get(j));
                    }
                    currentSuggestedPath.setDistance(distanceList.get(i));
                    currentSuggestedPath.setDuration(durationList.get(i));

                    if (i == 0) {
                        preferedSuggestedPath = currentSuggestedPath;
                    } else {
                        alternativeSuggestedPathList.add(currentSuggestedPath);
                    }
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
                    if (!minsPart.equals("")) {
                        durationInfo += "\n";
                    }
                    durationInfo += minsPart;
                    preferedDuration.setText(durationInfo);
                }
                if (alternativeSuggestedPathList != null) {
                    suggestedPathAdapter = new SuggestedPathAdapter(mContext, alternativeSuggestedPathList);
                    listView.setAdapter(suggestedPathAdapter);
                }

                if (mLayout.getPanelState() == PanelState.HIDDEN) {
                    mLayout.setPanelState(PanelState.COLLAPSED);
                }
                dragBarHeight=dragBar.getHeight();  //to fix the problem: dragBarHeight accidentally become zero
                mLayout.setPanelHeight(itemHeight + dragBarHeight);
                mLayout.setShadowHeight(shadowHeight);
                mLayout.setPanelState(PanelState.COLLAPSED);
                buttonPanelViewParams.setMargins(buttonPanelLeftMargin,buttonPanelTopMargin,buttonPanelRightMargin,buttonPanelBottomMargin+itemHeight + dragBarHeight);
                buttonPanelView.setLayoutParams(buttonPanelViewParams);

                //to solve AsyncTask problem in typing focus: delete marker -> reomve polyline -> add back marker -> add back polyline
                if (numOfMarkers < 2) {
                    polyline.remove();
                    //hide previous suggested paths
                    mLayout.setPanelHeight(0);
                    mLayout.setShadowHeight(0);
                    mLayout.setPanelState(PanelState.COLLAPSED);
                    buttonPanelViewParams.setMargins(buttonPanelLeftMargin,buttonPanelTopMargin,buttonPanelRightMargin,buttonPanelBottomMargin);
                    buttonPanelView.setLayoutParams(buttonPanelViewParams);
                }
            }
        }
    }

    private void drawSnappedGPSRoute(ArrayList<LatLng> path){
        //draw route
        PolylineOptions options = new PolylineOptions();
        options.addAll(path);
        options.width(10);
        options.color(Color.BLUE);
        GPSPolyline.add(mMap.addPolyline(options));
    }

    private static ArrayList<LatLng> extractJson(String result, LatLng originStart, LatLng originEnd){
        try {
            JSONObject json = new JSONObject(result);
            String str = "";
            JSONArray pointList = json.getJSONArray("snappedPoints");
            ArrayList<LatLng> points = new ArrayList<>();
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
            }
            //points.add(originEnd);  //to correct, because destination point get deviated
            return points;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static String GET(String url){
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
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}

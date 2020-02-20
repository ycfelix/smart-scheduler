package com.ust.smartph;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;

import com.ust.actiondetection.*;

import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKSensorDataListener;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;
import org.sensingkit.sensingkitlib.data.SKSensorData;

public class ActionDetectionActivity extends AppCompatActivity {

    @BindView(R.id.activitiesRecyclerView)
    RecyclerView activitiesRecyclerView;

    private ActivityAdapter activityAdapter;

    ActivityDetectionService mService;

    Intent serviceIntent;

    Unbinder unbinder;

    SensingKitLibInterface mSensingKitLib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_detect_main);
        this.unbinder=ButterKnife.bind(this);
        initRecyclerView();
        try{
            initializeSensors();
            mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.ACCELEROMETER);
        }catch (Exception e){
            e.printStackTrace();
        }

        serviceIntent = ActivityDetectionService.getStartIntent(this);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initRecyclerView() {
        activityAdapter = new ActivityAdapter();
        activitiesRecyclerView.setAdapter(activityAdapter);
        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activitiesRecyclerView.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void initializeSensors() throws SKException {
        mSensingKitLib = SensingKitLib.getSensingKitLib(this);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.GRAVITY);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.ROTATION);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.ACCELEROMETER, new SKSensorDataListener() {
            @Override
            public void onDataReceived(final SKSensorModuleType moduleType, final SKSensorData sensorData) {
                System.out.println(sensorData.getDataInCSV());  // Print data in CSV format
            }
        });
    }



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to the running Service, cast the IBinder and get instance
            ActivityDetectionService.LocalBinder binder = (ActivityDetectionService.LocalBinder) service;
            mService = binder.getService();
            mService.getObservable().subscribe(new Action1<DatedActivity>() {
                @Override
                public void call(DatedActivity datedActivity) {
                    Log.e("","added item");
                    activityAdapter.addItem(datedActivity);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };
}

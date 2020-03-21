package com.ust.smartph;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ust.customactiondetection.DataModel;

import org.jetbrains.annotations.NotNull;
import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKSensorDataListener;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;
import org.sensingkit.sensingkitlib.data.SKSensorData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserActionActivity extends AppCompatActivity {

    @BindView(R.id.action_grid)
    GridLayout grid;

    Unbinder unbinder;

    SensingKitLibInterface mSensingKitLib;

    ArrayList<DataModel> sensorModels;

    Map<SKSensorModuleType, String> readings;

    ArrayList<SKSensorModuleType> sensingSensors;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_action_home);
        unbinder = ButterKnife.bind(this);
        sensingSensors=new ArrayList<>();
        readings=new HashMap<>();
        sensorModels=new ArrayList<>();
        try {
            requestPermission();
            initializeSensors();
        } catch (SKException e) {
            e.printStackTrace();
        }
    }

    private void initGridItems(String[] csv) {
        //hardcoded from the doc
        String action=csv[2];
        String value=csv[3];
        for (int i = 0; i < grid.getChildCount(); i++) {
            final CardView cardView = (CardView) grid.getChildAt(i);
            LinearLayout layout = (LinearLayout) cardView.getChildAt(0);
            TextView tv = (TextView) layout.getChildAt(1);
            ProgressBar progressBar = (ProgressBar) layout.getChildAt(2);
            TextView percent = (TextView) layout.getChildAt(3);
            if(tv.getText().toString().toLowerCase().equals(action.toLowerCase())){
                progressBar.setProgress(Integer.parseInt(value));
                percent.setText(String.format("%s %%",value));
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Send feedback to the server
                }
            });
        }
    }
    @NotNull
    private DataModel getDataModel(SKSensorModuleType type) throws Exception {
        try {
            if (!mSensingKitLib.isSensorModuleRegistered(type)) {
                mSensingKitLib.registerSensorModule(type);
            }
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "device " + type.name() + " not available", Toast.LENGTH_LONG).show();
            throw new IllegalAccessException("the device is not available " + type.name());
        }

        DataModel result = new DataModel(type.name(), "1.0", "");
        mSensingKitLib.subscribeSensorDataListener(type, new SKSensorDataListener() {
            @Override
            public void onDataReceived(final SKSensorModuleType moduleType, final SKSensorData sensorData) {
                String csv = sensorData.getDataInCSV();
                //the first string doesn't seem to be any useful
                readings.put(moduleType,csv);
                result.setSensorNumber(csv.substring(csv.indexOf(",") + 1));
                if(moduleType==SKSensorModuleType.ACTIVITY){
                    initGridItems(csv.split(","));
                    grid.invalidate();
                }
            }
        });
        return result;
    }

    private void initializeSensors() throws SKException {

        mSensingKitLib = SensingKitLib.getSensingKitLib(this);
        EnumSet.allOf(SKSensorModuleType.class).stream().filter(e ->
                e != SKSensorModuleType.AUDIO_RECORDER
                        && e != SKSensorModuleType.AUDIO_LEVEL
                        && e != SKSensorModuleType.BLUETOOTH).forEach(sensor -> {
                    try {
                        sensorModels.add(getDataModel(sensor));
                        if (!mSensingKitLib.isSensorModuleSensing(sensor)) {
                            mSensingKitLib.startContinuousSensingWithSensor(sensor);
                            sensingSensors.add(sensor);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "you need to permit the location", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    99);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        try {
            sensingSensors.forEach(sensor -> {
                try {
                    mSensingKitLib.stopContinuousSensingWithSensor(sensor);
                    mSensingKitLib.unsubscribeAllSensorDataListeners(sensor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

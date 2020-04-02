package com.ust.smartph;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ust.customactiondetection.ActionDetectionAdaptor;
import com.ust.customactiondetection.DataModel;

import java.util.ArrayList;
import java.util.EnumSet;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.jetbrains.annotations.NotNull;
import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKSensorDataListener;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;
import org.sensingkit.sensingkitlib.data.SKSensorData;



public class CustomActionDetectionActivity extends AppCompatActivity {
    ArrayList<DataModel> dataModels;

    @BindView(R.id.action_detection_list)
    ListView listView;

    private ActionDetectionAdaptor adapter;

    SensingKitLibInterface mSensingKitLib;

    ArrayList<SKSensorModuleType> sensingSensors=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_detection_custom_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_detection_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Gson gson=new Gson();
                System.out.println(gson.toJson(dataModels));
                return false;
            }
        });
        ButterKnife.bind(this);

        try{
            requestPermission();
            dataModels=initializeSensors();
        }catch (Exception e){
            e.printStackTrace();
        }

        adapter= new ActionDetectionAdaptor(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel= dataModels.get(position);

                Snackbar.make(view, dataModel.getSensorName()+"\n"+dataModel.getSensorType()+" API: "+dataModel.getSensorNumber(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            sensingSensors.forEach(sensor->{
                try {
                    mSensingKitLib.stopContinuousSensingWithSensor(sensor);
                    mSensingKitLib.unsubscribeAllSensorDataListeners(sensor);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_detection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<DataModel> initializeSensors() throws SKException {

        ArrayList<DataModel> result=new ArrayList<>();
        mSensingKitLib = SensingKitLib.getSensingKitLib(this);
        EnumSet.allOf(SKSensorModuleType.class).stream().filter(e->
                    e!=SKSensorModuleType.AUDIO_RECORDER
                &&  e!=SKSensorModuleType.AUDIO_LEVEL
                &&  e!=SKSensorModuleType.BLUETOOTH).forEach(sensor->{
                try{
                    result.add(getDataModel(sensor));
                    if(!mSensingKitLib.isSensorModuleSensing(sensor)){
                        mSensingKitLib.startContinuousSensingWithSensor(sensor);
                        sensingSensors.add(sensor);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        );
        return result;
    }

    @NotNull
    private DataModel getDataModel(SKSensorModuleType type) throws Exception{
        try{
            if(!mSensingKitLib.isSensorModuleRegistered(type)){
                mSensingKitLib.registerSensorModule(type);
            }
        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(),"device "+type.name()+" not available",Toast.LENGTH_LONG).show();
            throw new IllegalAccessException("the device is not available "+type.name());
        }

        DataModel result=new DataModel(type.name(),"1.0","");
        mSensingKitLib.subscribeSensorDataListener(type, new SKSensorDataListener() {
            @Override
            public void onDataReceived(final SKSensorModuleType moduleType, final SKSensorData sensorData) {
                String csv=sensorData.getDataInCSV();
                //the first string doesn't seem to be any useful
                result.setSensorNumber(csv.substring(csv.indexOf(",")+1));
                //Log.d("",sensorData.getDataInCSV());
                adapter.notifyDataSetChanged();
            }
        });
        return result;
    }


    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "you need to permit the location", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    99);
        }
    }

}

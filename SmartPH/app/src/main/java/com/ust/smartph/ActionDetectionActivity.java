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

public class ActionDetectionActivity extends AppCompatActivity {

    @BindView(R.id.activitiesRecyclerView)
    RecyclerView activitiesRecyclerView;

    private ActivityAdapter activityAdapter;

    ActivityDetectionService mService;

    Intent serviceIntent;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_detect_main);
        this.unbinder=ButterKnife.bind(this);
        initRecyclerView();

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

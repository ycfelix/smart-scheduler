package com.ust.smartph;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ust.appusagechart.BarchartFragment;
import com.ust.appusagechart.PiechartFragment;
import com.ust.appusagechart.SlidePageAdatper;
import com.ust.appusagechart.TimelineFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppUsageHomeActivity extends AppCompatActivity {

    @BindView(R.id.swipe_pager)
    ViewPager pager;

    PagerAdapter adapter;

    @BindView(R.id.chart_tab)
    TabLayout chartTab;

    private static final int PERMISSION_USAGE = 0;

    private String[] titles={"Piechart","Timeline","barchart"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_usage_home);
        ButterKnife.bind(this);
        List<Fragment> fms=new ArrayList<>();
        fms.add(new PiechartFragment());
        fms.add(new TimelineFragment());
        fms.add(new BarchartFragment());
        chartTab.addTab(chartTab.newTab());
        chartTab.addTab(chartTab.newTab());
        chartTab.addTab(chartTab.newTab());
        chartTab.setupWithViewPager(this.pager,false);
        adapter=new SlidePageAdatper(getSupportFragmentManager(),fms);
        pager.setAdapter(adapter);
        for(int i=0;i<titles.length;i++){
            Objects.requireNonNull(chartTab.getTabAt(i)).setText(titles[i]);
        }
        checkPermission();
    }

    private void checkPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PACKAGE_USAGE_STATS)){
                }
        else {
            Toast.makeText(this, "Permission Required!", Toast.LENGTH_SHORT)
                    .show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, PERMISSION_USAGE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_USAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "usage success!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "usage FAILED!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}

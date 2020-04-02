package com.ust.smartph;


import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
        try {
            if(!isStatAccessPermissionSet(this)){
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("cannot get permission");
        }
    }

    private boolean isStatAccessPermissionSet(Context c) throws PackageManager.NameNotFoundException {
        PackageManager pm = c.getPackageManager();
        ApplicationInfo info = pm.getApplicationInfo(c.getPackageName(),0);
        AppOpsManager aom = (AppOpsManager) c.getSystemService(Context.APP_OPS_SERVICE);
        aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,info.uid,info.packageName);
        return aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,info.uid,info.packageName)
                == AppOpsManager.MODE_ALLOWED;
    }
}

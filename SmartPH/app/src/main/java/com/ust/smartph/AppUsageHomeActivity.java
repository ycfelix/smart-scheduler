package com.ust.smartph;


import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.material.tabs.TabLayout;
import com.ust.appusagechart.BarchartFragment;
import com.ust.appusagechart.PiechartFragment;
import com.ust.appusagechart.SlidePageAdatper;
import com.ust.appusagechart.TimelineFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AppUsageHomeActivity extends AppCompatActivity {

    @BindView(R.id.swipe_pager)
    ViewPager pager;

    PagerAdapter adapter;

    private final int REQUEST_CODE=12;

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
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),REQUEST_CODE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("cannot get permission");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
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

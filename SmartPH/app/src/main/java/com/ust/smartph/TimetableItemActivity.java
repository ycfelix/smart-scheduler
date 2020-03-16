package com.ust.smartph;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.tlaabs.timetableview.Schedule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ust.timetable.EditDialog;
import com.ust.timetable.EditDialogListener;
import com.ust.timetable.MonWedFragment;
import com.ust.timetable.RequestType;
import com.ust.timetable.ThrSunFragment;
import com.ust.timetable.TimetableAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author ycfelix
 * <p>
 * Custom timetable home view, controller to the view of timetable
 */
//this class handle add, import, export and match function
public class TimetableItemActivity extends AppCompatActivity {

    @BindView(R.id.swipe_timetable)
    ViewPager pager;

    @BindView(R.id.current_timetable)
    TextView timetableName;

    TimetableAdapter adapter;

    @BindView(R.id.fab_timetable_menu)
    FloatingActionMenu menu;

    @BindView(R.id.timetable_tab)
    TabLayout timetableTab;

    @BindView(R.id.add_fab)
    FloatingActionButton addBtn;

    ArrayList<Schedule> monWed;

    ArrayList<Schedule> thrSun;

    Unbinder unbinder;

    private String PREF_MON_WED="monwed_";
    private String PREF_THR_SUN="thrsun_";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        unbinder=ButterKnife.bind(this);

        Intent intent=getIntent();
        String prefix=intent.getStringExtra("TABLE_NAME");
        PREF_MON_WED+=prefix;
        PREF_THR_SUN+=prefix;

        loadByPreference();
        List<Fragment> fms = new ArrayList<>();

        MonWedFragment monWedFragment=new MonWedFragment();
        Bundle monWed=new Bundle();
        monWed.putString("TABLE_NAME",prefix);
        monWedFragment.setArguments(monWed);

        ThrSunFragment thrSunFragment=new ThrSunFragment();
        Bundle thrSun=new Bundle();
        thrSun.putString("TABLE_NAME",prefix);
        thrSunFragment.setArguments(thrSun);

        fms.add(monWedFragment);
        fms.add(thrSunFragment);

        timetableTab.addTab(timetableTab.newTab());
        timetableTab.addTab(timetableTab.newTab());
        timetableTab.setupWithViewPager(this.pager, false);
        adapter = new TimetableAdapter(getSupportFragmentManager(), fms);
        pager.setAdapter(adapter);
        timetableTab.getTabAt(0).setText("Mon-Wed");
        timetableTab.getTabAt(1).setText("Thr-Sun");
        timetableName.setText("PERSONAL TIMETABLE \n SWITCH TO GROUP TIMETABLE ?");
    }

    @OnClick(R.id.add_fab)
    void addSchedule(View v) {
        loadByPreference();
        EditDialog dialog = new EditDialog(this, RequestType.ADD);
        dialog.setEditDialogListener(new EditDialogListener() {
            @Override
            public void onEditResult(@Nullable Schedule schedule, RequestType type) {
                if (schedule != null) {
                    if (schedule.getDay() < 3) {
                        monWed.add(schedule);
                    } else {
                        thrSun.add(schedule);
                    }
                    Gson gson=new Gson();
                    String monwedData=gson.toJson(monWed);
                    System.out.println(monwedData);
                    String thrsunData=gson.toJson(thrSun);
                    saveByPreference(PREF_MON_WED,monwedData);
                    saveByPreference(PREF_THR_SUN,thrsunData);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
        menu.close(true);
    }


    private void loadByPreference(){
        this.monWed=new ArrayList<>();
        this.thrSun=new ArrayList<>();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String monwedData = mPref.getString(PREF_MON_WED, "");
        String thrsunData = mPref.getString(PREF_THR_SUN, "");
        Gson gson=new Gson();
        if(!TextUtils.isEmpty(monwedData)){
           this.monWed = gson.fromJson(monwedData,new TypeToken<ArrayList<Schedule>>(){}.getType());
        }
        if(!TextUtils.isEmpty(thrsunData)){
            this.thrSun = gson.fromJson(thrsunData,new TypeToken<ArrayList<Schedule>>(){}.getType());
        }
    }

    private void saveByPreference(String filename,String data) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(filename, data);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
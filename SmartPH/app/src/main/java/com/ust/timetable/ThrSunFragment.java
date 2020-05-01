package com.ust.timetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ust.smartph.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ThrSunFragment extends BaseTimetable {

    @BindView(R.id.timetable_thrsun)
    TimetableView timetable;

    String PREF_THR_SUN="thrsun_";

    Unbinder unbinder;

    public ThrSunFragment(){}

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        PREF_THR_SUN+=args.getString("TABLE_NAME");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.timetable_thursun, container, false);
        unbinder = ButterKnife.bind(this, root);
        loadTimetable();
        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {

                EditDialog dialog = new EditDialog(getActivity(), RequestType.EDIT,schedules);
                dialog.setEditDialogListener(new EditDialogListener() {
                    @Override
                    public void onEditResult(@Nullable Schedule schedule, RequestType type) {
                        if(type==RequestType.DELETE){
                            timetable.remove(idx);
                            timetable.getAllSchedulesInStickers().remove(idx);
                        }
                        else{
                            if(schedule!=null){
                                timetable.edit(idx, new ArrayList<>(Arrays.asList(schedule)));
                            }
                        }
                        Gson gson=new Gson();
                        ArrayList<Schedule> list=timetable.getAllSchedulesInStickers();
                        ArrayList<Schedule> toSave=new ArrayList<>();
                        for(Schedule sc:list){
                            Schedule s=new Schedule();
                            s.setDay(sc.getDay()+3);//need to add back the time diff
                            s.setClassPlace(sc.getClassPlace());
                            s.setClassTitle(sc.getClassTitle());
                            s.setProfessorName(sc.getProfessorName());
                            s.setStartTime(sc.getStartTime());
                            s.setEndTime(sc.getEndTime());
                            toSave.add(s);
                        }
                        saveByPreference(PREF_THR_SUN,gson.toJson(toSave));
                    }
                });
                dialog.show();
            }
        });
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    private void loadTimetable() {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String thrsunData = mPref.getString(PREF_THR_SUN, "");

        Gson gson = new Gson();
        if (!TextUtils.isEmpty(thrsunData)) {
            ArrayList<Schedule> thrSun = gson.fromJson(thrsunData, new TypeToken<ArrayList<Schedule>>() {
            }.getType());
            for(Schedule schedule: thrSun){
                //since I split the timetable into 2, the second half need to subtract 3
                schedule.setDay(schedule.getDay()-3);
                timetable.add(new ArrayList<>(Arrays.asList(schedule)));
            }
        }
    }
}

package com.ust.timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.tlaabs.timetableview.SaveManager;
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


public class MonWedFragment extends BaseTimetable {


    @BindView(R.id.timetable_monwed)
    TimetableView timetable;

    Unbinder unbinder;

    String PREF_MON_WED="monwed_";

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        PREF_MON_WED+=args.getString("TABLE_NAME");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.timetable_monwed, container, false);
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
                        }
                        else{
                            if(schedule!=null){
                                timetable.edit(idx, new ArrayList<>(Arrays.asList(schedule)));
                            }
                        }
                        Gson gson=new Gson();
                        System.out.println(gson.toJson(timetable.getAllSchedulesInStickers()));
                        System.out.println(PREF_MON_WED);
                        saveByPreference(PREF_MON_WED,gson.toJson(timetable.getAllSchedulesInStickers()));
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
        String monwedData = mPref.getString(PREF_MON_WED, "");
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(monwedData)) {
            ArrayList<Schedule> monWed = gson.fromJson(monwedData, new TypeToken<ArrayList<Schedule>>() {
            }.getType());
            monWed.forEach(e -> timetable.add(new ArrayList<>(Arrays.asList(e))));
        }
    }
}

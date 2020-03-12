package com.ust.timetable;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.github.tlaabs.timetableview.Schedule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class BaseTimetable extends Fragment {

    final String PREF_THR_SUN = "personal_thrsun";
    final String PREF_MON_WED = "personal_monwed";
    final String PREF_THR_SUN_GROUP = "group_thrsun";
    final String PREF_MON_WED_GROUP = "group_monwed";

    protected void saveByPreference(String filename, String data) {
        data = TextUtils.isEmpty(data) ? "" : data;
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(filename, data);
        editor.commit();
    }
}

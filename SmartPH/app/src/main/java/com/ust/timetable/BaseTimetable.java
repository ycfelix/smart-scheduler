package com.ust.timetable;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.core.app.Fragment;
import android.text.TextUtils;

public class BaseTimetable extends Fragment {

    protected void saveByPreference(String filename, String data) {
        data = TextUtils.isEmpty(data) ? "" : data;
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(filename, data);
        editor.commit();
    }
}

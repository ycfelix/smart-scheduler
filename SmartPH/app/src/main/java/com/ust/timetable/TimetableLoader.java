package com.ust.timetable;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class TimetableLoader extends Application {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor=preferences.edit();
    }

    @Nullable
    public static String getSchedule(String name){
        return preferences.getString(name,null);
    }

    public static void setSchedule(String name,String data){
        editor.putString(name,data);
        editor.commit();
    }



}

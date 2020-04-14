package com.ust.timetable;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

        // vanilla timetable matching function
    public static ArrayList<Schedule> matchTimeTable(ArrayList<Schedule> now, ArrayList<Schedule> other ) {
        return getCrashTime(now,other);
    }


    private static ArrayList<Schedule> getCrashTime(ArrayList<Schedule> t1, ArrayList<Schedule> t2) {
        ArrayList<Schedule> sameTime = new ArrayList<>();
        for (Schedule s : t1) {
            for(Schedule e: t2){
                if(s.getDay()==e.getDay()&&isTimeIntercept(s,e)){
                    Schedule free = new Schedule();
                    free.setDay(s.getDay());
                    free.setClassTitle("Free");
                    int lateStartHR=Math.max(s.getStartTime().getHour(),e.getStartTime().getHour());
                    int lateStartMin=Math.max(s.getStartTime().getMinute(),e.getStartTime().getMinute());
                    int earlyEndHR=Math.min(s.getEndTime().getHour(),e.getEndTime().getHour());
                    int earlyEndMin=Math.min(s.getEndTime().getMinute(),e.getEndTime().getMinute());
                    free.setStartTime(new Time(lateStartHR,lateStartMin));
                    free.setEndTime(new Time(earlyEndHR,earlyEndMin));
                    sameTime.add(free);
                }
            }
        }
        return sameTime;
    }

    public static ArrayList<Schedule> getFreeTime(ArrayList<Schedule> now) {
        ArrayList<Schedule> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final int today = i;
            Time time = new Time(0, 0);
            ArrayList<Schedule> sameDay = now.stream()
                    .filter(e -> e.getDay() == today).collect(Collectors.toCollection(ArrayList::new));
            for (int j = 0; j < sameDay.size(); j++) {
                Time end = sameDay.get(j).getStartTime();
                Schedule free = getFreeSchedule(i,time,end);
                time = sameDay.get(j).getEndTime();
                result.add(free);
            }
            if (result.size() > 0) {
                Time lastItem=result.get(result.size()-1).getEndTime();
                if(lastItem.getHour()==6 &&lastItem.getMinute()==59){
                    continue;
                }
                Schedule free = getFreeSchedule(i,time,new Time(23,59));
                result.add(free);
            }
            else{
                Schedule free = getFreeSchedule(i,time,new Time(23,59));
                result.add(free);
            }

        }
        return result;
    }

    private static Schedule getFreeSchedule(int day, Time begin, Time end){
        Schedule free = new Schedule();
        free.setDay(day);
        free.setClassTitle("Free");
        free.setStartTime(begin);
        free.setEndTime(end);
        return free;
    }

    private static boolean isTimeIntercept(Schedule t1, Schedule t2) {
        int startHR = Math.min(t1.getStartTime().getHour(), t2.getStartTime().getHour());
        Schedule early = t1.getStartTime().getHour() == startHR ? t1 : t2;
        Schedule late = early == t1 ? t2 : t1;
        if (early.getEndTime().getHour() > late.getStartTime().getHour()) {
            if(early.getEndTime().getHour() == late.getStartTime().getHour()){
                return early.getEndTime().getMinute() < late.getStartTime().getMinute();
            }
            return true;
        }
        return false;
    }
}

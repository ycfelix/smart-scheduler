package com.ust.appusagechart;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.ust.appusagechart.AppInfo.bootTime;

public class AppUsageInfo {
    final public static int DAY = 0;
    final public static int WEEK = 1;
    final public static int MONTH = 2;
    final public static int YEAR = 3;

    private ArrayList<AppInfo> ShowList;
    private ArrayList<AppInfo> AppInfoList;
    private List<UsageStats> result;
    private long totalTime;
    private int style;

    public AppUsageInfo(Context context, int style) {
        try {
            this.style = style;
            setUsageStatsList(context);
            setShowList();

        } catch (NoSuchFieldException e) {
            System.out.println("name not found");
        }
    }

    private void setShowList() {
        this.ShowList = new ArrayList<>();

        totalTime = 0;

        for (int i = 0; i < AppInfoList.size(); i++) {
            if (AppInfoList.get(i).getUsedTimebyDay() > 0) {
                if(AppInfoList.get(i).getLabel()==null||AppInfoList.get(i).getLabel().isEmpty()) continue;

                this.ShowList.add(AppInfoList.get(i));
                totalTime += AppInfoList.get(i).getUsedTimebyDay();
            }
        }
        for (int i = 0; i < this.ShowList.size() - 1; i++) {
            for (int j = 0; j < this.ShowList.size() - i - 1; j++) {
                if (this.ShowList.get(j).getUsedTimebyDay() < this.ShowList.get(j + 1).getUsedTimebyDay()) {
                    AppInfo temp = this.ShowList.get(j);
                    this.ShowList.set(j, this.ShowList.get(j + 1));
                    this.ShowList.set(j + 1, temp);
                }
            }
        }
    }

    private void setUsageStatsList(Context context) throws NoSuchFieldException {
        UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        this.AppInfoList = new ArrayList<>();
        if (m != null) {
            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis();
            long begintime = getBeginTime();
            if (style == DAY) {
                this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begintime, now);
                AppInfoList = getAccurateDailyStatsList(context, result, m, begintime, now);
            }
            else {
                if (style == WEEK)
                    this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, begintime, now);
                else if (style == MONTH)
                    this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, begintime, now);
                else if (style == YEAR)
                    this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, begintime, now);
                else {
                    this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, begintime, now);
                }

                List<UsageStats> Mergeresult = MergeList(this.result);
                for (UsageStats usageStats : Mergeresult) {
                    this.AppInfoList.add(new AppInfo(usageStats, context));
                }
                calculateLaunchTimesAfterBootOn(context, AppInfoList);
            }
        }
    }

    private ArrayList<AppInfo> getAccurateDailyStatsList(Context context, List<UsageStats> result, UsageStatsManager m, long begintime, long now) {
        HashMap<String, AppInfo> mapData = new HashMap<>();
        for (UsageStats stats : result) {
            if (stats.getLastTimeUsed() > begintime && stats.getTotalTimeInForeground() > 0) {
                if (mapData.get(stats.getPackageName()) == null) {
                    AppInfo information = new AppInfo(stats, context);
                    information.setTimes(0);
                    information.setUsedTimebyDay(0);
                    mapData.put(stats.getPackageName(), information);
                }
            }
        }
        long bootTime = AppInfo.bootTime();
        UsageEvents events = m.queryEvents(bootTime, now);

        UsageEvents.Event e = new UsageEvents.Event();
        while (events.hasNextEvent()) {
            events.getNextEvent(e);
            String packageName = e.getPackageName();

            AppInfo information = mapData.get(packageName);
            if (information == null) {
                continue;
            }
            if (e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                information.timesPlusPlus();
                if (e.getTimeStamp() < begintime){
                    continue;
                }
                information.setTimeStampMoveToForeground(e.getTimeStamp());
            } else if (e.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                if (e.getTimeStamp() < begintime){
                    continue;
                }
                information.setTimeStampMoveToBackGround(e.getTimeStamp());
                if (information.getTimeStampMoveToForeground() < 0) {
                    information.setTimeStampMoveToForeground(begintime);
                }
            }
            information.calculateRunningTime();
        }

        return new ArrayList<>(mapData.values());
    }


    private void calculateLaunchTimesAfterBootOn(Context context, List<AppInfo> AppInfoList) {

        UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (m == null || AppInfoList == null || AppInfoList.size() < 1) {
            return;
        }
        HashMap<String, AppInfo> mapData = new HashMap<>();

        UsageEvents events = m.queryEvents(bootTime(), System.currentTimeMillis());
        for (AppInfo information : AppInfoList) {
            mapData.put(information.getPackageName(), information);
            information.setTimes(0);
        }

        UsageEvents.Event e = new UsageEvents.Event();
        while (events.hasNextEvent()) {
            events.getNextEvent(e);
            String packageName = e.getPackageName();
            AppInfo information = mapData.get(packageName);
            if (information == null) {
                continue;
            }

            if (e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                information.timesPlusPlus();
            }
        }
    }

    private long getBeginTime() {
        Calendar calendar = Calendar.getInstance();
        long begintime;
        if (style == WEEK) {
            calendar.add(Calendar.DATE, -7);
            begintime = calendar.getTimeInMillis();
        } else if (style == MONTH) {
            calendar.add(Calendar.DATE, -30);
            begintime = calendar.getTimeInMillis();
        } else if (style == YEAR) {
            calendar.add(Calendar.YEAR, -1);
            begintime = calendar.getTimeInMillis();
        } else {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            calendar.add(Calendar.SECOND, -1 * second);
            calendar.add(Calendar.MINUTE, -1 * minute);
            calendar.add(Calendar.HOUR, -1 * hour);
            begintime = calendar.getTimeInMillis();

        }
        return begintime;
    }

    private List<UsageStats> MergeList(List<UsageStats> result) {
        List<UsageStats> Mergeresult = new ArrayList<>();
        long begintime;
        begintime = getBeginTime();
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getLastTimeUsed() > begintime) {
                int num = FoundUsageStats(Mergeresult, result.get(i));
                if (num >= 0) {
                    UsageStats u = Mergeresult.get(num);
                    u.add(result.get(i));
                    Mergeresult.set(num, u);
                } else Mergeresult.add(result.get(i));
            }
        }
        return Mergeresult;
    }

    private int FoundUsageStats(List<UsageStats> Mergeresult, UsageStats usageStats) {
        for (int i = 0; i < Mergeresult.size(); i++) {
            if (Mergeresult.get(i).getPackageName().equals(usageStats.getPackageName())) {
                return i;
            }
        }
        return -1;
    }


    public long getTotalTime() {
        return totalTime;
    }

    public ArrayList<AppInfo> getShowList() {
        return ShowList;
    }
}

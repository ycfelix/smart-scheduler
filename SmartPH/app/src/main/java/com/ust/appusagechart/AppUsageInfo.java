package com.ust.appusagechart;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.ust.appusagechart.AppInfo.bootTime;

public class AppUsageInfo {
    final public static int DAY = 0;
    final public static int WEEK = 1;
    final public static int MONTH = 2;
    final public static int YEAR = 3;

    private ArrayList<AppInfo> appList;
    private ArrayList<AppInfo> infoList;
    private List<UsageStats> result;
    private long totalTime;
    private int style;

    public AppUsageInfo(Context context, int style) {
        try {
            this.style = style;
            setUsageStatsList(context);
            setShowList();

        } catch (Exception e) {
            System.out.println("name not found");
        }
    }

    private void setShowList() {
        this.appList = new ArrayList<>();

        totalTime = 0;

        for (int i = 0; i < infoList.size(); i++) {
            if (infoList.get(i).getUsedTimebyDay() > 0) {
                if (TextUtils.isEmpty(infoList.get(i).getLabel())) continue;

                this.appList.add(infoList.get(i));
                totalTime += infoList.get(i).getUsedTimebyDay();
            }
        }

        Collections.sort(this.appList, (o1, o2) -> Long.compare(o1.getUsedTimebyDay(), o2.getUsedTimebyDay()));
    }

    private void setUsageStatsList(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        this.infoList = new ArrayList<>();
        if (manager == null) {
            System.out.println("stat manager is null!");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        long begin = getBeginTime();
        switch (style) {
            case DAY:
                this.result = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begin, now);
                infoList = getStatsList(context, result, manager, begin, now);
                break;
            case WEEK:
                this.result = manager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, begin, now);
                break;
            case MONTH:
                this.result = manager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, begin, now);
                break;
            case YEAR:
                this.result = manager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, begin, now);
                break;
            default:
                this.result = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, begin, now);
                break;
        }
        if (style != DAY) {
            List<UsageStats> Mergeresult = merge(this.result);
            for (UsageStats usageStats : Mergeresult) {
                this.infoList.add(new AppInfo(usageStats, context));
            }
            calLaunchTime(context, infoList);
        }
    }

    private AppInfo getInfo(UsageStats stats, Context context) {
        AppInfo information = new AppInfo(stats, context);
        information.setTimes(0);
        information.setUsagePerDay(0);
        return information;
    }

    private ArrayList<AppInfo> getStatsList(Context context, List<UsageStats> result, UsageStatsManager manager, long begintime, long now) {
        HashMap<String, AppInfo> map = new HashMap<>();
        for (UsageStats stats : result) {
            if (stats.getLastTimeUsed() > begintime && stats.getTotalTimeInForeground() > 0) {
                String name = stats.getPackageName();
                if (map.get(name) != null) {
                    continue;
                }
                map.put(name, getInfo(stats, context));
            }
        }
        long bootTime = AppInfo.bootTime();
        UsageEvents events = manager.queryEvents(bootTime, now);

        UsageEvents.Event event = new UsageEvents.Event();
        while (events.hasNextEvent()) {
            events.getNextEvent(event);
            String name = event.getPackageName();
            AppInfo info = map.get(name);
            if (info == null) {
                continue;
            }
            switch (event.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND: {
                    info.timesAdd();
                    if (event.getTimeStamp() >= begintime) {
                        info.setForeground(event.getTimeStamp());
                    }
                    break;
                }
                case UsageEvents.Event.MOVE_TO_BACKGROUND: {
                    if (event.getTimeStamp() >= begintime) {
                        info.setBackground(event.getTimeStamp());
                    }
                    if (info.getForeground() < 0) {
                        info.setForeground(begintime);
                    }
                    break;
                }
                default:
                    break;
            }
            info.calRunTime();
        }

        return new ArrayList<>(map.values());
    }


    private void calLaunchTime(Context context, List<AppInfo> infos) {

        UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (m == null || infos == null || infos.size() < 1) {
            return;
        }
        HashMap<String, AppInfo> mapData = new HashMap<>();
        UsageEvents events = m.queryEvents(bootTime(), System.currentTimeMillis());
        infos.forEach(e -> {
            mapData.put(e.getPackageName(), e);
            e.setTimes(0);
        });
        UsageEvents.Event e = new UsageEvents.Event();
        while (events.hasNextEvent()) {
            events.getNextEvent(e);
            String name = e.getPackageName();
            AppInfo info = mapData.get(name);
            if (info == null) {
                continue;
            }
            switch (e.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND:
                    info.timesAdd();
                    break;
                default:
                    break;
            }
        }
    }

    private long getBeginTime() {
        Calendar calendar = Calendar.getInstance();
        switch (style) {
            case WEEK: {
                calendar.add(Calendar.DATE, -7);
                break;
            }
            case MONTH: {
                calendar.add(Calendar.DATE, -30);
                break;
            }
            case YEAR: {
                calendar.add(Calendar.YEAR, -1);
                break;
            }
            default: {
                int hr = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                int sec = calendar.get(Calendar.SECOND);

                calendar.add(Calendar.SECOND, -1 * sec);
                calendar.add(Calendar.MINUTE, -1 * min);
                calendar.add(Calendar.HOUR, -1 * hr);
                break;
            }
        }
        return calendar.getTimeInMillis();
    }

    private List<UsageStats> merge(List<UsageStats> result) {
        List<UsageStats> merged = new ArrayList<>();
        long begin = getBeginTime();
        result.stream()
                .filter(e -> e.getLastTimeUsed() > begin)
                .forEach(e -> {
                    int num = indexOfStats(merged, e);
                    if (num >= 0) {
                        UsageStats u = merged.get(num);
                        u.add(e);
                        merged.set(num, u);
                    } else {
                        merged.add(e);
                    }
                });
        return merged;
    }

    private int indexOfStats(List<UsageStats> merged, UsageStats usageStats) {
        int result=-1;
        for (int i = 0; i < merged.size(); i++) {
            if (merged.get(i).getPackageName().equals(usageStats.getPackageName())) {
                result = i;
                return result;
            }
        }
        return result;
    }


    public long getTotalTime() {
        return totalTime;
    }

    public ArrayList<AppInfo> getShowList() {
        return appList;
    }
}

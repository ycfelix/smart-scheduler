package com.ust.appusagechart;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

public class AppInfo {
    private UsageStats usageStats;
    private String packageName;
    private String label;
    private long UsedTimebyDay;
    private Context context;
    private int times;
    private Bitmap appIcon;
    private Drawable drawableIcon;
    private long lastUsedTime;



    public AppInfo(UsageStats usageStats, Context context) {
        this.usageStats = usageStats;
        this.context = context;

        try {
            GenerateInfo();
        } catch (PackageManager.NameNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            System.out.println("package not found");
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }



    private void GenerateInfo() throws PackageManager.NameNotFoundException, NoSuchFieldException, IllegalAccessException {
        PackageManager packageManager = context.getPackageManager();
        this.packageName = usageStats.getPackageName();
        if (this.packageName != null && !this.packageName.equals("")) {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.packageName, 0);
            this.label = (String) packageManager.getApplicationLabel(applicationInfo);
            this.UsedTimebyDay = usageStats.getTotalTimeInForeground();
            this.times = (Integer) usageStats.getClass().getDeclaredField("mLaunchCount").get(usageStats);
            if (this.UsedTimebyDay > 0) {
                this.drawableIcon=applicationInfo.loadIcon(packageManager);
                this.appIcon=drawableToBitmap(this.drawableIcon);
            }
            this.lastUsedTime=usageStats.getLastTimeUsed();
        }
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public Drawable getDrawableIcon() {
        return drawableIcon;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void setUsedTimebyDay(long usedTimebyDay) {
        this.UsedTimebyDay = usedTimebyDay;
    }

    public Bitmap getAppIcon() {
        return appIcon;
    }

    public long getUsedTimebyDay() {
        return UsedTimebyDay;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    private long timeStampMoveToForeground = -1;

    private long timeStampMoveToBackGround = -1;


    public void setTimeStampMoveToForeground(long timeStampMoveToForeground) {
        this.timeStampMoveToForeground = timeStampMoveToForeground;
    }

    public void timesPlusPlus(){
        times++;
    }

    public void setTimeStampMoveToBackGround(long timeStampMoveToBackGround) {
        this.timeStampMoveToBackGround = timeStampMoveToBackGround;
    }

    public long getTimeStampMoveToForeground() {
        return timeStampMoveToForeground;
    }

    public void calculateRunningTime() {

        if (timeStampMoveToForeground < 0 || timeStampMoveToBackGround < 0) {
            return;
        }

        if (timeStampMoveToBackGround > timeStampMoveToForeground) {
            UsedTimebyDay += (timeStampMoveToBackGround - timeStampMoveToForeground);
            timeStampMoveToForeground = -1;
            timeStampMoveToBackGround = -1;
        }

    }
    public static long bootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }
}

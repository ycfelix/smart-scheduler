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
import android.text.TextUtils;

public class AppInfo {
    private UsageStats usageStats;
    private long UsedTimebyDay;
    private Context context;
    private int t;
    private Bitmap appIcon;
    private Drawable drawableIcon;
    private long lastUsedTime;
    private long timeStampMoveToForeground = -1;
    private long timeStampMoveToBackGround = -1;
    private String pkgname;
    private String label;

    public AppInfo(UsageStats usageStats, Context context) {
        this.usageStats = usageStats;
        this.context = context;

        try {
            generateInfo();
        } catch (Exception e) {
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



    private void generateInfo() throws Exception {
        PackageManager packageManager = context.getPackageManager();
        this.pkgname = usageStats.getPackageName();
        if (!TextUtils.isEmpty(this.pkgname)) {
            ApplicationInfo info = packageManager.getApplicationInfo(this.pkgname, 0);
            this.label = (String) packageManager.getApplicationLabel(info);
            this.UsedTimebyDay = usageStats.getTotalTimeInForeground();
            this.t = (Integer) usageStats.getClass().getDeclaredField("mLaunchCount").get(usageStats);
            if (this.UsedTimebyDay > 0) {
                this.drawableIcon=info.loadIcon(packageManager);
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
        return t;
    }

    public void setTimes(int times) {
        this.t = times;
    }

    public void setUsagePerDay(long usedTimebyDay) {
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
        return pkgname;
    }

    public void setForeground(long timeStampMoveToForeground) {
        this.timeStampMoveToForeground = timeStampMoveToForeground;
    }

    public void timesAdd(){
        t++;
    }

    public void setBackground(long timeStampMoveToBackGround) {
        this.timeStampMoveToBackGround = timeStampMoveToBackGround;
    }

    public long getForeground() {
        return timeStampMoveToForeground;
    }

    public void calRunTime() {
        long t1=timeStampMoveToForeground;
        long t2=timeStampMoveToBackGround;
        if (t1 < 0 || t2 < 0) {
            return;
        }

        if (t2 > t1) {
            UsedTimebyDay += (t2 - t1);
            timeStampMoveToForeground = timeStampMoveToBackGround = -1;
        }

    }
    public static long bootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }
}

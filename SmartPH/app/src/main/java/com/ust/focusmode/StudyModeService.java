package com.ust.focusmode;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.WallpaperManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.focusmode.widget.AutofitTextView;
import com.ust.smartph.FocusModeActivity;
import com.ust.smartph.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class StudyModeService extends Service {

    WindowManager.LayoutParams wl,wl_back;
    WindowManager windowManager;
    PackageManager pm;
    //AlarmManager am;
    SharedPreferences permittedApp,timeLast;
    Calendar saved_calendar,now;

    HomeRecevier innerReceiver = new HomeRecevier();

    TextView quit;
    RelativeLayout rootLayout;
    LinearLayout backLayout;
    RecyclerView permittedApplicationView;
    AutofitTextView timeTextView,dateTextView,message;
    Button backButton;
    ImageView openSettingButton;

    int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
    float x = 0,rx = 0,y = 0, ry = 0;
    long downTime = 0;
    boolean down = false,isRunning = false;
    boolean viewIsCreate = false, broadcastReceiverIsRegist = false, backIsCreate = false,isPlaying = false,isClosing = false;
    public boolean canExit = false;
    List<String> permittedAppPackNameList;
    String packName = "";

    @Override
    public void onCreate() {
        super.onCreate();

        //FocusModeActivity.close();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(innerReceiver, intentFilter);
        broadcastReceiverIsRegist = true;
        //Toast.makeText(this,"started",Toast.LENGTH_SHORT).show();
        initWindow();
    }

    /*@Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {// FIXME : 不准
        //    packName = getTaskPackname(this);
        //    System.out.println(packName);
        //    judgeApp();
        //}
    }

    @Override
    public void onInterrupt() {

    }*/


    public class SelfBind extends Binder
    {
        public StudyModeService getService()
        {
            return StudyModeService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SelfBind();
    }

    @Override
    public void onDestroy() {
        if (broadcastReceiverIsRegist)
        {
            unregisterReceiver(innerReceiver);
            broadcastReceiverIsRegist = false;
        }
        destroyWindow();
        destroyBack();
        Log.e("Q","Quit");
        super.onDestroy();
    }

    public void start()
    {
        isRunning = true;
        init();
        initViews();
        initThread();
        createWindow();
    }

    public void init()
    {
        pm = getPackageManager();
        //am = (AlarmManager) getSystemService(ALARM_SERVICE);
        permittedAppPackNameList = new ArrayList<>();
        timeLast = getSharedPreferences("timeLast",MODE_PRIVATE);
        permittedApp = getSharedPreferences("PermittedAppList",MODE_PRIVATE);
        initData();
        saved_calendar = Calendar.getInstance();
        Log.i("Now Time",saved_calendar.getTime().toString());
        year = timeLast.getInt("year",0);
        month = timeLast.getInt("month",0);
        day = timeLast.getInt("day",0);
        hour = timeLast.getInt("hour",0);
        minute = timeLast.getInt("minute",0);
        second = timeLast.getInt("second",0);
        saved_calendar.set(year,month,day,hour,minute,second);
        Log.i("Until Time",saved_calendar.getTime().toString());
    }

    public void initData()
    {
        permittedAppPackNameList.clear();
        for (String packName : permittedApp.getAll().keySet())
        {
            permittedAppPackNameList.add(packName);
        }
    }

    public void initWindow()
    {
        windowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        wl = new WindowManager.LayoutParams();
        wl.format = PixelFormat.RGBA_8888; //设置图片格式，效果为背景透明
        //wl.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN; //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wl.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wl.gravity = Gravity.LEFT | Gravity.TOP; //调整悬浮窗显示的停靠位置为左侧置顶
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;

        wl_back = new WindowManager.LayoutParams();
        wl_back.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wl_back.format = PixelFormat.RGBA_8888; //设置图片格式，效果为背景透明
        wl_back.gravity = Gravity.LEFT | Gravity.TOP;
        wl_back.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wl_back.height = WindowManager.LayoutParams.WRAP_CONTENT;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        wl_back.y = displayMetrics.heightPixels / 2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            wl_back.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else
        {
            wl.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            wl_back.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        rootLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.focus_mode_layout_float,null);
        backLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.focus_mode_layout_float_back,null);
    }

    public void createWindow()
    {
        try
        {
            if (!viewIsCreate)
            {
                down = false;
                windowManager.addView(rootLayout, wl);
                viewIsCreate = true;
                rootLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                        .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                if (message != null)
                {
                    message.setVisibility(View.GONE);
                }
            }
        }catch (RuntimeException e)
        {
            e.printStackTrace();
            showToast(getResources().getString(R.string.floating));
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //stopService(new Intent(StudyModeService.this,StudyModeService.class));
        }

    }

    public void createBack()
    {
        try
        {
            if (!backIsCreate)
            {
                isPlaying = true;
                windowManager.addView(backLayout, wl_back);
                backIsCreate = true;
                backLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                        .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            }
        }catch (RuntimeException e)
        {
            e.printStackTrace();
            showToast(getResources().getString(R.string.floating));
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //stopService(new Intent(StudyModeService.this,StudyModeService.class));
        }

    }

    public void destroyWindow()
    {
        if (viewIsCreate)
        {
            windowManager.removeView(rootLayout);
            viewIsCreate = false;
        }
    }

    public void destroyBack()

    {
        if (backIsCreate)
        {
            isPlaying = false;
            windowManager.removeView(backLayout);
            backIsCreate = false;
            isClosing = false;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public void initViews()
    {
        timeTextView = rootLayout.findViewById(R.id.time);
        dateTextView = rootLayout.findViewById(R.id.date);
        message = rootLayout.findViewById(R.id.message);
        backButton = backLayout.findViewById(R.id.back);
        permittedApplicationView = rootLayout.findViewById(R.id.app_permitted_view);
        quit = rootLayout.findViewById(R.id.quit);
        openSettingButton = rootLayout.findViewById(R.id.app_open_setting);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(StudyModeService.this);
        rootLayout.setBackground(wallpaperManager.getDrawable());

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        permittedApplicationView.setLayoutManager(staggeredGridLayoutManager);
        final AppIconAdapter appIconAdapter = new AppIconAdapter(this,pm,permittedAppPackNameList);
        appIconAdapter.setOnItemClickListener(new AppIconAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //destroyWindow();
                openApp(permittedAppPackNameList.get(position));
                isPlaying = true;
                //System.out.println(permittedAppPackNameList.get(position));
            }
        });
        appIconAdapter.setOnLongItemClickListener(new AppIconAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View v, int position) {
                if (!(Build.BRAND.equals("oppo") || Build.BRAND.equals("OPPO")))
                {
                    isClosing  = true;
                    Uri packageURI = Uri.parse("package:" + permittedAppPackNameList.get(position));
                    Intent mIt = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                    startActivity(mIt);
                    isPlaying = true;
                }
            }
        });
        permittedApplicationView.setAdapter(appIconAdapter);

        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        rx = event.getRawX();
                        ry = event.getRawY();
                        x = event.getX();
                        y = event.getY();
                        downTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //System.out.println(event.getRawX() + " " + wl_back.x);
                        wl_back.x = (int) (event.getRawX() - x);
                        float rawY = event.getRawY();
                        if (rawY - y >= getStatusBarHeight())
                        {
                            wl_back.y = (int) (rawY - y);
                        } else
                        {
                            wl_back.y = getStatusBarHeight();
                        }
                        windowManager.updateViewLayout(backLayout,wl_back);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - downTime < 500 && (Math.abs(event.getRawX()) - Math.abs(rx) < 10 && Math.abs(event.getRawY()) - Math.abs(ry) < 10))
                        {
                            //Toast.makeText(StudyModeService.this,"clicked",Toast.LENGTH_SHORT).show();
                            createWindow();
                            Intent mActivity = new Intent(StudyModeService.this, FocusModeActivity.class);
                            mActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mActivity);
                        }
                        break;
                }
                return false;
            }
        });


        quit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (down && canExit)
                {
                    quit();
                    stopService(new Intent(StudyModeService.this,StudyModeService.class));
                }
                return true;
            }
        });

        timeTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    down = true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    down = false;
                }
                return true;
            }
        });

        openSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appIconAdapter.changeDelStatus();
            }
        });
    }

    public void initThread()
    {
		@SuppressLint("HandlerLeak") final Handler handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
			    if (isPlaying)
                {
                    packName = getTaskPackname();
                    judgeApp();
                }
				//t.setText(saved_calendar.getTime() + "\n" + now.getTime() + "\n" + now.after(saved_calendar));
                int hour = now.get(Calendar.HOUR),min = now.get(Calendar.MINUTE);
			    if (hour == 0) hour = 12;
			    String m = String.valueOf(min);
			    if (min < 10) m = "0" + m;
                timeTextView.setText(hour + ":" + m);
                dateTextView.setText(now.get(Calendar.YEAR) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.DAY_OF_MONTH));
			}
		};

        Thread thread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                while (isRunning)
                {
                    now = Calendar.getInstance();
                    if (now.after(saved_calendar))
                    {
                        quit();
                        break;
                    }
                    handler.sendMessage(new Message());
                    try
                    {
                        sleep(1000);
                    } catch (InterruptedException e)
                    {

                    }
                }
                //System.exit(0);
                if (Build.VERSION.SDK_INT >= 24)
                {
                    //disableSelf();
                }
                stopService(new Intent(StudyModeService.this,StudyModeService.class));
            }
        };
        thread.start();
    }

    void quit()
    {
        isRunning = false;
        Log.i("Now Time",now.getTime().toString());
        if (broadcastReceiverIsRegist)
        {
            unregisterReceiver(innerReceiver);
            broadcastReceiverIsRegist = false;
        }
        destroyWindow();
        destroyBack();
    }

    void judgeApp()
    {
        if (permittedAppPackNameList.contains(packName) || packName.equals(getDefaultInputMethodPkgName(this)) || packName.equals("android") || (packName.equals("com.android.settings") && isClosing))
        {
            createBack();
            destroyWindow();
        } else
        {
            createWindow();
            destroyBack();
        }
    }

    public void openApp(String packName)
    {
        Intent intent = pm.getLaunchIntentForPackage(packName);
        if (intent != null)
        {
            message.setVisibility(View.VISIBLE);
            Intent mActivity = new Intent(StudyModeService.this, FocusModeActivity.class);
            mActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.putExtra("packName",packName);
            //startActivity(mActivity);
            startActivity(intent);
            this.packName = getTaskPackname();
            judgeApp();
        } else
        {
            Toast.makeText(this,getResources().getString(R.string.error_app_null), Toast.LENGTH_SHORT).show();
        }
    }

    static class AppIconAdapter extends RecyclerView.Adapter<AppIconAdapter.ViewBundle>
    {
        List<String> data;
        Context context;
        PackageManager pm;
        OnItemClickListener onItemClickListener;
        OnLongItemClickListener onLongItemClickListener;

        boolean isDeleteing = false;

        public AppIconAdapter(Context context, PackageManager pm, List<String> data)
        {
            this.data = data;
            this.context = context;
            this.pm = pm;
        }

        @Override
        public ViewBundle onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.focus_mode_grid_item,null);
            ViewBundle viewBundle = new ViewBundle(view);
            return viewBundle;
        }

        @Override
        public void onBindViewHolder(ViewBundle viewBundle, final int position) {
            String name = getAppName(data.get(position));
            if (name.equals(""))
            {
                viewBundle.appName.setText(context.getResources().getString(R.string.error_app_null));
                viewBundle.appIcon.setBackgroundResource(R.mipmap.ic_launcher_round);
            }else
            {
                viewBundle.appName.setText(name);
                viewBundle.appIcon.setBackground(getAppIcon(data.get(position)));
                viewBundle.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v, position);
                    }
                });
                viewBundle.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onLongItemClickListener.onLongItemClick(view,position);
                        return true;
                    }
                });
            }
            if (isDeleteing)
            {
                viewBundle.delButton.setVisibility(View.VISIBLE);
            }else
            {
                viewBundle.delButton.setVisibility(View.GONE);
            }
            viewBundle.delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO:
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void changeDelStatus()
        {
            isDeleteing = ! isDeleteing;
            notifyDataSetChanged();
        }

        public Drawable getAppIcon(String packname)
        {
            try {
                ApplicationInfo info = pm.getApplicationInfo(packname, 0);
                return info.loadIcon(pm);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getAppName(String packname){
            try {
                ApplicationInfo info = pm.getApplicationInfo(packname, 0);
                return info.loadLabel(pm).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        }

        class ViewBundle extends RecyclerView.ViewHolder
        {
            ImageView appIcon;
            AutofitTextView appName;
            View view;
            ImageView delButton;

            public ViewBundle(View view)
            {
                super(view);
                this.view = view;
                appIcon = view.findViewById(R.id.appIcon);
                appName = view.findViewById(R.id.appName);
                delButton = view.findViewById(R.id.delPermittedApp);
            }

        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener)
        {
            this.onItemClickListener = onItemClickListener;
        }

        public void setOnLongItemClickListener(OnLongItemClickListener onLongItemClickListener)
        {
            this.onLongItemClickListener = onLongItemClickListener;
        }

        interface OnItemClickListener
        {
            void onItemClick(View v, int position);
        }

        interface OnLongItemClickListener
        {
            void onLongItemClick(View v, int position);
        }
    }

    private String getDefaultInputMethodPkgName(Context context) {
        String mDefaultInputMethodPkg = null;

        String mDefaultInputMethodCls = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        //输入法类名信息
        if (!TextUtils.isEmpty(mDefaultInputMethodCls)) {
            //输入法包名
            mDefaultInputMethodPkg = mDefaultInputMethodCls.split("/")[0];
        }
        return mDefaultInputMethodPkg;
    }

    public String getTaskPackname() {
        String currentApp = "null";
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && hasModule()) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.i("APP", currentApp);
        return currentApp;
    }

    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = this.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public void showToast(String str)
    {
        Toast t = new Toast(this);
        LinearLayout l = new LinearLayout(this);
        l.setBackgroundColor(Color.parseColor("#FF7269"));
        TextView tv = new TextView(this);
        tv.setText(str);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(20);
        l.setGravity(Gravity.CENTER);
        float scale = this.getResources().getDisplayMetrics().density;
        int a = (int) (5 * scale + 0.5f);
        l.setPadding(a,a,a,a);
        l.addView(tv);
        t.setView(l);
        t.setDuration(Toast.LENGTH_SHORT);
        t.show();
    }

    private boolean hasModule() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    class HomeRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {

                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        //Toast.makeText(getApplicationContext(),SYSTEM_DIALOG_REASON_HOME_KEY,Toast.LENGTH_SHORT).show();
                        //直接进入学习模式
                        enter();
                    }
                    if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //Toast.makeText(getApplicationContext(),SYSTEM_DIALOG_REASON_RECENT_APPS,Toast.LENGTH_SHORT).show();
                        //允许
                        enter();
                    }
                }
            }
        }

        public void enter()
        {
            if (isRunning)
            {
                createWindow();
                destroyBack();
                Intent mActivity = new Intent(StudyModeService.this, FocusModeActivity.class);
                mActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mActivity);
            }
        }
    }
}

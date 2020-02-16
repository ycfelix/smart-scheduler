package com.ust.smartph;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ust.focusmode.StudyModeService;
import com.ust.focusmode.widget.*;
import com.ust.focusmode.widget.NumberPicker;

public class FocusModeActivity extends AppCompatActivity {

    PackageManager pm;
    SharedPreferences permittedAppCheckState, timeLast;
    SharedPreferences.Editor editor_permittedAppCheckState, editor_timeLast;
    StudyModeService bindedService;
    ServiceConnection serviceConnection;
    List<String> list_packName = new ArrayList<>();
    Map<String, Boolean> state_check = new HashMap<>();

    boolean isOpenTimeBundle;
    boolean timeRun;
    boolean isForceExitOn = false;

    static FocusModeActivity myActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.focus_mode_page);

        myActivity = this;

        // Init vars
        pm = getPackageManager();
        permittedAppCheckState = getSharedPreferences("PermittedAppList", MODE_PRIVATE);
        timeLast = getSharedPreferences("timeLast", MODE_PRIVATE);

        editor_permittedAppCheckState = permittedAppCheckState.edit();
        editor_timeLast = timeLast.edit();

        // Init List
        initData(state_check, list_packName, pm);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Bind service
        this.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                bindedService = ((StudyModeService.SelfBind) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent(this, StudyModeService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        startService(intent);

        // Bind Views
        Button start = findViewById(R.id.start);

        // Bind click listener
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasModule()) {
                    if (hasEnable()) {
                        if (!Settings.canDrawOverlays(FocusModeActivity.this)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivity(intent);
                            showToast(getResources().getString(R.string.floating));
                        } else requestPermission();
                    } else {
                        showToast(getResources().getString(R.string.usage));
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this.serviceConnection);
    }

    public void requestPermission() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder getPermissionBuilder = new AlertDialog.Builder(this);
            final AlertDialog getPermission = getPermissionBuilder
                    .setPositiveButton(getString(R.string.ok), null)
                    .setMessage(getString(R.string.permission_get))
                    .setCancelable(false).create();
            getPermission.show();
            getPermission.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        boolean isTip = shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (isTip) {// 表明用户没有彻底禁止弹出权限请求
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                        } else {// 表明用户已经彻底禁止弹出权限请求
                            // 这里一般会提示用户进入权限设置界面
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                            startActivity(intent);
                            showToast(getString(R.string.permission_get_setting));
                        }
                    } else {
                        showSettingDialog();
                        getPermission.dismiss();
                    }
                }
            });
        } else showSettingDialog();
    }

    public void showSettingDialog() {
        isOpenTimeBundle = true;

        // Root layout
        RelativeLayout dialogLayout = (RelativeLayout) LayoutInflater.from(FocusModeActivity.this).inflate(R.layout.focus_mode_layout_setting, null);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(FocusModeActivity.this);
        builder.setView(dialogLayout).setTitle(R.string.setting).create();
        builder.setNegativeButton(R.string.cancel, null);

        // Bind Views
        Button btn_permittedAppSetting = dialogLayout.findViewById(R.id.btn_setting_app_permit);
        final RelativeLayout timeBundle = dialogLayout.findViewById(R.id.timeBundle);
        final ListView listview_application = dialogLayout.findViewById(R.id.listview_app);
        final SwipeRefreshLayout swipeRefreshLayout = dialogLayout.findViewById(R.id.freshData_swipeRefreshLayout);
        final NumberPicker numPicker_h = dialogLayout.findViewById(R.id.numPick_h);
        final NumberPicker numPicker_m = dialogLayout.findViewById(R.id.numPick_m);
        AppCompatImageButton forceExitHelpButton = dialogLayout.findViewById(R.id.help_forceExit);
        AppCompatCheckBox checkBox_forceExit = dialogLayout.findViewById(R.id.checkbox_forceExit);

        checkBox_forceExit.setChecked(isForceExitOn);

        // Bind click listener
        btn_permittedAppSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenTimeBundle) {
                    timeBundle.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                } else {
                    timeBundle.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                }
                isOpenTimeBundle = !isOpenTimeBundle;
            }
        });

        String[] values_arr = new String[9];

        for (int i = 0; i <= 8; i++) {
            values_arr[i] = String.valueOf(i);
        }
        numPicker_h.setDisplayedValues(values_arr);
        numPicker_h.setShowCount(5);
        numPicker_h.setMaxValue(8);
        numPicker_h.setMinValue(0);

        values_arr = new String[60];
        for (int i = 0; i <= 59; i++) {
            values_arr[i] = String.valueOf(i);
        }
        numPicker_m.setDisplayedValues(values_arr);
        numPicker_m.setShowCount(5);
        numPicker_m.setMaxValue(59);
        numPicker_m.setMinValue(0);

        numPicker_h.setValue(timeLast.getInt("h", 0));
        numPicker_m.setValue(timeLast.getInt("m", 15));

        numPicker_h.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                editor_timeLast.putInt("h", newVal);
                editor_timeLast.commit();
            }
        });
        numPicker_m.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                editor_timeLast.putInt("m", newVal);
                editor_timeLast.commit();
            }
        });

        // Init listview
        final ListViewAdapter listViewAdapter = new ListViewAdapter(FocusModeActivity.this, pm, list_packName, state_check);
        listview_application.setAdapter(listViewAdapter);
        //listview_application.setFastScrollEnabled(true);
        listview_application.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewAdapter.ViewBundle viewBundle = (ListViewAdapter.ViewBundle) view.getTag();
                String packName = list_packName.get(position);
                if (!getAppName(pm, packName).equals("")) {
                    if (state_check.get(packName)) {
                        viewBundle.check_app_permitted.setChecked(false);//Only display, can't used for data
                        state_check.put(packName, false);
                        editor_permittedAppCheckState.remove(packName);
                    } else {
                        viewBundle.check_app_permitted.setChecked(true);//Only display, can't used for data
                        state_check.put(packName, true);
                        editor_permittedAppCheckState.putBoolean(packName, true);
                    }
                    editor_permittedAppCheckState.commit();
                } else listViewAdapter.notifyDataSetChanged();
            }
        });

        forceExitHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FocusModeActivity.this);
                builder.setMessage(R.string.forceExitHelp);
                builder.create().show();
            }
        });

        checkBox_forceExit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isForceExitOn = b;
            }
        });

        // Init swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        initData(state_check, list_packName, pm);
                        listViewAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        // Bind click listener
        builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //final int h = timeLast.getInt("h",0), m = timeLast.getInt("m",15);
                final int h = numPicker_h.getValue(), m = numPicker_m.getValue();
                editor_timeLast.putInt("h", h);
                editor_timeLast.putInt("m", m);
                editor_timeLast.commit();
                if (h == 0 && m == 0) {
                    showToast(getResources().getString(R.string.error_time_null));
                } else {
                    AlertDialog.Builder checkDialog = new AlertDialog.Builder(FocusModeActivity.this);
                    checkDialog.setTitle(getResources().getString(R.string.confirm));
                    ScrollView messageLayout = (ScrollView) LayoutInflater.from(FocusModeActivity.this).inflate(R.layout.focus_mode_layout_message, null);
                    checkDialog.setView(messageLayout);

                    TextView lastTimeTextView = messageLayout.findViewById(R.id.time_last_textView);
                    final TextView untilTimeTextView = messageLayout.findViewById(R.id.time_until_textView);
                    TextView permittedApplicationTextView = messageLayout.findViewById(R.id.app_permitted_textView);

                    String app = "";
                    Map<String, ?> map = permittedAppCheckState.getAll();
                    boolean tmp_appListFirst = true;
                    if (map.size() == 0) {
                        app = getResources().getString(R.string.nil);
                    } else for (String packName : map.keySet()) {
                        if (tmp_appListFirst) {
                            app = app + getAppName(pm, packName);
                            tmp_appListFirst = false;
                        } else {
                            app = app + "\n" + getAppName(pm, packName);
                        }

                    }
                    lastTimeTextView.setText(h + " " + getResources().getString(R.string.hours) + " " + m + " " + getResources().getString(R.string.mins));
                    untilTimeTextView.setText(calDateToString(calDate(h, m)));
                    permittedApplicationTextView.setText(app);

                    checkDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showSettingDialog();
                        }
                    });
                    checkDialog.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int[] date = calDate(h, m);
                            editor_timeLast.putInt("year", date[0]);
                            editor_timeLast.putInt("month", date[1]);
                            editor_timeLast.putInt("day", date[2]);
                            editor_timeLast.putInt("hour", date[3]);
                            editor_timeLast.putInt("minute", date[4]);
                            editor_timeLast.putInt("second", date[5]);
                            editor_timeLast.commit();
                            // Start Service
                            bindedService.canExit = isForceExitOn;
                            bindedService.start();
                            //Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            //intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            //startActivity(intent);


                        }
                    });

                    timeRun = true;
                    checkDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            timeRun = false;
                        }
                    });

                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            untilTimeTextView.setText(calDateToString(calDate(h, m)));
                        }
                    };

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            while (timeRun) {
                                handler.sendMessage(new Message());
                                try {
                                    sleep(1000);
                                } catch (InterruptedException e) {

                                }
                            }
                        }
                    }.start();
                    checkDialog.create().show();
                }
            }
        });
        builder.show();
    }

    public void initData(Map<String, Boolean> state_check, List<String> list_packName, PackageManager pm) {
        // Init List
        list_packName.clear();

        Map<String, String> map_appName_and_packName = new HashMap<String, String>();
        List<String> list_appName_and_packName = new ArrayList<String>();

        for (PackageInfo packageInfo : pm.getInstalledPackages(0)) {
            String packName = packageInfo.applicationInfo.packageName;
            if (pm.getLaunchIntentForPackage(packName) != null && !(packName.equals(getPackageName()))) {
                String appName = getAppName(pm, packName);
                map_appName_and_packName.put(appName + packName, packName);
                list_appName_and_packName.add(appName + packName);
            }
        }
        Collections.sort(list_appName_and_packName);
        for (String name : list_appName_and_packName) {
            list_packName.add(map_appName_and_packName.get(name));
        }
        map_appName_and_packName = null;
        list_appName_and_packName = null;

        // Init Map
        state_check.clear();
        for (String packName : list_packName) {
            state_check.put(packName, false);
        }

        Map<String, ?> state_check_saved = permittedAppCheckState.getAll();
        for (String packName : state_check_saved.keySet()) {
            if (state_check.containsKey(packName)) {
                state_check.put(packName, true);
            } else {
                editor_permittedAppCheckState.remove(packName);
            }
        }
        editor_permittedAppCheckState.commit();
    }

    public int[] calDate(int h, int m) {
        Calendar calendar = Calendar.getInstance();
        //int currentH = calendar.get(Calendar.HOUR_OF_DAY), currentM = calendar.get(Calendar.MINUTE), currentS = calendar.get(Calendar.SECOND);
        //int currentDay = calendar.get(Calendar.DAY_OF_MONTH), currentMon = calendar.get(Calendar.MONTH) + 1, currentYear = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.HOUR_OF_DAY, h);
        calendar.add(Calendar.MINUTE, m);
        //showToast(currentH + " "  + currentM + " " + currentS + "\n" + currentYear + " " + currentMon + " " + currentDay);
        int nowH = calendar.get(Calendar.HOUR_OF_DAY), nowM = calendar.get(Calendar.MINUTE), nowS = calendar.get(Calendar.SECOND);
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH), nowMon = calendar.get(Calendar.MONTH), nowYear = calendar.get(Calendar.YEAR);
        return new int[]{nowYear, nowMon, nowDay, nowH, nowM, nowS};
    }

    public String calDateToString(int[] calDate) {
        return calDate[0] + "/" + (calDate[1] + 1) + "/" + calDate[2] + "\n" + calDate[3] + ":" + calDate[4];
    }

    public void showToast(String str) {
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
        l.setPadding(a, a, a, a);
        l.addView(tv);
        t.setView(l);
        t.setDuration(Toast.LENGTH_SHORT);
        t.show();
    }

    public String getAppName(PackageManager pm, String packname) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    class ListViewAdapter extends BaseAdapter {
        List<String> data_packname;
        Context context;
        PackageManager pm;
        Map<String, Boolean> data_state_check;

        class ViewBundle {
            ImageView icon_app;
            AutofitTextView name_app;
            CheckBox check_app_permitted;
        }

        public ListViewAdapter(Context context, PackageManager pm, List<String> data_packname, Map<String, Boolean> data_state_check) {
            this.context = context;
            this.data_packname = data_packname;
            this.data_state_check = data_state_check;
            this.pm = pm;
        }

        @Override
        public Object getItem(int position) {
            return data_packname.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return data_packname.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewBundle viewBundle;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.focus_mode_list_item, null);
                viewBundle = new ViewBundle();
                viewBundle.icon_app = convertView.findViewById(R.id.appIcon);
                viewBundle.name_app = convertView.findViewById(R.id.appName);
                viewBundle.check_app_permitted = convertView.findViewById(R.id.check);
                viewBundle.check_app_permitted.setFocusable(false);
                viewBundle.check_app_permitted.setClickable(false);
                convertView.setTag(viewBundle);
            } else {
                viewBundle = (ViewBundle) convertView.getTag();
            }
            //Set data
            String name = getAppName(data_packname.get(position));
            if (name.equals("")) {
                viewBundle.name_app.setText(getResources().getString(R.string.error_app_null));
                viewBundle.icon_app.setBackgroundResource(R.mipmap.ic_launcher_round);
                viewBundle.check_app_permitted.setChecked(false);
            } else {
                viewBundle.name_app.setText(name);
                viewBundle.icon_app.setBackground(getAppIcon(data_packname.get(position)));
                viewBundle.check_app_permitted.setChecked(data_state_check.get(data_packname.get(position)));
            }
            return convertView;
        }

        public Drawable getAppIcon(String packname) {
            try {
                ApplicationInfo info = pm.getApplicationInfo(packname, 0);
                return info.loadIcon(pm);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getAppName(String packname) {
            try {
                ApplicationInfo info = pm.getApplicationInfo(packname, 0);
                return info.loadLabel(pm).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    private boolean hasModule() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private boolean hasEnable() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        return !(queryUsageStats == null || queryUsageStats.isEmpty());
    }

    public static void close() {
        if (myActivity != null) {
            myActivity.finish();
        }
    }
}

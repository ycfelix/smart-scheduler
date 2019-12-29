package com.ust.checklist;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import com.ust.smartph.R;
/**
 * Created by Jordan on 1/13/2016.
 *
 * This is a singleton that contains the settings for reminders.
 */
public class ReminderSettings
{
    private static final String PREFS_NAME = "settings";
    private static final String PREFS_ENABLED = "settings.enabled";
    private static final String PREFS_INTERVAL = "settings.interval";

    private static final int THIRTY_MINUTES = R.id.radioButton_thirty_minutes;
    private static final int ONE_HOUR = R.id.radioButton_one_hour;
    private static final int TWO_HOURS = R.id.radioButton_two_hours;
    private static final int FOUR_HOURS = R.id.radioButton_four_hours;
    private static final int EIGHT_HOURS = R.id.radioButton_eight_hours;
    private static final int TWELVE_HOURS = R.id.radioButton_twelve_hours;
    private static final int TWENTY_FOUR_HOURS = R.id.radioButton_twenty_four_hours;

    private static ReminderSettings sReminderSettings;

    private boolean mIsEnabled;

    private int mReminderInterval;

    private Context mAppContext;

    private Map<Integer, Long> mIntervalMap;

    private ReminderSettings(Context context)
    {
        mAppContext = context.getApplicationContext();

        //Load user preferences.
        SharedPreferences settings = mAppContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        //Update settings.
        mIsEnabled = settings.getBoolean(PREFS_ENABLED,false);
        mReminderInterval = settings.getInt(PREFS_INTERVAL,THIRTY_MINUTES);

        //Map Keys to values.
        mIntervalMap = new HashMap<>();
        mIntervalMap.put(THIRTY_MINUTES,30*60*1000L);
        mIntervalMap.put(ONE_HOUR,60*60*1000L);
        mIntervalMap.put(TWO_HOURS,120*60*1000L);
        mIntervalMap.put(FOUR_HOURS,240*60*1000L);
        mIntervalMap.put(EIGHT_HOURS,480*60*1000L);
        mIntervalMap.put(TWELVE_HOURS,720*60*1000L);
        mIntervalMap.put(TWENTY_FOUR_HOURS,1440*60*1000L);
    }

    public static ReminderSettings get(Context c)
    {
        if(sReminderSettings == null)
        {
            sReminderSettings = new ReminderSettings(c.getApplicationContext());
        }
        return sReminderSettings;
    }

    public boolean isEnabled()
    {
        return mIsEnabled;
    }
    public int getReminderInterval()
    {
        return mReminderInterval;
    }
    public void setEnabled(boolean isEnabled)
    {
        //Accept the change.
        mIsEnabled = isEnabled;
    }
    public void setReminderInterval(int interval)
    {
        //Accept the change.
        mReminderInterval = interval;
    }
    public void saveSettings()
    {
        //Save the current settings.
        SharedPreferences settings = mAppContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFS_ENABLED,mIsEnabled);
        editor.putInt(PREFS_INTERVAL, mReminderInterval);
        editor.apply();
    }

    public long getTimeInMillis()
    {
        return mIntervalMap.get(mReminderInterval);
    }
}

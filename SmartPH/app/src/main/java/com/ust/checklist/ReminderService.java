package com.ust.checklist;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.ust.smartph.ChecklistActivity;
import com.ust.smartph.R;
/**
 * Created by Jordan on 1/12/2016.
 *
 * This is the service to notify users of Items they which to be notified about.
 * Don't forget to enable this service in manifest.
 */
public class ReminderService extends IntentService
{
    private static final String TAG = "ReminderService";
    private static final String NUMBER_UNCHECKED = "reminderservice.numberunchecked";

    //Default constructor.
    public ReminderService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //Get the last known number of unchecked items.
        int numUnchecked = intent.getIntExtra(ReminderService.NUMBER_UNCHECKED,0);

        PendingIntent pi = PendingIntent.getActivity(this,0,new Intent(this, ChecklistActivity.class),0);

        //Start building the notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_done_white_24dp);
        builder.setAutoCancel(true);
        builder.setContentTitle("Check List");
        builder.setContentIntent(pi);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));

        if(numUnchecked > 1)
        {
            builder.setContentText("You have " + numUnchecked + " items unchecked.");
        }
        else if(numUnchecked == 1)
        {
            builder.setContentText("You have " + numUnchecked + " item unchecked.");
        }
        else
        {
            builder.setContentText("Your list is completely checked!");
        }

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0,notification);
    }

    public static void setServiceAlarm(Context context, boolean isOn, long intervalInMillis, int numUnchecked)
    {
        //Set up the reminder service intent.
        Intent i = new Intent(context,ReminderService.class);
        i.putExtra(NUMBER_UNCHECKED, numUnchecked);

        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if(isOn)
        {
            //Create the new alarm.
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+intervalInMillis,intervalInMillis,pi);
        }
        else
        {
            //Cancel the alarm and the pending intent.
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context)
    {
        Intent i = new Intent(context,ReminderService.class);
        PendingIntent pi = PendingIntent.getService(context,0,i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}

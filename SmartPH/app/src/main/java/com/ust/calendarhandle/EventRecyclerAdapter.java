package com.ust.calendarhandle;
//Event reveal
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.smartph.AlarmReceiver;
import com.ust.smartph.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Events> arrayList;
    DBOpenHelper dbOpenHelper;
    private OnRecyclerListerner monRecyclerListerner;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList, OnRecyclerListerner onRecyclerListerner) {
        this.context = context;
        this.arrayList = arrayList;
        this.monRecyclerListerner = onRecyclerListerner;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_firstrowlayout,parent,false);
        return new MyViewHolder(view, monRecyclerListerner);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.DateTxt.setText(events.getTIME());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView DateTxt, Event;
        OnRecyclerListerner onRecyclerListerner;

        public MyViewHolder(@NonNull View itemView, OnRecyclerListerner onRecyclerListerner) {
            super(itemView);
            DateTxt = itemView.findViewById(R.id.timeevent);
            Event = itemView.findViewById(R.id.eventtitle);
            this.onRecyclerListerner = onRecyclerListerner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyclerListerner.onRecyclerClick(getAdapterPosition());
        }
    }

    public interface  OnRecyclerListerner{
        void onRecyclerClick(int position);
    }

    //Convert the String to date
    public Date ConvertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try{
            date = format.parse(eventDate);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return  date;
    }

    //Convert the String to time
    public Date ConvertStringToTime(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        Date date = null;
        try{
            date = format.parse(eventDate);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return  date;
    }

    //Delete the event
    public  void deleteCalendarEvent(String event, String date, String time, String type){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event,date,time,type,database);
        dbOpenHelper.close();
    }

    //Check whether the event need to alarm
    public boolean isAlarmed(String date, String event, String time){
        boolean alarmed = false;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,event,time,database);
        while(cursor.moveToNext()){
            String notify = cursor.getString(cursor.getColumnIndex(DBStructure.Notify));
            if(notify.equals("on")){
                alarmed = true;
            }else{
                alarmed = false;
            }
        }
        cursor.close();
        dbOpenHelper.close();
        return alarmed;
    }

    public void setAlarm(Calendar calendar, String event, String time, int RequesCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("event",event);
        intent.putExtra("time",time);
        intent.putExtra("id",RequesCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,RequesCode,intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    public void cancelAlarm(int RequesCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,RequesCode,intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public int getRequestCode(String date, String event, String time){
        int code = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,event,time,database);
        while(cursor.moveToNext()){
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dbOpenHelper.close();
        return code;
    }

    public void updateEvent(String date, String event, String time, String notify, String type){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEvent(date,event,time,notify, type,database);
        dbOpenHelper.close();
    }

}

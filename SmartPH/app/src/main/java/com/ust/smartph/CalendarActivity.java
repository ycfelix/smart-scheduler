package com.ust.smartph;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ust.calendarhandle.ActivityTypeKeys;
import com.ust.calendarhandle.DBOpenHelper;
import com.ust.calendarhandle.DBStructure;
import com.ust.calendarhandle.EventRecyclerAdapter;
import com.ust.calendarhandle.Events;
import com.ust.calendarhandle.ViewAdapter;
import com.ust.calendarhandle.ieEvent;
import com.ust.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalendarActivity extends AppCompatActivity implements EventRecyclerAdapter.OnRecyclerListerner{
    private static final String TAG = "Position: ";
    CalendarView simpleCalendarView;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton ImportCalendar,ExportCalendar,addEvent,suggesEvent, Mapfuntion, DeleteAllEvnet;
    AlertDialog alertDialog, newalertDialog;
    DBOpenHelper dbOpenHelper;
    List<Events> eventsList = new ArrayList<>();
    List<ieEvent> imexportList = new ArrayList<>();
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    SimpleDateFormat eventDateFormate = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
    RecyclerView recyclerView;
    List<ImageView> mActivityTypeViews;
    ArrayList<Events> brandsList = new ArrayList<>();
    ArrayList<Events> storewhole = new ArrayList<>();
    ArrayList<String> userList = new ArrayList<>();
    int alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinute;
    int presentday,presentmonth,presentyear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initializelayout();

        //Calendar view
        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
                //Date currentTime = Calendar.getInstance().getTime();
                //System.out.println(currentTime);
                presentday = dayOfMonth;
                presentmonth = month;
                presentyear = year;
                initializeShowEventLayout();
                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + (month+1) + "/" + year, Toast.LENGTH_LONG).show();
            }
        });




        //Floating button functions
        //Mapfunction

        Mapfuntion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
                String emailStr = sp.getString("email", null);
                //String passStr = sp.getString("hashed_pwd", null);
                System.out.println("my email: "+emailStr);
                Intent mapIntent = new Intent(view.getContext(), OpenMapActivity.class);
                mapIntent.putExtra("emailStr", emailStr);
                startActivity(mapIntent);
            }
        });
        //Delete All Event
        DeleteAllEvnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteAllEvent();
                initializeShowEventLayout();
            }
        });
        //SuggestEvent UI
        suggesEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context suggesContext = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(suggesContext);
                builder.setCancelable(true);
                final View suggestView = LayoutInflater.from(v.getContext()).inflate(R.layout.suggest_layout,null);
                final Button go = suggestView.findViewById(R.id.search);
                Button back = suggestView.findViewById(R.id.back);
                final ListView selection = (ListView) suggestView.findViewById(R.id.listview);
                Calendar suggestdate = Calendar.getInstance();
                suggestdate.set(Calendar.MONTH,presentmonth);
                suggestdate.set(Calendar.YEAR,presentyear);
                suggestdate.set(Calendar.DAY_OF_MONTH,presentday);
                final String date = eventDateFormate.format(suggestdate.getTime());
                final String month = monthFormat.format(suggestdate.getTime());
                final String year = yearFormat.format(suggestdate.getTime());
               /* int num = 3;
                ArrayList<String> userList = getSuggestEvent(num,suggesContext);*/
               for(String userid : userList){
                    getSuggestedEvent(userid, suggesContext);
                }
                go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        brandsList.clear();
                        Spinner spinner = suggestView.findViewById(R.id.type);
                        //TextView brands = (TextView) findViewById(R.id.ActivityType);
                        final String activityType = String.valueOf(spinner.getSelectedItem());
                        //System.out.println("UserId:"+userList);
                        //System.out.println("StoreWhole:"+storewhole);
                        if (activityType.equals("Other")) {

                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Other")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Work")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Work")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Date")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Date")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Sport")){

                           for(Events newevent : storewhole){
                               if(newevent.getType().equals("Sport")){
                                   brandsList.add(newevent);
                               }
                           }
                        }
                        else if(activityType.equals("Reading")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Reading")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Travel")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Travel")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Volunteer")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Volunteer")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Study")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Study")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Shopping")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Shopping")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else if(activityType.equals("Chill")){
                            for(Events newevent : storewhole){
                                if(newevent.getType().equals("Chill")){
                                    brandsList.add(newevent);
                                }
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please select a type", Toast.LENGTH_LONG).show();
                        }
                        LayoutInflater inflater = (LayoutInflater) suggesContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        ViewAdapter adapter = new ViewAdapter(brandsList, inflater);
                        selection.setAdapter(adapter);
                        selection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Events selectedItem = (Events) parent.getItemAtPosition(position);
                                System.out.println(selectedItem);
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                builder1.setCancelable(true);
                                final View suggestOuterView = LayoutInflater.from(view.getContext()).inflate(R.layout.show_suggestevents_rowlayout,null);
                                final EditText eventname = suggestOuterView.findViewById(R.id.eventname);
                                final TextView eventtime = suggestOuterView.findViewById(R.id.eventtime);
                                Button add = suggestOuterView.findViewById(R.id.add);
                                ImageButton timeicon = suggestOuterView.findViewById(R.id.seteventtime);
                                eventname.setText(selectedItem.getEVENT());
                                timeicon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Calendar calendar = Calendar.getInstance();
                                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                        int minuts = calendar.get(Calendar.MINUTE);
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(suggestOuterView.getContext(), R.style.Theme_AppCompat_Dialog
                                                , new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                Calendar c = Calendar.getInstance();
                                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                                c.set(Calendar.MINUTE,minute);
                                                c.setTimeZone(TimeZone.getDefault());
                                                SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                                String event_Time = hformate.format(c.getTime());
                                                eventtime.setText(event_Time);
                                            }
                                        },hours,minuts,false);
                                        timePickerDialog.show();
                                    }
                                });

                                add.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SaveEvent(eventname.getText().toString(),eventtime.getText().toString(),date,month,year,"off", activityType);
                                        System.out.println("Add successfully");
                                        newalertDialog.dismiss();
                                        alertDialog.dismiss();
                                        initializeShowEventLayout();
                                    }
                                });

                                builder1.setView(suggestOuterView);
                                newalertDialog = builder1.create();
                                newalertDialog.show();
                            }
                        });
                    }
                });

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                builder.setView(suggestView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //Import function
        ImportCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context importContext = view.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(importContext);
                builder.setCancelable(true);
                final View importView = LayoutInflater.from(view.getContext()).inflate(R.layout.import_calendar_layout,null);
                final EditText otherUserId = importView.findViewById(R.id.import_other_id);
                Button importOwn = importView.findViewById(R.id.import_own);
                Button importOther = importView.findViewById(R.id.import_other);
                importOwn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            setAddEvent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        alertDialog.dismiss();
                    }
                });
                importOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String otherUserIdString = otherUserId.getText().toString();
                        if(TextUtils.isEmpty(otherUserIdString)){
                            otherUserId.setError("Please enter a user ID");
                        }
                        else{
                            try {
                                setAddOtherUserEvent(otherUserIdString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            alertDialog.dismiss();
                        }
                    }
                });
                builder.setView(importView);
                alertDialog = builder.create();
                alertDialog.show();
            }

        });

        //Export function
        ExportCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new Gson();
                imexportList.clear();
                dbOpenHelper = new DBOpenHelper(view.getContext());
                SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                //Cursor cursor = dbOpenHelper.ReadEventsperMonth(monthFormat.format(simpleCalendarView.getDate()),yearFormat.format(simpleCalendarView.getDate()),database);
                Cursor cursor = dbOpenHelper.ReadAllEvents(database);
                while(cursor.moveToNext()){
                    String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
                    String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
                    String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
                    String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
                    String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
                    String type = cursor.getString(cursor.getColumnIndex(DBStructure.TYPE));
                    String ID = cursor.getString(cursor.getColumnIndex(DBStructure.ID));
                    String Notify = cursor.getString(cursor.getColumnIndex(DBStructure.Notify));
                    ieEvent ieevents = new ieEvent(event,time,date,month,Year,type,ID,Notify);
                    System.out.println(ID);
                    imexportList.add(ieevents);
                }
                cursor.close();
                dbOpenHelper.close();
                System.out.println(gson.toJson(imexportList));

                setExportEvent(gson.toJson(imexportList));

            }
        });

        //Add event and time picker diaglo
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(v.getContext()).inflate(R.layout.add_newevent_layout,null);
                final EditText EventName = addView.findViewById(R.id.eventname);
                final TextView EventTime = addView.findViewById(R.id.eventtime);


                ImageButton SetTime = addView.findViewById(R.id.seteventtime);
                final CheckBox alarmMe = addView.findViewById(R.id.alarmme);
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.set(Calendar.MONTH,presentmonth);
                dateCalendar.set(Calendar.YEAR,presentyear);
                dateCalendar.set(Calendar.DAY_OF_MONTH,presentday);
                System.out.println(dateCalendar.getTime());
                alarmYear = dateCalendar.get(Calendar.YEAR);
                alarmMonth = dateCalendar.get(Calendar.MONTH);
                alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);

                Button AddEvent = addView.findViewById(R.id.addevent);
                SetTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minuts = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog
                                , new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c.set(Calendar.MINUTE,minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat hformate = new SimpleDateFormat("K:mm a",Locale.ENGLISH);
                                String event_Time = hformate.format(c.getTime());
                                EventTime.setText(event_Time);
                                alarmHour = c.get(Calendar.HOUR_OF_DAY);
                                alarmMinute = c.get(Calendar.MINUTE);
                            }
                        },hours,minuts,false);
                        timePickerDialog.show();
                    }
                });
                final String date = eventDateFormate.format(dateCalendar.getTime());
                final String month = monthFormat.format(dateCalendar.getTime());
                final String year = yearFormat.format(dateCalendar.getTime());

                //Activity type initialize and arraylist
                final String[] mActicityTypeTag = new String[1]; // Store the Activity
                mActivityTypeViews = new ArrayList<>();
                final TextView selectedtype = addView.findViewById(R.id.selectedType);
                ImageView mOtherView = addView.findViewById(R.id.icon_other);
                ImageView mWorkView = addView.findViewById(R.id.icon_work);
                ImageView mDateView = addView.findViewById(R.id.icon_date);
                ImageView mSportView = addView.findViewById(R.id.icon_sport);
                ImageView mReadingView = addView.findViewById(R.id.icon_reading);
                ImageView mTravelView = addView.findViewById(R.id.icon_travel);
                ImageView mVolunteerView = addView.findViewById(R.id.icon_volunteer);
                ImageView mStudyView = addView.findViewById(R.id.icon_study);
                ImageView mShoppingView = addView.findViewById(R.id.icon_shopping);
                ImageView mChillView = addView.findViewById(R.id.icon_chill);
                mActivityTypeViews.add(mOtherView);
                mActivityTypeViews.add(mWorkView);
                mActivityTypeViews.add(mDateView);
                mActivityTypeViews.add(mSportView);
                mActivityTypeViews.add(mReadingView);
                mActivityTypeViews.add(mTravelView);
                mActivityTypeViews.add(mVolunteerView);
                mActivityTypeViews.add(mStudyView);
                mActivityTypeViews.add(mShoppingView);
                mActivityTypeViews.add(mChillView);
                //Set the default selected
                mOtherView.setBackgroundResource(R.drawable.color_splotch_selected);
                mActicityTypeTag[0] = ActivityTypeKeys.OTHER_TAG;
                selectedtype.setText("Other");
                //Relative imageview listener
                mOtherView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.OTHER_TAG;
                        selectedtype.setText("Other");
                    }
                });
                mWorkView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.WORK_TAG;
                        selectedtype.setText("Work");
                    }
                });
                mDateView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.DATE_TAG;
                        selectedtype.setText("Date");
                    }
                });
                mSportView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.SPORT_TAG;
                        selectedtype.setText("Sport");
                    }
                });
                mReadingView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.READING_TAG;
                        selectedtype.setText("Reading");
                    }
                });
                mTravelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.TRAVEL_TAG;
                        selectedtype.setText("Travel");
                    }
                });
                mVolunteerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.VOLUNTEER_TAG;
                        selectedtype.setText("Volunteer");
                    }
                });
                mStudyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.STUDY_TAG;
                        selectedtype.setText("Study");
                    }
                });
                mShoppingView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.SHOPPING_TAG;
                        selectedtype.setText("Shopping");
                    }
                });
                mChillView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repaintIcons((ImageView) v);
                        mActicityTypeTag[0] = ActivityTypeKeys.CHILL_TAG;
                        selectedtype.setText("Chill");
                    }
                });


                AddEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String EventNameTest = EventName.getText().toString();
                        if(EventNameTest.isEmpty()){
                            EventName.setError("Please enter an Event Name");
                        }
                        else {
                            if (alarmMe.isChecked()) {
                                SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "on", mActicityTypeTag[0]);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute, 00);
                                setAlarm(calendar, EventName.getText().toString(), EventTime.getText().toString(),
                                        getRequestCode(date, EventName.getText().toString(), EventName.getText().toString()));
                                alertDialog.dismiss();
                            } else {
                                SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "off", mActicityTypeTag[0]);

                                alertDialog.dismiss();
                            }

                            initializeShowEventLayout();
                        }
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }



    //Initialze the layout part
    private void initializelayout(){
        Date currentTime = Calendar.getInstance().getTime();
        System.out.println(currentTime);
        userList = getSuggestEvent(3,this);
        simpleCalendarView = (CalendarView) findViewById(R.id.simpleCalendarView);
        ImportCalendar = findViewById(R.id.importcalendar);
        ExportCalendar = findViewById(R.id.exportcalendar);
        addEvent = findViewById(R.id.addEvent);
        suggesEvent = findViewById(R.id.suggestEvent);
        floatingActionMenu = findViewById(R.id.menu);
        Mapfuntion = findViewById(R.id.MapFunction);
        DeleteAllEvnet = findViewById(R.id.DeleteAllEvent);
        //Show today event
        Calendar firstpresentday = Calendar.getInstance();
        long todaydate = simpleCalendarView.getDate();
        Date date1 = new Date(todaydate);
        firstpresentday.setTime(date1);
        presentday = firstpresentday.get(Calendar.DAY_OF_MONTH);
        presentmonth = firstpresentday.get(Calendar.MONTH);
        presentyear = firstpresentday.get(Calendar.YEAR);
        System.out.println("Firstday: " + presentday + "/" + (presentmonth+1) + "/" + presentyear);
        String date = eventDateFormate.format(date1);
        ArrayList<Events> liarray = CollectEventByDate(date);
        recyclerView = findViewById(R.id.EventsRV2);
        recyclerView.setAdapter(null);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(getApplicationContext()
                , liarray,this);
        recyclerView.setAdapter(eventRecyclerAdapter);
        eventRecyclerAdapter.notifyDataSetChanged();

    }
    @Override
    public void onRecyclerClick(int position) {
        Log.d(TAG,"onRecyclerClick:clicked."+position);

        Calendar calendardate = Calendar.getInstance();
        calendardate.set(Calendar.YEAR,presentyear);
        calendardate.set(Calendar.MONTH,presentmonth);
        calendardate.set(Calendar.DAY_OF_MONTH,presentday);
        String date = eventDateFormate.format(calendardate.getTime());
        final ArrayList<Events> listarray = CollectEventByDate(date);
        final Events events = listarray.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        final View ShowView = LayoutInflater.from(this).inflate(R.layout.show_events_rowlayout,null);
        final TextView eventdate = ShowView.findViewById(R.id.eventdate);
        final TextView eventname = ShowView.findViewById(R.id.eventname);
        final TextView activitytype = ShowView.findViewById(R.id.TypeOfActivity);
        final TextView eventtime = ShowView.findViewById(R.id.eventime);
        final ImageButton Alarmset = ShowView.findViewById(R.id.alarmmeBtn);
        Button delete = ShowView.findViewById(R.id.delete);

        eventdate.setText(events.getDATE());
        eventname.setText(events.getEVENT());
        activitytype.setText(events.getType());
        eventtime.setText(events.getTIME());
        if(isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME())){
            Alarmset.setImageResource(R.drawable.ic_action_notification_on);

        }else {
            Alarmset.setImageResource(R.drawable.ic_action_notification_off);

        }

        Calendar datecalendar = Calendar.getInstance();
        datecalendar.setTime(ConvertStringToDate(events.getDATE()));
        final int alarmYear1 = datecalendar.get(Calendar.YEAR);
        final int alarmMonth1 = datecalendar.get(Calendar.MONTH);
        final int alarmDay1 = datecalendar.get(Calendar.DAY_OF_MONTH);
        Calendar timecalendar = Calendar.getInstance();
        timecalendar.setTime(ConvertStringToTime(events.getTIME()));
        final int alarmHour1 = timecalendar.get(Calendar.HOUR_OF_DAY);
        final int alarmMinute1 = timecalendar.get(Calendar.MINUTE);
        Alarmset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME())){
                    Alarmset.setImageResource(R.drawable.ic_action_notification_off);
                    cancelAlarm(getRequestCode(events.getDATE(),events.getEVENT(),events.getTIME()));
                    updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"off",events.getType());
                }else {
                    Alarmset.setImageResource(R.drawable.ic_action_notification_on);
                    Calendar alarmCalendar = Calendar.getInstance();
                    alarmCalendar.set(alarmYear1,alarmMonth1,alarmDay1,alarmHour1,alarmMinute1);
                    setAlarm(alarmCalendar,events.getEVENT(),events.getTIME(),getRequestCode(events.getDATE(),
                            events.getEVENT(),events.getTIME()));
                    updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"on",events.getType());
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCalendarEvent(events.getEVENT(),events.getDATE(),events.getTIME(),events.getType());
                listarray.remove(position);
                alertDialog.dismiss();
                initializeShowEventLayout();
            }
        });
        builder.setView(ShowView);
        alertDialog = builder.create();
        alertDialog.show();
    }
    private void initializeShowEventLayout(){
        Calendar calendardate = Calendar.getInstance();
        calendardate.set(Calendar.YEAR,presentyear);
        calendardate.set(Calendar.MONTH,presentmonth);
        calendardate.set(Calendar.DAY_OF_MONTH,presentday);
        String date = eventDateFormate.format(calendardate.getTime());
        ArrayList<Events> listarray = CollectEventByDate(date);
        recyclerView = findViewById(R.id.EventsRV2);
        //recyclerView.setAdapter(null);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(getApplicationContext()
                , listarray, this);
        recyclerView.setAdapter(eventRecyclerAdapter);
        eventRecyclerAdapter.notifyDataSetChanged();
    }






    //Alarm part
    private void setAlarm(Calendar calendar, String contenttitle, String contenttext, int RequesCode){
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ContentTitle",contenttitle);
        intent.putExtra("ContentText",contenttext);
        intent.putExtra("id",RequesCode);
        System.out.println(calendar.getTime()+"here");
        System.out.println(calendar.getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,RequesCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }
    public boolean isAlarmed(String date,String event,String time){
        boolean alarmed = false;
        dbOpenHelper = new DBOpenHelper(this);
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
    public void cancelAlarm(int RequesCode){
        Intent intent = new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,RequesCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }






    //DataBase part
    private  ArrayList<Events> CollectEventByDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date,database);
        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String type = cursor.getString(cursor.getColumnIndex(DBStructure.TYPE));
            Events events = new Events(event,time,Date,month,Year,type);
            arrayList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();

        return arrayList;
    }
    public void updateEvent(String date,String event,String time,String notify, String type){
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEvent(date,event,time,notify, type,database);
        dbOpenHelper.close();
    }
    public  void deleteCalendarEvent(String event, String date, String time, String type){
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event,date,time,type,database);
        dbOpenHelper.close();
    }
    private void SaveEvent(String event, String time, String date, String month,String year,String notify,String type){
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event,time,date,month,year,notify,type,database);
        dbOpenHelper.close();
        Toast.makeText(this,"Event Saved", Toast.LENGTH_SHORT).show();
    }
    private void DeleteAllEvent(){
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.DeleteAllEvent(database);
        dbOpenHelper.close();
    }
    private int getRequestCode(String date,String event,String time){
        int code = 0;
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,event,time,database);
        while(cursor.moveToNext()){
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dbOpenHelper.close();
        return code;
    }




    //String and date convert functions
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


    //Get suggested event
    private  void getSuggestedEvent(String userId, Context context){
        ArrayList<Events> suggestEventList = new ArrayList<>();
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand= String.format(Locale.US,"Select * from dbo.Calendardata WHERE user_id = '%s' ", userId);
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);
        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            if(result != null && result.length() > 0){
                                System.out.println("Eventlist:"+result);
                                for(int i = 0; i < result.length();i++){
                                    try{
                                        storewhole.add(new Events(result.getJSONObject(i).getString("Event"),
                                                result.getJSONObject(i).getString("Time"),
                                                result.getJSONObject(i).getString("Date"),
                                                result.getJSONObject(i).getString("EventMonth"),
                                                result.getJSONObject(i).getString("EventYear"),
                                                result.getJSONObject(i).getString("Type")));}
                                    catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //this is the string data you received
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
        //return suggestEventList;
    }
    //Import and export event part
    private List<String> fromjsontoSQL(String json){
        Gson gson = new Gson();
        List<String> commands = new ArrayList<>();
        String public_share = "true";
        ArrayList<ieEvent> ieEvents=gson.fromJson(json,new TypeToken<ArrayList<ieEvent>>(){}.getType());
        for(ieEvent Eventie:ieEvents) {
            String EventId = Eventie.getID();
            String Event = Eventie.getEVENT();
            String Time = Eventie.getTIME();
            String Date = Eventie.getDATE();
            String EventMonth = Eventie.getMONTH();
            String EventYear = Eventie.getYEAR();
            String Notify = Eventie.getNotify();
            String Type = Eventie.getType();
            SharedPreferences sp = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
            String userid = sp.getString("email", null);
            commands.add(String.format(Locale.US,
                    "insert into dbo.Calendardata(EventId,Event,Time,Date,EventMonth,EventYear,Notify,Type,user_id)" +
                            "values('%s', '%s', '%s', '%s', '%s', '%s', '%s','%s','%s')", EventId, Event, Time, Date, EventMonth, EventYear,
                    Notify, Type, userid)
            );
            commands.add(String.format(Locale.US,
                    "insert into dbo.Calendarpresent(EventId,Event,Time,Date,EventMonth,EventYear,Notify,Type,user_id, public_share)" +
                            "values('%s', '%s', '%s', '%s', '%s', '%s', '%s','%s','%s', '%s')", EventId, Event, Time, Date, EventMonth, EventYear,
                    Notify, Type, userid, public_share)
            );
        }
        return commands;
    }
    private  void setExportEvent(String json){
        deletepresentdatabase();
        String url = "http://13.70.2.33/api/sql_db";
        List<String> commands = fromjsontoSQL(json);
        for(int i=0;i<commands.size();i++){
            HashMap<String,String> data=new HashMap<>();
            data.put("db_name","Smart Scheduler");
            data.put("sql_cmd",commands.get(i));

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);
                            //response whether success or not
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                        }
                    }
            );
            queue.add(request);
        }

    }
    public void deletepresentdatabase(){
        SharedPreferences sp = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
        String userid = sp.getString("email", null);
        String url = "http://13.70.2.33/api/sql_db";
        String command = String.format(Locale.US,"DELETE dbo.Calendardata WHERE user_id= '%s' ", userid);
        HashMap<String,String> data=new HashMap<>();
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",command);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        //response whether success or not
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }
        );
        queue.add(request);
    }
    private void setAddOtherUserEvent(String userId) throws JSONException{
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand= String.format(Locale.US,"Select * from dbo.Calendarpresent WHERE user_id = '%s' ", userId);
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);
        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            if(result != null && result.length() > 0){

                            }
                            else
                                Toast.makeText(getApplicationContext(), "The userId is not exist or There are no present event in the userId ", Toast.LENGTH_LONG).show();
                            System.out.println(result);
                            for(int i = 0; i < result.length();i++){
                                try{
                                    SaveEvent(result.getJSONObject(i).getString("Event")+"--"+userId,
                                            result.getJSONObject(i).getString("Time"),
                                            result.getJSONObject(i).getString("Date"),
                                            result.getJSONObject(i).getString("EventMonth"),
                                            result.getJSONObject(i).getString("EventYear"),
                                            "off",
                                            result.getJSONObject(i).getString("Type"));}
                                catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                            initializeShowEventLayout();
                            //this is the string data you received
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }
    private void setAddEvent() throws JSONException {
        SharedPreferences sp = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
        String userid = sp.getString("email", null);
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand= String.format(Locale.US,"Select * from dbo.Calendarpresent WHERE user_id = '%s' ", userid);
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);
        String url = "http://13.70.2.33/api/sql_db";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            System.out.println(result);
                            for(int i = 0; i < result.length();i++){
                                try{
                                    SaveEvent(result.getJSONObject(i).getString("Event"),
                                            result.getJSONObject(i).getString("Time"),
                                            result.getJSONObject(i).getString("Date"),
                                            result.getJSONObject(i).getString("EventMonth"),
                                            result.getJSONObject(i).getString("EventYear"),
                                            "off",
                                            result.getJSONObject(i).getString("Type"));}
                                catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                            initializeShowEventLayout();
                            //this is the string data you received
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }
    private ArrayList<String> getSuggestEvent(int num,Context context){
        HashMap<String,String> data=new HashMap<>();
        ArrayList<String> userlist = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
        String userid = sp.getString("email", null);
        System.out.println(userid);
        String numOfUser = Integer.toString(num);
        data.put("user_id",userid);
        data.put("num_user",numOfUser);
        String url = "http://13.70.2.33/api/distance_matrix/3";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            System.out.println("result:"+result);
                            //this is the string data you received
                            for(int i = 0; i < result.length();i++){
                                userlist.add(result.getString(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
        return userlist;
    }






    //Activity type changing
    private void repaintIcons(ImageView thisView)
    {
        //Set this view to selected.
        thisView.setBackgroundResource(R.drawable.color_splotch_selected);

        //Set others to unselected.
        for(ImageView iv : mActivityTypeViews)
        {
            if(!iv.equals(thisView))
            {
                iv.setBackgroundResource(R.drawable.color_splotch_selector);
            }
        }
    }
}


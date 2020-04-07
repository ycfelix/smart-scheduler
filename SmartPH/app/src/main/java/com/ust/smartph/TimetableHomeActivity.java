package com.ust.smartph;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.google.gson.Gson;
import com.ust.timetable.TimetableHomeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimetableHomeActivity extends Activity {

    @BindView(R.id.recycler_timetable)
    RecyclerView recycler;

    @BindView(R.id.home_import_fab)
    FloatingActionButton importBtn;

    @BindView(R.id.debug_fab)
    FloatingActionButton debugBtn;

    @BindView(R.id.home_add_fab)
    FloatingActionButton addBtn;

    @BindView(R.id.fab_timetable_menu)
    FloatingActionMenu menu;

    private TimetableHomeAdapter adapter;

    private ArrayList<String> timetables;

    private ArrayList<String> notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_home);
        ButterKnife.bind(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        timetables=new ArrayList<>();
        notes=new ArrayList<>();
        adapter=new TimetableHomeAdapter(this,timetables,notes);
        recycler.setAdapter(adapter);
        getTimetables();
    }

    private void getTimetables(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);

        Map<String,?> prefs =pref.getAll();
        Set<String> keys =new TreeSet<>(prefs.keySet());
        keys.forEach(e-> {
            String s= (String) prefs.get(e);
            if(s.contains("timetable")) {
                timetables.add(e);
                notes.add(s.substring(s.indexOf("timetable")+"timetable".length()));
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.home_add_fab)
    void addTimetable(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.timetable_create,null);
        builder.setTitle("Create new timetable");
        EditText name = dialogView.findViewById(R.id.timetable_name);
        EditText description = dialogView.findViewById(R.id.timetable_description);
        builder.setView(dialogView);
        builder.setPositiveButton("Create",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String result=name.getText().toString();
                        String note=description.getText().toString();
                        note=TextUtils.isEmpty(note)?"custom timetable":note;
                        if(!TextUtils.isEmpty(result) && !timetables.contains(result)){
                            saveToSharePreference(result,note);
                            timetables.add(result);
                            notes.add(note);
                            adapter.notifyDataSetChanged();
                            menu.close(true);
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"timetable already exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @OnClick(R.id.debug_fab)
    void deletePrefs(View v){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=pref.edit();
        Map<String,?> prefs =pref.getAll();
        Set<String> keys =new TreeSet<>(prefs.keySet());
        keys.forEach(e->{
            if(((String)prefs.get(e)).contains("timetable")){
                System.out.println("key is "+e);
                editor.remove(e);
            }
        });
        editor.commit();
        adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.home_import_fab)
    void importSchedule(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_import,null);
        builder.setTitle("Input the generated number");
        builder.setView(dialogView);
        builder.setPositiveButton("enter",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText tokenEt = dialogView.findViewById(R.id.import_token);
                        String token=tokenEt.getText().toString();
                        if(!TextUtils.isEmpty(token)){
                            getScheduleFromServer(token);
                            menu.close(true);
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"wrong input!",Toast.LENGTH_SHORT);
                        }
                    }
                });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    void getScheduleFromServer(String token){
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand="Select * from dbo.user_schedule where token= '"+token+"'";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = this.getString(R.string.sql_api);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            if(result.length()==0){
                                return;
                            }
                            saveScheduleToPreference(result);
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

    void saveScheduleToPreference(JSONArray arr) throws JSONException {
        ArrayList<Schedule> monwed=new ArrayList<>();
        ArrayList<Schedule> thrsun=new ArrayList<>();
        String tableName="";
        for(int i=0;i<arr.length();i++){
            JSONObject row=arr.getJSONObject(i);
            Schedule schedule=new Schedule();
            schedule.setClassPlace(row.getString("class_place"));
            schedule.setClassTitle(row.getString("class_title"));
            schedule.setDay(row.getInt("day_of_week"));
            schedule.setStartTime(new Time(row.getInt("start_hour"),row.getInt("start_min")));
            schedule.setEndTime(new Time(row.getInt("end_hour"),row.getInt("end_min")));
            schedule.setProfessorName(row.getString("professor_name"));
            tableName=row.getString("table_name");
            if(schedule.getDay()<3){
                monwed.add(schedule);
            }
            else{
                thrsun.add(schedule);
            }
        }
        int cnt=0;
        while(this.timetables.contains(tableName)){
            tableName+=String.valueOf(cnt);
        }
        Gson gson=new Gson();
        saveToSharePreference(tableName,"");
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("monwed_"+tableName,gson.toJson(monwed));
        editor.putString("thrsun_"+tableName,gson.toJson(thrsun));
        editor.commit();
        timetables.add(tableName);
        notes.add("imported timetable");
        adapter.notifyDataSetChanged();
    }

    void saveToSharePreference(String prefix,String desciption){
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(prefix,"timetable"+desciption).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}

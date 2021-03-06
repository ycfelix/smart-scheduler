package com.ust.smartph;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.gson.reflect.TypeToken;
import com.ust.timetable.TimetableHomeAdapter;
import com.ust.timetable.TimetableLoader;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    ArrayList<Integer> matchChoices = new ArrayList<>();
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
//        saveFakeData();
        getTimetables();
    }

    private String fall1="[{\"classPlace\": \"\", \"classTitle\": \"COMP2822\", \"day\": 0, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1387\", \"day\": 0, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2068\", \"day\": 1, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4757\", \"day\": 1, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4965\", \"day\": 1, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1363\", \"day\": 2, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2936\", \"day\": 2, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1227\", \"day\": 2, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}]";
    private String fall2="[{\"classPlace\": \"\", \"classTitle\": \"COMP3951\", \"day\": 3, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1337\", \"day\": 3, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3183\", \"day\": 3, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4378\", \"day\": 4, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4185\", \"day\": 4, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4069\", \"day\": 4, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3259\", \"day\": 5, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4456\", \"day\": 5, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2225\", \"day\": 6, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2556\", \"day\": 6, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3905\", \"day\": 6, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}]";
    private String winter1="[{\"classPlace\": \"\", \"classTitle\": \"COMP2181\", \"day\": 0, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3278\", \"day\": 0, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4707\", \"day\": 1, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3540\", \"day\": 2, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2451\", \"day\": 2, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1109\", \"day\": 2, \"endTime\": {\"hour\": 12, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}]";
    private String winter2="[{\"classPlace\": \"\", \"classTitle\": \"COMP3951\", \"day\": 3, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1337\", \"day\": 3, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3183\", \"day\": 3, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4378\", \"day\": 4, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4185\", \"day\": 4, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4069\", \"day\": 4, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3259\", \"day\": 5, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4456\", \"day\": 5, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2225\", \"day\": 6, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2556\", \"day\": 6, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3905\", \"day\": 6, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}]";
    private String spring1="[{\"classPlace\": \"\", \"classTitle\": \"COMP2181\", \"day\": 0, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3278\", \"day\": 0, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4707\", \"day\": 1, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3540\", \"day\": 2, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2451\", \"day\": 2, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1109\", \"day\": 2, \"endTime\": {\"hour\": 12, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}]";
    private String spring2="[{\"classPlace\": \"\", \"classTitle\": \"COMP2181\", \"day\": 3, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3278\", \"day\": 3, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4707\", \"day\": 4, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3540\", \"day\": 5, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2451\", \"day\": 5, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1109\", \"day\": 5, \"endTime\": {\"hour\": 12, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3247\", \"day\": 6, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}]";
    private String summer1="[{\"classPlace\": \"\", \"classTitle\": \"COMP3951\", \"day\": 0, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1337\", \"day\": 0, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3183\", \"day\": 0, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4378\", \"day\": 1, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4185\", \"day\": 1, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4069\", \"day\": 1, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3259\", \"day\": 2, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP4456\", \"day\": 2, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}]";
    private String summer2="[{\"classPlace\": \"\", \"classTitle\": \"COMP3922\", \"day\": 3, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2309\", \"day\": 3, \"endTime\": {\"hour\": 12, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1050\", \"day\": 4, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2388\", \"day\": 4, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2044\", \"day\": 4, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1758\", \"day\": 5, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP3291\", \"day\": 5, \"endTime\": {\"hour\": 8, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP1360\", \"day\": 6, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2281\", \"day\": 6, \"endTime\": {\"hour\": 13, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"COMP2624\", \"day\": 6, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 10, \"minute\": 0}}]";
    private String break1="[{\"classPlace\": \"\", \"classTitle\": \"take rest\", \"day\": 0, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"shopping\", \"day\": 0, \"endTime\": {\"hour\": 12, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"cleaning\", \"day\": 0, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"exam\", \"day\": 1, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"programming\", \"day\": 1, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"make friends\", \"day\": 2, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}]";
    private String break2="[{\"classPlace\": \"\", \"classTitle\": \"take rest\", \"day\": 3, \"endTime\": {\"hour\": 7, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"shopping\", \"day\": 3, \"endTime\": {\"hour\": 12, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"cleaning\", \"day\": 3, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 11, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"exam\", \"day\": 4, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"programming\", \"day\": 4, \"endTime\": {\"hour\": 14, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 12, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"make friends\", \"day\": 5, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 6, \"minute\": 0}}, {\"classPlace\": \"\", \"classTitle\": \"make friends\", \"day\": 6, \"endTime\": {\"hour\": 9, \"minute\": 30}, \"professorName\": \"\", \"startTime\": {\"hour\": 7, \"minute\": 0}}]";


    void saveFakeData(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit=pref.edit();
        edit.putString("Fall","timetable");
        edit.putString("Winter","timetable");
        edit.putString("Spring","timetable");
        edit.putString("Summer","timetable");
        edit.putString("Semester break","timetable");
        edit.putString("monwed_Fall",fall1);
        edit.putString("thrsun_Fall",fall2);
        edit.putString("monwed_Winter",winter1);
        edit.putString("thrsun_Winter",winter2);
        edit.putString("monwed_Spring",spring1);
        edit.putString("thrsun_Spring",spring2);
        edit.putString("monwed_Summer",summer1);
        edit.putString("thrsun_Summer",summer2);
        edit.putString("monwed_Semester break",break1);
        edit.putString("thrsun_Semester break",break2);
        edit.commit();
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
                            Toast.makeText(getApplicationContext(),"timetable "+result+" already exist!", Toast.LENGTH_SHORT).show();
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
        timetables.clear();
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.match_fab)
    void matchTimetable(View v){
        final String[] items = timetables.toArray(new String[timetables.size()]);
        final boolean[] initChoiceSets=new boolean[items.length];
        matchChoices.clear();
        AlertDialog.Builder multiChoiceDialog =
                new AlertDialog.Builder(this);
        multiChoiceDialog.setTitle("Choose timetables to match with each other...");
        multiChoiceDialog.setMultiChoiceItems(items, initChoiceSets,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            matchChoices.add(which);
                        } else {
                            matchChoices.remove(which);
                        }
                    }
                });
        multiChoiceDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(matchChoices.size()<=1){
                            Toast.makeText(TimetableHomeActivity.this, "choose more than 2 timetable to match!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveToMatchedTimetable();
                        dialog.dismiss();
                    }
                });
        multiChoiceDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        multiChoiceDialog.show();
    }

    void saveToMatchedTimetable(){
        ArrayList<Schedule> matched=getMatchedTimetable();
        ArrayList<Schedule> monwed=new ArrayList<>();
        ArrayList<Schedule> thrsun=new ArrayList<>();
        String tableName="matched timetable";
        for(int i=0;i<matched.size();i++){
            Schedule schedule=matched.get(i);
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
        notes.add("matched timetable");
        menu.close(true);
        adapter.notifyItemInserted(timetables.size()-1);
    }

    ArrayList<Schedule> getMatchedTimetable(){
        ArrayList<Schedule> result=null;
        Gson gson=new Gson();
        String now=timetables.get(matchChoices.get(0));
        ArrayList<Schedule> t1=new ArrayList<>();
        String nowJson= TimetableLoader.getSchedule("monwed_"+now);
        t1.addAll(Objects.requireNonNull(gson.fromJson(nowJson, new TypeToken<ArrayList<Schedule>>() {
        }.getType())));
        nowJson= TimetableLoader.getSchedule("thrsun_"+now);
        t1.addAll(Objects.requireNonNull(gson.fromJson(nowJson, new TypeToken<ArrayList<Schedule>>() {
        }.getType())));
        t1=TimetableLoader.getFreeTime(t1);
        for(int i=1;i<matchChoices.size();i++){
            String next=timetables.get(matchChoices.get(i));
            ArrayList<Schedule> t2=new ArrayList<>();
            String nextJson= TimetableLoader.getSchedule("monwed_"+next);
            t2.addAll(Objects.requireNonNull(gson.fromJson(nextJson, new TypeToken<ArrayList<Schedule>>() {
            }.getType())));
            nextJson= TimetableLoader.getSchedule("thrsun_"+next);
            t2.addAll(Objects.requireNonNull(gson.fromJson(nextJson, new TypeToken<ArrayList<Schedule>>() {
            }.getType())));
            result=TimetableLoader.matchTimeTable(t1,TimetableLoader.getFreeTime(t2));

            System.out.println(result.size());
            t1=result;
        }
        return result;
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

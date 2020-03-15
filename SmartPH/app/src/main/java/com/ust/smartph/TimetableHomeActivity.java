package com.ust.smartph;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.ust.timetable.PreviewTimetableDialog;
import com.ust.timetable.TimetableHomeAdapter;

import java.util.ArrayList;
import java.util.Collections;
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

    @BindView(R.id.home_export_fab)
    FloatingActionButton exportBtn;

    @BindView(R.id.home_add_fab)
    FloatingActionButton addBtn;

    @BindView(R.id.fab_timetable_menu)
    FloatingActionMenu menu;

    private TimetableHomeAdapter adapter;

    private ArrayList<String> timetables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_home);
        ButterKnife.bind(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        timetables=new ArrayList<>();
        adapter=new TimetableHomeAdapter(this,timetables);
        recycler.setAdapter(adapter);
        getTimetables();
    }

    private void getTimetables(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);

        Map<String,?> prefs =pref.getAll();
        Set<String> keys =new TreeSet<>(prefs.keySet());
        keys.forEach(e-> {
            String s= (String) prefs.get(e);
            if(s.equals("timetable")) timetables.add(e);
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

    @OnClick(R.id.home_export_fab)
    void exportSchedule(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.timetable_export,null);
        builder.setTitle("Your generated share code");
        TextView edit_text = dialogView.findViewById(R.id.timetable_token);
        //TODO: send schedules to server
        //TODO: recevice token from server
        String token="1234";//getTokenFromServer(uid)...
        edit_text.setText(token);
        builder.setView(dialogView);
        builder.setPositiveButton("Share",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, token);
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        //startActivity(shareIntent);
                        PreviewTimetableDialog preview = new PreviewTimetableDialog(TimetableHomeActivity.this);
                        preview.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        preview.show();
                    }
                });
        builder.show();
    }

    @OnClick(R.id.home_add_fab)
    void addTimetable(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.timetable_create,null);
        builder.setTitle("Your generated share code");
        EditText editText = dialogView.findViewById(R.id.timetable_name);
        builder.setView(dialogView);
        builder.setPositiveButton("Create",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String result=editText.getText().toString();
                        if(!TextUtils.isEmpty(result)){
                            saveToSharePreference(result);
                            timetables.add(result);
                            adapter.notifyDataSetChanged();
                            menu.close(true);
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"wrong input!", Toast.LENGTH_SHORT).show();
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
        keys.forEach(e->editor.remove(e));
        editor.commit();
        adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.home_import_fab)
    void importSchedule(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.timetable_import,null);
        builder.setTitle("Input the generated number");
        builder.setView(dialogView);
        builder.setPositiveButton("enter",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edit_text = dialogView.findViewById(R.id.timetable_input_token);
                        if(!TextUtils.isEmpty(edit_text.getText().toString())){
                            dialog.dismiss();
                            //TODO: received schedules by token
                            //TODO: create a preview dialog
                            //TODO: merge into gp/personal timetable
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

    void saveToSharePreference(String prefix){
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(prefix,"timetable").commit();
    }
}

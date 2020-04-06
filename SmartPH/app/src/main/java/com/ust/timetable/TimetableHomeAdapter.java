package com.ust.timetable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.tlaabs.timetableview.Schedule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ust.smartph.R;
import com.ust.smartph.TimetableItemActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class TimetableHomeAdapter extends RecyclerView.Adapter<TimetableHomeAdapter.TimetableViewHolder> {

    private Context context;
    private ArrayList<String> timetables;
    private ArrayList<String> notes;

    public TimetableHomeAdapter(Context context, ArrayList<String> timetables, ArrayList<String> notes){
        this.context=context;
        this.timetables=timetables;
        this.notes=notes;
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.timetable_row, viewGroup, false);

        return new TimetableViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder timetableViewHolder, int i) {
        timetableViewHolder.setTimetableName(timetables.get(i));
        timetableViewHolder.setTimetableNote(notes.get(i));

        timetableViewHolder.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TimetableItemActivity.class);
                intent.putExtra("TABLE_NAME",timetables.get(i));
                context.startActivity(intent);
            }
        });

        timetableViewHolder.deleteRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(TimetableHomeAdapter.this.context);
                normalDialog.setTitle("Delete timetable");
                normalDialog.setMessage("Confirm delete this timetable?");
                normalDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String toDelete=timetableViewHolder.timetableName.getText().toString();
                                updateDB(toDelete,null);
                                TimetableLoader.setSchedule(toDelete,"");
                                TimetableLoader.setSchedule("monwed_"+toDelete,"");
                                TimetableLoader.setSchedule("thrsun_"+toDelete,"");
                                int index=timetables.indexOf(toDelete);
                                timetables.remove(index);
                                notes.remove(index);
                                TimetableHomeAdapter.this.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                normalDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                normalDialog.show();
            }
        });

        timetableViewHolder.export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(TimetableHomeAdapter.this.context);
                final View dialogView = LayoutInflater.from(TimetableHomeAdapter.this.context)
                        .inflate(R.layout.dialog_export, null);
                builder.setTitle("Your generated share code");
                TextView tokenTv = dialogView.findViewById(R.id.export_token);
                String tableName=timetableViewHolder.timetableName.getText().toString();
                String token = getToken(tableName);
                CheckBox shareAll=dialogView.findViewById(R.id.public_share);
                tokenTv.setText(token);
                builder.setView(dialogView);
                builder.setPositiveButton("Share",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                updateDB(tableName, new Function<String, Void>() {
                                    @Override
                                    public Void apply(String s) {
                                        return sendScheduleToServer(s,shareAll.isChecked());
                                    }
                                });
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, token);
                                sendIntent.setType("text/plain");
                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                TimetableHomeAdapter.this.context.startActivity(shareIntent);
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
    }

    private void updateDB(String timetableName, Function<String,Void> callback){
        String url = context.getString(R.string.server_ip);
        HashMap<String, String> data = new HashMap<>();
        String token=getToken(timetableName);
        data.put("db_name", "Smart Scheduler");
        data.put("sql_cmd", String.format("delete from dbo.user_schedule where token ='%s'",token));
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        if(callback!=null){
                            callback.apply(timetableName);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        queue.add(request);
    }

    private Void sendScheduleToServer(String timetableName,boolean isShareAll){
        String url = context.getString(R.string.server_ip);
        //do a select first, then
        List<String> commands=getSQLCommands(timetableName,isShareAll);
        for(int i=0;i<commands.size();i++){
            HashMap<String,String> data=new HashMap<>();
            data.put("db_name","Smart Scheduler");
            data.put("sql_cmd",commands.get(i));

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );
            queue.add(request);
        }
        return null;
    }

    private String getToken(String timetableName){
        String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return HashGenerator.toHashCode(android_id)+HashGenerator.toHashCode(timetableName);
    }

    @Nullable
    private List<String> getSQLCommands(String timetableName,boolean isShareAll){
        List<String> schedules=new ArrayList<>();
        String token=getToken(timetableName);
        String PREF_MON_WED="monwed_"+timetableName;
        String PREF_THR_SUN="thrsun_"+timetableName;
        String monwedData = TimetableLoader.getSchedule(PREF_MON_WED);
        String thrsunData = TimetableLoader.getSchedule(PREF_THR_SUN);
        if(TextUtils.isEmpty(monwedData)&&TextUtils.isEmpty(thrsunData)){
            Toast.makeText(context,"your timetable is empty!",Toast.LENGTH_SHORT);
            return null;
        }
        schedules.addAll(formatJsonToSQL(monwedData,token,timetableName,isShareAll));
        schedules.addAll(formatJsonToSQL(thrsunData,token,timetableName,isShareAll));
        return schedules;
    }

    private List<String> formatJsonToSQL(String json,String token,String timetableName,boolean isShareAll){
        Gson gson = new Gson();
        ArrayList<Schedule> schedules=gson.fromJson(json,new TypeToken<ArrayList<Schedule>>(){}.getType());
        List<String> commands=new ArrayList<>();
        for(Schedule schedule:schedules) {
            String class_place = schedule.getClassPlace();
            String class_title = schedule.getClassTitle();
            int day = schedule.getDay();
            int start_hr = schedule.getStartTime().getHour();
            int start_min = schedule.getStartTime().getMinute();
            int end_hr = schedule.getEndTime().getHour();
            int end_min = schedule.getEndTime().getMinute();
            String professor_name = schedule.getProfessorName();
            commands.add(String.format(Locale.US,
                            "insert into dbo.user_schedule(class_place,class_title,day_of_week,start_hour," +
                            "start_min,end_hour,end_min,professor_name,token,table_name,public_share) values('%s', '%s'," +
                            "%d, %d, %d, %d, %d,'%s','%s','%s',%d)", class_place, class_title, day, start_hr, start_min, end_hr, end_min, professor_name,
                    token, timetableName,isShareAll?1:0)
            );
        }
        return commands;
    }


    @Override
    public int getItemCount() {
        return this.timetables.size();
    }


    public static class TimetableViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView timetableName;
        TextView timetableNote;
        View view;
        ImageView deleteRow;
        ImageView export;
        TimetableViewHolder(View v) {
            super(v);
            this.view=v;
            timetableName = v.findViewById(R.id.timetable_name);
            timetableNote=v.findViewById(R.id.timetable_note);
            deleteRow=v.findViewById(R.id.delete_row);
            export=v.findViewById(R.id.export_timetable);
        }

        public void setTimetableName(String name){
            this.timetableName.setText(name);
        }

        public void setTimetableNote(String timetableNote) {
            this.timetableNote.setText(timetableNote);
        }
    }
}

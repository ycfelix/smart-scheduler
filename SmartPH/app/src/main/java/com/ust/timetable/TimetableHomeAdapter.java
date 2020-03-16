package com.ust.timetable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ust.smartph.TimetableHomeActivity;
import com.ust.smartph.TimetableItemActivity;
import com.ust.smartph.R;

import java.util.ArrayList;

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
                                SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(TimetableHomeAdapter.this.context);
                                pref.edit().putString(toDelete,"").commit();
                                timetables.remove(toDelete);
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
                        .inflate(R.layout.timetable_export, null);
                builder.setTitle("Your generated share code");
                TextView tokenTv = dialogView.findViewById(R.id.timetable_token);
                //TODO: send schedules to server

                //TODO: recevice token from server
                String token = "1234";//getTokenFromServer(uid)...
                tokenTv.setText(token);
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
                                TimetableHomeAdapter.this.context.startActivity(shareIntent);
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
    }

    private void sendScheduleToServer(String timetableName){
        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
        String data=pref.getString("monwed_"+timetableName,"");
        data+=pref.getString("thrsun_"+timetableName,"");
        String ip = context.getString(R.string.server_ip);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://13.70.2.33:5000/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Toast.makeText(context,
                                "It works! Content = " + response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,
                        "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

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

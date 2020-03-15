package com.ust.timetable;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.smartph.TimetableItemActivity;
import com.ust.smartph.R;

import java.util.ArrayList;

public class TimetableHomeAdapter extends RecyclerView.Adapter<TimetableHomeAdapter.TimetableViewHolder> {

    private Context context;
    private ArrayList<String> timetables;

    public TimetableHomeAdapter(Context context, ArrayList<String> timetables){
        this.context=context;
        this.timetables=timetables;
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
        timetableViewHolder.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TimetableItemActivity.class);
                intent.putExtra("TABLE_NAME",timetables.get(i));
                context.startActivity(intent);
            }
        });
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
        TimetableViewHolder(View v) {
            super(v);
            this.view=v;
            timetableName = v.findViewById(R.id.timetable_name);
        }
        public void setTimetableName(String name){
            this.timetableName.setText(name);
        }

        public void setTimetableNote(String timetableNote) {
            this.timetableNote.setText(timetableNote);
        }
    }
}

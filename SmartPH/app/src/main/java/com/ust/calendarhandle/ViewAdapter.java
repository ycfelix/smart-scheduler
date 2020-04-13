package com.ust.calendarhandle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ust.smartph.R;

import java.util.ArrayList;

public class ViewAdapter extends BaseAdapter {

    private ArrayList<Events> eventsArrayList;
    private LayoutInflater inflater;

    static class ViewHolder{
        TextView eventname;

    }

    public ViewAdapter(ArrayList<Events> arrayList, LayoutInflater inflater){
        this.eventsArrayList = arrayList;
        this.inflater = inflater;
    }
    @Override
    public int getCount() {
        return eventsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.suggest_events_firstlayout, null);
        holder.eventname = (TextView)convertView.findViewById(R.id.titleevent);



        System.out.println(eventsArrayList.get(position).getEVENT());
        holder.eventname.setText(eventsArrayList.get(position).getEVENT());

        return convertView;
    }
}

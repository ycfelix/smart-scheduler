package com.felix.checklist;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChecklistAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {

    Context context;
    ArrayList<DataModel> data;

    public ChecklistAdapter(@NonNull Context context,ArrayList<DataModel> data) {
        super(context,R.layout.checklist_list_item,data);
        this.context=context;
        this.data=data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.checklist_list_item,parent,false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.checklist_title);
            holder.detail = convertView.findViewById(R.id.checklist_detail);
            holder.checkBox = convertView.findViewById(R.id.checklist_checkbox);
            holder.icon = convertView.findViewById(R.id.checklist_icon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        DataModel selected = data.get(position);

        holder.title.setText(selected.getTitle());
        holder.detail.setText(selected.getDetail());
        holder.checkBox.setChecked(selected.isChecked());
        holder.icon.setImageDrawable(context.getDrawable(selected.getIcon()));
        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(selected.isChecked());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout r= (RelativeLayout) v.getParent();
                LinearLayout l= (LinearLayout) r.getChildAt(0);
                TextView title= (TextView) l.getChildAt(0);
                TextView detail= (TextView) l.getChildAt(1);
                if (((CheckBox)v).isChecked()){
                    selected.setChecked(true);
                    data.remove(position);
                    data.add(selected);
                    title.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    detail.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else{
                    selected.setChecked(false);
                    title.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
                    detail.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.checklist_checkbox:
                RelativeLayout r= (RelativeLayout) v.getParent();
                LinearLayout l= (LinearLayout) r.getChildAt(0);
                TextView title= (TextView) l.getChildAt(0);
                TextView detail= (TextView) l.getChildAt(1);
                title.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                detail.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                break;
        }
    }


    private static class ViewHolder {
        ImageView icon;
        TextView title;
        TextView detail;
        //TextView date;
        CheckBox checkBox;
    }
}

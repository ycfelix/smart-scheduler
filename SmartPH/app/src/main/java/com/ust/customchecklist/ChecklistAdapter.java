package com.ust.customchecklist;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ust.smartph.R;

import java.util.ArrayList;
import java.util.Collections;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {
    private ItemClickListener onItemClickListener;
    Context context;
    ArrayList<DataModel> data;

    public ChecklistAdapter(@NonNull Context context,ArrayList<DataModel> data,ItemClickListener listener) {
        this.context=context;
        this.data=data;
        this.onItemClickListener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.checklist_list_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        DataModel selected = data.get(i);
        Drawable icon = ContextCompat.getDrawable(context, IconDictionary.getIcon(selected.getIcon()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,i);
            }
        });
        holder.title.setText(selected.getTitle());
        holder.detail.setText(selected.getDetail());
        holder.checkBox.setChecked(selected.isChecked());
        holder.icon.setImageDrawable(icon);
        holder.checkBox.setTag(i);
        holder.checkBox.setChecked(selected.isChecked());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                RelativeLayout r= (RelativeLayout) v.getParent();
//                LinearLayout l= (LinearLayout) r.getChildAt(0);
//                TextView title= (TextView) l.getChildAt(0);
//                TextView detail= (TextView) l.getChildAt(1);
                if (((CheckBox)v).isChecked()){
                    notifyItemMoved(holder.getAdapterPosition(),data.size()-1);
                    selected.setChecked(true);
                    Collections.swap(data,holder.getAdapterPosition(),data.size()-1);
                }
                else{
                    Collections.swap(data,holder.getAdapterPosition(),0);
                    selected.setChecked(false);
                    notifyItemMoved(holder.getAdapterPosition(),0);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return data.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView detail;
        //TextView date;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.checklist_title);
            this.detail = itemView.findViewById(R.id.checklist_detail);
            this.checkBox = itemView.findViewById(R.id.checklist_checkbox);
            this.icon = itemView.findViewById(R.id.checklist_icon);
        }
    }
}

package com.ust.customchecklist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ust.smartph.ChecklistHomeActivity;
import com.ust.smartph.R;
import com.ust.timetable.HashGenerator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {
    Context context;
    ArrayList<DataModel> data;
    private static final String PREF_NAME="checklist";

    public ChecklistAdapter(@NonNull Context context,ArrayList<DataModel> data) {
        this.context=context;
        this.data=data;
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
        int id=IconDictionary.getIcon(selected.getIcon());
        if(id!=-1){
            Drawable icon = ContextCompat.getDrawable(context, IconDictionary.getIcon(selected.getIcon()));
            holder.icon.setImageDrawable(icon);
        }
        else{
            holder.icon.setImageDrawable( ContextCompat.getDrawable(context,
                    IconDictionary.getIcon(R.id.default_icon)));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int index=holder.getAdapterPosition();
                EditDialog dialog = new EditDialog(
                        ChecklistAdapter.this.context,
                        RequestType.EDIT,
                        data.get(index));

                dialog.setEditDialogListener(new EditDialogListener() {
                    @Override
                    public void onEditResult(@NotNull DataModel data, RequestType type) {
                        if(type==RequestType.DELETE){
                            ChecklistAdapter.this.data.remove(index);
                            notifyItemRemoved(index);
                        }
                        else{
                            ChecklistAdapter.this.data.set(index,data);
                            notifyItemChanged(index);
                        }
                        saveDataToPreference();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        holder.title.setText(selected.getTitle());
        holder.detail.setText(selected.getDetail());
        holder.checkBox.setChecked(selected.isChecked());
        holder.checkBox.setTag(i);
        holder.checkBox.setChecked(selected.isChecked());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //maybe future inplment the strike style
//                RelativeLayout r= (RelativeLayout) v.getParent();
//                LinearLayout l= (LinearLayout) r.getChildAt(0);
//                TextView title= (TextView) l.getChildAt(0);
//                TextView detail= (TextView) l.getChildAt(1);
                if (((CheckBox)v).isChecked()){
                    Collections.swap(data,holder.getAdapterPosition(),data.size()-1);
                    notifyItemMoved(holder.getAdapterPosition(),data.size()-1);
                    selected.setChecked(true);
                }
                else{
                    Collections.swap(data,holder.getAdapterPosition(),0);
                    notifyItemMoved(holder.getAdapterPosition(),0);
                    selected.setChecked(false);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    void saveDataToPreference(){
        Gson gson=new Gson();
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString(PREF_NAME,gson.toJson(data));
        editor.commit();
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

package com.ust.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ust.smartph.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {


    DeleteItemListener deleteItemListener;

    Context context;

    ArrayList<Friend> friends;


    public void setDeleteItemListener(DeleteItemListener listener) {
        this.deleteItemListener = listener;
    }

    public FriendAdapter(@NotNull Context context, ArrayList<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FriendAdapter.ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.friendlist_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        if (position < 0 || position > friends.size()) {
            //wrong position
            return;
        }
        Friend friend = friends.get(position);
        viewHolder.online.setText("Online : " + friend.isOnline());
        viewHolder.userID.setText("User ID: " + friend.getUserID());
        viewHolder.name.setText("Name: " + friend.getName());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FriendAdapter.this.context);
                dialog.setTitle("Delete friend");
                dialog.setMessage("Are you sure to delete " + friend.getName() + " ?");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FriendAdapter.this.friends.remove(position);
                        notifyItemRemoved(position);
                        dialog.dismiss();
                        deleteItemListener.onDeleteItem();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] schedules = {"user_schedule", "user_checklist"};
                ArrayList<Share> shares = new ArrayList<>();
                getShareItemFromDB(schedules, friend.getUserID(), 0, shares, position,friend.getName());
            }
        });
    }

    private void getShareItemFromDB(String[] schedules, String userID, int idx, ArrayList<Share> shares, final int adapterPosition, String fdName) {
        if (idx >= schedules.length) {
            return;
        }
        String schedule = schedules[idx];
        HashMap<String, String> data = new HashMap<>();
        String sqlCommand = "Select * from dbo." + schedule + " where user_id= '" + userID + "' and public_share = 1";
        data.put("db_name", "Smart Scheduler");
        data.put("sql_cmd", sqlCommand);
        System.out.println("getting "+schedule);
        String url = context.getString(R.string.sql_api);
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("result");
                            System.out.println(response);
                            getShareList(schedule, shares, result);
                            if (idx == schedules.length - 1) {
                                ShareDialog dialog = new ShareDialog(context, shares,fdName);
                                dialog.setEditNameListener(new EditNameListener() {
                                    @Override
                                    public void onEditResult(String name) {
                                        friends.get(adapterPosition).setName(name);
                                        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
                                        SharedPreferences.Editor editor=preferences.edit();
                                        editor.putString(friends.get(adapterPosition).getEmail(),name);
                                        editor.apply();
                                        notifyDataSetChanged();
                                    }
                                });
                                dialog.show();
                            } else {
                                getShareItemFromDB(schedules, userID, idx + 1, shares, adapterPosition,fdName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("cannot get frds");
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }

    private void getShareList(String itemName, ArrayList<Share> shares, JSONArray arr) throws JSONException {
        if(arr.length()==0){
            return;
        }
        ArrayList<Share> dataModels = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject row = arr.getJSONObject(i);
            String token=row.getString("token");
            if(shares.stream().anyMatch(e->e.getCode().equals(token))){
                continue;
            }
            dataModels.add(new Share(row.getString("token"), itemName));
        }
        shares.addAll(dataModels);
    }


    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView userID;
        TextView online;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            userID = itemView.findViewById(R.id.friend_id);
            online = itemView.findViewById(R.id.friend_online);
            delete = itemView.findViewById(R.id.delete_row);
        }
    }
}

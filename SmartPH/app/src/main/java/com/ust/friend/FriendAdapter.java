package com.ust.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ust.smartph.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {


    Context context;
    ArrayList<Friend> friends;

    public FriendAdapter(@NotNull Context context, ArrayList<Friend> friends){
        this.context=context;
        this.friends=friends;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FriendAdapter.ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.friendlist_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int position=viewHolder.getAdapterPosition();
        if(position<0 || position>friends.size()){
            //wrong position
            return;
        }
        Friend friend=friends.get(position);
        viewHolder.online.setText("Online : "+friend.isOnline());
        viewHolder.userID.setText("User ID: "+friend.getUserID());
        viewHolder.name.setText("Name: "+friend.getName());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(FriendAdapter.this.context);
                dialog.setTitle("Delete friend");
                dialog.setMessage("Are you sure to delete "+friend.getName()+" ?");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FriendAdapter.this.friends.remove(position);
                        notifyItemRangeRemoved(position,1);
                        dialog.dismiss();
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
    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView userID;
        TextView online;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.user_name);
            userID= itemView.findViewById(R.id.friend_id);
            online= itemView.findViewById(R.id.friend_online);
            delete=itemView.findViewById(R.id.delete_row);
        }
    }
}

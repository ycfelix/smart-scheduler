package com.ust.friend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.smartph.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ShareItemAdapter extends RecyclerView.Adapter<ShareItemAdapter.ViewHolder> {

    ArrayList<Share> data;
    Context context;

    ShareItemAdapter(@NotNull Context context,@NotNull ArrayList<Share> data){
        this.context=context;
        this.data=data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShareItemAdapter.ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.friend_share,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int position=viewHolder.getAdapterPosition();
        Share share=data.get(position);
        viewHolder.code.setText(share.getCode());
        viewHolder.name.setText(share.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView code;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_name);
            code=itemView.findViewById(R.id.share_code);
        }
    }
}

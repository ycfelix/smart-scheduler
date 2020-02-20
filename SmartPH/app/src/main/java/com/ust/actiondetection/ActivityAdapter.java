package com.ust.actiondetection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.smartph.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH);

    private List<DatedActivity> datedActivityList = new ArrayList<>();


    public void addItem(DatedActivity datedActivity) {
        datedActivityList.add(datedActivity);

        notifyDataSetChanged();
    }

    public DatedActivity getItem(int position) {
        return datedActivityList.get(datedActivityList.size() - position - 1);
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_detect_recycler, parent, false);
        return new ActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        holder.setDatedActivity(getItem(position));
    }

    @Override
    public int getItemCount() {
        return datedActivityList.size();
    }

    class ActivityViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.activityType)
        TextView activityType;
        @BindView(R.id.activityConfidence)
        TextView activityConfidence;
        @BindView(R.id.activityDate)
        TextView activityDate;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDatedActivity(DatedActivity datedActivity) {
            activityType.setText(datedActivity.getType());
            activityConfidence.setText(datedActivity.getConfidence() + "%");
            activityDate.setText(sdf.format(datedActivity.date));
        }
    }
}

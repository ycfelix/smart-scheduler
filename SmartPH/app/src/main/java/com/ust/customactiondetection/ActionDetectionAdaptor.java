package com.ust.customactiondetection;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ust.smartph.R;

import java.util.ArrayList;

public class ActionDetectionAdaptor extends ArrayAdapter<DataModel> implements View.OnClickListener {

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtVersion;
        ImageView info;
    }

    public ActionDetectionAdaptor(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.action_detection_row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {
            case R.id.sensor_item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.action_detection_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.sensor_name);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.sensor_number);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.sensor_item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.action_detection_bottomup : R.anim.action_detection_topdown);
        //result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getSensorName());
        viewHolder.txtVersion.setText(dataModel.getSensorNumber());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

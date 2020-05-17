package com.ust.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ust.smartph.R;
import java.util.ArrayList;

public class SuggestedPathAdapter extends ArrayAdapter<SuggestedPath> {

    private Context mContext;
    private ArrayList<SuggestedPath> suggestedPathList = new ArrayList<SuggestedPath>();

    public SuggestedPathAdapter(Context context, ArrayList<SuggestedPath> list) {
        super(context, 0 , list);
        mContext = context;
        suggestedPathList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }
        SuggestedPath currentSuggestedPath = suggestedPathList.get(position);

        ImageView imageView = (ImageView)listItem.findViewById(R.id.mode);
        imageView.setImageResource(currentSuggestedPath.getModeImageDrawable());

        TextView distanceView = (TextView) listItem.findViewById(R.id.distance);
        double km = ((double)(currentSuggestedPath.getDistance()))/1000;
        String kmPart = km+" km";
        String distanceInfo = kmPart;
        distanceView.setText(distanceInfo);

        TextView durationView = (TextView) listItem.findViewById(R.id.duration);
        int hours = ((int) currentSuggestedPath.getDuration()/3600);
        int mins = ((int) (currentSuggestedPath.getDuration()%3600)/60);
        String hoursPart;
        String minsPart;
        if(hours==0){
            hoursPart="";
        }
        else{
            hoursPart=hours+" hours";
        }
        if(mins==0){
            minsPart="";
        }
        else{
            minsPart=mins+" mins";
        }
        String durationInfo="";
        durationInfo+=hoursPart;
        if(minsPart!=""){
            durationInfo+="\n";
        }
        durationInfo+=minsPart;
        durationView.setText(durationInfo); //((int) currentSuggestedPath.getDuration()/3600)+" hours"+((int) (currentSuggestedPath.getDuration()%3600)/60)+" mins"+(currentSuggestedPath.getDuration()%60)+" sec"

        return listItem;
    }
}

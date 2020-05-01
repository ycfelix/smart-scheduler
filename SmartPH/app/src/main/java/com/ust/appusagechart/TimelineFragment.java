package com.ust.appusagechart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.ust.smartph.R;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineFragment extends Fragment {

    @BindView(R.id.timeline)
    ListView timeline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.app_usage_timeline, container, false);

        ButterKnife.bind(this,root);

        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(getContext(), 0, generateRows(),
                false);
        timeline.setAdapter(myAdapter);
        return root;
    }

    private  ArrayList<TimelineRow> generateRows(){
        ArrayList<TimelineRow> result=new ArrayList<>();
        AppUsageInfo statisticsInfo = new AppUsageInfo(getActivity(),AppUsageInfo.DAY);
        ArrayList<AppInfo> showList = (ArrayList<AppInfo>) statisticsInfo.getShowList();

        Collections.sort(showList, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return new Date(o1.getLastUsedTime()).compareTo(new Date(o2.getLastUsedTime()));
            }
        });
        for(int i=1;i<showList.size();i++){//hardcoded to remove the first item(useless)
            result.add(generateRow(i,showList.get(i)));
        }
        return result;
    }





    private TimelineRow generateRow(int id,AppInfo appinfo){

        Date date=new Date(appinfo.getLastUsedTime());
        TimelineRow myRow = new TimelineRow(id);
        myRow.setDate(date);
        myRow.setTitle(appinfo.getLabel());

        DateFormat dateFormat = new SimpleDateFormat("MMM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        String description=String.format("Open at: %s \nUsed %d times today",strDate,appinfo.getTimes());
        myRow.setDescription(description);
        myRow.setImage(appinfo.getAppIcon());

        Random rand=new Random();
        myRow.setBellowLineColor(ColorTemplate.PASTEL_COLORS[rand.nextInt(ColorTemplate.PASTEL_COLORS.length)]);

        myRow.setBellowLineSize(3);
        myRow.setImageSize(40);
        myRow.setDateColor(Color.argb(255, 0, 0, 0));
        myRow.setTitleColor(Color.argb(255, 14, 77, 87));
        myRow.setDescriptionColor(Color.argb(255, 0, 0, 0));
        return myRow;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

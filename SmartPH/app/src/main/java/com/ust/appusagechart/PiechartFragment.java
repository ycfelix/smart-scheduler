package com.ust.appusagechart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ust.smartph.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PiechartFragment extends Fragment {

    @BindView(R.id.pie_chart)
    PieChart chart;

    @BindView(R.id.used_time)
    TextView usedTime;

    private int style = AppUsageInfo.DAY;

    private long totaltime;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root= (ViewGroup) inflater.inflate(R.layout.app_usage_piechart,container,false);
        ButterKnife.bind(this,root);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        //chart.setOnChartValueSelectedListener(this);
        setData(this.style);
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        l.setTextColor(Color.BLACK);

        return root;
    }

    private void setData(int style) {

        //TODO: wrong pkg info if deleted
        AppUsageInfo statisticsInfo = new AppUsageInfo(getActivity(),style);

        ArrayList<AppInfo> ShowList = statisticsInfo.getShowList();

        totaltime = statisticsInfo.getTotalTime();

        SpannableString sp = new SpannableString("total used time " + DateUtils.formatElapsedTime(totaltime / 1000));
        sp.setSpan(new RelativeSizeSpan(1.35f), 0, sp.length(), 0);
        sp.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, sp.length(), 0);
        usedTime.setText(sp);

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        if(ShowList.size() < 6) {
            for (int i = 0; i < ShowList.size(); i++) {
                float apptime = (float)ShowList.get(i).getUsedTimebyDay() / 1000;
                if(apptime / totaltime * 1000 >= 0.001)
                    entries.add(new PieEntry(apptime, ShowList.get(i).getLabel()));
            }
        }
        else {
            for(int i = 0;i < 6;i++) {
                float apptime = (float)ShowList.get(i).getUsedTimebyDay() / 1000;
                if(apptime / totaltime * 1000 >= 0.001)
                    entries.add(new PieEntry(apptime, ShowList.get(i).getLabel()));
            }
            long otherTime = 0;
            for(int i=6;i<ShowList.size();i++) {
                otherTime += ShowList.get(i).getUsedTimebyDay() / 1000;
            }
            if(1.0 * otherTime / totaltime * 1000 >= 0.001)
                entries.add(new PieEntry((float)otherTime, "other app"));
        }

        entries.forEach(e-> System.out.println(e.getLabel()));
        PieDataSet dataSet = new PieDataSet(entries, "Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);
        // undo all highlights
        chart.highlightValues(null);
        chart.setEntryLabelColor(Color.BLACK);
        chart.invalidate();

    }
}

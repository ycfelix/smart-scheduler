package com.ust.appusagechart;

import android.graphics.Color;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        setChartPieData(this.style);
        chart.animateY(1400, Easing.EaseInOutQuad);
        Legend chartLegend = chart.getLegend();
        chartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        chartLegend.setOrientation(Legend.LegendOrientation.VERTICAL);
        chartLegend.setDrawInside(false);
        chartLegend.setEnabled(false);
        chartLegend.setTextColor(Color.BLACK);

        return root;
    }

    private void setChartPieData(int style) {

        AppUsageInfo statisticsInfo = new AppUsageInfo(getActivity(),style);

        ArrayList<AppInfo> ShowList = statisticsInfo.getShowList();

        totaltime = statisticsInfo.getTotalTime();

        SpannableString sp = new SpannableString("total used time " + DateUtils.formatElapsedTime(totaltime / 1000));
        sp.setSpan(new RelativeSizeSpan(1.35f), 0, sp.length(), 0);
        sp.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, sp.length(), 0);
        usedTime.setText(sp);

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < Math.min(ShowList.size(),6); i++) {
            float t = (float)ShowList.get(i).getUsedTimebyDay() / 1000;
            if(t / totaltime * 1000 >= 0.01)
                entries.add(new PieEntry(t, ShowList.get(i).getLabel()));
        }

        if(ShowList.size() >= 6) {
            long t = 0;
            for(int i=6;i<ShowList.size();i++) {
                t += ShowList.get(i).getUsedTimebyDay() / 1000;
            }
            if(1.0 * t / totaltime * 1000 >= 0.01)
                entries.add(new PieEntry((float)t, "other app"));
        }

        entries.forEach(e-> System.out.println(e.getLabel()));


        PieData data = new PieData(getDataSet(entries));
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);
        chart.highlightValues(null);
        chart.setEntryLabelColor(Color.BLACK);
        chart.invalidate();
    }

    private PieDataSet getDataSet(ArrayList<PieEntry> entries){
        PieDataSet dataSet = new PieDataSet(entries, "Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
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
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        return dataSet;
    }
}

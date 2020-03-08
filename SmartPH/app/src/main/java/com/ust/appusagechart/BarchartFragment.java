package com.ust.appusagechart;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ust.smartph.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BarchartFragment extends Fragment{

    @BindView(R.id.usage_barchart)
    BarChart chart;

    ArrayList<AppInfo> showList;

    private Unbinder unbinder;

    private XAxis xAxis;

    private YAxis leftAxis;

    private YAxis rightAxis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root= (ViewGroup) inflater.inflate(R.layout.app_usage_barchart,container,false);
        unbinder= ButterKnife.bind(this,root);
        getActivity().setTitle("App Usage Bar Chart");
        AppUsageInfo statisticsInfo = new AppUsageInfo(getActivity(),AppUsageInfo.DAY);
        showList = statisticsInfo.getShowList();
        chart.getDescription().setEnabled(false);
        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setDoubleTapToZoomEnabled(false);


        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
        final ArrayList<String> xLabel = new ArrayList<>();
        for(int i=0;i<10;i++){
            xLabel.add(String.valueOf(i+10));
        }
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if(showList.size()<=(int)(value)) return "";
                if((int)(value)>=6) return "others";
                String name=showList.get((int)(value)).getLabel();
                if(name==null) return "null";
                return name.length()<=4?name:name.substring(0,3)+"...";
            }
        });


        ValueFormatter custom=new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + " min";
            }
        };
        leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        // add a nice and smooth animation
        chart.animateY(1500);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        setDataBarChart();
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                AppInfo info=showList.get((int)e.getX());
                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setIcon(info.getDrawableIcon());
                dialog.setTitle("App usage details");
                String msg=String.format("App %s \nOpened %d times Used %d mins today",
                        info.getLabel(),info.getTimes(),(int)e.getY());
                dialog.setMessage(msg);
                final AlertDialog alert = dialog.create();
                alert.show();
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        alert.dismiss();
                    }
                }.start();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return  root;
    }
    private void setDataBarChart() {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        if(showList.size() < 6) {
            for (int i = 0; i < showList.size(); i++) {
                float value = (float)(1.0 * showList.get(i).getUsedTimebyDay() / 1000 / 60);
                yVals1.add(new BarEntry(i, value));
            }
        }
        else {
            for(int i = 0;i < 6;i++) {
                float value = (float)(1.0 * showList.get(i).getUsedTimebyDay() / 1000 / 60 );
                yVals1.add(new BarEntry(i, value));
            }
            long otherTime = 0;
            for(int i=6;i<showList.size();i++) {
                otherTime += showList.get(i).getUsedTimebyDay();
            }
            yVals1.add(new BarEntry(6,(float)(1.0 * otherTime / 1000 / 60)));
        }
        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Different APPs");
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);
            set1.setGradientColors(generateColor(ColorTemplate.JOYFUL_COLORS));
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
        chart.invalidate();
    }

    private ArrayList<GradientColor> generateColor(int[] colors){
        ArrayList<GradientColor> result=new ArrayList<>();
        Arrays.stream(colors).forEach(e->result.add(new GradientColor(Color.WHITE,e)));
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


}

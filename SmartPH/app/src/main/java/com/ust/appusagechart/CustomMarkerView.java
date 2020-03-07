package com.ust.appusagechart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.ust.smartph.R;

public class CustomMarkerView extends MarkerView {

    private TextView barContent;
    public CustomMarkerView(Context context) {
        super(context, R.layout.custom_marker_view);
        barContent = findViewById(R.id.bar_content);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        barContent.setText(String.format("X:%s Y:%s",String.valueOf(e.getX()),String.valueOf(e.getY())));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}


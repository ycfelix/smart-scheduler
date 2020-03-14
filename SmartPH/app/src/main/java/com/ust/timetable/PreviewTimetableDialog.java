package com.ust.timetable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.ust.smartph.R;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PreviewTimetableDialog extends Dialog {


    @BindView(R.id.preview_enter)
    Button enter;

    @BindView(R.id.preview_cancel)
    Button cancel;

    @BindView(R.id.preview_timetable)
    TimetableView timetable;


    public PreviewTimetableDialog(@NonNull Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timetable_preview);
        ButterKnife.bind(this);
        ArrayList<Schedule> result=receiveSchedules("1234");
        result.forEach(e->timetable.add(new ArrayList<>(Arrays.asList(e))));
    }

    private ArrayList<Schedule> receiveSchedules(String token){
        //retrieve schedules from server using the token
        ArrayList<Schedule> result=new ArrayList<>();
        return result;
    }

    @OnClick(R.id.preview_enter)
    void enterPressed(View v){
        //TODO: add to a new timetable
        this.dismiss();
    }

    @OnClick(R.id.preview_cancel)
    void cancelPressed(View v){
        this.dismiss();
    }

}

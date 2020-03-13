package com.ust.timetable;

import android.support.annotation.Nullable;

import com.github.tlaabs.timetableview.Schedule;


public interface EditDialogListener {
    public void onEditResult(@Nullable Schedule schedule, RequestType type);
}

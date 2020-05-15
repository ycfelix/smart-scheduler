package com.ust.timetable;

import com.github.tlaabs.timetableview.Schedule;

import androidx.annotation.Nullable;


public interface EditDialogListener {
    public void onEditResult(@Nullable Schedule schedule, RequestType type);
}

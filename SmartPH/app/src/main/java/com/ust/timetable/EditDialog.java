package com.ust.timetable;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.ust.smartph.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditDialog extends Dialog {

    private EditDialogListener editDialogListener;

    @BindView(R.id.delete_btn)
    Button deleteBtn;

    @BindView(R.id.submit_btn)
    Button submitBtn;

    @BindView(R.id.subject_edit)
    EditText subjectEdit;

    @BindView(R.id.classroom_edit)
    EditText classroomEdit;

    @BindView(R.id.professor_edit)
    EditText professorEdit;

    @BindView(R.id.day_spinner)
    Spinner daySpinner;

    @BindView(R.id.start_time)
    TextView startTv;

    @BindView(R.id.end_time)
    TextView endTv;

    private Schedule schedule;

    private ArrayList<Schedule> editSchedule;

    {
        schedule = new Schedule();
        schedule.setStartTime(new Time(10,0));
        schedule.setEndTime(new Time(13,30));
    }

    private RequestType mode;

    public EditDialog(@NotNull Activity activity,RequestType type) {
        super(activity);
        this.mode=type;
    }
    public EditDialog(@NotNull Activity activity, RequestType type, ArrayList<Schedule> schedules) {
        super(activity);
        this.mode=type;
        this.editSchedule=schedules;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        if(mode==RequestType.EDIT){
            deleteBtn.setVisibility(View.VISIBLE);
        }
        if(this.editSchedule!=null){
            loadScheduleData();
        }
        initView();
    }

    private void initView(){
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schedule.setDay(position);
                if(position>3){
                    schedule.setDay(position-3);
                }
                ((TextView)daySpinner.getSelectedView()).
                        setText(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(),listener,schedule.getStartTime().getHour(), schedule.getStartTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String hhmm=String.format("%d:%02d",hourOfDay,minute);
                    startTv.setText(hhmm);
                    schedule.getStartTime().setHour(hourOfDay);
                    schedule.getStartTime().setMinute(minute);
                }
            };
        });
        endTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(),listener,schedule.getEndTime().getHour(), schedule.getEndTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String hhmm=String.format("%d:%02d",hourOfDay,minute);
                    endTv.setText(hhmm);
                    schedule.getEndTime().setHour(hourOfDay);
                    schedule.getEndTime().setMinute(minute);
                }
            };
        });

    }



    @OnClick({R.id.submit_btn,R.id.delete_btn})
    void finishModifySchedule(View v){
        String subject=subjectEdit.getText().toString();
        schedule.setClassTitle(subjectEdit.getText().toString());
        schedule.setClassPlace(classroomEdit.getText().toString());
        schedule.setProfessorName(professorEdit.getText().toString());
        if(subject.isEmpty()){
            schedule=null;//dont add empty schedule
        }
        //special case to delete item, set the mode to delete and put in onResult
        if(v.getId()==R.id.delete_btn){
            this.mode=RequestType.DELETE;
        }
        this.editDialogListener.onEditResult(schedule,this.mode);
        dismiss();
    }



    public void setEditDialogListener(EditDialogListener listener){
        this.editDialogListener=listener;
    }

    private void loadScheduleData(){
        Schedule schedule=editSchedule.get(0);
        subjectEdit.setText(schedule.getClassTitle());
        classroomEdit.setText(schedule.getClassPlace());
        professorEdit.setText(schedule.getProfessorName());
        daySpinner.setSelection(schedule.getDay());
    }

}

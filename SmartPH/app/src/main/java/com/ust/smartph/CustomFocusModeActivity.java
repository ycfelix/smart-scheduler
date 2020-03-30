package com.ust.smartph;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomFocusModeActivity extends AppCompatActivity {

    @BindView(R.id.focus_timer)
    TextView timer;

    @BindView(R.id.focus_button)
    Button button;

    @BindView(R.id.tree_progress)
    CircularProgressBar progressBar;

    @BindView(R.id.tree_switcher)
    ImageSwitcher switcher;

    private int[] images={R.drawable.s1,R.drawable.s2,R.drawable.s3,R.drawable.s4,R.drawable.s5};

    private int position=0;
    private boolean isCountdown;

    private CountDownTimer countDownTimer;

    private long remaining=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.focusmode_home);
        ButterKnife.bind(this);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView image = new ImageView(getApplicationContext());
                image.setImageDrawable(ContextCompat.getDrawable(
                        CustomFocusModeActivity.this,images[0]));
                image.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.
                        MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return image;
            }
        });
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCountdown) return;
                final Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getDefault());
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog dialog= new TimePickerDialog(CustomFocusModeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int outMinute) {
                        int diff=(hourOfDay*60+outMinute)-(hour*60+minute);
                        if(diff<0){
                            Toast.makeText(CustomFocusModeActivity.this, "wrong input!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            position=0;
                            remaining=TimeUnit.MINUTES.toMillis(diff);
                            setTimer(remaining);
                            switcher.setImageDrawable(ContextCompat.getDrawable(
                                    CustomFocusModeActivity.this, images[0]));
                        }
                    }
                }, hour, minute,
                        true);
                dialog.show();
            }
        });
    }

    private void stopTimer(){
        isCountdown=false;
        countDownTimer.cancel();
    }

    @OnClick(R.id.focus_button)
    void buttonClick(View v){
        if(remaining<=0){
            Toast.makeText(this, "set a time range first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isCountdown){
            startTimer(remaining);
            isCountdown=true;
            button.setText("Abort!");
        }
        else{
            isCountdown=false;
            stopTimer();
            button.setText("Start!");
        }
    }

    private void setTimer(long millisUntilFinished){
        String hms = String.format(Locale.US,
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
        timer.setText(hms);//set text
    }


    private void startTimer(long millis) {
        countDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                remaining=millisUntilFinished;
                float percentage=(1.0f-(float)(millisUntilFinished)/(float)millis)*100;
                progressBar.setProgress(percentage);
                int index=(int)(percentage/20);
                if(position!=index &&index>=0 &&index<images.length){
                    position=index;
                    switcher.setImageDrawable(ContextCompat.getDrawable(
                            CustomFocusModeActivity.this, images[position]));
                }
                //Convert milliseconds into hour,minute and seconds
                setTimer(remaining);
            }

            public void onFinish() {
                timer.setText("WELL DONE!");
                countDownTimer.cancel();
                countDownTimer=null;
                isCountdown=false;
                progressBar.setProgress(100);
                switcher.setImageDrawable(ContextCompat.getDrawable(
                        CustomFocusModeActivity.this, images[images.length-1]));
                button.setText("Start!");
            }
        }.start();
    }

}

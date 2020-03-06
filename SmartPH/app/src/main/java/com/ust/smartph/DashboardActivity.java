package com.ust.smartph;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_home);
    }

    public void startAboutUs(View arg){
        startActivity(new Intent(this, AboutUsActivity.class));
    }
    public void startCalendar(View arg){
        startActivity(new Intent(this, CalendarActivity.class));
    }
    public void startTimetable(View arg){
        startActivity(new Intent(this, TimetableActivity.class));
    }
    public void startChecklist(View arg){
        startActivity(new Intent(this, ChecklistActivity.class));
    }
    public void startAppUsage(View arg){
        startActivity(new Intent(this, AppUsageHomeActivity.class));
    }

    public void toBeContinue(View arg){
        Toast.makeText(this.getApplicationContext(),"coming soon",Toast.LENGTH_SHORT).show();
    }
    public void startFocusMode(View arg){
        startActivity(new Intent(this, FocusModeActivity.class));
    }

    public void startActionDetection(View arg){
        startActivity(new Intent(this, CustomActionDetectionActivity.class));
    }
}

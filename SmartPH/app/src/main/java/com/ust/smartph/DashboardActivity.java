package com.ust.smartph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ust.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {
    private final String TAG = "DASHBOARD";

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
        startActivity(new Intent(this, TimetableHomeActivity.class));
    }
    public void startChecklist(View arg){
        startActivity(new Intent(this, ChecklistHomeActivity.class));
    }
    public void startAppUsage(View arg){
        startActivity(new Intent(this, AppUsageHomeActivity.class));
    }

    public void toBeContinue(View arg){
        Toast.makeText(this.getApplicationContext(),"coming soon",Toast.LENGTH_SHORT).show();
    }
    public void startFocusMode(View arg){
        startActivity(new Intent(this, CustomFocusModeActivity.class));
    }

    public void startActionDetection(View arg){
        startActivity(new Intent(this, UserActionActivity.class));
    }

    public void logout(View arg) {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_LONG).show();
        SharedPreferences sp = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
//        try {
//            JSONObject email = new JSONObject();
//            email.put("email", sp.getString("email", null));
//            connectServer(email, getString(R.string.logout_api), new VolleyCallback() {
//                @Override
//                public void onSuccess(JSONObject result) {
//
//                }
//
//                @Override
//                public void onFailure() {
//                    // nothing to do ...
//                }
//            });
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        finally {
            editor.remove("email");
            editor.remove("hashed_pwd");
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
//        }
    }

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
//        finish();
    }
}

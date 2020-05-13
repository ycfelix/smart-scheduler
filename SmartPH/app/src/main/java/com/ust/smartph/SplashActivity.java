package com.ust.smartph;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.ust.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SPLASH";

    @BindView(R.id.loginText)
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);
        final int delayMS = 2000;
        final int timeOut = 10000; // 10 secounds
        SharedPreferences sp = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
        String emailStr = sp.getString("email", null);
        String passStr = sp.getString("hashed_pwd", null);
        JSONObject accInfo = new JSONObject();
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Toast.makeText(SplashActivity.this, inputMessage.obj.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Message = " + inputMessage.obj.toString());
            }
        };
        Log.d(TAG, "emailStr = " + emailStr + "; passStr = " + passStr);

        //delay thread
        Thread th=new Thread(){
            @Override
            public void run() {
            long begin = System.currentTimeMillis();
            while (!canSwitch(begin,1000));
            loginText.setText(getString(R.string.login_hello));

            begin = System.currentTimeMillis();
            while (!canSwitch(begin,delayMS));

            loginText.setText(getString(R.string.loginText));
            try{
                if (emailStr == null || passStr == null) {
                    Message msg = mHandler.obtainMessage(0, "You need to login first");
                    msg.sendToTarget();
                    startActivity(loginIntent);
                } else {
                    accInfo.put("email", emailStr);
                    accInfo.put("hashed_pwd", passStr);
                    Utils.connectServer(accInfo, getString(R.string.login_api),
                            timeOut, Request.Method.POST, SplashActivity.this,
                        new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                String resultStr = result.getString("result");
                                int error_code = result.getInt("error_code");
                                Log.d(TAG, "resultStr = " + resultStr);
                                Log.d(TAG, "error_code = " + error_code);
                                if (error_code == -1 ) {
                                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                                }
                                else {
                                    // Indicate authentication error
                                    Log.d(TAG, "Authentication failed...");
                                    Message msg = mHandler.obtainMessage(0, "Authentication failed. Please login again.");
                                    msg.sendToTarget();
                                    startActivity(loginIntent);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, "JSON retrieve result failed!");
                                Message msg = mHandler.obtainMessage(0, "Unexpected error occurred. Please login again." +
                                        "\nError: JSON_Retrieve_Result_Error");
                                msg.sendToTarget();
                                startActivity(loginIntent);
                            }
                        }
                        @Override
                        public void onFailure() {
                            Log.d(TAG, "Failed to check!");
                            Message msg = mHandler.obtainMessage(0, "Unexpected error occurred. Please login again." +
                                    "\nError: Server_Error");
                            msg.sendToTarget();
                            startActivity(loginIntent);
                        }
                    });
                }
            } catch (JSONException e) {
                Log.d(TAG,  "Error in assigning account info. Go to login instead");
                Message msg = mHandler.obtainMessage(0, "Unexpected error occurred. Please login again." +
                        "\nError: JSON_Parse_accInfo_Error");
                msg.sendToTarget();
                startActivity(loginIntent);
            }
            finally {
                finish();
            }
            }
        };

        th.start();
    }
    private boolean canSwitch(long begin,int ms){
        long now=System.currentTimeMillis();
        if(now-begin>ms){
            return true;
        }
        return false;
    }

}


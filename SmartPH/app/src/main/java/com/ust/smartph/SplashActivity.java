package com.ust.smartph;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //delay thread
        Thread th=new Thread(){
            @Override
            public void run() {
                long begin=System.currentTimeMillis();
                while (!canSwitch(begin,5000));
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
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


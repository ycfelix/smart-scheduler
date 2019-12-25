package com.ust.smartph;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delay(3000);
        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        // close splash activity

        finish();
    }
    private void delay(int ms){
        long begin=System.currentTimeMillis();
        while(System.currentTimeMillis()-begin<ms);
    }
}


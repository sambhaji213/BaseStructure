package com.basestructure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.basestructure.R;
import com.basestructure.util.AppAndroidUtils;


public class SplashScreen extends AppCompatActivity {

    public static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                AppAndroidUtils.startFwdAnimation(SplashScreen.this);
                finish();
            }
        }, 3000);

    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}

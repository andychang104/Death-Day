package com.cactime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by andy on 2018/3/2.
 */

public class SplashActivity extends AppCompatActivity {

    private static  int SPLASH_TIM_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String packageName = getPackageName();
        Log.e("@@@@@@@","@@@@@@@@-"+packageName);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, LogInActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIM_OUT);

    }
}

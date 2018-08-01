package com.cactime.shortcuts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cactime.LogInActivity;
import com.cactime.MainActivity;


public class ShortcutsNewFutureActivity extends Activity{

    private SharedPreferences sp;
    private String Uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("CacUserData", MODE_PRIVATE);
        Uid = sp.getString("Uid", Uid);

        if(Uid.length() == 0){
            Intent homeIntent = new Intent(ShortcutsNewFutureActivity.this, LogInActivity.class);
            startActivity(homeIntent);
            finish();
        }
        else{
            Intent homeIntent = new Intent(ShortcutsNewFutureActivity.this, MainActivity.class);
            homeIntent.putExtra("uid", Uid);
            homeIntent.putExtra("type", "futurenew");
            startActivity(homeIntent);
            finish();
        }
    }
}

package com.cactime.shortcuts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cactime.LogInActivity;
import com.cactime.MainActivity;

public class ShortcutsDeaireActivity extends Activity {

    private SharedPreferences sp;
    private String Uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("CacUserData", MODE_PRIVATE);
        Uid = sp.getString("Uid", Uid);

        if(Uid.length() == 0){
            Intent homeIntent = new Intent(ShortcutsDeaireActivity.this, LogInActivity.class);
            startActivity(homeIntent);
            finish();
        }
        else{
            Intent homeIntent = new Intent(ShortcutsDeaireActivity.this, MainActivity.class);
            homeIntent.putExtra("uid", Uid);
            homeIntent.putExtra("type", "deaire");
            startActivity(homeIntent);
            finish();
        }
    }
}

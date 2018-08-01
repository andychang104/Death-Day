package com.cactime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class OtherActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private TextView tv_number;
    private TextView tv_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();

        setUI();
    }

    @SuppressLint("StringFormatMatches")
    private void setUI() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherActivity.this.finish();
            }
        });

        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);

        tv_number.setText(getString(R.string.other_number)+getLocalVersionName(mContext));

        tv_bottom.setText(String.format(getString(R.string.other_bottom_msg), Calendar.getInstance().get(Calendar.YEAR)));

    }


    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

}

package com.cactime;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cactime.api.PastData;

/**
 * Created by andy on 2018/3/2.
 */

public class EditManDataActivity extends AppCompatActivity {

    private Context mContext;

    private Toolbar toolbar;

    private LinearLayout llt_birthday;
    private LinearLayout llt_sex;

    private TextView tv_birthday;
    private TextView tv_sex_title;
    private TextView tv_sex;

    private EditText edit_name_title;

    private Button btn_put;
    private Button btn_push;

    private Calendar calendar;

    private DatePickerDialog datePickerDialog;

    private boolean isPush = false;

    private int mYear;
    private int mMonth;
    private int mDay;

    private String userName = "";
    private String Sex = "";

    private final int TAG_PUT = 0, TAG_BIRTHDAY = 1, TAG_SEX = 2, TAG_PUSH = 3;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_man_data);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();
        setUI();
    }

    private void setUI() {

        sp = getSharedPreferences("CacUserData", MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditManDataActivity.this.finish();
            }
        });

        edit_name_title = (EditText) findViewById(R.id.edit_name_title);

        btn_put = (Button) findViewById(R.id.btn_put);
        btn_push = (Button) findViewById(R.id.btn_push);

        llt_birthday = (LinearLayout) findViewById(R.id.llt_birthday);
        llt_sex = (LinearLayout) findViewById(R.id.llt_sex);

        tv_sex = (TextView) findViewById(R.id.tv_sex);

        tv_birthday = (TextView) findViewById(R.id.tv_birthday);

        llt_birthday.setTag(TAG_BIRTHDAY);
        llt_birthday.setOnClickListener(onClickListener);
        btn_push.setTag(TAG_PUSH);
        btn_push.setOnClickListener(onClickListener);
        btn_put.setTag(TAG_PUT);
        btn_put.setOnClickListener(onClickListener);
        llt_sex.setTag(TAG_SEX);
        llt_sex.setOnClickListener(onClickListener);

        getData();
    }

    private void getData() {

        mYear = sp.getInt("mYear", mYear);
        mMonth = sp.getInt("mMonth", mMonth);
        mDay = sp.getInt("mDay", mDay);
        userName = sp.getString("UserName", userName);
        Sex = sp.getString("Sex", Sex);
        isPush = sp.getBoolean("Push", isPush);


        if(calendar == null){
            calendar = Calendar.getInstance();
        }

        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.MONTH, (mMonth));
        calendar.set(Calendar.DAY_OF_MONTH, mDay);

        String format = mYear+getString(R.string.index_year)+(mMonth+1)+getString(R.string.index_month)+mDay+getString(R.string.index_day);

        edit_name_title.setText(userName);
        tv_birthday.setText(format);
        tv_sex.setText(Sex);

        if(isPush){
            isPush = true;
            btn_push.setBackgroundResource(R.drawable.btn_switch_on);
        }
        else{
            isPush = false;
            btn_push.setBackgroundResource(R.drawable.btn_switch_off);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            int tag = (Integer) v.getTag();
            switch (tag) {
                case TAG_PUT:// 完成
                    String errorMsg = getErrorMsg();
                    if(errorMsg.length() == 0){
                        setData();
                        intent = new Intent();
                        setResult(RESULT_OK, intent);
                        EditManDataActivity.this.finish();
                    }
                    else{
                        new AlertDialog.Builder(EditManDataActivity.this)
                                .setTitle(getString(R.string.dialog_title))
                                .setMessage(errorMsg)
                                .setPositiveButton(getString(R.string.dialog_ok_btn), null)
                                .show();
                    }
                    break;
                case TAG_BIRTHDAY:// 選擇生日
                    closekeyboard();
                    if(calendar == null){
                        calendar = Calendar.getInstance();
                    }
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(EditManDataActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {

                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, day);

                            SharedPreferences.Editor specheck = sp.edit();
                            specheck.putInt("mYear", year);
                            specheck.putInt("mMonth", month);
                            specheck.putInt("mDay", day);
                            specheck.commit();


                            mYear = year;
                            mMonth = (month+1);
                            mDay = day;
                            String format = mYear+getString(R.string.index_year)+mMonth+getString(R.string.index_month)+mDay+getString(R.string.index_day);
                            tv_birthday.setText(format);
                        }

                    }, mYear,mMonth, mDay);



                    datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

                    datePickerDialog.show();
                    break;
                case TAG_SEX:// 性別
                    closekeyboard();
                    final String [] sexdata = getResources().getStringArray(R.array.sexTitle);
                    new AlertDialog.Builder(EditManDataActivity.this)
                            .setTitle(getString(R.string.index_sex_msg))
                            .setItems(sexdata, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tv_sex.setText(sexdata[which]);
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                    break;
                case TAG_PUSH:// 通知
                    if(isPush){
                        isPush = false;
                        btn_push.setBackgroundResource(R.drawable.btn_switch_off);
                    }
                    else{
                        isPush = true;
                        btn_push.setBackgroundResource(R.drawable.btn_switch_on);
                    }
                    break;

            }
        }
    };

    private void setData() {
        SharedPreferences.Editor specheck = sp.edit();
        specheck.putString("Sex", tv_sex.getText().toString());
        specheck.putString("UserName", edit_name_title.getText().toString());
        specheck.putBoolean("Push", isPush);
        specheck.commit();
    }


    //檢核欄位
    private String getErrorMsg(){
        String errorMsg = "";
        if(edit_name_title.getText().toString().replace(" ", "").replace("\n", "").equals("")) {
            errorMsg = getString(R.string.newday_new_msg_hint);
        }

        if(tv_birthday.getText().toString().length() == 0){
            errorMsg = setErrorMsg(errorMsg, getString(R.string.newday_new_day_hint));
        }

        return errorMsg;
    }

    //組合錯誤訊息
    public String setErrorMsg(String errorMsg, String newMsg){

        if(errorMsg.length() > 0){
            errorMsg = errorMsg+"\n"+newMsg;
        }
        else{
            errorMsg = newMsg;
        }

        return errorMsg;
    }



    private void closekeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if(EditManDataActivity.this.getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(EditManDataActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

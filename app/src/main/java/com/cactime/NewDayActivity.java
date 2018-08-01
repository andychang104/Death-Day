package com.cactime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cactime.api.PastData;
import com.cactime.util.ListDataSave;

/**
 * Created by andy on 2018/2/26.
 */

public class NewDayActivity extends AppCompatActivity{

    private Context mContext;

    private Toolbar toolbar;

    private LinearLayout llt_day;

    private Button btn_switch;
    private Button btn_put;
    private Button btn_push;
    private Button btn_del;

    private TextView tv_day;
    private TextView tv_title;
    private TextView tv_push_title;

    private boolean isSwitch = false;
    private boolean isPush = false;

    private Calendar calendar;

    private EditText edit_name_title;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int Type;

    private List<PastData> pastData;

    private DatePickerDialog datePickerDialog;

    private ListDataSave dataSave;


    private final int TAG_PUT = 0, TAG_DAY = 1, TAG_SWITCH = 2, TAG_PUSH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_day);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();
        setUI();
    }

    private void setUI() {

        Type = getIntent().getIntExtra("Type", Type);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDayActivity.this.finish();
            }
        });

        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_del = (Button) findViewById(R.id.btn_del);
        llt_day = (LinearLayout) findViewById(R.id.llt_day);
        tv_day = (TextView) findViewById(R.id.tv_day);
        btn_switch = (Button) findViewById(R.id.btn_switch);
        tv_push_title = (TextView) findViewById(R.id.tv_push_title);
        btn_push = (Button) findViewById(R.id.btn_push);

        edit_name_title = (EditText) findViewById(R.id.edit_name_title);
        btn_put = (Button) findViewById(R.id.btn_put);

        btn_del.setVisibility(View.GONE);

        llt_day.setTag(TAG_DAY);
        llt_day.setOnClickListener(onClickListener);
        btn_switch.setTag(TAG_SWITCH);
        btn_switch.setOnClickListener(onClickListener);
        btn_push.setTag(TAG_PUSH);
        btn_push.setOnClickListener(onClickListener);
        btn_put.setTag(TAG_PUT);
        btn_put.setOnClickListener(onClickListener);

        if(Type == 1){
            dataSave = new ListDataSave (mContext, "NewDay1");
            pastData = dataSave.getDataList("DataList1");
            tv_title.setText(getString(R.string.newday_new_title1));
            tv_push_title.setText(getString(R.string.newday_new_push1));
        }
        else if(Type == 2){
            dataSave = new ListDataSave (mContext, "NewDay2");
            pastData = dataSave.getDataList("DataList2");
            tv_title.setText(getString(R.string.newday_new_title2));
            tv_push_title.setText(getString(R.string.newday_new_push2));
        }


        if(pastData == null){
            pastData = new ArrayList<PastData>();
        }

    }

    private void setData() {
        PastData item = new PastData();
        item.setItemName(edit_name_title.getText().toString());
        item.setItemYear(mYear);
        item.setItemMonth(mMonth);
        item.setItemDay(mDay);
        item.setItemIsTop(isSwitch);
        item.setItemIsPush(isPush);

        if(isSwitch){
            pastData.add(0,item);
        }
        else{
            pastData.add(item);
        }

        if(Type == 1){
            dataSave.setDataList("DataList1", pastData);
        }
        else if(Type == 2){
            dataSave.setDataList("DataList2", pastData);
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
                        NewDayActivity.this.finish();
                    }
                    else{
                        new AlertDialog.Builder(NewDayActivity.this)
                                .setTitle(getString(R.string.dialog_title))
                                .setMessage(errorMsg)
                                .setPositiveButton(getString(R.string.dialog_ok_btn), null)
                                .show();
                    }
                    break;
                case TAG_DAY:// 日期
                    closekeyboard();
                    if(calendar == null){
                        calendar = Calendar.getInstance();
                    }
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(NewDayActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {

                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, day);


                            mYear = year;
                            mMonth = (month+1);
                            mDay = day;
                            String format = mYear+getString(R.string.index_year)+mMonth+getString(R.string.index_month)+mDay+getString(R.string.index_day);
                            tv_day.setText(format);
                        }

                    }, mYear,mMonth, mDay);


                    if(Type == 1){
                        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                    }
                    else if(Type == 2){
                        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                    }
                    datePickerDialog.show();
                    break;
                case TAG_SWITCH:// 置頂開關
                    if(isSwitch){
                        isSwitch = false;
                        btn_switch.setBackgroundResource(R.drawable.btn_switch_off);
                    }
                    else{
                        isSwitch = true;
                        btn_switch.setBackgroundResource(R.drawable.btn_switch_on);
                    }
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

    //檢核欄位
    private String getErrorMsg(){
        String errorMsg = "";
        if(edit_name_title.getText().toString().replace(" ", "").replace("\n", "").equals("")) {
            errorMsg = getString(R.string.newday_new_msg_hint);
        }

        if(tv_day.getText().toString().length() == 0){
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
        if(NewDayActivity.this.getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(NewDayActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closekeyboard();
    }

}

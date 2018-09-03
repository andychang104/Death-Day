package com.cactime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cactime.itemclass.DesireData;
import com.cactime.itemclass.UserData;
import com.cactime.service.NotificationService;
import com.cactime.service.PlayReceiver;
import com.cactime.util.AllClass;
import com.cactime.util.FireDataBaseUtil;
import com.cactime.util.ListDataSave;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewpager;

    private ImageView iv_face;
    private ImageView iv_tab_past;
    private ImageView iv_tab_future;
    private ImageView iv_all_bg;

    private RelativeLayout rlt_checkbg;
    private RelativeLayout rlt_tab_past;
    private RelativeLayout rlt_tab_future;
    private RelativeLayout rlt_setting;

    private LinearLayout llt_desire_bg;

    private EditText edit_username;

    private PagerAdapter mPagerAdapter;

    private TextView tv_birthday;
    private TextView tv_sex;
    private TextView tv_all_day;
    private TextView tv_all_day_title;
    private TextView tv_username;
    private TextView tv_usersex;
    private TextView tv_desire;

    private Button btn_back;
    private Button btn_output;
    private Button btn_change;

    private ScrollView scroll_userdata_bg;

    private Locale locale;

    private boolean isYear = false;

    private Calendar calendar;

    private DatePickerDialog datePickerDialog;

    private String localeString = "";
    private String startDay = "";
    private String overDay = "";
    private String userName = "";
    private String Sex = "";
    public String uid = "";

    private int mYear;
    private int mMonth;
    private int mDay;

    private int imageType;
    private int aspectX;
    private int aspectY;
    private int outputX;
    private int outputY;

    private MenuItem nav_logout;

    private final int TAG_BACK = 0, TAG_OUTPUT = 1, TAG_BIRTHDAY = 2, TAG_SEX = 3, TAG_CHANG = 4, TAG_FACE = 5, TAG_DESIRE = 6;

    private SharedPreferences sp;


    private final int edituserdata = 200, requestcodepick = 300, requestcodecutting = 400, desirelist = 500, editdesirelist = 600;

    private static int REQUEST_CODE = 0;

    private Uri imageUri;

    private List<String> forbidAutoRunList = new ArrayList<String>();

    private UserData userData = new UserData();

    private FireDataBaseUtil fireDataBaseUtil = new FireDataBaseUtil();

    private ArrayList<String> desireList;
    private ArrayList<DesireData> checkdesireList;

    private AllClass allClass = new AllClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);

        Menu menu = navigationView.getMenu();
        nav_logout = menu.findItem(R.id.nav_logout);

        rlt_checkbg = (RelativeLayout) findViewById(R.id.rlt_checkbg);
        rlt_setting = (RelativeLayout) findViewById(R.id.rlt_setting);

        viewpager = (ViewPager) findViewById(R.id.viewpager);

        iv_tab_past = (ImageView) findViewById(R.id.iv_tab_past);
        iv_tab_future = (ImageView) findViewById(R.id.iv_tab_future);
        iv_all_bg = (ImageView) findViewById(R.id.iv_all_bg);

        rlt_tab_past = (RelativeLayout) findViewById(R.id.rlt_tab_past);
        rlt_tab_future = (RelativeLayout) findViewById(R.id.rlt_tab_future);

        scroll_userdata_bg = (ScrollView) findViewById(R.id.scroll_userdata_bg);
        edit_username = (EditText) findViewById(R.id.edit_username);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        llt_desire_bg = (LinearLayout) findViewById(R.id.llt_desire_bg);
        tv_desire = (TextView) findViewById(R.id.tv_desire);
        tv_all_day = (TextView) findViewById(R.id.tv_all_day);
        tv_all_day_title = (TextView) findViewById(R.id.tv_all_day_title);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_output = (Button) findViewById(R.id.btn_output);
        btn_change = (Button) findViewById(R.id.btn_change);

        iv_face = (ImageView) header.findViewById(R.id.iv_face);
        tv_username = (TextView) header.findViewById(R.id.tv_username);
        tv_usersex = (TextView) header.findViewById(R.id.tv_usersex);

        uid = getIntent().getStringExtra("uid");

        setUi();
    }

    private void setUi() {
        locale = Locale.getDefault();
        localeString = locale.getLanguage()+"-"+locale.getCountry();

        Log.d("localelocale", "localelocale-"+localeString);

        viewpager.setOffscreenPageLimit(2);

        mPagerAdapter = new PagerAdapter(MainActivity.this);
        mPagerAdapter.addTab(PastFragment.class, null);
        mPagerAdapter.addTab(FutureFragment.class, null);

        viewpager.setAdapter(mPagerAdapter);

        rlt_tab_past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                viewpager.setCurrentItem(0);
            }
        });
        rlt_tab_future.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                viewpager.setCurrentItem(1);
            }
        });

        viewpager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                if(position == 0){
                    iv_tab_past.setVisibility(View.VISIBLE);
                    iv_tab_future.setVisibility(View.GONE);
                }
                else if(position == 1){
                    iv_tab_past.setVisibility(View.GONE);
                    iv_tab_future.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_birthday.setTag(TAG_BIRTHDAY);
        tv_birthday.setOnClickListener(onClickListener);
        tv_sex.setTag(TAG_SEX);
        tv_sex.setOnClickListener(onClickListener);
        btn_back.setTag(TAG_BACK);
        btn_back.setOnClickListener(onClickListener);
        btn_output.setTag(TAG_OUTPUT);
        btn_output.setOnClickListener(onClickListener);
        btn_change.setTag(TAG_CHANG);
        btn_change.setOnClickListener(onClickListener);
        iv_face.setTag(TAG_FACE);
        iv_face.setOnClickListener(onClickListener);
        tv_desire.setTag(TAG_DESIRE);
        tv_desire.setOnClickListener(onClickListener);

        sp = getSharedPreferences("CacUserData", MODE_PRIVATE);

        if(uid != null){
            if(uid.length() != 0){
                sp.edit().putString("Uid", uid).commit();
            }
        }

        String bgData = "";
        String faceData = "";
        bgData = sp.getString("BgData", bgData);
        faceData = sp.getString("FaceData", faceData);

        if(bgData != null){
            if(bgData.length() != 0){
                Bitmap bg = base64ToBitmap(bgData);
                iv_all_bg.setImageBitmap(bg);
            }
        }

        if(faceData != null){
            if(faceData.length() != 0){
                Bitmap bg = base64ToBitmap(faceData);
                iv_face.setImageBitmap(bg);
            }
        }

        startPush2();
        getData();

        if(getIntent().getStringExtra("type") != null){
            if(getIntent().getStringExtra("type").equals("futurenew")){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NewDayActivity.class);
                intent.putExtra("Type", 2);
                startActivityForResult(intent, 100);
            }
            else if(getIntent().getStringExtra("type").equals("pastnew")){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NewDayActivity.class);
                intent.putExtra("Type", 1);
                startActivityForResult(intent, 100);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.dialog_back_title))
                    .setMessage(getString(R.string.dialog_back_msg))
                    .setPositiveButton(getString(R.string.dialog_ok_btn), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_no_btn), null)
                    .show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, EditManDataActivity.class);
            startActivityForResult(intent, edituserdata);
            return true;
        }
        else if(id == R.id.image_settings){
            imageType = 1;
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, requestcodepick);
                setImageSetting();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
            return true;
        }
        else if(id == R.id.desireList_settings){
            if(uid.equals(getString(R.string.nologin_id))){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_title))
                        .setMessage(getString(R.string.dialog_no_login_main))
                        .setPositiveButton(getString(R.string.dialog_no_login_main_btnok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //                                sp.edit().clear().commit();
                                //                                getSharedPreferences("NewDay1", MODE_PRIVATE).edit().clear().commit();
                                //                                getSharedPreferences("NewDay2", MODE_PRIVATE).edit().clear().commit();

                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, LogInActivity.class);
                                intent.putExtra("type", "Experience");
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
            else{
                MainApp.userDesireData = checkdesireList;
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EditDesireListActivity.class);
                //intent.putStringArrayListExtra("checkdesireList", checkdesireList);
                startActivityForResult(intent, editdesirelist);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, PersonalActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, OtherActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            shareText(this, "https://play.google.com/store/apps/details?id=com.m104vip");
        } else if (id == R.id.nav_send) {
            if(uid.equals(getString(R.string.nologin_id))){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_title))
                        .setMessage(getString(R.string.dialog_no_login_main))
                        .setPositiveButton(getString(R.string.dialog_no_login_main_btnok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //                                sp.edit().clear().commit();
                                //                                getSharedPreferences("NewDay1", MODE_PRIVATE).edit().clear().commit();
                                //                                getSharedPreferences("NewDay2", MODE_PRIVATE).edit().clear().commit();

                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, LogInActivity.class);
                                intent.putExtra("type", "Experience");
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
            else {
                MainApp.userDesireData = checkdesireList;
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EditDesireListActivity.class);
                //intent.putStringArrayListExtra("checkdesireList", checkdesireList);
                startActivityForResult(intent, editdesirelist);
            }
        } else if (id == R.id.nav_logout) {

            if(uid.equals(getString(R.string.nologin_id))){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_title))
                        .setMessage(getString(R.string.Dialog_logout_msg2))
                        .setPositiveButton(getString(R.string.dialog_ok_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

//                                sp.edit().clear().commit();
//                                getSharedPreferences("NewDay1", MODE_PRIVATE).edit().clear().commit();
//                                getSharedPreferences("NewDay2", MODE_PRIVATE).edit().clear().commit();

                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, LogInActivity.class);
                                intent.putExtra("type", "Experience");
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
            else{
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_title))
                        .setMessage(getString(R.string.Dialog_logout_msg))
                        .setPositiveButton(getString(R.string.dialog_ok_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                // Google sign out
                                MainApp.mGoogleSignInClient.signOut();
                                if(sp != null){
                                    sp.edit().clear().commit();
                                }
                                MainApp.desireData = new ArrayList<DesireData>();
//                                getSharedPreferences("NewDay1", MODE_PRIVATE).edit().clear().commit();
//                                getSharedPreferences("NewDay2", MODE_PRIVATE).edit().clear().commit();

                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, LogInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            int tag = (Integer) v.getTag();
            switch (tag) {
                case TAG_BACK:// 離開
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    // Google sign out
                    MainApp.mGoogleSignInClient.signOut();
//                    sp.edit().clear().commit();
//                    getSharedPreferences("NewDay1", MODE_PRIVATE).edit().clear().commit();
//                    getSharedPreferences("NewDay2", MODE_PRIVATE).edit().clear().commit();
                    MainApp.desireData = new ArrayList<DesireData>();
                    intent.setClass(MainActivity.this, LogInActivity.class);
                    intent.putExtra("type", "Back");
                    startActivity(intent);
                    finish();
                    break;
                case TAG_OUTPUT:// 送出
                    String errorMsg = checkData();
                    if(errorMsg.length() == 0){
                        rlt_checkbg.setVisibility(View.GONE);
                        rlt_setting.setVisibility(View.GONE);
                        startDay(isYear);
                        SharedPreferences.Editor specheck = sp.edit();
                        specheck.putBoolean("Push", true);
                        specheck.commit();
                    }
                    else{
                        new AlertDialog.Builder(MainActivity.this)
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
                    datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                case TAG_SEX://選擇性別
                    closekeyboard();
                    final String [] sexdata = getResources().getStringArray(R.array.sexTitle);
                    new AlertDialog.Builder(MainActivity.this)
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
                case TAG_CHANG://年/日顯示切換
                    SharedPreferences.Editor specheck = sp.edit();
                    if(isYear){
                        isYear = false;
                        specheck.putBoolean("isYear", isYear);

                    }
                    else{
                        isYear = true;
                        specheck.putBoolean("isYear", isYear);
                    }
                    specheck.commit();
                    setData();
                    break;
                case TAG_FACE://大頭照設定
                    imageType = 2;
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(pickIntent, requestcodepick);
                        setImageSetting();
                    }
                    else{
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                    }
                    break;
                case TAG_DESIRE:
                    closekeyboard();
                    allClass.getDesireList(MainActivity.this, desireList, localeString);
                    break;
            }
        }
    };

    private String checkData(){
        String errorMsg = "";

        if(edit_username.getText().toString().replace(" ", "").replace("\n", "").equals("")){
            errorMsg = getString(R.string.dialog_username_error);;
        }
        else if(tv_birthday.getText().toString().length() == 0){
            errorMsg = getString(R.string.dialog_birthday_error);
        }
        else if(tv_sex.getText().toString().length() == 0){
            errorMsg = getString(R.string.dialog_sex_error);
        }
        else if(tv_desire.getText().toString().replace(" ", "").replace("\n", "").equals("")){
            if(!uid.equals(getString(R.string.nologin_id))){
                errorMsg = getString(R.string.dialog_desire_error);
            }
        }

        return errorMsg;
    }


    private void startDay(boolean Type){

        int life = 76;

        if(tv_sex.getText().toString().equals(getString(R.string.index_mr))){
            life = 76;
            if(localeString.equals("zh-TW")){
                life = 76;
            }
            else if(localeString.equals("zh-CN")){
                life = 74;
            }
            else if(localeString.equals("zh-HK")){
                life = 80;
            }
            else if(localeString.equals("ko-KR")){
                life = 79;
            }
            else if(localeString.equals("ja-JP")){
                life = 81;
            }
        }
        else if(tv_sex.getText().toString().equals(getString(R.string.index_miss)))
        {
            life = 81;
            if(localeString.equals("zh-TW")){
                life = 86;
            }
            else if(localeString.equals("zh-CN")){
                life = 77;
            }
            else if(localeString.equals("zh-HK")){
                life = 84;
            }
            else if(localeString.equals("ko-KR")){
                life = 86;
            }
            else if(localeString.equals("ja-JP")){
                life = 87;
            }
        }

        int year = mYear;

        calendar.add(Calendar.YEAR, life);

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        overDay = mYear+"-"+(mMonth+1)+"-"+mDay;
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");

        SharedPreferences.Editor specheck = sp.edit();
        try {

            Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
            startDay = format.format(curDate);

            Date beginDate= format.parse(startDay);
            Date endDate= format.parse(overDay);
            long day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
            specheck.putLong("AllDay", day);

            if(day<0){
                tv_all_day_title.setText(getString(R.string.index_allday_title));
            }
            else{
                tv_all_day_title.setText(getString(R.string.index_title_msg));
            }

            long daymain = Math.abs(day);
            day = (int) daymain;

            if(!Type){
                tv_all_day.setText(day+getString(R.string.index_day2));
            }
            else{
                if(day < 365){
                    Toast.makeText(this,getText(R.string.toast_year_error),Toast.LENGTH_SHORT).show();
                }
                else{

                    String stringyear = "";
                    String stringday = "";

                    double tiastday  = (endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
                    tiastday = (tiastday/365);
                    String stringtiastday  = String.format("%.3f", (tiastday));
                    if(stringtiastday.indexOf(".")>0){
                        stringyear = stringtiastday.substring(0, stringtiastday.indexOf("."));
                        stringday = stringtiastday.substring(stringtiastday.indexOf("."), stringtiastday.indexOf(".") + 4);
                        stringday = "0"+stringday;
                        stringday =  totalMoney((Double.parseDouble(stringday)*365));
                    }
                    else{
                        stringyear = stringtiastday;
                        stringday = "0";
                    }




                    stringyear = stringyear.replaceAll("-","");

                    if(stringday.equals("0")){
                        tv_all_day.setText(stringyear+getString(R.string.index_year));
                    }
                    else{
                        tv_all_day.setText(stringyear+getString(R.string.index_year)+stringday+getString(R.string.index_day2));                    }


                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        userName = edit_username.getText().toString();
        Sex = tv_sex.getText().toString();

        specheck.putString("Sex", Sex);
        specheck.putString("UserName", userName);

        if(sp.getBoolean("Push", false)){
            specheck.putBoolean("Push", true);
        }
        else{
            specheck.putBoolean("Push", false);
        }

        specheck.commit();

        tv_username.setText(edit_username.getText().toString());
        tv_usersex.setText(tv_sex.getText().toString());

        if(!uid.equals(getString(R.string.nologin_id))){
            setUserData(uid, year, mMonth, mDay, userName, Sex, isYear);
        }
    }

    private void closekeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if(MainActivity.this.getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

        private final Context mContext;

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {

            private final Class<?> mClss;
            private final Bundle mArgs;

            TabInfo(Class<?> aClass, Bundle args) {
                mClss = aClass;
                mArgs = args;

            }
        }

        public PagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
        }

        @Override
        public int getCount() {

            return mTabs.size();


        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.mClss.getName(),
                    info.mArgs);
        }

        public void addTab(Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            mTabs.add(info);
            notifyDataSetChanged();
        }

    }

    private void setData() {
        mYear = sp.getInt("mYear", mYear);
        mMonth = sp.getInt("mMonth", mMonth);
        mDay = sp.getInt("mDay", mDay);
        userName = sp.getString("UserName", userName);
        Sex = sp.getString("Sex", Sex);
        isYear = sp.getBoolean("isYear", isYear);

        if(!uid.equals(getString(R.string.nologin_id))){
            setUserData(uid, mYear, mMonth, mDay, userName, Sex, isYear);
        }

        if(isYear){
            btn_change.setText(getText(R.string.index_day));
        }
        else{
            btn_change.setText(getText(R.string.index_year));
        }

        if(mYear != 0 && mYear != 0 && mDay != 0 && userName.length() != 0 && Sex.length() != 0){
            rlt_setting.setVisibility(View.GONE);

            if(calendar == null){
                calendar = Calendar.getInstance();
            }

            calendar.set(Calendar.YEAR, mYear);
            calendar.set(Calendar.MONTH, mMonth);
            calendar.set(Calendar.DAY_OF_MONTH, mDay);

            edit_username.setText(sp.getString("UserName", ""));
            tv_sex.setText(sp.getString("Sex", ""));

            startDay(isYear);


        }

    }

    //四捨五入
    public static String totalMoney(double money) {
        java.math.BigDecimal bigDec = new java.math.BigDecimal(money);
        double total = bigDec.setScale(1, java.math.BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        DecimalFormat df = new DecimalFormat("0");
        return df.format(total);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, requestcodepick);
                    setImageSetting();
                }
                return;
            }
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == edituserdata) {

                setData();

            }
            else if (requestCode == requestcodepick){
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == requestcodecutting){
                try {
                    //startPhotoZoom(data.getData());
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    String bgdata = bitmapToBase64(bitmap);
                    SharedPreferences.Editor specheck = sp.edit();

                    if(imageType == 1){
                        specheck.putString("BgData", bgdata);
                        iv_all_bg.setImageBitmap(bitmap);
                    }
                    else if(imageType == 2){
                        specheck.putString("FaceData", bgdata);
                        iv_face.setImageBitmap(bitmap);
                    }
                    specheck.commit();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == desirelist){
                String desire = "";
                for(int i=0; i<MainApp.desireData.size(); i++){
                    if(MainApp.desireData.get(i).getisCheck()){
                        if(desire.length() == 0){
                            desire = MainApp.desireData.get(i).getdesireName();
                        }
                        else{
                            desire += ","+MainApp.desireData.get(i).getdesireName();
                        }
                    }
                }
                tv_desire.setText(desire);
            }
            else if(requestCode == editdesirelist){
                checkdesireList = MainApp.userDesireData;
                //checkdesireList = data.getStringArrayListExtra( "checkdesireList");
                setData();
            }
        }
    }

    public void startPhotoZoom(Uri uri) {

        imageUri = getTmpUri();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestcodecutting);
    }


    //获得临时保存图片的Uri，用当前的毫秒值作为文件名
    private Uri getTmpUri() {
        String IMAGE_FILE_DIR = Environment.getExternalStorageDirectory() + "/" + "DeathDay";
        File dir = new File(IMAGE_FILE_DIR);
        File file = new File(IMAGE_FILE_DIR, Long.toString(System.currentTimeMillis()));
        //非常重要！！！如果文件夹不存在必须先手动创建
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return Uri.fromFile(file);
    }



    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private void startPush() {
        setTime();
        scheduleNotifications();
    }

    private void setTime() {
        Calendar c = Calendar.getInstance();              //得到當前日期和時間
        c.set(Calendar.HOUR_OF_DAY,  8 );                 //把當前時間小時變成０
        c.set(Calendar.MINUTE,  0 );                      //把當前時間分鐘變成０
        c.set(Calendar.SECOND,  0 );                      //把當前時間秒數變成０
        c.set(Calendar.MILLISECOND,  0 );                 //把當前時間毫秒變成０
        Date date1 = c.getTime();                         //創建當天的0時0分0秒一個date對象

        sp.edit().putLong("TIME", date1.getTime()).apply();
    }

    private void startPush2() {
        setTime();
        Calendar cal = Calendar.getInstance();
        // 設定於 3 分鐘後執行
        cal.add(Calendar.MINUTE, 1);
        Intent intent = new Intent(this, PlayReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

    //設定裁切圖片比例大小
    private void setImageSetting(){
        if(imageType == 1){
            aspectX = 2;
            aspectY = 1;
            outputX = 750;
            outputY = 270;
        }
        else if(imageType == 2){
            aspectX = 1;
            aspectY = 1;
            outputX = 270;
            outputY = 270;
        }
    }

    private void scheduleNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    PersistableBundle persiBundle = new PersistableBundle();
                    persiBundle.putString("servicename", NotificationService.class.getName());
                    JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), NotificationService.class.getName()))
                            .setRequiresCharging(false)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE) //任何有网络的状态
                            .setPersisted(true) //系统重启后保留job
                            .setMinimumLatency(1000 * 60) //这里的单位是毫秒，1000 * 60 * 60 * 24代表一天（24小时）
                            .setExtras(persiBundle)
                            .build();
                    jobScheduler.schedule(jobInfo);
                }
                else{

                    JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), NotificationService.class.getName()))
                            .setRequiresCharging(false)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE) //任何有网络的状态
                            .setPersisted(true) //系统重启后保留job
                            .setPeriodic(1000 * 60) //这里的单位是毫秒，1000 * 60 * 60 * 24代表一天（24小时）
                            .build();
                    jobScheduler.schedule(jobInfo);

                }

            } catch (Exception ex) {

            }
        }
    }

    public static void shareText(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.Share_title));
        intent.putExtra(Intent.EXTRA_TEXT, extraText);//extraText为文本的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//为Activity新建一个任务栈
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.Share_title)));//R.string.action_share同样是标题
    }

    private void setUserData(String uid, int mYear, int mMonth, int mDay, String userName, String Sex, boolean isYear){

        if(checkdesireList == null){
            checkdesireList = new ArrayList<DesireData>();
            if(MainApp.desireData.size() != 0){
                for(int i=0; i<MainApp.desireData.size(); i++){
                    if(MainApp.desireData.get(i).getisCheck()){
                        DesireData item = new DesireData();
                        item.setdesireName(MainApp.desireData.get(i).getdesireName());
                        item.setisCheck(false);
                        checkdesireList.add(item);
                    }
                }
            }
        }
        userData.setuid(uid);
        if(mYear != 0){
            userData.setmYear(mYear);
        }
        userData.setmMonth(mMonth+1);
        userData.setmDay(mDay);
        userData.setuserName(userName);
        userData.setSex(Sex);
        userData.setisYear(isYear);
        if(checkdesireList.size() != 0){
            userData.setDesireList(checkdesireList);
        }
        fireDataBaseUtil.setUserData(userData);

    }

    private void getData() {

        if(uid == null){
            uid =  sp.getString("Uid", uid);
        }

        if(uid.equals(getString(R.string.nologin_id))){
            nav_logout.setTitle(getString(R.string.Login_title));
            mYear = sp.getInt("mYear", mYear);
            if(mYear == 0){
                scroll_userdata_bg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                rlt_setting.setVisibility(View.VISIBLE);
                llt_desire_bg.setVisibility(View.GONE);
                rlt_checkbg.setVisibility(View.GONE);
            }
            else{
                setData();
            }
        }
        else{
            rlt_checkbg.setVisibility(View.VISIBLE);
            postComment();
        }
    }

    //取得使用者資料
    private void postComment() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        UserData user = dataSnapshot.getValue(UserData.class);

                        if(user != null){
                            userName = user.getuserName();

                            Sex = user.getSex();
                            mYear = user.getmYear();
                            mMonth = user.getmMonth();
                            mMonth = mMonth-1;
                            mDay = user.getmDay();
                            isYear = user.getisYear();
                            checkdesireList = user.getDesireList();


                            if(getIntent().getStringExtra("type") != null){
                                if(getIntent().getStringExtra("type").equals("deaire") && checkdesireList != null){
                                    MainApp.userDesireData = checkdesireList;
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, EditDesireListActivity.class);
                                    //intent.putStringArrayListExtra("checkdesireList", checkdesireList);
                                    startActivityForResult(intent, editdesirelist);
                                }
                            }


                            SharedPreferences.Editor specheck = sp.edit();
                            specheck.putInt("mYear", mYear);
                            specheck.putInt("mMonth", mMonth);
                            specheck.putInt("mDay", mDay);
                            specheck.putBoolean("isYear", isYear);
                            specheck.putString("Sex", Sex);
                            specheck.putString("UserName", userName);;
                            specheck.commit();

                            rlt_checkbg.setVisibility(View.GONE);

                            if(mYear == 0 && mYear == 0 && mDay == 0 && userName.length() == 0 && Sex.length() == 0){
                                rlt_setting.setVisibility(View.VISIBLE);
                            }

                            setData();
                        }
                        else{

                            userName = sp.getString("UserName", userName);
                            Sex = sp.getString("Sex", Sex);
                            mYear = sp.getInt("mYear", mYear);
                            mMonth = sp.getInt("mMonth", mMonth);
                            mDay = sp.getInt("mDay", mDay);

                            if(userName != null){
                                edit_username.setText(userName);
                            }
                            if(Sex != null){
                                tv_sex.setText(Sex);
                            }
                            if(mYear !=0 && mMonth != 0 && mDay != 0){
                                if(calendar == null){
                                    calendar = Calendar.getInstance();
                                }
                                calendar.set(Calendar.YEAR, mYear);
                                calendar.set(Calendar.MONTH, mMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, mDay);
                                mMonth = (mMonth+1);
                                String format = mYear+getString(R.string.index_year)+mMonth+getString(R.string.index_month)+mDay+getString(R.string.index_day);
                                tv_birthday.setText(format);
                            }
                            rlt_setting.setVisibility(View.VISIBLE);
                            rlt_checkbg.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


//    //取得預設問券資料
//    private void getDesireList() {
//        if(MainApp.desireData.size() != 0){
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, DesireListActivity.class);
//            startActivityForResult(intent, desirelist);
//        }
//        else{
//            FirebaseDatabase.getInstance().getReference().child("DesireList")
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            // Get user information
//                            desireList = new ArrayList<String>();
//                            for (DataSnapshot ds : dataSnapshot.getChildren() ){
//                                DesireData contact = ds.getValue(DesireData.class);
//                                desireList.add(contact.getdesireName());
//                                MainApp.desireData.add(contact);
//                            }
//                            Intent intent = new Intent();
//                            intent.setClass(MainActivity.this, DesireListActivity.class);
//                            startActivityForResult(intent, desirelist);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//        }
//
//    }



}

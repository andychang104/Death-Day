package com.cactime;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cactime.api.PastData;
import com.cactime.util.ListDataSave;

/**
 * Created by andy on 2018/2/22.
 */

public class FutureFragment extends Fragment {


    private Context context;

    private FloatingActionButton fab;

    private ListView listView;

    private TextView tv_no_data;

    private PastDayAdapter pastdayAdapter;

    private List<PastData> pastData;

    private ListDataSave dataSave;

    private String uid = "";

    private SharedPreferences sp;

    private final int additem = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future, container, false);

        context = getActivity();

        listView = (ListView) view.findViewById(R.id.listView);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);

        setUi();

        return view;
    }

    private void setUi() {

        sp = context.getSharedPreferences("CacUserData", context.MODE_PRIVATE);

        uid = sp.getString("Uid", uid);

        dataSave = new ListDataSave (context, "NewDay2");

        pastData = dataSave.getDataList("DataList2");

        if(pastData == null){
            pastData = new ArrayList<PastData>();
        }

        pastdayAdapter = new PastDayAdapter();

        listView.setAdapter(pastdayAdapter);

        if(pastData.size() == 0){
            tv_no_data.setVisibility(View.VISIBLE);
        }
        else{
            tv_no_data.setVisibility(View.GONE);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isIntent = true;
                if(uid.equals(getString(R.string.nologin_id))){
                    if(pastData.size() >= 1){
                        isIntent = false;
                    }
                }
                if(isIntent){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), NewDayActivity.class);
                    intent.putExtra("Type", 2);
                    startActivityForResult(intent, additem);
                }
                else{
                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.dialog_title))
                            .setMessage(getString(R.string.dialog_no_login))
                            .setPositiveButton(getString(R.string.dialog_no_login_main_btnok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    sp.edit().clear().commit();
//                                    context.getSharedPreferences("NewDay1", context.MODE_PRIVATE).edit().clear().commit();
//                                    context.getSharedPreferences("NewDay2", context.MODE_PRIVATE).edit().clear().commit();
                                    Intent intent = new Intent();
                                    intent.setClass(context, LogInActivity.class);
                                    intent.putExtra("type", "Experience");
                                    startActivity(intent);
                                    getActivity().finish();
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
        });
    }


    private class PastDayAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public PastDayAdapter() {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return pastData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        class ViewHolder {
            private TextView tv_time_name;
            private TextView tv_time_day;
            private TextView tv_time_allday;



        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup rootView) {
            final ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater.inflate(R.layout.time_item, null);
                holder = new ViewHolder();
                holder.tv_time_name = (TextView) convertView.findViewById(R.id.tv_time_name);
                holder.tv_time_day = (TextView) convertView.findViewById(R.id.tv_time_day);
                holder.tv_time_allday = (TextView) convertView.findViewById(R.id.tv_time_allday);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_time_name.setText(pastData.get(pos).getItemName());


            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, pastData.get(pos).getItemYear());
            calendar.set(Calendar.MONTH, (pastData.get(pos).getItemMonth()-1));
            calendar.set(Calendar.DAY_OF_MONTH, pastData.get(pos).getItemDay());

            int weekday = calendar.get(Calendar.DAY_OF_WEEK);   //取出星期幾，編號由Sunday(1)~Saturday(7)

            String[] str1 = new String[]{"Error",getString(R.string.past_array1),getString(R.string.past_array2),getString(R.string.past_array3),getString(R.string.past_array4),
                    getString(R.string.past_array5),getString(R.string.past_array6),getString(R.string.past_array7)};

            holder.tv_time_day.setText(getString(R.string.past_aims_day)+pastData.get(pos).getItemMonth() +getString(R.string.index_month)+" "+pastData.get(pos).getItemDay()+","+pastData.get(pos).getItemYear()+getString(R.string.past_item_msg)+str1[weekday]+")");

            checkDay(1, pastData.get(pos).getItemYear(), pastData.get(pos).getItemMonth(), pastData.get(pos).getItemDay(), holder.tv_time_allday, pos);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), EditDayActivity.class);
                    intent.putExtra("Type", 2);
                    intent.putExtra("Item", pos);
                    startActivityForResult(intent, additem);
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                /**
                 * Called when a view has been clicked and held.
                 *
                 * @param v The view that was clicked and held.
                 * @return true if the callback consumed the long click, false otherwise.
                 */
                @Override
                public boolean onLongClick(View v) {
                    checkDay(2,pastData.get(pos).getItemYear(), pastData.get(pos).getItemMonth(), pastData.get(pos).getItemDay(), holder.tv_time_allday, pos);
                    return true;
                }


            });

            return convertView;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == additem) {


            if(dataSave.getDataList("DataList2") != null){
                pastData = dataSave.getDataList("DataList2");

                if(pastData.size() != 0){
                    Log.d("DataData2", "DataData2-"+pastData.get(0).getItemName());
                }

                pastdayAdapter.notifyDataSetChanged();

                if(pastData.size() == 0){
                    tv_no_data.setVisibility(View.VISIBLE);
                }
                else{
                    tv_no_data.setVisibility(View.GONE);
                }

            };
        }
    }

    private void checkDay(int Type, int mYear, int mMonth, int mDay, TextView tv_all_day, int index){

        try {
            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
            String startDay = format.format(curDate);
            String overDay = mYear+"-"+(mMonth)+"-"+mDay;
            Date beginDate= format.parse(startDay);
            Date endDate= format.parse(overDay);
            long day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
            if(Type == 1){
                tv_all_day.setText(getString(R.string.future_too_msg)+day+getString(R.string.index_day2));
                pastData.get(index).setItemAllDay(day);
                dataSave.setDataList("DataList2", pastData);
            }
            else{
                if(day < 365){
                    Toast.makeText(context,getString(R.string.toast_year_error),Toast.LENGTH_SHORT).show();
                }
                else{
                    double tiastday  = (endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
                    tiastday = (tiastday/365);
                    String stringtiastday  = String.format("%.3f", (tiastday));
                    String stringyear = stringtiastday.substring(0, stringtiastday.indexOf("."));
                    String stringday = stringtiastday.substring(stringtiastday.indexOf("."), stringtiastday.indexOf(".") + 4);
                    stringday = "0"+stringday;
                    stringday =  totalMoney((Double.parseDouble(stringday)*365));
                    Toast.makeText(context,getString(R.string.toast_msg_error)+stringyear+getString(R.string.index_year)+stringday+getString(R.string.index_day2),Toast.LENGTH_LONG).show();
                }


            }
        } catch (ParseException e) {
            e.printStackTrace();
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

}

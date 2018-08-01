package com.cactime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cactime.itemclass.DesireData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DesireListActivity extends AppCompatActivity {

    private Context mContext;

    private Toolbar toolbar;

    private Button btn_put;
    private Button btn_ok;

    private ListView list_desirelist;

    private ArrayList<DesireData> desireList = new ArrayList<DesireData>();

    private DesireAdapter desireAdapter;

    private final int TAG_PUT = 0, TAG_NEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desirelist);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();
        setUI();
    }

    private void setUI() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DesireListActivity.this.finish();
            }
        });

        desireList = MainApp.desireData;

        btn_ok = (Button) findViewById(R.id.btn_ok);
        list_desirelist = (ListView) findViewById(R.id.list_desirelist);
        btn_put = (Button) findViewById(R.id.btn_put);

        desireAdapter = new DesireAdapter();
        list_desirelist.setAdapter(desireAdapter);


        btn_ok.setTag(TAG_NEW);
        btn_ok.setOnClickListener(onClickListener);
        btn_put.setTag(TAG_PUT);
        btn_put.setOnClickListener(onClickListener);
    }


    private class DesireAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public DesireAdapter() {
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return desireList.size();
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
            private TextView tvDesireName;
            private TextView tvDesireLine;
            private CheckBox checkDesire;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup rootView) {
            final DesireAdapter.ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater.inflate(R.layout.desirelist_item, null);
                holder = new DesireAdapter.ViewHolder();
                holder.tvDesireName = (TextView) convertView.findViewById(R.id.tv_desire_name);
                holder.tvDesireLine = (TextView) convertView.findViewById(R.id.tv_desire_line);
                holder.checkDesire = (CheckBox) convertView.findViewById(R.id.check_desire);


                convertView.setTag(holder);
            } else {
                holder = (DesireAdapter.ViewHolder) convertView.getTag();
            }

            holder.tvDesireLine.setVisibility(View.VISIBLE);

            if (pos == (desireList.size() - 1)) {
                holder.tvDesireLine.setVisibility(View.GONE);
            }

            holder.tvDesireName.setText(desireList.get(pos).getdesireName());

            holder.checkDesire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        desireList.get(pos).setisCheck(true);
                    } else {
                        desireList.get(pos).setisCheck(false);
                    }
                }
            });


            holder.checkDesire.setChecked(desireList.get(pos).getisCheck());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickCheckBox(holder.checkDesire, pos);
                }
            });

            return convertView;
        }
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            int tag = (Integer) v.getTag();
            switch (tag) {
                case TAG_PUT:// 確定
                    intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case TAG_NEW:// 新增
                    final EditText edit = new EditText(DesireListActivity.this);
                    edit.setHint(R.string.desirelist_hint);
                    new AlertDialog.Builder(DesireListActivity.this)
                            .setTitle(getString(R.string.desirelist_new_title))
                            .setView(edit)
                            .setPositiveButton(getString(R.string.dialog_ok_btn), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String newdesire = edit.getText().toString();
                                    if(newdesire.replace(" ", "").replace("\n", "").equals("")){
                                        Toast.makeText(mContext,getText(R.string.desirelist_hint),Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        DesireData item = new DesireData();
                                        item.setdesireName(newdesire);
                                        desireList.add(0, item);
                                        desireAdapter.notifyDataSetChanged();
                                        edit.setText("");
                                        Toast.makeText(mContext,getText(R.string.desirelist_new_ok),Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                    break;
            }
        }
    };

    private void setClickCheckBox(CheckBox view, int i) {
        if (view.isChecked()) {
            view.setChecked(false);
        } else {
            view.setChecked(true);
        }
    }

}

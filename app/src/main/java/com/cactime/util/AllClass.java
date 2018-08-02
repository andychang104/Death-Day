package com.cactime.util;

import android.app.Activity;
import android.content.Intent;

import com.cactime.DesireListActivity;
import com.cactime.MainApp;
import com.cactime.itemclass.DesireData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllClass {

    public int desirelist = 500;

    public ArrayList<String> desireList;

    //取得預設問券資料
    public void getDesireList(final Activity activity, ArrayList<String> desireListAdd) {
        if(MainApp.desireData.size() != 0){
            Intent intent = new Intent();
            intent.setClass(activity, DesireListActivity.class);
            activity.startActivityForResult(intent, desirelist);
        }
        else{
            desireList = desireListAdd;
            FirebaseDatabase.getInstance().getReference().child("DesireList")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user information
                            desireList = new ArrayList<String>();
                            for (DataSnapshot ds : dataSnapshot.getChildren() ){
                                DesireData contact = ds.getValue(DesireData.class);
                                desireList.add(contact.getdesireName());
                                MainApp.desireData.add(contact);
                            }
                            Intent intent = new Intent();
                            intent.setClass(activity, DesireListActivity.class);
                            activity.startActivityForResult(intent, desirelist);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    }
}

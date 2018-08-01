package com.cactime.util;

import com.cactime.itemclass.UserData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireDataBaseUtil {

    public void setUserData(UserData userData){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef = myRef.child("Users").child(userData.getuid());
        myRef.setValue(userData);
    }


}

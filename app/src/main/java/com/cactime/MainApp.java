package com.cactime;

import android.app.Application;
import android.content.Context;

import com.cactime.itemclass.DesireData;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

public class MainApp extends Application {

    private Context context;

    public static GoogleSignInClient mGoogleSignInClient;

    public static ArrayList<DesireData> desireData = new ArrayList<DesireData>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        FirebaseApp.initializeApp(context);
    }
}

package com.cactime;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LogInActivity extends AppCompatActivity {

    private final int TAG_LOGIN = 0, TAG_SENDPASSWORD = 1, TAG_REMEMBER = 2, TAG_GOOGLE = 3, TAG_NOLOGIN = 4;

    private Context mContext;

    private Toolbar toolbar;

    private TextView tv_title;

    private EditText edit_login_mail;
    private EditText edit_login_password;

    private Button btn_login;
    private Button btn_lost_password;
    private Button btn_nologin;

    private SignInButton signInButton;

    private CheckBox check_remember_account;

    private LinearLayout llt_remember_account;

    private LoginButton loginButton;

    private FirebaseAuth mAuth;

    private SharedPreferences sp;

    private CallbackManager callbackManager;

    private static final String TAG = "FacebookLogin";

    private static final int RC_SIGN_IN = 9001;

    private ProgressDialog dialog;

    private String uid = "";
    private String type = "";

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            int tag = (Integer) v.getTag();
            switch (tag) {
                case TAG_LOGIN:// 登入
                    if(check_remember_account.isChecked()){
                        sp.edit().putString("Email", edit_login_mail.getText().toString()).commit();
                    }
                    login();
                    break;
                case TAG_SENDPASSWORD:// 忘記密碼
                    final EditText editText = new EditText(mContext);
                    editText.setHint(R.string.Login_edit_mail_hint);
                    editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    new AlertDialog.Builder(mContext)
                            .setTitle(getString(R.string.dialog_title))
                            .setView(editText)
                            .setPositiveButton(getString(R.string.dialog_ok_btn), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(editText.getText().toString().length() !=0 ){
                                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
                                            sendPassword(editText.getText().toString());
                                        }
                                        else{
                                            Toast.makeText(mContext, getText(R.string.toast_mag8), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(mContext, getText(R.string.toast_mag8), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                    break;
                case TAG_REMEMBER:// 記住帳號
                    if(check_remember_account.isChecked()){
                        check_remember_account.setChecked(false);
                    }
                    else{
                        check_remember_account.setChecked(true);
                        PackageInfo info;


                        try{
                            info = getPackageManager().getPackageInfo("cactime.com.cactime", PackageManager.GET_SIGNATURES);
                            for(Signature signature : info.signatures)
                            {      MessageDigest md;
                                md =MessageDigest.getInstance("SHA");
                                md.update(signature.toByteArray());
                                String KeyResult =new String(Base64.encode(md.digest(),0));//String something = new String(Base64.encodeBytes(md.digest()));
                                Log.e("hash key", KeyResult);
                                //Toast.makeText(mContext,"My FB Key is \n"+ KeyResult , Toast.LENGTH_LONG ).show();
                            }
                        }catch(PackageManager.NameNotFoundException e1){Log.e("name not found", e1.toString());
                        }catch(NoSuchAlgorithmException e){Log.e("no such an algorithm", e.toString());
                        }catch(Exception e){Log.e("exception", e.toString());}

                    }
                    break;
                case TAG_GOOGLE:// google登入
                    googleSignIn();
                    break;
                case TAG_NOLOGIN://試用體驗
                    Intent homeIntent = new Intent(mContext, MainActivity.class);
                    homeIntent.putExtra("uid", getString(R.string.nologin_id));
                    startActivity(homeIntent);
                    finish();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        mContext = LogInActivity.this;
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        MainApp.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        setUI();
    }

    private void setUI() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        edit_login_mail = (EditText) findViewById(R.id.edit_login_mail);
        edit_login_password = (EditText) findViewById(R.id.edit_login_password);
        llt_remember_account = (LinearLayout) findViewById(R.id.llt_remember_account);
        check_remember_account = (CheckBox) findViewById(R.id.check_remember_account);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_lost_password = (Button) findViewById(R.id.btn_lost_password);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        btn_nologin = (Button) findViewById(R.id.btn_nologin);

        signInButton.setSize(SignInButton.SIZE_WIDE);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                updateUI(null);
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, exception.getMessage());
                updateUI(null);
            }
        });


        btn_login.setTag(TAG_LOGIN);
        btn_login.setOnClickListener(onClickListener);
        btn_lost_password.setTag(TAG_SENDPASSWORD);
        btn_lost_password.setOnClickListener(onClickListener);
        llt_remember_account.setTag(TAG_REMEMBER);
        llt_remember_account.setOnClickListener(onClickListener);
        signInButton.setTag(TAG_GOOGLE);
        signInButton.setOnClickListener(onClickListener);
        btn_nologin.setTag(TAG_NOLOGIN);
        btn_nologin.setOnClickListener(onClickListener);

        mAuth = FirebaseAuth.getInstance();

        sp = getSharedPreferences("CacUserData", MODE_PRIVATE);

        String email = sp.getString("Email", "");

        edit_login_mail.setText(email);

        check_remember_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){

                    sp.edit()
                            .putString("Email", edit_login_mail.getText().toString())
                            .commit();
                }else{

                }
            }
        });

        type = getIntent().getStringExtra("type");
        if(type != null){
            if(type.equals("Experience")){
                btn_nologin.setVisibility(View.GONE);
            }
        }
        else{
            type = "";
        }

    }

    public void login() {
        int isOk = 0;
        final String email = edit_login_mail.getText().toString();
        final String password = edit_login_password.getText().toString();

        if(email != null){
            if(email.length() != 0){
                isOk++;
            }

        }

        if(password != null){
            if(password.length() != 0){
                isOk++;
            }
        }

        if(isOk == 2){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();

                                Toast.makeText(mContext, getString(R.string.toast_mag1), Toast.LENGTH_SHORT).show();
                                updateUI(user);

                            } else {
                                // If sign in fails, display a message to the user.

                                String errorMsg = "";
                                if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                    errorMsg = getString(R.string.toast_mag2);
                                } else {
                                    errorMsg = task.getException().getMessage();

                                    new AlertDialog.Builder(mContext)
                                            .setTitle(getString(R.string.dialog_title))
                                            .setMessage(getString(R.string.Login_dialog_join))
                                            .setPositiveButton(getString(R.string.dialog_ok_btn), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    join();
                                                }
                                            })
                                            .setNegativeButton(getString(R.string.dialog_no_btn), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .show();
                                }

                                Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();


                            }
                        }
                    });

        }

    }

    public void join() {
        final String email = edit_login_mail.getText().toString();
        final String password = edit_login_password.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(mContext, getString(R.string.toast_mag3), Toast.LENGTH_SHORT).show();
                            updateUI(user);

                        } else {

                            Toast.makeText(mContext, getString(R.string.toast_mag4),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void sendPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(mContext, getString(R.string.toast_mag5), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, getString(R.string.toast_mag6), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
//            String userid = "";
//            sp = getSharedPreferences("CacUserData", MODE_PRIVATE);
//            userid =  sp.getString("Uid", userid);
//            if(userid != null){
//                if(!userid.equals(getString(R.string.nologin_id))){
//                    getSharedPreferences("NewDay1", MODE_PRIVATE).edit().clear().commit();
//                    getSharedPreferences("NewDay2", MODE_PRIVATE).edit().clear().commit();
//                }
//            }
            uid = user.getUid();
            Intent homeIntent = new Intent(mContext, MainActivity.class);
            homeIntent.putExtra("uid", uid);
            startActivity(homeIntent);
            finish();
        }
        else{
            sp = getSharedPreferences("CacUserData", MODE_PRIVATE);
            uid =  sp.getString("Uid", uid);

            if(uid != null){
                if(uid.length() != 0){
                    if(uid.equals(getString(R.string.nologin_id))){
                        if(!type.equals("Experience") && !type.equals("Back")){
                            Intent homeIntent = new Intent(mContext, MainActivity.class);
                            homeIntent.putExtra("uid", uid);
                            startActivity(homeIntent);
                            finish();
                        }
                    }
                }
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(mContext, getString(R.string.toast_mag9),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
                Toast.makeText(mContext, getString(R.string.toast_mag9), Toast.LENGTH_SHORT).show();
                if (dialog !=null) {
                    dialog.cancel();
                }
            }
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        dialog.cancel();

        dialog = ProgressDialog.show(mContext, null, getString(R.string.toast_mag7),true);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(mContext, getString(R.string.toast_mag9), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        dialog.cancel();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void googleSignIn() {
        dialog = ProgressDialog.show(mContext, null, getString(R.string.toast_mag7),true);
        Intent signInIntent = MainApp.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }




}




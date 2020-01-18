package com.example.roommangement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.s3.transferutility.*;

import com.example.roommangement.AWS_Services.*;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.global_vars.auths;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    File file_2_upload =   new File("");
    upload_files uplink;
    download_files downlink = new download_files();
    String curr_path;
    GoogleSignInClient log_in;
    DisplayMetrics displayMetrics;
    int dpHeight;
    int dpwidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("view", "onCreate: ");
        uplink = new upload_files();
        uplink.setScreen(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        log_in = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
       updateUI(account);//log-in method

        auths auth_lvl = auths.get_auth();
        displayMetrics = this.getResources().getDisplayMetrics();
        dpwidth = (int) (displayMetrics.widthPixels);
        dpHeight = (int) (displayMetrics.heightPixels );

        Button lvl_1 = findViewById(R.id.maid);
        Button lvl_2 = findViewById(R.id.maintence);
        Button lvl_3 = findViewById(R.id.owner);

        lvl_1.setOnClickListener(v1->{
            auth_lvl.auth_lvl = 1;
            onClick();
        });
        lvl_2.setOnClickListener(v1->{
            auth_lvl.auth_lvl = 2;
            onClick();
        });
        lvl_3.setOnClickListener(v1->{
            auth_lvl.auth_lvl = 3;
            onClick();
        });


       /* SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setMinimumWidth(dpwidth);
        signInButton.setMinimumHeight((int)(dpHeight*.2));
        signInButton.setOnClickListener(this::onClick);*/
    }

    private void updateUI(GoogleSignInAccount account) {
    }


    public void onClick() {
        Intent intent = new Intent(MainActivity.this, maid_home_screen.class);
        startActivity(intent);
        /*switch (v.getId()) {
            case R.id.sign_in_button:
                signIn(); //sign-in method
                break;
            // ...
        }*/
    }

    private void signIn() {
        Log.d("clicked", "signIn: ");
        Intent signInIntent = log_in.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }


    public void onSaveClicked(View view) {

        // Finish this activity and return to the prior activity
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
       if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Log-in failed", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }






}



/* public void set_up_cordinator(){
        curr_path = "writtentest2.txt";
        uplink.set_file_path(curr_path);
        // callback method to call credentialsProvider method.
        uplink.s3credentialsProvider();
        // callback method to call the setTransferUtility method
        uplink.setTransferUtility();
    }
    public void uploadFileToS3(View view){
        uplink.write_2_file("this is a message");
        uplink.vals.transferObserverListener("upload");
    }


    public void downloadFileFromS3(View view){
        curr_path = "writtentest2.txt";
        File downed = new File("");
        try{
            downed = File.createTempFile("item",".txt");
            downlink.set_file(downed);
        } catch (IOException e) {
            Log.d("this->", "downloadFileFromS3: broke");
            e.printStackTrace();
        }
        if(downed==null){
            Log.d("look here ", downed.getName());
        }
        downlink.set_file_path();
        downlink.setScreen(this);
        downlink.s3credentialsProvider();
        downlink.setTransferUtility();
        downlink.set_bucket("room-check-test");
        downlink.vals.transferObserverListener("download");


    }*/
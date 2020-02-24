package com.example.roommangement.AWS_Cognito;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.regions.Regions;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.global_vars.auths;

import java.util.List;
import java.util.Map;

public class aws_cognito {
    private static volatile aws_cognito base;

    public static synchronized aws_cognito getInstance(){
        if (base == null){
            base= new aws_cognito();
        }
        return base;
    }

    private  String AWS_cred ="";
    private String curr_id ="";

    public Map token;
    Context screen;
    auths auth_lvl = auths.get_auth();
    db_cordinator curr_table = db_cordinator.getInstance(screen);

    CognitoCachingCredentialsProvider creds;
    public void set_screen(Context t){
        screen =t;
    }
    // Create a new credentials provider
    public void set_token(Map k){
        token =k;
    }
    public void sign_in(Context screen){

          CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                screen, // Context
                  "231867092748",
                "us-west-2:e4f1669c-a472-43e4-8fc2-73de20022289", // Identity Pool ID
                  "arn:aws:iam::231867092748:role/Cognito_test_hotelUnauth_Role",// an authenticated role ARN
                  "arn:aws:iam::231867092748:role/Cognito_test_hotelAuth_Role",
                Regions.US_WEST_2 // Region
        );
          credentialsProvider.setLogins(token);
        Thread t = new Thread(()->{
            String identityId = credentialsProvider.getIdentityId();
            AWS_cred = identityId;
            Log.d("LogTag", "my ID is " + identityId);
            set_up(identityId);
        });
        t.start();
    }

    public String get_ID(){
        return AWS_cred;
    }

    public void set_table_name(String nPool){
        curr_table.set_Table_name(nPool);
    }
    private void set_up(String ID){
        curr_table.set_Table_name("users");
        String TAG = "set_up";
        curr_table.set_token(token);
        curr_table.load_table();


        auth_lvl.auth_lvl = 3;
        try{
            /*Document tmp = curr_table.getMemoById(ID);
            auth_lvl.auth_lvl = tmp.get("auth_lvl").asInt();
            Log.d(TAG, Integer.toString(auth_lvl.auth_lvl));*/
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }

    }

}

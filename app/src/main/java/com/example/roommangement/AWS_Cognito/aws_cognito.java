package com.example.roommangement.AWS_Cognito;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import java.util.Map;

public class aws_cognito {


    private  String AWS_cred ="";

    public Map token;

    CognitoCachingCredentialsProvider creds;
    // Create a new credentials provider
    public void set_token(Map k){
        token =k;
    }
    public void sign_in(Context screen){


        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                screen, // Context
                "us-west-2:e4f1669c-a472-43e4-8fc2-73de20022289", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        String identityId = credentialsProvider.getIdentityId();
        AWS_cred = identityId;
        Log.d("LogTag", "my ID is " + identityId);
    }
    public String get_cred(){
        return AWS_cred;
    }
}

package com.example.roommangement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.s3.transferutility.*;

import com.amazonaws.regions.Regions;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.roommangement.AWS_Cognito.aws_cognito;
import com.example.roommangement.AWS_Services.*;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.global_vars.auths;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //Display values
    DisplayMetrics displayMetrics;
    int dpHeight;
    int dpwidth;

    //log-in variables
    AuthState mAuthState;
    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";
    AuthorizationServiceConfiguration serviceConfiguration;

    //Global Context
    Context holder = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Metrics for displaying on screen XML
        displayMetrics = this.getResources().getDisplayMetrics();
        dpwidth = (int) (displayMetrics.widthPixels);
        dpHeight = (int) (displayMetrics.heightPixels );


       // ProgressBar pgsBar;
        String TAG = "GOOGLE";
        Intent intent = getIntent();
        if(intent!=null){
            Log.d(TAG, "onCreate: not null");
            Thread ta = new Thread(()->{
                try{
                    //checks to see if user has signed in
                    handleAuthorizationResponse(intent);

                    //hide log-in button
                    Button x = findViewById(R.id.GOOGLE_BTN);
                    x.setVisibility(View.INVISIBLE);

                    //loading circle
                    ProgressBar pgsBar = findViewById(R.id.pBar);
                    pgsBar.setX((dpwidth-77)/2);
                    pgsBar.setY((dpHeight-73)/2);
                    pgsBar.setVisibility(View.VISIBLE);

                    //display loading
                    TextView words = findViewById(R.id.log);
                    words.setVisibility(View.VISIBLE);
                    words.setText("Loging in");
                    words.setTextSize(40);
                    words.setX((dpwidth-200)/2);
                    words.setY((dpHeight-73)/3);
                }catch (Exception e){
                    Log.d(TAG, "not logged in");
                }
                
            });
            ta.start();
        }


        // Add a call to initialize AWSMobileClient
        serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        );
        Log.d("view", "onCreate: ");

    }


    public void google_log_in(View view){//log-in including O-auth sign-in

        //https://codelabs.developers.google.com/codelabs/appauth-android-codelab/#5
        Thread tmp = new Thread(()->{
            //google api info
            String clientId = "742932088080-l4ve9odnp9j10gd65hectcks1vktbvra.apps.googleusercontent.com";
            String url="com.example.roommangement%3A/oauth2redirect";
            Uri redirectUri = Uri.parse("com.example.roommangement:/oauth2callback");
            //end google api info

            //build
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    AuthorizationRequest.RESPONSE_TYPE_CODE,
                    redirectUri
            );
            builder.setScopes("profile");
            AuthorizationRequest request = builder.build();

            //https://codelabs.developers.google.com/codelabs/appauth-android-codelab/#6
            AuthorizationService authorizationService = new AuthorizationService(view.getContext());
            String action = "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE";
            Intent postAuthorizationIntent = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), request.hashCode(), postAuthorizationIntent, 0);
            authorizationService.performAuthorizationRequest(request, pendingIntent);
        });
        tmp.start();
    }

    public void onClick() {
        Intent intent = new Intent(MainActivity.this, view_selector.class);
        startActivity(intent);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    // do nothing
            }
        }
    }

    //stuff happens here
    //sets up auth for tables and Cognito
    private void handleAuthorizationResponse(@NonNull Intent intent) {
        String LOG_TAG = "new copied stuff";
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);
        if (response != null) {
            Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w(LOG_TAG, "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            persistAuthState(authState);

                            Map<String, String> logins = new HashMap<String, String>();
                            logins.put("accounts.google.com", tokenResponse.idToken);
                            Thread task1 = new Thread(()->{
                                download_files tmp  = download_files.get_server_down();
                                tmp.setScreen(holder);
                                tmp.s3credentialsProvider(logins);

                                db_cordinator tmp2 = db_cordinator.getInstance(holder);
                                tmp2.set_token(logins);
                            });
                            Thread task2 = new Thread(()->{
                                aws_cognito t = aws_cognito.getInstance();
                                t.set_token(logins);
                                t.set_screen(holder);
                                t.sign_in(holder);
                            });
                            task2.start();
                            task1.start();
                            String TAG = "line 269";
                            Log.d(TAG, "onTokenRequestCompleted: ");
                            onClick();
                        }
                    }
                }
            });
        }
    }

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .commit();
        enablePostAuthorizationFlows();
    }

    private void enablePostAuthorizationFlows() {
        mAuthState = restoreAuthState();
        if (mAuthState != null && mAuthState.isAuthorized()) {
           /* if (mMakeApiCall.getVisibility() == View.GONE) {
                mMakeApiCall.setVisibility(View.VISIBLE);
                mMakeApiCall.setOnClickListener(new MakeApiCallListener(this, mAuthState, new AuthorizationService(this)));
            }
            if (mSignOut.getVisibility() == View.GONE) {
                mSignOut.setVisibility(View.VISIBLE);
                mSignOut.setOnClickListener(new SignOutListener(this));
            }*/
        } else {
            /*mMakeApiCall.setVisibility(View.GONE);
            mSignOut.setVisibility(View.GONE);*/
        }
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.fromJson(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }


}




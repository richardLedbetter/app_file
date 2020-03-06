package com.example.roommangement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.roommangement.AWS_Cognito.aws_cognito;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.global_vars.auths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class view_selector extends AppCompatActivity {
    /*TO-DO
    * ALLOW FOR MULTIPLE HOTELS
    * */


    LinearLayout page;
    int Hotels =1;
    auths auth_lvl;

    DisplayMetrics displayMetrics;
    int dpHeight;
    int dpwidth;

    aws_cognito creds;
    db_cordinator table;

    LinearLayout curr;
    int curr_Hotel = -1;
    int buttons_displayed = 0;
    JSONObject info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selector);
        page = findViewById(R.id.selector_page);
        auth_lvl = auths.get_auth();
        creds = aws_cognito.getInstance();
        Hotels = 1;//auth_lvl.get_Hotel();
        Thread date = new Thread(()->{
            Log.d(" happening ", "onCreate: ");
            datebase_int();
        });
        date.start();
    }

    public void datebase_int(){
        displayMetrics = this.getResources().getDisplayMetrics();
        dpwidth = (int) (displayMetrics.widthPixels);
        dpHeight = (int) (displayMetrics.heightPixels );
        table = db_cordinator.getInstance(this);
        table.set_Table_name("users");
        table.set_token(creds.token);
        table.load_table();

        Document row = table.getMemoById(creds.get_ID());

        try {
            info = new JSONObject(Document.toJson(row));
            info = info.getJSONObject("hotels");
            Log.d("returned val:", info.toString());
            Iterator<String> keys = info.keys();
            if(info.length()==1){

            }else {
                for (int i = 0; i < info.length(); i++) {
                    String Room = keys.next();
                    int auth_lvl_local = info.getInt(Room);
                    Button tmp_btn = new Button(this);
                    tmp_btn.setWidth(dpwidth);
                    tmp_btn.setHeight(dpHeight / 5);
                    tmp_btn.setTextSize(40);
                    tmp_btn.setText(Room + "\n" + auth_lvl_local);
                    tmp_btn.setId(i);
                    tmp_btn.setOnClickListener(v1 -> {
                        Remove_Hotels();
                        if(curr_Hotel!=tmp_btn.getId()) {
                            onClick_Hotel(tmp_btn.getId(),auth_lvl_local);
                        }else{
                            curr_Hotel = -1;
                        }
                    });
                    Thread u = new Thread(() -> {
                        page.addView(tmp_btn);
                    });
                    runOnUiThread(u);
                }
                runOnUiThread(() -> {
                    //  onClick_Hotel();
                });

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public void setup(){
        if(Hotels ==1){
           // onClick_Hotel();
        }
    }
    public void Remove_Hotels(){
        if(curr_Hotel==-1){
            return;
        }
        int tmp = curr_Hotel;
        tmp = tmp+buttons_displayed;
        while(tmp>curr_Hotel){
            page.removeViewAt(curr_Hotel+1);
            tmp--;
        }
        buttons_displayed =0;

    }
    public void onClick_Hotel(int Hotel,int local_auth){
        curr_Hotel = Hotel;

        /*TO-DO
        * Make this hotel specific through drop down menu
        * */
        //this will be hotel specific
        //display messurements
        displayMetrics = this.getResources().getDisplayMetrics();
        dpwidth = (int) (displayMetrics.widthPixels);
        dpHeight = (int) (displayMetrics.heightPixels );

        //auth_lvl.setauth_lvl();

        //MAID view button
        Button lvl_1 = new Button(this);
        lvl_1.setWidth(dpwidth);
        lvl_1.setHeight(dpHeight/5);
        lvl_1.setText("MAID LOG-IN");
        lvl_1.setTextSize(40);
        if(local_auth==1||local_auth==3){
            page.addView(lvl_1,Hotel+1);
            buttons_displayed++;
        }

        lvl_1.setOnClickListener(v1->{
            auth_lvl.auth_lvl = 1;
            onClick();
        });

        //MAINTENCE view button
        Button lvl_2 = new Button(this);
        lvl_2.setText("MAINTENANCE LOG-IN");
        lvl_2.setTextSize(40);
        lvl_2.setOnClickListener(v1->{
            auth_lvl.auth_lvl = 2;
            onClick();
        });
        lvl_2.setWidth(dpwidth);
        lvl_2.setHeight(dpHeight/5);
        if(local_auth>1){
            page.addView(lvl_2,Hotel+2);
            buttons_displayed++;
        }



        //MANAGER view button
        Button lvl_3 = new Button(this);
        lvl_3.setText("MANAGER LOG-IN");
        lvl_3.setTextSize(40);
        lvl_3.setOnClickListener(v1->{
            auth_lvl.auth_lvl = 3;
            onClick();
        });
        lvl_3.setWidth(dpwidth);
        lvl_3.setHeight(dpHeight/5);
        if(local_auth==3){
            page.addView(lvl_3,Hotel+3);
            buttons_displayed++;
        }

    }
    public void onClick(){
        Intent intent = new Intent(view_selector.this, maid_home_screen.class);
        startActivity(intent);
    }
}

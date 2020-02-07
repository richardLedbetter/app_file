package com.example.roommangement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ScrollView;

import com.example.roommangement.global_vars.auths;

public class view_selector extends AppCompatActivity {

    ScrollView page;
    int Hotels =1;
    auths auth_lvl;

    DisplayMetrics displayMetrics;
    int dpHeight;
    int dpwidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selector);
        page = findViewById(R.id.scroll);
        auth_lvl = auths.get_auth();



    }

    public void setup(){
        if(Hotels ==1){
            onClick_Hotel();
        }
    }
    public void onClick_Hotel(){
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
        lvl_1.setWidth(dpwidth);
        lvl_1.setHeight(dpHeight/5);
        page.addView(lvl_1);
        lvl_2.setWidth(dpwidth);
        lvl_2.setHeight(dpHeight/5);
        page.addView(lvl_2);
        lvl_3.setWidth(dpwidth);
        lvl_3.setHeight(dpHeight/5);
        page.addView(lvl_3);
    }

    public void onClick(){

    }
}

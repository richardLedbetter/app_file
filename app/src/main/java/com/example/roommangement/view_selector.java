package com.example.roommangement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.roommangement.AWS_Cognito.aws_cognito;
import com.example.roommangement.global_vars.auths;

public class view_selector extends AppCompatActivity {

    LinearLayout page;
    int Hotels =1;
    auths auth_lvl;

    DisplayMetrics displayMetrics;
    int dpHeight;
    int dpwidth;

    aws_cognito creds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selector);
        page = findViewById(R.id.selector_page);
        auth_lvl = auths.get_auth();
        creds = aws_cognito.getInstance();
        Hotels = 1;//auth_lvl.get_Hotel();
        setup();
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

        Button lvl_1 = new Button(this);
        Button lvl_2 = new Button(this);
        Button lvl_3 = new Button(this);

        lvl_1.setText("MAID LOG-IN");
        lvl_1.setTextSize(40);
        lvl_2.setText("MAINTENANCE LOG-IN");
        lvl_2.setTextSize(40);
        lvl_3.setText("MANAGER LOG-IN");
        lvl_3.setTextSize(40);
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

        String TAG = "made it";
        Log.d(TAG, "onClick_Hotel: ");
    }

    public void onClick(){
        Intent intent = new Intent(view_selector.this, maid_home_screen.class);
        startActivity(intent);
    }
}

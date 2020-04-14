package com.example.roommangement.maid_screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.Maintance_screens.see_Maintence;
import com.example.roommangement.R;
import com.example.roommangement.Maintance_screens.Report_Maintence;

import java.sql.Time;
import java.sql.Timestamp;

public class room_options_maid extends AppCompatActivity {
    String TAG = "looik";
    LinearLayout ll;
    int Room;
    db_cordinator table;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_options_maid);
        Room = getIntent().getIntExtra("Room_num",-1);
        TextView label =  findViewById(R.id.Room_num);
        label.setTextSize(50);
        label.setGravity(Gravity.CENTER);
        Log.d("fasdf", Integer.toString(Room));
        if (Room == -1){
            label.setText("Unknown Room");
        }else{
            String t = "Room: "+ Room;
            Log.d(TAG, t);
            label.setText(t);
        }
        populate();
        table = db_cordinator.getInstance(this);
    }
    public void populate(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dpHeight = (int) (displayMetrics.heightPixels / (displayMetrics.density*8));
        //creating onscreen objects
        ll = findViewById(R.id.actions);

        //clean button

            Button clean_btn = new Button(this);
            clean_btn.setTextSize(30);
            clean_btn.setBottom(40);
            clean_btn.setPadding(0,0,0,40);
            clean_btn.setText("Clean");
            clean_btn.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            clean_btn.setHeight(dpHeight);
            clean_btn.setOnClickListener(v -> {
                Runnable r = () -> {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    Document builder = table.getMemoById(Room);
                    builder.put("room_status","clean");
                    builder.put("time_stamp", timestamp.toString());
                    table.update(builder);

                };
                Thread task1 = new Thread(r);
                task1.start();
                finish();
            });
            runOnUiThread(() -> ll.addView(clean_btn));

        //end clean button


        //report maintence issue
        Button report_btn = new Button(this);
        report_btn.setTextSize(30);
        report_btn.setPadding(0,0,0,40);
        report_btn.setText("Report Maintence Issue");
        report_btn.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        report_btn.setHeight(dpHeight);
        report_btn.setOnClickListener(v -> {
            Intent intent = new Intent(room_options_maid.this, Report_Maintence.class);
            Log.d("Room_number is:", Integer.toString(Room));
            intent.putExtra("Room_num",Room);
            startActivity(intent);
        });
        runOnUiThread(() -> ll.addView(report_btn));

        //See maintence issues
        Button see_issue_btn = new Button(this);
        see_issue_btn.setTextSize(30);
        see_issue_btn.setPadding(0,0,0,40);
        see_issue_btn.setText("See Maintence Issue");
        see_issue_btn.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        see_issue_btn.setHeight(dpHeight);
        see_issue_btn.setOnClickListener(v -> {
            Intent intent = new Intent(room_options_maid.this, see_Maintence.class);
            Log.d("Room_number is:", Integer.toString(Room));
            intent.putExtra("Room_num",Room);
            startActivity(intent);
        });
        runOnUiThread(() -> ll.addView(see_issue_btn));
    }


    class UpdateItemAsyncTask extends AsyncTask<Void,Void,Document> {

        @Override
        protected Document doInBackground(Void... params) {
            Document doc = new Document();
            return doc;
        }
        protected void execute(Document doc) {
            if (doc != null) {
                db_cordinator table = db_cordinator.getInstance(room_options_maid.this);
                table.update(doc);
            }
        }
    }

}



package com.example.roommangement.maneger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.roommangement.AWS_Services.download_files;
import com.example.roommangement.File_list.file_list;
import com.example.roommangement.R;
import com.google.common.io.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class see_past_cleaning_log extends AppCompatActivity {
    String TAG = "see_past_cleaning_log commented";
    String Rel_Path = "com/example/roommangement/maneger/see_past_cleaning_log.java";


    LinearLayout ll;
    //S3 download object
    download_files downlink = download_files.get_server_down();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "class_path: "+Rel_Path);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_past_cleaning_log);

        ll = findViewById(R.id.list);
        display();

    }


    public void display(){
        //linked list object for saving files and paths
        file_list list = downlink.vals.list_file;
        list.curr_2_start();

        //TO-DO 0 get rid of Intent to prevent future errors
        //find better way to have a file move
        //current solution pass position to this activity and then iterate to position
        int length =getIntent().getIntExtra("length",0);
        for (int i =0;i<length-1;i++){
            list.move_one();
        }

        File tmp_file = list.get_curr_file();
        String tmp_file_path = "";

        try {
            tmp_file_path =  tmp_file_path =  Files.toString(tmp_file, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //parsing file to allow for recreation
        JSONArray tmp_json = new JSONArray();
        try {
            tmp_json = new JSONArray(tmp_file_path);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //To-Do 2 find better way to populate array list
        //possible solution sort prior to storing
        // check unclean all rooms for current solution
        //sorting
        List list2 = new ArrayList();
        //filling ArrayList
        for(int i = 0; i < tmp_json.length(); i++) {
            try {
                list2.add(new JSONObject(tmp_json.getString(i)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(list2, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return compare((JSONObject)o1,(JSONObject)o2);
            }

            private static final String KEY_NAME = "room_num";


            public int compare(JSONObject a, JSONObject b) {
                Integer str1 = 0;
                Integer str2 = 0;
                try {
                    str1 = (int)a.get(KEY_NAME);
                    str2 = (int)b.get(KEY_NAME);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                return str1.compareTo(str2);
            }
        });


       // Log.d(TAG, list2.toString());
        //empty tmp_json Array
        tmp_json = new JSONArray(list2);

        /*for(int i = 0; i < list2.size(); i++) {
            tmp_json.put(list2.get(i));
        }*/

        for (int i = 0; i <tmp_json.length(); i++){
            Button btn = new Button(this);
            //setting button attributes
            btn.setTextSize(30);
            btn.setHeight(200);
            try {
                Log.d("Room info", tmp_json.getString(i));
                JSONObject Room = new JSONObject(tmp_json.getString(i));
                //creating button text
                //find better formatting technique
                String tmp2 = "";
                tmp2 = tmp2 + "Room: " + Room.getString("room_num") + "\n";
                tmp2 = tmp2 + "Status: " + Room.getString("room_status") + "\n";
                tmp2 = tmp2 + "cleaned by: " + Room.getString("cleaned_by") + "\n";
                tmp2 = tmp2 + "issues: " + Room.getString("maintence_issues") + "\n";

                //setting text
                btn.setText(tmp2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(()->{
                //add button to screan
                ll.addView(btn);
            });
        }



    }


}

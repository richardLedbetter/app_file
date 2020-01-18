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

    LinearLayout ll;
    download_files downlink = download_files.get_server_down();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_past_cleaning_log);
        ll = findViewById(R.id.list);
        display();

    }
    public void display(){
        file_list list = downlink.vals.list_file;

        int length = getIntent().getIntExtra("length",0);
        list.setFirst();
        for (int i =0;i<length-1;i++){
            list.move_one();
        }
        File tmp_file = list.get_cur();
        String tmp = "";
        try {
            tmp =  tmp =  Files.toString(tmp_file, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray tmp_json = new JSONArray();
        try {
            tmp_json = new JSONArray(tmp);
           // Log.d("Json", tmp_json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List list2 = new ArrayList();

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
        Log.d("list2", list2.toString());
        tmp_json = new JSONArray();

        for(int i = 0; i < list2.size(); i++) {
            tmp_json.put(list2.get(i));
        }
        for (int i = 0; i <tmp_json.length(); i++){
            Button btn = new Button(this);
            try {
                Log.d("Room info", tmp_json.getString(i));
                JSONObject Room = new JSONObject(tmp_json.getString(i));
                String tmp2 = "";
                tmp2 = tmp2 + "Room: " + Room.getString("room_num") + "\n";
                tmp2 = tmp2 + "Status: " + Room.getString("room_status") + "\n";
                tmp2 = tmp2 + "cleaned by: " + Room.getString("cleaned_by") + "\n";
                tmp2 = tmp2 + "issues: " + Room.getString("maintence_issues") + "\n";
                btn.setTextSize(30);
                btn.setHeight(200);
                btn.setText(tmp2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(()->{
                ll.addView(btn);
            });
        }

    }


}

package com.example.roommangement.maneger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.roommangement.AWS_Services.download_files;
import com.example.roommangement.AWS_Services.upload_files;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.File_list.file_list;
import com.example.roommangement.R;

public class see_past_data extends AppCompatActivity {

    download_files downlink = download_files.get_server_down();
    db_cordinator table = db_cordinator.getInstance(this);
    upload_files uploader = upload_files.get_loader();
    Thread task1;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_past_data);
        ll = findViewById(R.id.list);
        task1= new Thread(()->{
           set_up();
        });
        task1.start();
    }
    public void set_up(){
        //room_check_sample/past/past/1578953386820
        downlink.set_Hotel("room_check_sample");
        downlink.set_floor("past");
        downlink.set_room("past");
        downlink.vals.set_file_path();
        downlink.setScreen(this);
        //downlink.s3credentialsProvider();
        downlink.setTransferUtility();
        Log.d("see_past_data", downlink.vals.file_path);
        downlink.vals.get_folder();
        int length = downlink.vals.list_file.length;
        file_list list_file = downlink.vals.list_file;
        for (int i =0; i<length;i++){
            //String []tmp = list_file.get_cur().getAbsolutePath().split("/");
            Button btn = new Button(this);
            btn.setText(list_file.get_stamp());
            btn.setTextSize(30);
            btn.setWidth(10000);
            btn.setHeight(200);
            runOnUiThread(()->{
                ll.addView(btn);
            });
            btn.setId(i);
            btn.setOnClickListener(v1->{
                Intent intent = new Intent(see_past_data.this, see_past_cleaning_log.class);
                intent.putExtra("length",btn.getId());
                startActivity(intent);
            });

        }
    }
}

package com.example.roommangement.maneger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.roommangement.AWS_Services.download_files;
import com.example.roommangement.AWS_Services.upload_files;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.R;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.example.roommangement.AWS_Services.upload_files.uploader;

public class settings extends AppCompatActivity {

    download_files downlink = download_files.get_server_down();
    db_cordinator table = db_cordinator.getInstance(this);
    upload_files uploader = upload_files.get_loader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }
    public List<Document> onSaveClicked() {
        List<Document> test;
            String TAG = "database:";
            Document memo = new Document();
            try{
                table.load_table();
            }catch (Exception e){
                e.printStackTrace();
            }

            downlink.setScreen(this);
           // downlink.s3credentialsProvider();
            downlink.setTransferUtility();
            test = table.getAllMemos();
            Log.d(TAG, test.get(0).toString());
        return test;
    }
    public void unclean_all_rooms(View view){
        Thread task2 = new Thread(()->{
            uploader.vals.set_Hotel_name("room-check-test");
            uploader.vals.set_Hotel_floor("past");
            uploader.vals.set_Hotel_room("past");
            uploader.vals.set_issue_path(Long.toString(System.currentTimeMillis()));
            Document previous = new Document();
                    File prev_up  = new File("");
            try{
               prev_up = File.createTempFile("past",".txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Document> rooms_table = onSaveClicked();
            Thread task3 = new Thread(()->{
                for (int i =0; i<rooms_table.size(); i++){
                    int finalI = i;
                    Runnable r = () -> {
                        Document builder = table.getMemoById(rooms_table.get(finalI).get("room_num").asInt());
                        previous.put(builder.get("room_num").asString(),builder.get("room_status").asString());
                        builder.put("room_status","unclean");
                        table.update(builder);
                    };
                    Thread task1 = new Thread(r);
                    task1.start();
                    try {
                        task1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            task3.start();
            try {
                task3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BufferedWriter bw = null;
            JSONArray tmp = new JSONArray();
            for (int i = 0; i <rooms_table.size();i++){
                try {
                    tmp.put(Document.toJson(rooms_table.get(i)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            try {

                bw = new BufferedWriter(new FileWriter(prev_up));
                bw.write(tmp.toString());
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploader.set_file(prev_up);
            Log.d("file_size", Long.toString(prev_up.length()));
            uploader.vals.transferObserverListener("upload");
        });

        task2.start();
        finish();
    }

    public void see_past_data(View view){
        Intent intent = new Intent(settings.this,see_past_data.class);
        startActivity(intent);

    }
}

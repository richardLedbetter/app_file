package com.example.roommangement.Maintance_screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.roommangement.AWS_Services.download_files;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.File_list.file_list;
import com.example.roommangement.R;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class see_Maintence extends AppCompatActivity {

    RecyclerView page;
    LinearLayout ll;
    db_cordinator table;

    String [] file_list;
    int Room;
    int issue_num;

    String TAG = "see_Maintence";
    download_files downlink;
    String curr_path;

    Thread task1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see__maintence);

        table = db_cordinator.getInstance(this);
        downlink = download_files.get_server_down();

        Room = getIntent().getIntExtra("Room_num",-1);
        Runnable UI_set_up = ()->{set_up();};
        task1 = new Thread(UI_set_up);
        task1.start();
    }

    public void delete(int lookup){


        Runnable r = () -> {
            downlink.vals.delete_file(file_list[lookup]);
            Document builder = table.getMemoById(Room);
            builder.put("maintence_issues",builder.get("maintence_issues").asInt()-1);
            table.update(builder);
        };
        Thread task2 = new Thread(r);
        task2.start();
        finish();

    }

    public void set_up(){
        issue_num = table.getMemoById(Room).get("maintence_issues").asInt();
        ll = findViewById(R.id.page);
        Bitmap bitmap;

        downlink.set_floor(Integer.toString(1));
        downlink.set_room(Integer.toString(Room));
        downlink.set_Hotel("room_check_sample");
        downlink.set_file_path();


        downloadFileFromS3();
        while(!downlink.vals.download_complete){
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
        }
        file_list curr2 = downlink.vals.list_file;
        //Log.d(TAG, curr2.curr.]);
        curr2.setFirst();
        file_list = new String[curr2.length];
        Log.d("length--sadf", Integer.toString(curr2.length));
        for (int i =0;i<curr2.length;i++){
            ImageView photo_dis = new ImageView(this);

            Button btn = new Button(this);
            btn.setText("Fix issue :"+Integer.toString((i+1)/2));
            btn.setHeight(100);
            btn.setId(i);
            btn.setOnClickListener(v1->{
                delete(btn.getId());
            });
            file_list[i] = curr2.get_cur().getAbsolutePath();
            Log.d("text file", curr2.get_cur().getAbsolutePath());
            if (file_list[i].endsWith(".txt")){
                Log.d("text file", Long.toString(curr2.get_cur().length()));
                String tmp = "";
                try {
                   tmp =  Files.toString(curr2.get_cur(), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TextView show_text = new TextView(this);
                show_text.setText(tmp);
                show_text.setTextSize(40);
                runOnUiThread(()->{
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(584, 584);
                    layoutParams.leftMargin = 10;
                    layoutParams.topMargin = 10;
                    ll.addView(show_text);
                    ll.addView(btn);
                });
            }else {
                bitmap = BitmapFactory.decodeFile(curr2.get_cur().getAbsolutePath());
                photo_dis.setImageBitmap(bitmap);
                photo_dis.setId(i);
                Runnable t = () -> {
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(584, 584);
                    layoutParams.leftMargin = 10;
                    layoutParams.topMargin = 10;
                    ll.addView(photo_dis, layoutParams);
                };
                runOnUiThread(t);
            }
            curr2.print_num();
            curr2.move_one();
        }

        Button btn;



    }


    public void downloadFileFromS3(){

        File downed = new File("");
        try{
            downed = File.createTempFile("item",".jpg");
            downlink.set_file(downed);
        } catch (IOException e) {
            Log.d("this->", "downloadFileFromS3: broke");
            e.printStackTrace();
        }
        if(downed==null){
            Log.d("look here ", downed.getName());
        }
        downlink.setScreen(this);
        //downlink.s3credentialsProvider();
        downlink.setTransferUtility();
        downlink.set_bucket("room-check-test");
        //file selector

        //end file selector
        downlink.vals.get_folder();

    }
}

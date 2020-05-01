package com.example.roommangement.Maintance_screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.roommangement.maid_home_screen;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class see_Maintence extends AppCompatActivity {

    String TAG = "see_Maintence commented";
    String Rel_Path = "com/example/roommangement/Maintance_screens/see_Maintence.java";


    LinearLayout ll;


    String [] file_list;
    int Room;
    int issue_num;

    db_cordinator table;//dynamodb database object
    download_files downlink;//s3 download object

    Thread task1;//used for cloud transactions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see__maintence);

        Log.d(TAG,Rel_Path);

        //initialize AWS interface
        table = db_cordinator.getInstance(this);
        downlink = download_files.get_server_down();


        //TO-DO get rid of Intent find better way to pass Room value
        Room = getIntent().getIntExtra("Room_num",-1);

        //builds UI in background to take tasks off of main thread
        Runnable UI_set_up = ()->{set_up();};
        task1 = new Thread(UI_set_up);
        task1.start();
    }

    public void delete(int lookup){
        //look up is the file path index of the text for the given ticket
        int text_file_index = lookup;
        int image_file_index = lookup-1;
        Runnable r = () -> {
            //Log.d(TAG, "deleting..\n"+ file_list[text_file_index] +"\n" +file_list[image_file_index]);
            //deleting from s3 storage
            downlink.vals.delete_file(file_list[text_file_index]);
            downlink.vals.delete_file(file_list[image_file_index]);

            //updating table
            Document builder = table.getMemoById(Room);
            builder.put("maintence_issues",builder.get("maintence_issues").asInt()-1);
            table.update(builder);

        };

        Thread task2 = new Thread(r);
        task2.start();
        //sends user back one screen

        Intent intent = new Intent(this, maid_home_screen.class);
        startActivity(intent);

    }

    public void set_up(){
        //gets amount of maintenance issues
        issue_num = table.getMemoById(Room).get("maintence_issues").asInt();

        ll = findViewById(R.id.page);
        Bitmap bitmap;//used to store image of maintance isuse

        //set file path and pulls file from S3
        downlink.set_floor(Integer.toString(1));
        downlink.set_room(Integer.toString(Room));
        downlink.set_Hotel("room_check_sample");
        downlink.set_file_path();
        downloadFileFromS3();
        //waits for files to finish downloading
        while(!downlink.vals.download_complete){
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
        }

        //linked list object of downloaded files
        file_list curr2 = downlink.vals.list_file;
        curr2.curr_2_start();

        file_list = new String[curr2.length];
        //Log.d(TAG, Integer.toString(curr2.length));

        for (int i =0;i<curr2.length;i++){

            ImageView displayed_pic = new ImageView(this);

            //repair button creation
            Button btn = new Button(this);
            btn.setText("Fix issue :"+Integer.toString((i+1)/2));
            btn.setHeight(100);
            btn.setId(i);
            btn.setOnClickListener(v1->{
                delete(btn.getId());
            });
            //end repair creation

            //gets internal path to file
            file_list[i] = curr2.get_curr_path();
            //Log.d("text file", curr2.get_curr_path());

            if (file_list[i].endsWith(".txt")){
                //displays text file
               // Log.d("text file", Long.toString(curr2.get_cur().length()));
                String tmp = "";
                //reads file
                try {
                   tmp =  Files.toString(curr2.get_curr_file(), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //initializing TextView to display description
                TextView show_text = new TextView(this);
                show_text.setText(tmp);
                show_text.setTextSize(40);


                runOnUiThread(()->{
                    //layout formatting
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(584, 584);
                    layoutParams.leftMargin = 10;
                    layoutParams.topMargin = 10;
                    ll.addView(show_text);
                    ll.addView(btn);
                });
            }else {
                //to display photo on screen
                bitmap = BitmapFactory.decodeFile(curr2.get_curr_file().getAbsolutePath());
                displayed_pic.setImageBitmap(bitmap);
                displayed_pic.setId(i);
                Runnable t = () -> {
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(584, 584);
                    layoutParams.leftMargin = 10;
                    layoutParams.topMargin = 10;
                    ll.addView(displayed_pic, layoutParams);
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

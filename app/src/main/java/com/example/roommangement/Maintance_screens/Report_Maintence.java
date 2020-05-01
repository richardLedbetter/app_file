package com.example.roommangement.Maintance_screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.roommangement.AWS_Services.upload_files;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.R;
import com.example.roommangement.photo.photo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static android.graphics.BitmapFactory.decodeByteArray;

public class Report_Maintence extends AppCompatActivity {
    //other variables
    int Room;
    int issue_num;


    String TAG = "Report_Maintence";

    //views
    RecyclerView r;
    FrameLayout frameLayout;
    Button b;
    EditText input;
    DisplayMetrics displayMetrics;
    int dpHeight;
    int dpwidth;

    //files
    File photo;
    File discription;
    String [] issue_name  ={"Shower","Toilet","Sink","Ded","Night stand","TV","AC","Desk","Window"};


    //Camera
    photo showCamera;
    Camera camera;

    //AWS s3
    upload_files uploader = upload_files.get_loader();

    //AWS dynamodb
    db_cordinator table = db_cordinator.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_maintence);
        Room = getIntent().getIntExtra("Room_num",-1);
        issue_num = getIntent().getIntExtra("issue_num",0);

        //display measurements
        displayMetrics = this.getResources().getDisplayMetrics();
        dpwidth = (int) (displayMetrics.widthPixels);
        dpHeight = (int) (displayMetrics.heightPixels );
        area();
        //set_up();
    }

    public void set_up(){
        //Camera setup
        camera = Camera.open();
        showCamera = new photo(this, camera);

        //f
        frameLayout = (FrameLayout) findViewById(R.id.takePhoto);
        dpHeight = (int) (displayMetrics.heightPixels);
        dpwidth= (int) (displayMetrics.widthPixels);
        frameLayout.setLayoutParams( new ConstraintLayout.LayoutParams(dpwidth,(int)(dpHeight*.8)));
        frameLayout.addView(showCamera);

        //PHOTO BUTTON
        b = findViewById(R.id.photo_btn);
        b.setText("Take Photo");
        b.setTextSize(30);
        b.setOnClickListener(this::photo_button);
        b.setLayoutParams(new ConstraintLayout.LayoutParams(dpwidth,(int)(dpHeight*.2)));
        dpHeight = (int) (displayMetrics.heightPixels);
        dpwidth= (int) (displayMetrics.widthPixels);
        Log.d("screen size", Integer.toString(dpHeight));
        dpHeight = (int) (displayMetrics.heightPixels);
        b.setY((int)(dpHeight+20));
        b.setX((int)(dpwidth));
    }

    public void report_issues(View view){
        uploader.vals.set_Hotel_name("room-check-test");
        uploader.vals.set_Hotel_floor(Integer.toString(1));
        uploader.vals.set_Hotel_room(Integer.toString(Room));
        uploader.vals.set_issue_path(Long.toString(System.currentTimeMillis())+".jpg");
        uploader.set_file(photo);
        Log.d(TAG, photo.toString());
        uploader.vals.transferObserverListener("upload");

        try {
            photo = File.createTempFile("discription", ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runnable r = () -> {
            Document builder = table.getMemoById(Room);
            builder.put("maintence_issues",builder.get("maintence_issues").asInt()+1);
            table.update(builder);

        };

        Thread task1 = new Thread(r);
        task1.start();
        Editable tmp = input.getText();
        String tmp2 = tmp.toString();

        try {
            photo = File.createTempFile("tempfile", ".txt");
            //System.out.println(vals.file.getName());
            BufferedWriter bw = new BufferedWriter(new FileWriter(photo));
            bw.write(tmp2);
            bw.close();
            //Log.d("things in file", Integer.toString((int)vals.file.length()));
        } catch (IOException e) {
            Log.d("things went wrong", "uploadFileToS3: ");
            e.printStackTrace();
        }

        uploader.vals.set_issue_path(Long.toString(System.currentTimeMillis())+".txt");
        uploader.set_file(photo);
        uploader.vals.transferObserverListener("upload");
        finish();
    }


    public void area(){
        GridLayout t = findViewById(R.id.problems);
        int catigories = 9;

        for(int i=0;i<issue_name.length;i++){
            Button tmp = new Button(this);
            tmp.setText(issue_name[i]);
            tmp.setWidth(200);
            tmp.setHeight(250);
            tmp.setOnClickListener(v1->{
                t.removeAllViews();
                t.setVisibility(View.GONE);
                ConstraintLayout tmp2 = findViewById(R.id.palet);
                tmp2.removeView(t);
                set_up();
            });
            t.addView(tmp,i);
        }
    }


    public void move(){
        frameLayout.removeAllViews();
        input = new EditText(this);
        input.setWidth(dpwidth);
        input.setHeight((int)(dpHeight*.8));
        input.setHint("input issues");
        frameLayout.addView(input);
        b.setText("Submit Issue");
        int hex = 0XFFDAA520;
        b.setBackgroundColor(hex);
        b.setTextColor(Color.BLACK);
        b.setOnClickListener(this::report_issues);

    }






    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        String TAG = "ERROR FINDER";
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken: line 202");
            Camera.Parameters params = camera.getParameters();
            Log.d(TAG, "onPictureTaken: line 203");
            try {
                photo = File.createTempFile("photo", ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                Log.d(TAG, "onPictureTaken: line 211");
                FileOutputStream stream = new FileOutputStream(photo);
                Bitmap t = BitmapFactory.decodeByteArray(data,0,data.length);
                t.compress(Bitmap.CompressFormat.JPEG,75,stream);
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.release();
            move();
        }


    };


    public void photo_button(View v) {
        Log.d("clicked", "photo_button: ");
        camera.takePicture(null,null,mPictureCallback);
    }

}

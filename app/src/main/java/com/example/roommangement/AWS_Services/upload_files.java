package com.example.roommangement.AWS_Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class upload_files {
    public static volatile upload_files uploader;
    public cordinator vals = cordinator.get_orchestrate();

    public static upload_files get_loader(){
        if (uploader ==null){
            uploader = new upload_files();
        }
        return uploader;
    }
    public void set_screen(Context p){
        vals.screen = p;
    }
    public void set_file_path(String path){
        vals.file_path = path;
    }
    public void set_file(File tmp){
        vals.file = tmp;
    }
    public void setScreen(Context curr_screen){
        vals.screen = curr_screen;
    }
    public void write_2_file(String tmp){
        try {
            vals.file = File.createTempFile("tempfile", ".txt");
            //System.out.println(vals.file.getName());
            BufferedWriter bw = new BufferedWriter(new FileWriter(vals.file));
            bw.write(tmp);
            bw.close();
            Log.d("things in file", Integer.toString((int)vals.file.length()));
        } catch (IOException e) {
            Log.d("things went wrong", "uploadFileToS3: ");
            e.printStackTrace();
        }

        set_file(vals.file);
    }
    public void s3credentialsProvider(){

        // Initialize the AWS Credential
        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider = new CognitoCachingCredentialsProvider(
                vals.screen,
                vals.pool, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );
        setAmazonS3Client(cognitoCachingCredentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){

        // Create an S3 client
        vals.s3Client = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        vals.s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
    }

    public void setTransferUtility(){

        vals.transferUtility = new TransferUtility(vals.s3Client,
                vals.screen);
    }



}

package com.example.roommangement.AWS_Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.event.ProgressListenerChain;
import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.MultipleFileDownload;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferProgress;
import com.amazonaws.mobileconnectors.s3.transfermanager.internal.MultipleFileDownloadImpl;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.roommangement.File_list.file_list;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class cordinator {
    //cordinator
    private static volatile cordinator orchestrate = new cordinator();
    public static cordinator get_orchestrate(){
        return orchestrate;
    }

    public Context screen;
    public void setScreen(Context curr_screen){
        screen = curr_screen;
    }

    //transfer file
    public File file =   new File("");
    public void set_file(File tmp){
        file = tmp;
    }

    public file_list list_file;


    AmazonS3 s3Client;
    public TransferUtility transferUtility;
    //credentials
    String key;
    public String file_path;
    public String Bucket;
    //us-west-2:e4f1669c-a472-43e4-8fc2-73de20022289
    public String pool = "us-west-2:e4f1669c-a472-43e4-8fc2-73de20022289";

    public void set_pool(String pools){pool = pools;}
    public void set_bucket(String buckets){Bucket = buckets;}


    List<String> listing;
    JSONObject message;
    public Boolean download_complete =false;


    public String Hotel_name = "";
    public String Hotel_floor = "";
    public String Hotel_room = "";

    public void set_Hotel_name(String name){
        Hotel_name = "room_check_sample";
    }
    public void set_Hotel_floor(String floor){
        Hotel_floor = floor;
    }
    public void set_Hotel_room(String room){
        Hotel_room = room;
    }
    public void set_issue_path(String issue_num){
        file_path = Hotel_name+'/'+
                Hotel_floor+'/'+
                Hotel_room+'/'+
                issue_num;
        Log.d("cordinator", file_path);
    }
    public void set_file_path(){
        file_path = Hotel_name+'/'+
                Hotel_floor+'/'+
                Hotel_room+'/'+"1";
    }
//s3://room-check-test/room_check_sample/1/101/0
    public void set_message(String tmp){
        try {
            message = new JSONObject(tmp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void print(){
        String tmp = "";
        try {
            //downed = File.createTempFile("tempfile", ".txt");
            //System.out.println(downed.getName());
            BufferedReader br = new BufferedReader(new FileReader(file));
            //Log.d("look here", Integer.toString((int)downlink.vals.file.length()));
            String st;
            while ((st = br.readLine()) != null){
                tmp = tmp+st;
                Log.d("message:", st);
            }
            br.close();
            message = new JSONObject(tmp);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            Log.d("things went wrong", "uploadFileToS3: ");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void get_folder(){
        download_complete = false;
        Log.d("path1", file_path);
        ListObjectsV2Request req = new ListObjectsV2Request().
                withBucketName("room-check-test")
                .withPrefix(file_path).withDelimiter("/");
        ListObjectsV2Result listing = s3Client.listObjectsV2(req);
        Boolean first = false;
        list_file = new file_list();
        for (S3ObjectSummary summary: listing.getObjectSummaries()) {

            file_path = summary.getKey();

            transferObserverListener("download");
            Log.d("path2", file_path);


        }
        try {
            Thread.sleep(1000);
            download_complete = true;
        }catch (Exception e){

        }

    }

    public void transferObserverListener(String up_down){
        download_complete =false;
        TransferObserver transferObserver;
        if (up_down == "upload") {
            Log.d("things->", file.getName());
            transferObserver = transferUtility.upload(
                    "room-check-test",          /* The bucket to upload to */
                    file_path,/* The key for the uploaded object */
                    file      /* The file where the data to upload exists */
            );
        }else{
            Log.d("things->", file_path);
            String [] tmp = file_path.split("/");
            String f= "";
            if (file_path.contains(".txt")){
                f = ".txt";
            }else if (file_path.contains(".jpg")){
                f = ".jpg";
            }else{
                f = new Date(Long.parseLong(tmp[tmp.length-1])).toString();
            }

            transferObserver = transferUtility.download(
                    "room-check-test",          /* The bucket to upload to */
                    file_path,/* The key for the uploaded object */
                    list_file.add(f)     /* The file where the data to upload exists */
            );
        }

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                //Toast.makeText(screen, "State Change" + state,
                        //Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                if (bytesTotal==0){
                    bytesTotal = 1;
                    Log.d("loading", "onProgressChanged: ");
                }
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                //Toast.makeText(screen, "Progress in %" + percentage,
                        //Toast.LENGTH_SHORT).show();
                if (bytesCurrent==bytesTotal){
                    Log.d("file size", Integer.toString((int)file.length()));

                }
            }

            @Override
            public void onError(int id, Exception ex) {

                Log.e("cordinator",ex.getMessage());
            }

        });
    }

    public void delete_file(String del_key){
        s3Client.deleteObject(Bucket,del_key);
    }
}

package com.example.roommangement.AWS_Services;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;

import com.amazonaws.auth.IdentityChangedListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.roommangement.MainActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class download_files {

    private static volatile download_files from_server = new download_files();
    public static download_files get_server_down(){
        return from_server;
    }

    public cordinator vals = cordinator.get_orchestrate();
    public void set_screen(Context p){
        vals.screen = p;
    }
    public void set_file_path(){
        vals.set_issue_path(Integer.toString(1));//need to change
    }
    public void set_issue_num(int issue){
        vals.set_issue_path(Integer.toString(issue));
    }
    public void set_file(File tmp){
        vals.file = tmp;
    }
    public void setScreen(Context curr_screen){
        vals.screen = curr_screen;
    }
    public void set_bucket(String buc){
        vals.Bucket = buc;
    }
    public JSONObject get_message(){
        return  vals.message;
    }
    public void downloadFileFromS3(View view){

        TransferObserver transferObserver = vals.transferUtility.download(
                vals.Bucket,     /* The bucket to download from */
               vals.key,    /* The key for the object to download */
               vals.file       /* The file to download the object to */
        );
        vals.transferObserverListener("down");
    }
    public void downloadFileS3(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    Looper.prepare();
                    vals.listing = getObjectNamesForBucket(vals.Bucket, vals.s3Client);

                    for (int i=0; i< vals.listing.size(); i++){
                        Toast.makeText(vals.screen, vals.listing.get(i),Toast.LENGTH_LONG).show();
                    }
                    Looper.loop();
                    // Log.e("tag", "listing "+ listing);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("tag", "Exception found while listing "+ e);
                }

            }
        });
        thread.start();
    }
    public void fetchFileFromS3(View view){

        // Get List of files from S3 Bucket
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    Looper.prepare();
                    vals.listing = getObjectNamesForBucket(vals.Bucket, vals.s3Client);

                    for (int i=0; i< vals.listing.size(); i++){
                        Toast.makeText(vals.screen,
                                vals.listing.get(i),Toast.LENGTH_LONG).show();
                    }
                    Looper.loop();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("tag", "Exception found while listing "+ e);
                }

            }
        });
        thread.start();
    }
    public void set_Hotel(String Hotel){
        vals.Hotel_name = Hotel;
    }
    public void set_floor(String floor){
        vals.Hotel_floor = floor;
    }
    public void set_room(String Room){
        vals.Hotel_room = Room;
    }
    public void s3credentialsProvider(){

        Log.d("ran", "s3credentialsProvider: ");

        // Initialize the AWS Credential
        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider = new CognitoCachingCredentialsProvider(
                vals.screen,
                vals.pool, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );
        //cognitoCachingCredentialsProvider.setLogins();
        setAmazonS3Client(cognitoCachingCredentialsProvider);
    }

    public void s3credentialsProvider(Map token){

        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider = new CognitoCachingCredentialsProvider(
                vals.screen, // get the context for the current activity
                "231867092748", // your AWS Account id
                vals.pool, // your identity pool id
                "arn:aws:iam::231867092748:role/Cognito_test_hotelUnauth_Role",// an authenticated role ARN
                "arn:aws:iam::231867092748:role/Cognito_test_hotelAuth_Role", // an unauthenticated role ARN
                Regions.US_WEST_2 //Region
        );

        Log.d("logged in", "s3credentialsProvider: ");

        cognitoCachingCredentialsProvider.setLogins(token);
        cognitoCachingCredentialsProvider.registerIdentityChangedListener(new IdentityChangedListener() {
            @Override
            public void identityChanged(String oldIdentityId, String newIdentityId) {
                Log.d("stuffs", "identityChanged: ");
            }
        });

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
    /**
     * @desc This method is used to return list of files name from S3 Bucket
     * @param bucket
     * @param s3Client
     * @return object with list of files
     */
    private List<String> getObjectNamesForBucket(String bucket, AmazonS3 s3Client) {
        ObjectListing objects=s3Client.listObjects(bucket);
        List<String> objectNames=new ArrayList<String>(objects.getObjectSummaries().size());
        Iterator<S3ObjectSummary> iterator=objects.getObjectSummaries().iterator();
        while (iterator.hasNext()) {
            objectNames.add(iterator.next().getKey());
        }
        while (objects.isTruncated()) {
            objects=s3Client.listNextBatchOfObjects(objects);
            iterator=objects.getObjectSummaries().iterator();
            while (iterator.hasNext()) {
                objectNames.add(iterator.next().getKey());
            }
        }
        return objectNames;
    }



}

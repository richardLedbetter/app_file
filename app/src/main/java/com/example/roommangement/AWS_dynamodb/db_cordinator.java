package com.example.roommangement.AWS_dynamodb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Expression;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanFilter;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.DynamoDBEntry;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.example.roommangement.AWS_Services.cordinator;
import com.example.roommangement.AWS_Services.download_files;
import com.example.roommangement.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class db_cordinator {

    //passable self
    private static volatile db_cordinator base = new db_cordinator();

    public static synchronized db_cordinator getInstance(Context s){
        if (base == null){
            base = new db_cordinator();
        }
        base.screen =s;
        return base;
    }
    //cred info
    cordinator vals = cordinator.get_orchestrate();
    Context screen;

    //Pool info
    String Pool = "us-west-2:e4f1669c-a472-43e4-8fc2-73de20022289";
    public void set_Pool(String nPool){
       vals.set_pool(nPool);
    }
    public String get_Pool(){
        return vals.pool;
    }

    Regions Region = Regions.US_WEST_2;// Voodoo magic its the wrong region but works
    public Document Doc = new Document();


    String Table_name = "test_sample";
    public void set_Table_name(String t){
        Table_name = t;
    }
    public String get_Table_name(){
        return Table_name;
    }


    //working tools
    private Table curr_table;
    AmazonDynamoDBClient table_client;
    public Map token;


    CognitoCachingCredentialsProvider creds;
    // Create a new credentials provider
    public void set_token(Map k){
        token =k;
    }
    public void load_table(){
        String TAG = "p";
        download_files downlink = download_files.get_server_down();
        vals.set_pool("us-west-2:e4f1669c-a472-43e4-8fc2-73de20022289");
         creds = new CognitoCachingCredentialsProvider(
                vals.screen, // get the context for the current activity
                "231867092748", // your AWS Account id
                vals.pool, // your identity pool id
                "arn:aws:iam::231867092748:role/Cognito_test_hotelUnauth_Role",// an authenticated role ARN
                "arn:aws:iam::231867092748:role/Cognito_test_hotelAuth_Role", // an unauthenticated role ARN
                Regions.US_WEST_2 //Region
        );

        creds.setLogins(token);
        table_client= new AmazonDynamoDBClient(creds);
        table_client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
        curr_table = Table.loadTable(table_client, Table_name);
        Log.d(TAG, curr_table.getTableName());
    }

    /**
     * create a new memo in the database
     * @param memo the memo to create
     */
    public void create(Document memo) {
        if (!memo.containsKey("userId")) {
            memo.put("userId", creds.getCachedIdentityId());
        }
        if (!memo.containsKey("noteId")) {
            memo.put("noteId", UUID.randomUUID().toString());
        }
        if (!memo.containsKey("creationDate")) {
            memo.put("creationDate", System.currentTimeMillis());
        }
        curr_table.putItem(memo);
    }


    public void update(Document memo) {
        Document document = curr_table.updateItem(memo, new UpdateItemOperationConfig().withReturnValues(ReturnValue.UPDATED_NEW));
        Log.d("updated", document.toString());
    }
    /**
     * Delete an existing memo in the database
     * @param memo the memo to delete
     */
    public void delete(Document memo) {
        curr_table.deleteItem(
                memo.get("userId").asPrimitive(),   // The Partition Key
                memo.get("noteId").asPrimitive());  // The Hash Key
    }
    /**
     * Retrieve a memo by noteId from the database
     * @param noteId the ID of the note
     * @return the related document
     */
    public Document getMemoById(String noteId) {
        return curr_table.getItem(new Primitive(noteId));
    }
    public Document getMemoById(int noteId) {
        return curr_table.getItem(new Primitive(noteId));
    }
    /**
     * Retrieve all the memos from the database
     * @return the list of memos
     */
    public List<Document> getAllMemos() {
        String TAG = "Expression";
        Expression params = new Expression();
        //params.setExpressionStatement("room_num > :val");
        //params.withExpressionAttibuteNames("val","200");
        //params.addExpressionAttributeValues("val",tmp);
       //params.addExpressionAttributeNames("room_num","200");
//        Log.d(TAG, params.getExpressionStatement());
        //params.
        return curr_table.scan(params).getAllResults();
    }

    public List<Document> updated_info(List<Document> memo){
        return null;
    }

}

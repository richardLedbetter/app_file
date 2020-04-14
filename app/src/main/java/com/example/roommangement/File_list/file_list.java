package com.example.roommangement.File_list;

import android.util.Log;

import java.io.File;
import java.io.IOException;


/*
Linked list structure for Files downloaded from S3
    Variables
        first = head
        curr= current
        length = size of list

  Subclass file_node contains
     public File current;
     public file_nodes next;
     public String time_stamp;
     public String cloud_path;

 */
public class file_list {
    public String TAG = "file_list Documented";


    public file_nodes first;
    public file_nodes curr;
    public int length =0;

    //constructors
    public file_list(){
        first = null;
        curr = first;
    }

    //set/getter for cloud path of current node
    public void set_curr_path(String pa){
        curr.cloud_path = pa;
    }
    public String get_curr_path(){
        return curr.cloud_path;
    }


    //add file to list once downloaded
    public File add(String suf){
        length++;
        if (first==null){
           // Log.d(TAG, "add: 1");
            first = new file_nodes();
            curr = first;
            try {
                if (!suf.contains(".")){
                    first.time_stamp =suf;
                    //Log.d("file_name", suf);
                    first.current = File.createTempFile(suf,".txt");
                }else{
                    first.current = File.createTempFile("photo",suf);
                }

            } catch (IOException e) {
                Log.d(TAG,"=========ERROR=======");
                e.printStackTrace();
            }
            //Log.d(TAG, first.current.getAbsolutePath());
            return first.current;
        }else{
           // Log.d("ran this", "add: 2");
            curr.next = new file_nodes();
            curr = curr.next;
            curr.num = length;
            try {
                if (!suf.contains(".")){
                    curr.time_stamp =suf;
                    curr.current = File.createTempFile(suf,".txt");
                }else{
                    curr.current = File.createTempFile("photo",suf);
                }
            } catch (IOException e) {
                Log.d(TAG,"=========ERROR=======");
                e.printStackTrace();
            }
            //Log.d("File2", curr.current.getAbsolutePath());
            return curr.current;
        }

    }


    //returns current element
    public file_nodes get_cur(){
        return curr;
    }

    //gets current nodes file
    public File get_curr_file(){
        return curr.current;
    }


    //sets current back to first
    public void curr_2_start(){
        curr = first;
    }

    //moves linked list one
    public void move_one(){
        curr = curr.next;
    }

    //used for debugging prints linked list pos
    public void print_num(){
        Log.d(TAG+" file_num", Integer.toString(curr.num));
    }

    //returns time stamp
    public String get_stamp(){
        return curr.time_stamp;
    }


}
 class file_nodes{
     public File current;
     public file_nodes next;
     public String time_stamp;
     public String cloud_path;
     int num =0;
}
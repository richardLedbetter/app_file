package com.example.roommangement.File_list;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class file_list {
    public file_nodes first;
    public file_nodes curr;
    public int length =0;
    public file_list(File t){
        length++;
        first = new file_nodes();
        first.current = t;
        first.num = length;
        curr = first;
    }
    public file_list(){
        first = null;
        curr = first;
    }
    public void set_curr_path(String pa){
        File tmp = new File(pa);

        curr.current.renameTo(tmp);
        Log.d("file_path", curr.current.getAbsolutePath());
    }
    public File add(String suf){
        length++;
        if (first==null){
            Log.d("ran this", "add: 1");
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
                e.printStackTrace();
            }
            Log.d("File", first.current.getAbsolutePath());
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
                e.printStackTrace();
            }
            Log.d("File", curr.current.getAbsolutePath());
            return curr.current;
        }

    }
    public void setFirst(){
//        Log.d("current1", curr.current.getName());
        curr = first;
//        Log.d("current2", curr.current.getName());
    }
    public File get_cur(){
        return curr.current;
    }
    public void move_one(){
        curr = curr.next;
    }
    public void print_num(){
        Log.d("file_num", Integer.toString(curr.num));
    }
    public String get_stamp(){
        return curr.time_stamp;
    }
}
 class file_nodes{
     public File current;
     public file_nodes next;
     public String time_stamp;
     int num =0;
}
package com.example.roommangement;




import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.roommangement.AWS_Services.download_files;
import com.example.roommangement.AWS_dynamodb.db_cordinator;
import com.example.roommangement.global_vars.auths;
import com.example.roommangement.maid_screens.room_options_maid;
import com.example.roommangement.maneger.settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class maid_home_screen extends AppCompatActivity {

    download_files downlink = download_files.get_server_down();
    db_cordinator table = db_cordinator.getInstance(this);
    SwipeRefreshLayout pullToRefresh;
    LinearLayout ll;
    auths auth_lvl = auths.get_auth();

    String curr_path;
    Thread task1,task2,task3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maid_home_screen);
        ll =  (LinearLayout)(findViewById(R.id.ll));
        set_up();

        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.sprit);
        Log.d("MS1", Integer.toString(pullToRefresh.getWidth()));
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ll.removeAllViews();
                Log.d("maneger_screen_one", "onRefresh: ");
                set_up();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void set_up(){
        Button other_features = new Button(this);
        other_features.setText("Other features");
        other_features.setTextSize(50);
        other_features.setOnClickListener(v1->{
            Intent intent = new Intent(maid_home_screen.this, settings.class);
            startActivity(intent);
        });
        if (auth_lvl.auth_lvl==3){
            ll.addView(other_features);
        }
        Button floor_1 = new Button(this);
        Button floor_2 = new Button(this);

        final Boolean[] floor_1_clicked = {false};
        floor_1.setText("floor 1 ⌄");
        floor_1.setTextSize(50);
        floor_1.setOnClickListener(v1->{

            Runnable update_screen = ()-> {
                if (auth_lvl.auth_lvl == 3){
                    runOnUiThread(()->{
                        ll.removeAllViews();
                        ll.addView(other_features);
                    });
                }
                runOnUiThread(()->{
                    if (auth_lvl.auth_lvl != 3){
                        ll.removeAllViews();
                    }

                    ll.addView(floor_1);
                });
                if (floor_1_clicked[0]){
                    floor_1_clicked[0] = false;
                }else{
                    other_stuff(1);
                    floor_1_clicked[0] = true;
                }
                runOnUiThread(()->{
                    ll.addView(floor_2);
                });

            };
            task2 = new Thread(update_screen);
            task2.start();

        });

        final boolean [] floor_2_clicked = {false};
        floor_2.setText("floor 2 ⌄");
        floor_2.setOnClickListener(v1->{
            if (auth_lvl.auth_lvl == 3){
                runOnUiThread(()->{
                    ll.removeAllViews();
                    ll.addView(other_features);
                });
            }
            runOnUiThread(()->{
                if (auth_lvl.auth_lvl != 3){
                    ll.removeAllViews();
                }
                ll.addView(floor_1);
            });
            runOnUiThread(()->{
                ll.addView(floor_2);
            });
            Runnable update_screen = ()-> {
                if (floor_2_clicked[0]){
                    floor_2_clicked[0] = false;
                }else{
                    other_stuff(2);
                    floor_2_clicked[0] = true;
                }

            };
            task2 = new Thread(update_screen);
            task2.start();
        });
        floor_2.setTextSize(50);
        runOnUiThread(()->{
            ll.addView(floor_1);
            ll.addView(floor_2);
        });

    }

    public List<Document> onSaveClicked() {
        String TAG = "database:";
        Document memo = new Document();
        table.set_Table_name("test_sample");
        try{
            table.load_table();
        }catch (Exception e){
            e.printStackTrace();
        }

        List<Document> test = table.getAllMemos();
        //Log.d(TAG, test.get(0).toString());
        return test;
    }



    public void showRoom(List<Document> rooms_table,int floor){

        JSONObject  sample_room= new JSONObject();
        int issue_num = 0;


        for (int i =0; i<rooms_table.size(); i++){
            int room_floor = 0;
            try {
                String TAG = "Room";
                sample_room = new JSONObject(rooms_table.get(i).toString());
                //Log.d(TAG, sample_room.toString());
                issue_num = Integer.parseInt(sample_room.
                        getJSONObject("maintence_issues")
                        .getString("value"));

                //num_rooms = sample_room.getJSONObject("")
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                room_floor = (sample_room.getJSONObject("room_num").getInt("value"));
                //Log.d("floor b", Integer.toString(room_floor));
                room_floor = room_floor/100;
                //Log.d("floor a", Integer.toString(room_floor));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (auth_lvl.auth_lvl==2&&issue_num<=0){

            }else if(room_floor==floor){
                //button atributes
                Button btnTag = new Button(this);
                btnTag.setId(i);
                btnTag.setWidth(99999);
                btnTag.setTextSize(28);

                try {
                    String tmp = "";
                    tmp = tmp + "Room: " + sample_room.getJSONObject("room_num").getString("value") + "\n";
                    tmp = tmp + "Status: " + sample_room.getJSONObject("room_status").getString("value") + "\n";
                    tmp = tmp + "cleaned by: " + sample_room.getJSONObject("cleaned_by").getString("value") + "\n";
                    tmp = tmp + "issues: " + sample_room.getJSONObject("maintence_issues").getString("value");
                    btnTag.setText(tmp);
                    btnTag.setId(Integer.parseInt(sample_room.getJSONObject("room_num").getString("value")));

                    List<Document> finalRooms_table = rooms_table;
                    int finalI = i;
                    btnTag.setOnClickListener(v1 -> {
                        Log.d("room clicked", Integer.toString(btnTag.getId()));
                        int tmp_i = btnTag.getId();
                        Room_select(tmp_i);
                        table.Doc = finalRooms_table.get(finalI);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Runnable add = () -> {
                    btnTag.setLayoutParams(new ViewGroup.LayoutParams
                            (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ll.addView(btnTag);
                };
                runOnUiThread(add);
            }
        }
    }


    public void Room_select(int room_num){
        Intent intent = new Intent(maid_home_screen.this, room_options_maid.class);
        // Log.d("Room_number is:", Integer.toString(room_num));
        intent.putExtra("Room_num",room_num);

        startActivity(intent);
    }

    public void other_stuff(int floor){
        List<Document> rooms_table = onSaveClicked();
        String TAG = "ROOM:";
        for (int i =0;i<rooms_table.size();i++){
            Log.d(TAG, rooms_table.get(i).toString());
        }

        rooms_table = sorter(rooms_table);


         LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_maid_home_screen, null);
        ScrollView sv = (ScrollView) v.findViewById(R.id.some);
        // Find the ScrollView
        showRoom(rooms_table,floor);


        // Add text

        int num_rooms = 0;
        //Button btnTag;
       // String TAG = "ran";




       int room_num = 0;
        String cleaned_by ="";
        String info = "";
        String room_sts = "";
        String rep_prob = "";





    }

    public List<Document> sorter(List<Document> rooms){
        List <Document> tmp = new ArrayList<>();
        List <CustomObject> holder = new ArrayList<>();
        int n = rooms.size();
        int pivot = 0;
        for (int i =0;i<n;i++){
            try {
                pivot = Integer.parseInt(new JSONObject
                        (rooms.get(i).toString())
                        .getJSONObject("room_num").getString("value"));
                holder.add(new CustomObject(pivot,i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(holder);
        for (int i =0;i<n;i++){
            tmp.add(rooms.get(holder.get(i).value2));
        }
        return tmp;
    }

};
//used for sorting rooms by room_num
   class CustomObject implements Comparable<CustomObject> {
        int value1;
        int value2;

        CustomObject(int v1, int v2) {
        value1 = v1;
        value2 = v2;
        }
       public int getCreatedOn() {
           return value1;
       }

       public void setCreatedOn(int createdOn) {
           this.value1 = createdOn;
       }
       @Override
       public int compareTo(CustomObject o) {
            return Integer.compare(this.value1,o.value1);

       }
   }
package com.example.roommangement.global_vars;

public class auths {
    public static auths o;
public int auth_lvl;
int Hotels;
public void set_Hotel(int H){
    Hotels = H;
}
public int get_Hotel(){
    return Hotels;
}
String username;
public void set_username(String t){
    username = t;
}
public String get_username(){
    return username;
}
public static auths get_auth(){
    if(o ==null){
        o = new auths();
        return o;
    }else{
        return o;
    }
}
}

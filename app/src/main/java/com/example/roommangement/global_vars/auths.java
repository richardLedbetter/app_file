package com.example.roommangement.global_vars;

public class auths {
    public static auths o;
public int auth_lvl;
public static auths get_auth(){
    if(o ==null){
        o = new auths();
        return o;
    }else{
        return o;
    }
}
}

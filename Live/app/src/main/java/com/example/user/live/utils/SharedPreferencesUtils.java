package com.example.user.live.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.user.live.app.App;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.google.gson.Gson;

/**
 * Created by user on 2018/4/23.
 */
public class SharedPreferencesUtils {
    private  SharedPreferences sharedPreferences;
    private final static String NAME = "share";
    private SharedPreferences.Editor editor;

    public  SharedPreferencesUtils(Context context){
        sharedPreferences = context.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void putString(String key,String value){
        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key){
        return sharedPreferences.getString(key,"");
    }

    public void clear(String key){
        editor.remove(key);
        editor.commit();
    }

}

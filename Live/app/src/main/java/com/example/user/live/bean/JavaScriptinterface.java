package com.example.user.live.bean;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by user on 2018/1/8.
 */
public class JavaScriptinterface {


    public JavaScriptinterface(){

    }

    /*
    js调用的Android方法
     */
    @JavascriptInterface
    public void  openLive(){
        Log.e("tag_openLive","openLive");
    }

}

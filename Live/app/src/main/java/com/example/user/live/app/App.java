package com.example.user.live.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.duanqu.qupai.jni.ApplicationGlue;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by administrator on 2016/6/13.
 */
public class App extends Application {

    private  static RequestQueue  requestQueue;
    private  static App app;
    public  static int upCount = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("gnustl_shared");
//        System.loadLibrary("ijkffmpeg");//目前使用微博的ijkffmpeg会出现1K再换wifi不重连的情况
        System.loadLibrary("qupai-media-thirdparty");
//        System.loadLibrary("alivc-media-jni");
        System.loadLibrary("qupai-media-jni");
        ApplicationGlue.initialize(this);

        requestQueue = Volley.newRequestQueue(this);
        initPush();
    }

    public static App getInstance(){
        return app;
    }



    private void initPush() {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
//        JAnalyticsInterface.setDebugMode(true); //设置是否开启debug模式。true则会打印更多的日志信息。建议在init接口之前调用。
//        JAnalyticsInterface.init(this);   //极光统计SDK 初始化
////        JAnalyticsInterface.initCrashHandler(this); //开启crashlog日志上报
//        JAnalyticsInterface.stopCrashHandler(this);//关闭crashlog日志上报
    }

    public  static  void  addVolleyQueue(Request request){
        requestQueue.add(request);
    }

    public static void  removeVolleyQueue(String tag){
        requestQueue.cancelAll(tag);
    }




}

package com.example.user.live.NetWork;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by user on 2018/1/5.
 */
public class NetWorkRequest {

    private  static  NetWorkRequest  mInstance;
    private  static  RequestQueue    mRequestQueue;

    private NetWorkRequest(Context context){
        init(context);
    }

    private void init(Context context) {
        if(mRequestQueue != null){
            return;
        }
        mRequestQueue = Volley.newRequestQueue(context);

    }


}

package com.example.user.live.NetWork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Carson_Ho on 16/10/31.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        动态注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        解除注册 unregisterReceiver(networkChangeReceiver);
         */
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            Toast.makeText(context, "当前网络可用", Toast.LENGTH_SHORT).show();
            String type  = networkInfo.getTypeName();
            if(type.equalsIgnoreCase("WIFI")){
                //WIFI环境
            }else if(type.equalsIgnoreCase("MOBILE")){
                //手机网络
            }
        } else {
            Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
        }

    }
}



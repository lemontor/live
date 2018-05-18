package com.example.user.live.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.user.live.R;

/**
 * Created by user on 2018/4/25.
 */
public class LoadDialog {

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Context context;
    public LoadDialog(Context context) {
        this.context = context;
        builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_load,null);
        builder.setView(dialogView);
        alertDialog = builder.create();
    }

    public void showDialog(){
        if(!((Activity)context).isFinishing() && alertDialog != null){
            alertDialog.show();
        }
    }

    public void dismissDialog(){
        if(!((Activity)context).isFinishing() && alertDialog != null){
            alertDialog.dismiss();
        }
    }

    public  boolean  isShow(){
        return alertDialog.isShowing();
    }




}

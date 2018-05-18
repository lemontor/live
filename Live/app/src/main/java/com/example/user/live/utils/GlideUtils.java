package com.example.user.live.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.user.live.R;

import java.io.File;

/**
 * Created by user on 2018/4/26.
 */
public class GlideUtils {



    public  static void  loadBitmap(String url){

    }

    public  static void  loadLocalBitmap(Context context, String path, ImageView imageView,int width,int height){
        File file = new File(path);
        Glide.with(context).load(file).placeholder(R.drawable.mis_default_error).override(width,height).centerCrop().into(imageView);
    }


}

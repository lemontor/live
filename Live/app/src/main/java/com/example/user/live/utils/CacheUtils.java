package com.example.user.live.utils;

import android.os.Environment;
import android.util.Log;

import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2018/6/13.
 */
public class CacheUtils {
    private File rootFile;
    private File cacheFile;

//    public CacheUtils init() {
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            rootFile = Environment.getExternalStorageDirectory();
//            cacheFile = new File(rootFile,"videoInfo");
//            if(!cacheFile.exists()){
//                cacheFile.mkdir();
//            }
//        }
//        return this;
//    }
//
//    public CacheUtils putCacheForVideoInfo(final VideoTotalEntity data, final String fileName){
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                String info = new Gson().toJson(data);
//                File file = new File(cacheFile,fileName+".txt");
//                if(!file.exists()){
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                FileWriter fileWriter = null;
//                try {
//                    fileWriter = new FileWriter(file);
//                    fileWriter.write(info);
//                    fileWriter.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }finally {
//                    if(fileWriter != null){
//                        try {
//                            fileWriter.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }.start();
//        return this;
//    }
//
//    public String getVideoInfoFromCache(String fileName){
//        File file = new File(cacheFile,fileName+".txt");
//        StringBuffer stringBuffer = new StringBuffer();
//        if(file != null && file.exists()){
//            FileReader fileReader = null;
//            BufferedReader br = null;
//                try {
//                    fileReader = new FileReader(file);
//                    br = new BufferedReader(fileReader);
//                    String strLine = null;
//                    while ((strLine = br.readLine()) != null) {
//                        stringBuffer.append(strLine);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (br != null) {
//                        try {
//                            br.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (fileReader != null) {
//                        try {
//                            fileReader.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//            }
//        }
//        return stringBuffer.toString();
//    }
//
//
//    public void  remove(final String fileName){
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                File file = new File(cacheFile,fileName+".txt");
//                if(file != null && file.exists()){
//                    boolean status = file.delete();
//                    Log.e("tag_status",status+"");
//                }
//            }
//        }.start();
//
//    }



}

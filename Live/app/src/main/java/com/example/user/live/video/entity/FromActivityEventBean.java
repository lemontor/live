package com.example.user.live.video.entity;

import android.support.v4.util.Pools;

import java.util.List;

/**
 * Created by user on 2018/5/31.
 */
public class FromActivityEventBean {
    private  int  event;//1:开始；2:暂停
    private  int  poi;//上传数据的下标
    private  String  fileName;
    private List<VideoEntity>  videoEntityList;
    private boolean  isUp;
    private boolean  isNet = false;

    public boolean isNet() {
        return isNet;
    }

    public void setNet(boolean net) {
        isNet = net;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }

    public FromActivityEventBean() {
    }

    public List<VideoEntity> getVideoEntityList() {
        return videoEntityList;
    }

    public void setVideoEntityList(List<VideoEntity> videoEntityList) {
        this.videoEntityList = videoEntityList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPoi() {
        return poi;
    }

    public void setPoi(int poi) {
        this.poi = poi;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

//    private static  final Pools.SynchronizedPool<FromActivityEventBean>  upLoadInstance = new Pools.SynchronizedPool<>(10);
//
//    public static FromActivityEventBean obtain(){
//        FromActivityEventBean instance = upLoadInstance.acquire();
//        return (instance != null) ? instance : new FromActivityEventBean();
//    }
//
//    public void  recycle(){
//        upLoadInstance.release(this);
//    }

}

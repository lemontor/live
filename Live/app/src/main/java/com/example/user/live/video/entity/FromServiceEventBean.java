package com.example.user.live.video.entity;

import android.support.v4.util.Pools;

/**
 * Created by user on 2018/5/31.
 */
public class FromServiceEventBean {
    private  int  event;//1:开始；2:暂停;3:提示下载哪个文件；
    private  int  poi;//上传的数据下标

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getPoi() {
        return poi;
    }

    public void setPoi(int poi) {
        this.poi = poi;
    }

//    private static  final Pools.SynchronizedPool<FromServiceEventBean>  upLoadInstance = new Pools.SynchronizedPool<>(5);
//
//    public static FromServiceEventBean obtain(){
//        FromServiceEventBean instance = upLoadInstance.acquire();
//        return (instance != null) ? instance : new FromServiceEventBean();
//    }
//
//    public void  recycle(){
//        upLoadInstance.release(this);
//    }


}

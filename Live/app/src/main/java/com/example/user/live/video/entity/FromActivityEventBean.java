package com.example.user.live.video.entity;

/**
 * Created by user on 2018/5/31.
 */
public class FromActivityEventBean {
    private  int  event;//1:开始；2:暂停
    private  int  poi;//上传数据的下标

    public FromActivityEventBean() {
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

}

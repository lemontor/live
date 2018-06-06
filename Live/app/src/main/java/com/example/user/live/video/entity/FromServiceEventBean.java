package com.example.user.live.video.entity;

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
}

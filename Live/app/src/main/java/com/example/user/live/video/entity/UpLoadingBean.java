package com.example.user.live.video.entity;

/**
 * Created by user on 2018/6/19.
 */
public class UpLoadingBean {
    private  String len;//已经下载的数据
    private  int  progress;//进度
    private  int poi;

    public UpLoadingBean() {
    }

    public int getPoi() {
        return poi;
    }

    public void setPoi(int poi) {
        this.poi = poi;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }



}

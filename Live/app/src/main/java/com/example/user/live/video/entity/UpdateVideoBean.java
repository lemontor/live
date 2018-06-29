package com.example.user.live.video.entity;

/**
 * Created by user on 2018/6/28.
 */
public class UpdateVideoBean {

    private  String fileName;
    private  boolean  isUp;


    public UpdateVideoBean() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }
}

package com.example.user.live.video.upload.test;

/**
 * Created by user on 2018/6/25.
 */
public interface ProgressInterface {

    public void progress(int pro);
    public void fail(String error);
    public void good(String find);



}

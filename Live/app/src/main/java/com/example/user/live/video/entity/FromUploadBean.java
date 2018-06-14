package com.example.user.live.video.entity;

/**
 * Created by user on 2018/5/31.
 */
public class FromUploadBean {
    private  int  progress;//进度
    /*
    1：等待中
    2：开始
    3：连接失败
    4：本地文件不存在
    5：暂停
    6：上传中
    7：上传失败
    8：上传完成
     */
    private  int  type;
    private  String len;//已经下载的数据
    private  int  poi;

    public FromUploadBean() {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}

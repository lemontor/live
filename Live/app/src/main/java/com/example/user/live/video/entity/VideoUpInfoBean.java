package com.example.user.live.video.entity;

import java.util.List;

/**
 * Created by user on 2018/6/12.
 */
public class VideoUpInfoBean {
    private  String  msg;
    private  int  code;
    private List<VideoBean> data;

    public VideoUpInfoBean() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<VideoBean> getData() {
        return data;
    }

    public void setData(List<VideoBean> data) {
        this.data = data;
    }

    public static class VideoBean{
        private  int  video_time;
        private  String  path;
        private  String  create_time;
        private  long  size;
        private  String  user_id;
        private  String  file_name;
        private  String  from_mark;
        private  String  video_cloud_id;
        private  String  show_size;

        public VideoBean() {
        }

        public int getVideo_time() {
            return video_time;
        }

        public void setVideo_time(int video_time) {
            this.video_time = video_time;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getFrom_mark() {
            return from_mark;
        }

        public void setFrom_mark(String from_mark) {
            this.from_mark = from_mark;
        }

        public String getVideo_cloud_id() {
            return video_cloud_id;
        }

        public void setVideo_cloud_id(String video_cloud_id) {
            this.video_cloud_id = video_cloud_id;
        }

        public String getShow_size() {
            return show_size;
        }

        public void setShow_size(String show_size) {
            this.show_size = show_size;
        }
    }



}

package com.example.user.live.utils;

/**
 * Created by user on 2018/1/8.
 */
public class ConstantUtils {
    public static final String BASE_URL = "http://210.12.56.75:8777";
//        public static final String BASE_URL = "http://study.huatec.com";
    public static final String URL = "http://192.168.11.10:8773";
    //根据直播id查询评论列表（直播中的评论查询）需4条
    public static final String GET_CONTENT_PERSON_COUNT = BASE_URL + "/message/directVideoComment/H5commentList";

    //根据直播id查询评论总条数，全部人数
    public static final String GET_ALL_CONTENT_PERSON_COUNT = BASE_URL + "/message/directVideoComment/count";

    //根据直播id获取当前人数和评论保存推送
    public static final String GET_PUSH_CONTENT = BASE_URL + "/message/directVideoComment/H5Push";

    //登录认证
    public static final String CHECK_LOGIN = "http://sso.huatec.com/Login/JsonpLogin";

    public static final String LOGIN_ID = BASE_URL + "/login";

    public static final String GET_CODE = BASE_URL + "/obtainMobileCode";

    public static final String LOGIN_PHONE = BASE_URL + "/mobileLogin";

    public static final String LIVE_LIST = BASE_URL + "/portal/directVideo/H5directList";

    public static final String VIDEO_LIST = URL + "/app/uploadVideo/queryUploadRecord";

    public static final String VIDEO_INFO = URL + "/app/uploadVideo/clientUploadVideo";

    public static final String TOKEN = "token";
    public static final String DEP_ID = "dep_id";
    public static final String USER_ID = "user_id";

    public static final String HOST = "49.4.15.103";
    public static final String USENAME = "study1";
    public static final String PASSWORD = "study1";
    public static final String SERVERPATH = "/mnt/sdc/storage/video/dev/android/";
    public static final int PORT = 21;


}

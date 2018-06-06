//package com.example.user.live.utils;
//
//import android.util.Log;
//
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPReply;
//
//import java.io.IOException;
//
///**
// * Created by user on 2018/6/4.
// */
//public class FtpUtils {
//    private FTPClient  ftpClient;
//    private String  serviceIP;
//    private int     port;
//    private String  userName;
//    private String  passWord;
//
//
//    public  FtpUtils(){
//        ftpClient = new FTPClient();
//        try {
//            ftpClient.connect(serviceIP,port);
//            ftpClient.login(userName,passWord);
//            int replyCode = ftpClient.getReplyCode();
//            if(!FTPReply.isPositiveCompletion(replyCode)){
//                ftpClient.disconnect();
//                Log.e("tag","无法连接到ftp服务器，错误码为:"+replyCode);
//                return;
//            }
//            ftpClient.setCharset(TEXT_CHARSET);
//            boolean  isCompression = ftpClient.isCompressionSupported();
//            if(isCompression){
//                ftp.setCompressionEnabled(true);
//            }
//            //更改服务端当前目录
//            ftpClient.changeDirectory(serverPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}

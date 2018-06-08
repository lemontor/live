//package com.example.user.live.utils;
//
//
//import android.content.Intent;
//import android.util.Log;
//
//import com.example.user.live.video.entity.FromUploadEventBean;
//import com.example.user.live.video.entity.FtpUploadInfoEntity;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import it.sauronsoftware.ftp4j.FTPClient;
//import it.sauronsoftware.ftp4j.FTPDataTransferListener;
//import it.sauronsoftware.ftp4j.FTPException;
//import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
//
///**
// * Created by user on 2018/6/4.
// */
//public class FtpUtils {
//
//    private int port;
//    private String userName;
//    private String passWord;
//    private String servicePath;
//    private String filePath;
//    private String fileName;
//    private long fileSize;
//    private String host;
//    private FTPClient ftpClient;
//    private int uploadSize;
//
//    public void upLoad(FtpUploadInfoEntity infoEntity) {
//        port = infoEntity.port;
//        userName = infoEntity.userName;
//        passWord = infoEntity.password;
//        servicePath = infoEntity.serverPath;
//        filePath = infoEntity.path;
//        fileName = infoEntity.fileName;
//        fileSize = infoEntity.len;
//    }
//
//    private void initFtpClient(){
//        ftpClient = new FTPClient();
//        try {
//            ftpClient.connect(host,port);
//            ftpClient.login(userName,passWord);
//            ftpClient.setType(FTPClient.TYPE_BINARY);//设置传输的类型
//            ftpClient.setCharset("GBK");
//            ftpClient.setPassive(true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (FTPIllegalReplyException e) {
//            e.printStackTrace();
//        } catch (FTPException e) {
//            e.printStackTrace();
//        }
//        ftpClient.setPassive(true);
//    }
//
//
//
//    class UploadThread extends Thread {
//
//        @Override
//        public void run() {
//            try {
//                ftpClient.upload(packageFile, new MyTransferListener());
//            } catch (FileNotFoundException e) {
//                Log.e(TAG, "UploadThread FileNotFoundException");
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Thread.currentThread().interrupt();
//            }
//
//        }
//    }
//
//
//    public class MyTransferListener implements FTPDataTransferListener {
//
//        long percent = 0L;
//
//        //传输中止时触发
//        public void aborted() {
//            Log.e("MyFTPClient", "file aborted");
//            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
//            fromUploadEventBean.setEventType(2);
//            fromUploadEventBean.setProgress(0);
//            fromUploadEventBean.setType(5);
//            EventBus.getDefault().post(fromUploadEventBean);
//        }
//
//        //文件传输完成时，触发
//        public void completed() {
//            Log.e("MyFTPClient", "file completed");
//            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
//            fromUploadEventBean.setEventType(2);
//            fromUploadEventBean.setProgress(0);
//            fromUploadEventBean.setType(3);
//            EventBus.getDefault().post(fromUploadEventBean);
//
//        }
//
//        //传输失败时触发
//        public void failed() {
//            Log.e("MyFTPClient", "file failed");
//            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
//            fromUploadEventBean.setEventType(2);
//            fromUploadEventBean.setProgress(0);
//            fromUploadEventBean.setType(4);
//            EventBus.getDefault().post(fromUploadEventBean);
//
//        }
//
//        //文件开始上传或下载时触发
//        public void started() {
//            Log.e("MyFTPClient", "file start");
//            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
//            fromUploadEventBean.setEventType(2);
//            fromUploadEventBean.setProgress(0);
//            fromUploadEventBean.setType(1);
//            EventBus.getDefault().post(fromUploadEventBean);
//        }
//
//        //显示已经传输的字节数
//        public void transferred(int arg0) {
//            uploadSize += arg0;
//            percent = (long) (uploadSize * 100 / (fileSize * 1.0));
//            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
//            fromUploadEventBean.setEventType(2);
//            fromUploadEventBean.setProgress((int) percent);
//            double mSize = (double) uploadSize / (1024 * 1024);
////            fromUploadEventBean.setLen(df.format(mSize));
//            fromUploadEventBean.setType(2);
//            EventBus.getDefault().post(fromUploadEventBean);
//            Log.e("MyFTPClient", "percent:" + percent + "%  UploadSize:" + uploadSize + "byte");
//        }
//    }
//
//
//}

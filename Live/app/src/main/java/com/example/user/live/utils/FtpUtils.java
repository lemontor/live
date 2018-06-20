package com.example.user.live.utils;


import android.util.Log;

import com.example.user.live.video.entity.FromUploadBean;
import com.example.user.live.video.entity.FromUploadEventBean;
import com.example.user.live.video.entity.UpLoadingBean;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

/**
 * Created by user on 2018/6/4.
 */
public class FtpUtils {

    private FTPClient ftpClient;
    private boolean stop = false;
    private long percent;
    DecimalFormat df = new DecimalFormat("#.0");

    public boolean upLoad(String localFilePath, String fileName, int poi, boolean isClose) {
//        for (int i = 0;i < 30;i++){

            FromUploadBean fromUploadBean = new FromUploadBean();//等待中
            fromUploadBean.setPoi(poi);
            fromUploadBean.setType(1);
            EventBus.getDefault().post(fromUploadBean);
            ftpClient = new FTPClient();
            OutputStream output = null;
            RandomAccessFile raf = null;
            try {
                ftpClient.connect(ConstantUtils.HOST, 21);
                ftpClient.login(ConstantUtils.USENAME, ConstantUtils.PASSWORD);
//                ftpClient.enterLocalActiveMode();
                ftpClient.setControlEncoding("iso-8859-1");
                if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                    ftpClient.disconnect();
                    FromUploadBean connedBean = new FromUploadBean();//连接失败
                    fromUploadBean.setPoi(poi);
                    fromUploadBean.setType(3);
                    EventBus.getDefault().post(connedBean);
                    return false;
                }
                File localFile = new File(localFilePath);
                if (!localFile.exists()) {
                    FromUploadBean connedBean = new FromUploadBean();//本地文件不存在
                    fromUploadBean.setPoi(poi);
                    fromUploadBean.setType(4);
                    EventBus.getDefault().post(connedBean);
                    return false;
                }
                long localSize = localFile.length();
                ftpClient.changeWorkingDirectory(ConstantUtils.SERVERPATH);//切换目录
//            String remotePath =  servicePath+fileName;
//            FTPFile[] files = ftpClient.listFiles(remotePath);
                FTPFile[] files = ftpClient.listFiles();
                int index = 0;
                String  md5 = Md5Util.getMd5(fileName);
                Log.e("tag_md5",md5);
                String file = md5 + ".mp4";
                for (index = 0; index < files.length; index++) {
                    Log.e("tag_file_name", files[index].getName());
                    if (files[index].getName().equals(file)) {
                        break;
                    }
                }
                long serviceSize = 0;
                if (index == files.length) {
                    serviceSize = 0;
                } else {
                    serviceSize = files[index].getSize();// 服务器文件的长度
                    if (localSize == serviceSize) {
                        Log.e("tag_event", "已经上传过了");
                        FromUploadBean finishBean = new FromUploadBean();
                        finishBean.setType(8);
                        finishBean.setPoi(poi);
                        EventBus.getDefault().post(finishBean);
                        return true;
                    }
                }
                raf = new RandomAccessFile(localFile, "r");
                // 进度
                long step = localSize / 100;
                long process = 0;
                long currentSize = serviceSize;
//            ftpClient.setDefaultTimeout(60 * 1000);
//            ftpClient.setConnectTimeout(60 * 1000);
//            ftpClient.setDataTimeout(60 * 1000);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.setRestartOffset(serviceSize);
                raf.seek(serviceSize);
                output = ftpClient.appendFileStream(file);
                byte[] b = new byte[1024];
                int length = 0;
                FromUploadBean startBean = new FromUploadBean();//开始上传
                fromUploadBean.setPoi(poi);
                fromUploadBean.setType(2);
                EventBus.getDefault().post(startBean);
                while (!stop && (length = raf.read(b)) != -1) {
                    output.write(b, 0, length);
                    currentSize = currentSize + length;
                    Log.e("tag_currentSize", currentSize + "");
                    percent = (long) (currentSize * 100 / (localSize * 1.0));
//                FromUploadBean uPingBean = new FromUploadBean();
//                uPingBean.setProgress((int) percent);
//                double mSize = (double) currentSize / (1024 * 1024);
//                uPingBean.setLen(df.format(mSize));
//                uPingBean.setType(6);
//                uPingBean.setPoi(poi);
//                EventBus.getDefault().post(uPingBean);
                    UpLoadingBean uPingBean = new UpLoadingBean();
                    uPingBean.setProgress((int) percent);
                    double mSize = (double) currentSize / (1024 * 1024);
                    uPingBean.setLen(df.format(mSize));
                    uPingBean.setPoi(poi);
                    EventBus.getDefault().post(uPingBean);
                    if (currentSize == localSize) {//上传完成
                        FromUploadBean finishBean = new FromUploadBean();
                        finishBean.setType(8);
                        finishBean.setPoi(poi);
                        EventBus.getDefault().post(finishBean);
//                        i = 30;
                    }
                }
                if (stop) {
//                    i = 30;
                    FromUploadBean finishBean = new FromUploadBean();
                    finishBean.setType(5);
                    finishBean.setPoi(poi);
                    EventBus.getDefault().post(finishBean);
                }
                stop = false;
                output.flush();
                output.close();
                raf.close();
//            if (ftpClient.completePendingCommand()) {
//                Log.e("tag_上传进度：","文件上传成功");
//                return true;
//            } else {
//                return false;
//            }
                if (!ftpClient.completePendingCommand()) {
                    FromUploadBean failBean = new FromUploadBean();
                    failBean.setType(7);
                    failBean.setPoi(poi);
                    EventBus.getDefault().post(failBean);
                    Log.e("tag_上传进度：", "文件上传失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
                stop = false;
                try {
                    if(output != null){
                        output.flush();
                        output.close();
                    }
                    if(raf != null){
                        raf.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                FromUploadBean failBean = new FromUploadBean();
                failBean.setType(3);
                failBean.setPoi(poi);
                EventBus.getDefault().post(failBean);
                Log.e("tag_", "ftp连接失败:" + e.getMessage());
            } finally {
                if (isClose) {
                    Log.e("tag_finally", "需要关闭消息");
                    if (ftpClient != null) {
                        try {
                            ftpClient.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e("tag_finally", "不需要关闭消息");
                }
            }
//        }
        return false;
    }

    public void stop() {
        stop = true;
    }

}

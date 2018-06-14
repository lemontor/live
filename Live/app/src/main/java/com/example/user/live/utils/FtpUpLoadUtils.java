package com.example.user.live.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.user.live.video.entity.FromActivityEventBean;
import com.example.user.live.video.entity.FromUploadEventBean;
import com.example.user.live.video.entity.FtpUploadInfoEntity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

/**
 * Created by user on 2018/5/18.
 * ftp_ip: 49.4.15.103
 * ftp_port: 21
 * ftp_user: study1
 * ftp_pwd: study1
 * ftp_base_path: /mnt/sdc/storage/video/dev/andriod
 */
public class FtpUpLoadUtils {

    private UploadThread thread;    //上传线程
    private Thread continueThread;    //断点上传线程
    private File packageFile;    //需上传的文件
    private long fileSize = 0L;    //需上传文件的大小
    private long uploadSize = 0L;    //已上传的文件的大小
    private Context context;    //上下文根
    private String ip;    //需要连接的FTP服务器ip地址
    private int port;    //需要连接的FTP服务器端口号
    private String userName;    //登录FTP服务器的用户名
    private String password;    //登录FTP服务器密码
    private String path;        //需上传的文件的完整路径
    private String serverPath;      //服务端存储路径（根路径为/）
    private long len;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private BroadcastReceiver connctionChangeReceiver;
    private String fileName;

    private static final String BREAKPOINT_INFO = "breakpointinfo";  //SharedPreferences里存储断点信息的对象名
    private static final String TEXT_CHARSET = "utf8"; //字符编码
    private static final String UPLOAD_COMPLETED_ACTION = "com.uyunke.inspection.intent.broadcast.UPLOAD_COMPLETED_ACTION";
    //网络状态改变ACTION
    private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String TAG = "FTPUpload";
    private FtpUploadInfoEntity entity;


    public String uploadFile(Context context, FtpUploadInfoEntity entity) {
        this.context = context;
        ip = entity.ip;
        port = entity.port;
        userName = entity.userName;
        password = entity.password;
        path = entity.path;
        serverPath = entity.serverPath;
        fileName = entity.fileName;
        len = entity.len;
        Log.e(TAG, "ip:" + ip + "," + "port:" + port + "," + "usrName:" + userName + "," + "password:" + password + "serverPath:" + serverPath + "," + "path:" + path);
        if (path == null || "".equals(path.trim()) || new File(path).isDirectory() || !new File(path).exists()) {
            Log.e(TAG, "File is not exsist");
            return "4";
        }
        packageFile = new File(path);
        fileSize = packageFile.length();
        //检索并获得名字为BREAKPOINT_INFO的SharedPreferences对象
        preferences = context.getSharedPreferences(BREAKPOINT_INFO, context.MODE_PRIVATE);
        editor = preferences.edit();
        df = new DecimalFormat("#.0");
        new Thread() {
            @Override
            public void run() {
                super.run();
//        ftpUpLoadUtils.uploadFile(UpLoadService.this, infoEntity);
                initConnection(ip, port, userName, password);

            }
        }.start();
//        initConnection(ip, port, userName, password);
        return null;
    }

    DecimalFormat df;
    FTPClient ftp;
    ConnectionChangeReceiver connectionChangeReceiver;
    private void initConnection(String ip, int port, String userName, String password) {
        try {
            ftp = new FTPClient();
            ftp.connect(ip, port);
            ftp.login(userName, password);
            ftp.setType(FTPClient.TYPE_BINARY);//设置传输的类型
            ftp.setCharset(TEXT_CHARSET);
            ftp.setPassive(true);
            boolean isCompression = ftp.isCompressionSupported();
            if (isCompression) {
                ftp.setCompressionEnabled(true);
            }
            //更改服务端当前目录
//            String dir = ftp.currentDirectory();
            ftp.changeDirectory(serverPath);
            String realName = fileName + ".mp4";
//            getListFiles(ftp,"");
            boolean isAdd = false;
            long size = 0;
            try {
                size = ftp.fileSize(realName);
                if (fileSize <= size) {//已经上传过了
                    Log.e("tag_", "已经上传过了...");
                    FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
                    fromUploadEventBean.setEventType(2);
                    fromUploadEventBean.setProgress(0);
                    fromUploadEventBean.setType(3);
                    EventBus.getDefault().post(fromUploadEventBean);
                    return;
                } else {
                    isAdd = true;
                    Log.e("tag_up_size",size+":"+fileSize);
                }
            } catch (Exception e) {
                isAdd = false;
            }
            IntentFilter filter = new IntentFilter(CONNECTIVITY_CHANGE_ACTION);
            asynUpload(isAdd, size);
            connectionChangeReceiver = new ConnectionChangeReceiver();
            context.registerReceiver(connectionChangeReceiver, filter);
        } catch (Exception e) {
//            Log.e("MyFTPClient", e.getMessage());
          Log.e("tag_ftp","ftp 失败");
        }
    }

    /**
     * TODO 上传文件
     */
    public void asynUpload(boolean isAdd, long upSize) {
//        boolean b = isFirstUpload(getFileName(path));
        if (!isAdd) {
            Log.e(TAG, "is first upload");
            thread = new UploadThread();
            thread.start();
        } else {
            Log.e(TAG, "is not first upload");
            HashMap<String, Object> infos = getBreakpointInfo();
            String info_path = (String) infos.get("info_path");
            String info_serverpath = (String) infos.get("info_serverpath");
            Long info_uploadsize = (Long) infos.get("info_uploadsize");
            uploadSize = info_uploadsize;
            continueThread = new ContinueUploadThread(path, serverPath, upSize);
//            continueThread = new ContinueUploadThread(info_path, info_serverpath, info_uploadsize);
            continueThread.start();
        }
// 	此处若传入true，则ftp客户端和服务端交互后再终止传输，则获得的已传输数据量有偏差，客户端记录数量偏大。
// 	应传入false,则客户端与服务端不交互直接终止传输，客户端和服务端记录的已传输数据相同。
// 	ftp.abortCurrentDataTransfer(false);
    }

    /**
     * TODO
     *
     * @return 返回保存的断点信息数组
     * infos[0] 为上传文件的绝对路径
     * infos[1] 为服务端存储路径
     * infos[2] 为已上传的文件大小
     */
    private HashMap<String, Object> getBreakpointInfo() {
        String fileName = getFileName(path);
        String[] keys = getKeys(fileName);
        String info_path = preferences.getString(keys[0], null);
        String info_serverpath = preferences.getString(keys[1], null);
        Long info_uploadsize = preferences.getLong(keys[2], 0);
        HashMap<String, Object> infos = new HashMap<String, Object>();
        infos.put("info_path", info_path);
        infos.put("info_serverpath", info_serverpath);
        infos.put("info_uploadsize", info_uploadsize);
        return infos;
    }

    /**
     * TODO 判断是否是首次上传，如果SharedPreferences中不存在该信息，则为首次上传
     *
     * @param fileName
     * @return
     */
    private boolean isFirstUpload(String fileName) {
        String info = preferences.getString(fileName, null);
        if (info == null) {
            return true;
        }
        return false;
    }

    /**
     * TODO 文件上传线程
     * 2012-1-10 下午5:33:22
     *
     * @author zhuoran.xu
     */
    class UploadThread extends Thread {

        @Override
        public void run() {
            try {
                ftp.upload(packageFile, new MyTransferListener());
            } catch (FileNotFoundException e) {
                Log.e(TAG, "UploadThread FileNotFoundException");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

        }
    }

    /**
     * TODO 文件断点上传线程
     * 2012-1-10 下午5:33:43
     *
     * @author zhuoran.xu
     */
    class ContinueUploadThread extends Thread {
        String path = null;
        String currentDirectory = null;
        long uploadSize = 0L;

        public ContinueUploadThread() {

        }

        public ContinueUploadThread(String path, String currentDirectory, long uploadSize) {
            this.path = path;
            this.currentDirectory = currentDirectory;
            this.uploadSize = uploadSize;
            Log.e(TAG, "Continue Info: path" + path + "serverpath:" + currentDirectory + " uploadSize:" + uploadSize);
        }

        @Override
        public void run() {
            if (ftp.isResumeSupported()) {
                Log.e("MyFTPClient", "ftpServerSupport!");
                try {
//                    ftp.changeDirectory(currentDirectory);
                    ftp.upload(packageFile, uploadSize, new MyTransferListener());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    /**
     * TODO 数据上传监听器
     * 2012-1-10 下午5:34:01
     *
     * @author zhuoran.xu
     */
    public class MyTransferListener implements FTPDataTransferListener {

        long percent = 0L;

        //传输中止时触发
        public void aborted() {
            Log.e("MyFTPClient", "file aborted");
            Log.e("MyFTPClient", "MyFTPClientaborted:" + uploadSize);
            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
            fromUploadEventBean.setEventType(2);
            fromUploadEventBean.setProgress(0);
            fromUploadEventBean.setType(5);
//            EventBus.getDefault().post(fromUploadEventBean);
//            saveBreakpointInfo();
            disconnect();
//            context.unregisterReceiver(connctionChangeReceiver);
        }

        //文件传输完成时，触发
        public void completed() {
            Log.e("MyFTPClient", "file completed");
            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
            fromUploadEventBean.setEventType(2);
            fromUploadEventBean.setProgress(0);
            fromUploadEventBean.setType(3);
            EventBus.getDefault().post(fromUploadEventBean);
            //文件传输完成时，将已上传文件大小置空
            uploadSize = 0;
            String fileName = getFileName(path);
            String[] keys = getKeys(fileName);
            //文件传输完成时，将本地化信息清空。
            editor.remove(fileName);
            editor.remove(keys[0]);
            editor.remove(keys[1]);
            editor.remove(keys[2]);
            editor.commit();
            disconnect();
            context.unregisterReceiver(connctionChangeReceiver);
            Intent intent = new Intent();
            intent.setAction(UPLOAD_COMPLETED_ACTION);
            intent.putExtra("path", path);
            context.sendBroadcast(intent);
        }

        //传输失败时触发
        public void failed() {
            Log.e("MyFTPClient", "file failed");
            Log.e("MyFTPClient", "MyFTPClientFailed:" + uploadSize);
            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
            fromUploadEventBean.setEventType(2);
            fromUploadEventBean.setProgress(0);
            fromUploadEventBean.setType(4);
            EventBus.getDefault().post(fromUploadEventBean);
            saveBreakpointInfo();
            disconnect();
            context.unregisterReceiver(connctionChangeReceiver);
        }

        //文件开始上传或下载时触发
        public void started() {
            Log.e("MyFTPClient", "file start");
            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
            fromUploadEventBean.setEventType(2);
            fromUploadEventBean.setProgress(0);
            fromUploadEventBean.setType(1);
            EventBus.getDefault().post(fromUploadEventBean);
        }

        //显示已经传输的字节数
        public void transferred(int arg0) {
            Log.e("tag——————", "transferred");
            uploadSize += arg0;
            percent = (long) (uploadSize * 100 / (fileSize * 1.0));
            FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
            fromUploadEventBean.setEventType(2);
            fromUploadEventBean.setProgress((int) percent);
            double mSize = (double) uploadSize / (1024 * 1024);
            fromUploadEventBean.setLen(df.format(mSize));
            fromUploadEventBean.setType(2);
            EventBus.getDefault().post(fromUploadEventBean);
            Log.e("MyFTPClient", "percent:" + percent + "%  UploadSize:" + uploadSize + "byte");
        }
    }

    public List<FTPFile> getListFiles(FTPClient client, String path) {
        List<FTPFile> filesList = new ArrayList<FTPFile>();
        try {
//            client.changeDirectory(path);
            FTPFile[] fileNames = client.list(); //.listNames();
            if (null != fileNames) {
                for (FTPFile file : fileNames) {
                    if (file.getType() == FTPFile.TYPE_FILE) {
                        filesList.add(file);
                        Log.e("tag_list", file.getName() + ":" + file.getSize());
                        //System.out.println(file.getName()+" | "+file.getSize()+" | "+DateUtil.converDateToString(file.getModifiedDate(),DateUtil.defaultDatePattern));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("###[Error] FTPToolkit.getlistFiles()" + e.getMessage());
        }
        return filesList;
    }


    /**
     * TODO 终止数据的上传，在上传线程中调用无效，需在其他线程中调用。
     */
    public void abortDataTransfer() {
        try {
            ftp.abortCurrentDataTransfer(false);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * TODO 断开和服务端的连接
     */
    private void disconnect() {
        if (ftp != null) {
            try {
                context.unregisterReceiver(connectionChangeReceiver);
                ftp.disconnect(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    续传
     */
    public void reUpload(Context context,FtpUploadInfoEntity ftpUploadInfoEntity) {
        uploadFile(context,ftpUploadInfoEntity);
    }


    /**
     * TODO 根据路径获取文件名
     *
     * @param path
     * @return
     */
    private String getFileName(String path) {
        String fileName = null;
        path = path.trim();
        fileName = path.substring(path.lastIndexOf("/") + 1);
        return fileName;
    }

    /**
     * TODO 往SharedPreferences中存储断点信息，以要上传的文件名（UUID）为Key
     */
    private void saveBreakpointInfo() {
        String fileName = getFileName(path);
        Log.e(TAG, "fileName:" + fileName);
        Log.e(TAG, "saveBreakpointInfo");
        String[] keys = getKeys(fileName);
        //拼接要存入的断点信息
        try {
            //将断点信息存入
            editor.putString(fileName, fileName);
            editor.putString(keys[0], path);
            editor.putString(keys[1], serverPath);
            editor.putLong(keys[2], uploadSize);
            boolean b = editor.commit();
            Log.e(TAG, "Is save successfully?" + b);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 构造存入sharedPreferences里的key值
     *
     * @param fileName
     * @return
     */
    private String[] getKeys(String fileName) {
        String keyPath = fileName + "_Path";//sharedPreferences里保存文件路径的key
        String keyServerPath = fileName + "_ServerPath";//sharedPreferences里保存服务端存储路径的key
        String keyUploadSize = fileName + "_UploadSize";//sharedPreferences里保存已上传文件大小的key
        String[] keys = {keyPath, keyServerPath, keyUploadSize};
        return keys;
    }


    /**
     * TODO 网络状态广播接收者
     * 2012-1-11 下午5:54:06
     *
     * @author zhuoran.xu
     */
    class ConnectionChangeReceiver extends BroadcastReceiver {
        private static final String TAG = "ConnectionChangeReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (activeNetInfo != null && activeNetInfo.isConnected()) {
                Toast.makeText(context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
                switch (activeNetInfo.getType()) {
                    case ConnectivityManager.TYPE_MOBILE:
                        FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
                        fromUploadEventBean.setEventType(2);
                        fromUploadEventBean.setProgress(0);
                        fromUploadEventBean.setType(11);
                        EventBus.getDefault().post(fromUploadEventBean);
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        FromUploadEventBean fromUploadEventBean1 = new FromUploadEventBean();
                        fromUploadEventBean1.setEventType(2);
                        fromUploadEventBean1.setProgress(0);
                        fromUploadEventBean1.setType(12);
                        EventBus.getDefault().post(fromUploadEventBean1);
                        break;
                    default:
                        break;
                }
            } else {
                FromUploadEventBean fromUploadEventBean = new FromUploadEventBean();
                fromUploadEventBean.setEventType(2);
                fromUploadEventBean.setProgress(0);
                fromUploadEventBean.setType(10);
                EventBus.getDefault().post(fromUploadEventBean);
                try {
                    ftp.abortCurrentDataTransfer(false);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}


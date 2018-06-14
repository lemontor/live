package com.example.user.live.video;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.user.live.R;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by user on 2018/6/11.
 */
public class Test  extends Activity{

    private String host = "49.4.15.103";
    private String userName= "study1";
    private String passWord = "study1";
    private String fileName = "V80425-171727.mp4";
    private String servicePath = "/mnt/sdc/storage/video/dev/andriod/";
    private String localFilePath = "/storage/emulated/0/DCIM/Video/V80425-171727.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void up(View view){
        new Thread() {
            @Override
            public void run() {
                super.run();
                upLoad();
            }
        }.start();
    }

    public void stop(View view){
        stop = true;
    }
    private boolean  stop = false;
    FTPClient ftpClient;
    private boolean upLoad() {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(host, 21);
            ftpClient.login(userName, passWord);
            ftpClient.setControlEncoding("iso-8859-1");
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
                ftpClient.disconnect();
                Log.e("tag_", "ftp连接失败");
                return false;
            }
            File localFile = new File(localFilePath);
            if(!localFile.exists()){
                Log.e("tag_","本地文件不存在");
                return false;
            }
            long localSize = localFile.length();
//            ftpClient.changeWorkingDirectory(servicePath);//切换目录
//            String remotePath =  servicePath+fileName;
//            FTPFile[] files = ftpClient.listFiles(remotePath);
            FTPFile[] files = ftpClient.listFiles();
            int index = 0;
            for(index = 0;index < files.length;index++){
                Log.e("tag_file_name",files[index].getName());
                if(files[index].getName().equals(fileName)){
                    break;
                }
            }
            long serviceSize = 0;
            if(index == files.length){
                serviceSize = 0;
            }else{
                serviceSize = files[index].getSize();// 服务器文件的长度
            }
//            if(files != null && files.length > 0){
//                FTPFile file = files[0];  //文件信息
//                serviceSize = file.getSize();// 服务器文件的长度
//            }else{
//                serviceSize = 0;
//            }
            RandomAccessFile raf = new RandomAccessFile(localFile, "r");
            // 进度
            long step = localSize / 100;
            long process = 0;
            long currentSize = serviceSize;
            // 好了，正式开始上传文件
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setRestartOffset(serviceSize);
            raf.seek(serviceSize);
            OutputStream output = ftpClient.appendFileStream(fileName);
            byte[] b = new byte[1024];
            int length = 0;
            while (!stop && (length = raf.read(b)) != -1) {
                output.write(b, 0, length);
                currentSize = currentSize + length;
                Log.e("tag_currentSize",currentSize+"");
                if (currentSize / step != process) {
                    process = currentSize / step;
                    if (process % 10 == 0) {
                        Log.e("tag_上传进度：",process+"");
                    }
                }
            }
            stop = false;
            output.flush();
            output.close();
            raf.close();
            if (ftpClient.completePendingCommand()) {
                Log.e("tag_上传进度：","文件上传成功");
                return true;
            } else {
                Log.e("tag_上传进度：","文件上传失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("tag_", "ftp连接失败");
        }
        return false;
    }


}

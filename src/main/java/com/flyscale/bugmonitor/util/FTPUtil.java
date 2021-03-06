package com.flyscale.bugmonitor.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.flyscale.bugmonitor.global.Constants;
import com.flyscale.bugmonitor.net.ThreadPool;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bian on 2018/8/30.
 */

public class FTPUtil {
    private static final String TAG = "FTPUtil";

    public static void upLoadFile(String filePath) {
        try {
            FileInputStream in = new FileInputStream(new File(filePath));
            boolean flag = uploadFile("127.0.0.1", 21, "test", "test", "D:/ftp", "test.txt", in, null, false, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void upLoadFileToServer(Context context, final String absPath, final String fileName, final FTPClientListener listener) {
        final String hostname = PreferenceUtil.getString(context, Constants.FTP_HOSTNAME, null);
        String portStr = PreferenceUtil.getString(context, Constants.FTP_PORT, null);
        final String username = PreferenceUtil.getString(context, Constants.FTP_USERNAME, null);
        final String password = PreferenceUtil.getString(context, Constants.FTP_PASSWD, null);
        final String remotePath = PreferenceUtil.getString(context, Constants.FTP_DOWNLOAD_FILE_REMOTEPATH, null);
        Log.d(TAG, "upLoadFileToServer::hostname=" + hostname + ",port=" + portStr + ",username=" + username + ",passwd=" + password
                + ",remotePath=" + remotePath + ",absPath=" + absPath);
        if (TextUtils.isEmpty(hostname) || TextUtils.isEmpty(portStr) || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(remotePath) || TextUtils.isEmpty(absPath)) {
            return;
        }
        final int port = Integer.parseInt(portStr);
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                FileInputStream fis = null;
                File file = new File(absPath);
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                uploadFile(hostname, port, username, password, remotePath, fileName, fis, absPath, true, listener);
            }
        });
    }

    public static void downLoadFileFromDefServer(Context context) {
        String hostname = PreferenceUtil.getString(context, Constants.FTP_HOSTNAME, null);
        String portStr = PreferenceUtil.getString(context, Constants.FTP_PORT, null);
        String username = PreferenceUtil.getString(context, Constants.FTP_USERNAME, null);
        String password = PreferenceUtil.getString(context, Constants.FTP_PASSWD, null);
        String remotePath = PreferenceUtil.getString(context, Constants.FTP_DOWNLOAD_FILE_REMOTEPATH, null);
        String localPath = PreferenceUtil.getString(context, Constants.FTP_DOWNLOAD_FILE_LOCALPATH, null);
        String filename = PreferenceUtil.getString(context, Constants.FTP_DOWNLOAD_FILE_NAME, null);
        Log.d(TAG, "downLoadFileFromDefServer::hostname=" + hostname + ",port=" + portStr + ",username=" + username + ",passwd=" + password
                + ",remotePath=" + remotePath + ",localPathi=" + localPath + ",fileName=" + filename);
        if (TextUtils.isEmpty(hostname) || TextUtils.isEmpty(portStr) || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(remotePath) || TextUtils.isEmpty(localPath) ||
                TextUtils.isEmpty(filename)) {
            return;
        }
        int port = Integer.parseInt(portStr);
        downLoadFile(hostname, port, username, password, remotePath, filename, localPath);
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param hostname   FTP服务器hostname
     * @param port       FTP服务器端口
     * @param username   FTP登录账号
     * @param password   FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @param localPath  下载后保存到本地的路径
     * @return
     */
    public static boolean downLoadFile(String hostname, int port, String username, String password, String remotePath, String fileName, String localPath) {
        Log.d(TAG, "hostname=" + hostname + ",port=" + port + ",username=" + username + ",passwd=" + password
                + ",remotePath=" + remotePath + ",localPathi=" + localPath + ",fileName=" + fileName);
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(hostname, port);
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);//登录
            reply = ftp.getReplyCode();
            Log.d(TAG, "replyCode = " + reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                Log.d(TAG, "negtive, disconnect");
                return success;
            }
            ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                Log.d(TAG, "file=" + ff.getName());
                if (ff.getName().equals(fileName)) {
                    File localpath = new File(localPath);
                    if (!localpath.exists()) {
                        boolean mkdirs = localpath.mkdirs();
                        Log.d(TAG, "mkdirs=" + mkdirs);
                        if (!mkdirs) {
                            return false;
                        }
                    }
                    File localFile = new File(localPath + File.separator + ff.getName());

                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }
            ftp.logout();
            success = true;
            Log.d(TAG, "download complelte!!!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url       FTP服务器hostname
     * @param port      FTP服务器端口
     * @param username  FTP登录账号
     * @param password  FTP登录密码
     * @param path      FTP服务器保存目录
     * @param filename  上传到FTP服务器上的文件名
     * @param input     输入流
     * @param localFile 本地上传的文件
     * @param delete    上传结束后是否删除,不管成功或者失败
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String url, int port, String username, String password,
                                     String path, String filename, InputStream input, String localFile, boolean delete,
                                     FTPClientListener listener) {
        Log.d(TAG, "uploadFile");
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            Log.d(TAG, "start connect to ftp server ...");
            ftp.connect(url, port);//连接FTP服务器
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            Log.d(TAG, "connect ftp server sucdess!!");
            Log.d(TAG, "start login!!");
            ftp.login(username, password);//登录
            Log.d(TAG, "login success!!");
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                Log.e(TAG, "ftp disconnect !!!");
                if (listener != null){
                    listener.onFailed(localFile, null, "FTP Server getReplyCode error!");
                }
                if (delete) {
                    delete(localFile);
                }
                return success;
            }
            //被动模式,要在设置setFileType之前
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.changeWorkingDirectory(path);
            Log.d(TAG, "start to upload...");
            ftp.storeFile(filename, input);
            input.close();
            ftp.logout();
            success = true;
            Log.d(TAG, "upload success!!!");
            if (listener != null){
                listener.onSuccess(localFile, filename);
            }
            if (delete) {
                delete(localFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null){
                listener.onFailed(localFile, e, "FTP Server getReplyCode error!");
            }
        } finally {
            if (delete) {
                delete(localFile);
            }
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return success;
    }

    private static void delete(String localFile) {
        Log.d(TAG, "delete,localFile=" + localFile);
        File file = new File(localFile);
        if (file.exists()) {
            file.delete();
        }
    }

    public interface FTPClientListener {
        void onSuccess(String localFile, String remoteFile);

        void onFailed(String localFile, Exception e, String msg);
    }

}

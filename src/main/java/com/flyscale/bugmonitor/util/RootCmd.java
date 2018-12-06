package com.flyscale.bugmonitor.util;

/**
 * Created by bian on 2018/7/21.
 */

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Android运行linux命令
 */
public final class RootCmd {
    private static final String TAG = "RootCmd";
    private static boolean mHaveRoot = false;
    /**
     *   判断机器Android是否已经root，即是否获取root权限
     */
    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                Log.i(TAG, "have root!");
                mHaveRoot = true;
            } else {
                Log.i(TAG, "not root!");
            }
        } else {
            Log.i(TAG, "mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

    /**
     * 执行命令并且输出结果
     */
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 执行命令但不关注结果输出
     */
    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("sh");//如果 没有root权限就要使用sh，有root则su
            dos = new DataOutputStream(p.getOutputStream());

            Log.i(TAG, cmd);
            dos.writeBytes("su" + "\n");
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
            Log.d(TAG, "result=" + result);
            InputStream errorStream = p.getErrorStream();
            DataInputStream dataInputStream = new DataInputStream(errorStream);
            byte[] buf = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while (dataInputStream.read(buf, 0, 1024) != 0){
//                sb.append(String.valueOf(buf));
                Log.d(TAG, "errormsg=" + new String(buf));
            }
            Log.d(TAG, "errormsg=" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     *
     * @param parentPath 打包文件的父目录
     * @param target    要被打包的文件
     * @return
     */
    public static int tar(String parentPath, String target) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("sh");//如果 没有root权限就要使用sh，有root则su
            dos = new DataOutputStream(p.getOutputStream());

            Log.i(TAG, "parentPath=" + parentPath + ",target=" + target);
//            dos.writeBytes("su" + "\n");
            dos.writeBytes("cd " + parentPath + "\n");
            dos.writeBytes("tar -zcvf " + target + ".tar.gz " + target + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
            Log.d(TAG, "result=" + result);
            InputStream errorStream = p.getErrorStream();
            DataInputStream dataInputStream = new DataInputStream(errorStream);
            byte[] buf = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while (dataInputStream.read(buf, 0, 1024) != 0){
//                sb.append(String.valueOf(buf));
                Log.d(TAG, "errormsg=" + new String(buf));
            }
            Log.d(TAG, "errormsg=" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
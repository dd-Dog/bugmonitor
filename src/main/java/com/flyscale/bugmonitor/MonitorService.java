package com.flyscale.bugmonitor;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flyscale.bugmonitor.global.Constants;
import com.flyscale.bugmonitor.util.DateFormatUtil;
import com.flyscale.bugmonitor.util.FTPUtil;
import com.flyscale.bugmonitor.util.SDCardUtil;
import com.flyscale.bugmonitor.util.ZipCompressor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by bian on 2018/12/6.
 */

public class MonitorService extends IntentService {
    private TelephonyManager mTm;

    public MonitorService() {
        super("MonitorService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MonitorService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //在子线程中执行
        Log.d(TAG, "onHandleIntent, thread=" + Thread.currentThread().getName());
        comporessAndUpload();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return super.onStartCommand(intent, flags, startId);
    }

    private void comporessAndUpload() {
        File filesDir = getFilesDir();
        String[] outFiles = new String[Constants.SRC_FILES.length];
        String outFilePath = filesDir.getAbsolutePath() + File.separator + "log";
        File fileDir = new File(outFilePath);
        if (fileDir.exists()) {
            boolean delete = SDCardUtil.delAllFile(outFilePath);
            Log.d(TAG, "delete " + fileDir.getAbsolutePath() + (delete ? " success" : " failed"));
            boolean mkdirs = fileDir.mkdirs();
            Log.d(TAG, "mkdirs " + fileDir.getAbsolutePath() + (mkdirs ? " success" : " failed"));
        } else {
            boolean mkdirs = fileDir.mkdirs();
            Log.d(TAG, "mkdirs " + fileDir.getAbsolutePath() + (mkdirs ? " success" : " failed"));
        }
        //第一次分别压缩
        for (int i = 0; i < Constants.SRC_FILES.length; i++) {
            String outFile = outFilePath + File.separator + Constants.SRC_FILES[i] + ".zip";
            File file = new File(outFile);
            if (file.exists()) {
                boolean delete = file.delete();
                Log.d(TAG, "delete " + file.getAbsolutePath() + (delete ? " success" : "failed"));
            }
            Log.d(TAG, "outFile=" + outFile);
            ZipCompressor zipCompressor = new ZipCompressor(outFile);
            zipCompressor.compress(Constants.SRC_BASE + Constants.SRC_FILES[i]);
            outFiles[i] = outFile;
        }

        ArrayList<String> destZips = getFullName();
        Log.d(TAG, "destZip=" + destZips);
        File destFile = new File(destZips.get(0));
        if (destFile.exists()) {
            boolean delete = destFile.delete();
            Log.d(TAG, "delete " + destFile.getAbsolutePath() + (delete ? " success" : "failed"));
        }

        //再次压缩，要防止新生成文件与被压缩文件位于同一路径，会造成循环压缩，充满存储
        ZipCompressor zipCompressor = new ZipCompressor(filesDir.getAbsolutePath() + File.separator + destZips.get(1));
        zipCompressor.compress(outFilePath);

        //压缩后删除子文件
        for (int k = 0; k < outFiles.length; k++) {
            File file = new File(outFiles[k]);
            boolean delete = file.delete();
            Log.d(TAG, "delete zip file " + file.getAbsolutePath() + (delete ? " success" : "failed"));
        }
        FTPUtil.upLoadFileToServer(this, destZips.get(0), destZips.get(1));
        //上传完成后删除压缩文件

    }

    private ArrayList<String> getFullName() {
        ArrayList<String> strings = new ArrayList<String>();
        String time1 = DateFormatUtil.getTime3();
        String product = Build.PRODUCT;
        File filesDir = getFilesDir();
        String name = filesDir.getAbsolutePath() + File.separator +
                product + "-" + "s_modem" + "-" + time1 + ".zip";
        Log.d(TAG, "getFullName=" + name);
        strings.add(name);
        strings.add(product + "-" + "s_modem" + "-" + time1 + ".zip");
        return strings;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.flyscale.bugmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.bugmonitor.global.Constants;
import com.flyscale.bugmonitor.util.ServiceUtil;

/**
 * Created by bian on 2018/12/6.
 */

public class MonitorReceiver extends BroadcastReceiver {
    private static final String TAG = "MonitorReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String dataString = intent.getDataString();
        Log.d(TAG, "action=" + action + ",dataString=" + dataString);
        if (TextUtils.equals(action, "android.provider.Telephony.SECRET_CODE")) {
            boolean serviceRunning = ServiceUtil.isServiceRunning(context, UploadService.class.getName());
            Log.d(TAG, "serviceRunning=" + serviceRunning);
            if (!serviceRunning) {
                Intent service = new Intent(context, UploadService.class);
                context.startService(service);
            } else {
                context.sendBroadcast(new Intent(Constants.FTP_UPLOAD_SERVICE_RUNNING));
            }
        }
    }
}

package com.flyscale.bugmonitor;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApplicationInfo mAppInfo = getApplicationInfo();
        Log.d(TAG, "setReplace=" + (mAppInfo != null) + ",setSystemApp="
                + ((mAppInfo != null) && ((1 & ApplicationInfo.FLAG_SYSTEM) != 0)));

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                }
            }.start();
        }
        return super.onKeyUp(keyCode, event);
    }

}

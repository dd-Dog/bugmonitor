package com.flyscale.bugmonitor.net;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bian on 2018/12/6.
 */

public class ThreadPool {

    public static ExecutorService getInstance() {
        return SingletonHolder.singleThreadExecutor;
    }

    private static class SingletonHolder {
        private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    }
}

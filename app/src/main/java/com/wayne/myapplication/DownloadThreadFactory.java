package com.wayne.myapplication;

import android.util.Log;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 11271
 */
public class DownloadThreadFactory implements ThreadFactory {

    //通过计数器，可以更好的管理线程
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "download-thread-" + atomicInteger.getAndIncrement());
        Log.d("Wayne",t.getName() + " has been created");
        return t;
    }
}
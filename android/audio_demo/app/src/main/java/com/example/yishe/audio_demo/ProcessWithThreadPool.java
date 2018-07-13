package com.example.yishe.audio_demo;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessWithThreadPool {
    private static final String TAG="ThreadPool";
    private static final int KEEP_ALIVE_TIME=10;
    private static final TimeUnit  TIME_UNIT=TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue;
    private ThreadPoolExecutor mThreadPool;

    public ProcessWithThreadPool(){
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maximumPoolSize = corePoolSize*2;
        workQueue = new LinkedBlockingDeque<>();
        mThreadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,KEEP_ALIVE_TIME,TIME_UNIT,workQueue);
    }

    public synchronized  void post(final byte[] frameData){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                processFrame(frameData);
            }
        });
    }

    private void processFrame(byte[] data){
        Log.i(TAG,"data.length="+data.length);
    }
}

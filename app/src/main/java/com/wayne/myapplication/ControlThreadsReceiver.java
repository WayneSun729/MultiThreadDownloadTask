package com.wayne.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ControlThreadsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int flag = intent.getIntExtra("singleOrAll", -1);
        int status = intent.getIntExtra("status", -1);
        Log.e("Wayne", "flag="+flag);
        Log.e("Wayne", "status="+status);
        if (flag == DataManager.FLAG_SINGLE_CONTROL){
            int id = intent.getIntExtra("id", -1);
            DownloadThread thread = DataManager.getDownloadThreads().get(id);
            if (thread.getRunning()){
                thread.setRunning(false);
            }else {
                thread.setRunning(true);
                DataManager.getThreadPoolExecutor().execute(thread);
            }
        }else if (flag == DataManager.FLAG_ALL_CONTROL){
            if (status == DataManager.STATUS_NEW){
                DataManager.setThreadsStatus(DataManager.STATUS_START);
            }else if (status == DataManager.STATUS_START){
                DataManager.setThreadsStatus(DataManager.STATUS_PAUSE);
                for (DownloadThread downloadThread : DataManager.getDownloadThreads()) {
                    downloadThread.setRunning(false);
                }
            }else if (status == DataManager.STATUS_PAUSE) {
                DataManager.setThreadsStatus(DataManager.STATUS_START);
                for (DownloadThread downloadThread : DataManager.getDownloadThreads()) {
                    downloadThread.setRunning(true);
                    DataManager.getThreadPoolExecutor().execute(downloadThread);
                }
            }else if (status == DataManager.STATUS_FINISH){
                Toast.makeText(MyApplication.getContext(), "已经下载完成了~请不要重复下载哦~",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
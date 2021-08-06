package com.wayne.myapplication;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Message;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author sunbowen
 */
public class DownloadThread implements Runnable{

    private static final int BUFF_SIZE = 2048;

    /**缓存的FIle*/
    private File file;
    /**开始位置*/
    private long startPosition;
    /**结束位置*/
    private long endPosition;
    /**当前位置*/
    private long curPosition;
    /**完成*/
    private boolean finished = false;
    /**整个文件已经下载多少*/
    private long downloadSize;
    private static final String TAG = "Wayne";
    /**每一个线程需要下载的大小 */
    private long blockSize;
    /**线程编号*/
    private int ThreadNo;
    //状态标志
    private boolean running;
    //本线程已下载的大小
    long sum = 0;
    //总共要下载的大小
    long total;

    private DownloadActivity.MyHandler handler ;

    private Editor editor = DataManager.getSp().edit();

    /**
     * 下载的构造函数
     * @param handler   UI更新
     */
    public DownloadThread(DownloadActivity.MyHandler handler, long blockSize, int ThreadNo, File targetFile) throws InterruptedException {
        this.handler = handler;
        this.blockSize = blockSize;
        this.ThreadNo = ThreadNo;
        this.file = targetFile;
        downloadSize = blockSize * ThreadNo;
        running = false;
    }

    @Override
    public void run() {
        running = true;
        SharedPreferences sp = DataManager.getSp();
        //初始化下载信息
        startPosition = sp.getLong(ThreadNo+"startPosition", downloadSize);
        endPosition = sp.getLong(ThreadNo+"endPosition", (ThreadNo+1) != DataManager.getThreadNum() ? ((ThreadNo+1)*blockSize-1) : (DataManager.getFileSize() - 1));
        curPosition = sp.getLong(ThreadNo+"curPosition", startPosition);
        if (ThreadNo != DataManager.getThreadNum()-1){
            total = blockSize;
        }else{
            total = DataManager.getFileSize() - startPosition;
        }
        sum = curPosition-startPosition+1;
        byte[] buf = new byte[BUFF_SIZE];
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .addHeader("RANGE", "bytes=" + downloadSize + "-" + DataManager.getFileSize())
                .url(DataManager.getURL())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(DataManager.getDownloadFail());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                RandomAccessFile rAccessFile = new RandomAccessFile(file, "rwd");//读写
                BufferedInputStream bis = new BufferedInputStream(Objects.requireNonNull(response.body()).byteStream(), BUFF_SIZE);
                try {
                    //设置从什么位置开始写入数据
                    rAccessFile.seek(curPosition);
                    while (curPosition < endPosition)  //当前位置小于结束位置  继续下载
                    {
                        int len = bis.read(buf, 0, BUFF_SIZE);
                        if (len == -1) {  //下载完成
                            break;
                        }
                        if (!running){
                            break;
                        }
                        rAccessFile.write(buf, 0, len);
                        curPosition = curPosition + len;
                        sum+=len;
                        double temp = (double) sum/total;
                        temp*=1000;
                        handler.setNowProgress((int) temp);
                        sendMessage(DataManager.getDownloadProgress());
                        if (curPosition > endPosition) {    //如果下载多了，则减去多余部分
                            System.out.println("  curPosition > endPosition  !!!!");
                            long extraLen = curPosition - endPosition;
                            downloadSize += (len - extraLen + 1);
                            curPosition=endPosition;
                        } else {
                            downloadSize += len;
                        }
                    }
                    if (running){
                        Log.e(TAG, "当前线程" + ThreadNo + "下载完成");
                        if (downloadSize==DataManager.getFileSize()){
                            sendMessage(DataManager.getUpdateText());
                            finished = true;  //当前任务下载完成
                        }
                    }else {
                        Log.e(TAG, "当前线程" + ThreadNo + "停止下载");
                        editor.putLong(ThreadNo+"startPosition", startPosition);
                        editor.putLong(ThreadNo+"endPosition", endPosition);
                        editor.putLong(ThreadNo+"curPosition", curPosition);
                        editor.apply();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "download error Exception " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        //关闭流
                        bis.close();
                        rAccessFile.close();
                        running = false;
                    } catch (IOException e) {
                        Log.e("AccessFile", "AccessFile IOException " + e.getMessage());
                    }
                }
            }
        });
    }

    private void sendMessage(int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    public boolean getRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

}

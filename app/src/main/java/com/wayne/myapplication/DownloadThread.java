package com.wayne.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author sunbowen
 */
public class DownloadThread extends Thread{

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
    /**已经下载多少*/
    private long downloadSize;

    public int nowNumProgress;

    private static final String TAG = "Wayne";
    /**每一个线程需要下载的大小 */
    private long blockSize;

    /**线程编号*/
    private int ThreadNo;
    /**下载的百分比*/
    private int downloadPercent = 0;
    /**下载的 平均速度*/
    private int downloadSpeed = 0;
    /**下载用的时间*/
    private int usedTime = 0;
    /**当前时间*/
    private long curTime;

    private Handler handler ;

    /**
     * 下载的构造函数
     * @param handler   UI更新
     */
    public DownloadThread(Handler handler, long blockSize, int ThreadNo, File targetFile) throws InterruptedException {
        this.handler = handler;
        this.blockSize = blockSize;
        this.ThreadNo = ThreadNo;
        this.file = targetFile;
        downloadSize = blockSize * ThreadNo;
    }

    @Override
    public void run() {
        long curThreadEndPosition = (ThreadNo+1) != DataManager.getThreadNum() ? ((ThreadNo+1)*blockSize-1) : DataManager.getFileSize();
        endPosition  = curThreadEndPosition;
        byte[] buf = new byte[BUFF_SIZE];
        startPosition = downloadSize+1;
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
                //
                sendMessage(DataManager.getDownloadFail());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                RandomAccessFile rAccessFile = new RandomAccessFile(file, "rwd");//读写
                BufferedInputStream bis = new BufferedInputStream(Objects.requireNonNull(response.body()).byteStream(), BUFF_SIZE);
                try {
                    //设置从什么位置开始写入数据
                    rAccessFile.seek(startPosition);
                    curPosition = startPosition;
                    while (curPosition < endPosition)  //当前位置小于结束位置  继续下载
                    {
                        int len = bis.read(buf, 0, BUFF_SIZE);
                        if (len == -1)   //下载完成
                        {
                            break;
                        }
                        rAccessFile.write(buf, 0, len);
                        curPosition = curPosition + len;
                        if (curPosition > endPosition) {    //如果下载多了，则减去多余部分
                            System.out.println("  curPosition > endPosition  !!!!");
                            long extraLen = curPosition - endPosition;
                            downloadSize += (len - extraLen + 1);
                        } else {
                            downloadSize += len;
                        }
                    }
                    finished = true;  //当前阶段下载完成
                    Log.e(TAG, "当前线程" + ThreadNo + "下载完成");
                } catch (Exception e) {
                    Log.e(TAG, "download error Exception " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        //关闭流
                        bis.close();
                        rAccessFile.close();
                    } catch (IOException e) {
                        Log.e("AccessFile", "AccessFile IOException " + e.getMessage());
                    }
                }
            }
        });
        if (ThreadNo==4){
            sendMessage(DataManager.getDownloadSuccess());
        }
    }


    /**
     * 发送消息，用户提示
     * */
    private void sendMessage(int what)
    {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }


//    private void download() {


//    }
}

package com.wayne.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author sunbowen
 */
public class DownloadActivity extends AppCompatActivity {

    private TextView textView;

    private String responseData;

    private final int UPDATE_TEXT = 1;

    private final int DOWNLOAD_SUCCESS = 2;

    private final int DOWNLOAD_FAIL = 3;

    private final int DOWNLOAD_PROGRESS = 4;

    private static final String TAG = "Wayne";
    /**每一个线程需要下载的大小 */
    private long blockSize;
    /*** 线程数量<br> 默认为5个线程下载*/
    private int threadNum = 5;
    /*** 文件大小 */
    private long fileSize;

    private int numProgress = 0;

    private ProgressBar progressBar;

    private static final ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
            DataManager.getThreadNum(),
            DataManager.getThreadNum(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new DownloadThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull @NotNull Message msg) {
                switch (msg.what){
                    case UPDATE_TEXT:
                        showResponse(responseData);
                        break;
                    case DOWNLOAD_SUCCESS:
                        showResponse("下载成功");
                        break;
                    case DOWNLOAD_FAIL:
                        showResponse("下载失败");
                        break;
                    case DOWNLOAD_PROGRESS:
                        StringBuilder sb = new StringBuilder();
                        double numPro = (double) numProgress/100;
                        sb.append(numPro).append("%");
                        showResponse(sb.toString());
                        progressBar.setProgress(numProgress);
                    default:
                        break;
                }
            }
        };
        try {
            download();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void download() throws IOException{
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(DataManager.getURL())
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //
                sendMessage(DOWNLOAD_FAIL);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                DataManager.setFileSize(Objects.requireNonNull(response.body()).contentLength());
                long fileSize = DataManager.getFileSize();
                //只创建一个文件，saveFile下载内容
                File saveFile = new File(DataManager.getSavePath() + "/" + DataManager.getFileName());
                Log.e(TAG, "文件一共：" + fileSize + " savePath " + DataManager.getSavePath() + "  fileName  " + DataManager.getFileName());
                RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
                //设置本地文件的长度和下载文件相同
                accessFile.setLength(DataManager.getFileSize());
                accessFile.close();
                //每块线程下载数据
                blockSize = ((fileSize % threadNum) == 0) ? (fileSize / threadNum) : (fileSize / threadNum + 1);
                Log.e(TAG, "每个线程分别下载 ：" + blockSize);
                try {
                    threadPoolExecutor.execute(new DownloadThread(handler,blockSize,0,saveFile));
                    threadPoolExecutor.execute(new DownloadThread(handler,blockSize,1,saveFile));
                    threadPoolExecutor.execute(new DownloadThread(handler,blockSize,2,saveFile));
                    threadPoolExecutor.execute(new DownloadThread(handler,blockSize,3,saveFile));
                    threadPoolExecutor.execute(new DownloadThread(handler,blockSize,4,saveFile));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void sendMessage(int what){
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    private void showResponse(String responseData) {
        textView.setText(responseData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadPoolExecutor.shutdown();
    }
}

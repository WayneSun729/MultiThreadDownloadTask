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

    private String TAG = "Wayne";

    private final int UPDATE_TEXT = 1;

    private final int DOWNLOAD_SUCCESS = 2;

    private final int DOWNLOAD_FAIL = 3;

    private final int DOWNLOAD_PROGRESS = 4;

    private int numProgress = 0;

    private ProgressBar progressBar;

    private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
            3,
            3,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new DownloadThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    private Handler handler = new Handler(){
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                download();
//                try {
//                    sendRequestWithOkhttp();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private void download() {
        String url = "https://downloads.gradle-dn.com/distributions/gradle-6.7.1-bin.zip";
        String fileName = "TARGET_FILE";
        String filesDirPath = getFilesDir().getPath();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
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
                // Log.d(TAG, "onResponse.");

                InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
                File target = new File(filesDirPath, fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(target);
                long total = Objects.requireNonNull(response.body()).contentLength();
//                Log.d(TAG, String.valueOf(response.body().contentLength()));

                try {
                    byte[] buffer = new byte[2048];
                    int len;
                    long sum = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        sum+=len;
                        double temp = (double) sum/total;
                        temp*=10000;
//                        Log.d(TAG, String.valueOf((int)temp));
                        numProgress = (int) temp;
                        sendMessage(DOWNLOAD_PROGRESS);
//                         Log.d(TAG, "read: " + len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMessage(DOWNLOAD_SUCCESS);
            }
        });

    }

    private void sendRequestWithOkhttp() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.body() != null){
            responseData = response.body().string();
            Log.d(TAG, responseData);
        }
        sendMessage(UPDATE_TEXT);
    }

    private void sendMessage(int flag){
        Message msg = new Message();
        msg.what = flag;
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
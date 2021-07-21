package com.wayne.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author sunbowen
 */
public class DownloadActivity extends AppCompatActivity {

    private TextView textView;

    private String responseData;

    private final int updateText = 1;

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
                case updateText:
                    showResponse(responseData);
                    break;
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
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendRequestWithOkhttp();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadPoolExecutor.shutdown();
    }

    private void sendRequestWithOkhttp() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.body() != null){
            responseData = response.body().string();
            Log.d("Wayne", responseData);
        }

            Message msg = new Message();
            msg.what = updateText;
            handler.sendMessage(msg);


    }

    private void showResponse(String responseData) {
        textView.setText(responseData);
    }


}
package com.wayne.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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

    private File file;

    private List<MyHandler> myHandlerList = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            switch (msg.what){
                case DOWNLOAD_SUCCESS:
                    showResponse("下载成功");
                    break;
                case DOWNLOAD_FAIL:
                    showResponse("下载失败");
                    break;
                default:
                    break;
            }
        }
    };

    RecyclerView recyclerView;

    private static final ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
            DataManager.getThreadNum(),
            DataManager.getThreadNum(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new DownloadThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);

        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v ->{
            try {
                DataManager.setFileSize(getContentLength(DataManager.getURL()));
                fileSize = DataManager.getFileSize();
                //只创建一个文件，file下载内容
                file = new File(DataManager.getSavePath() +  DataManager.getFileName());
                Log.e(TAG, "文件一共：" + fileSize + " savePath " + DataManager.getSavePath() + "  fileName  " + DataManager.getFileName());
                if (file.exists()){
                    DataManager.setDownloadLength(file.length());
                }
                if (DataManager.getFileSize() == 0) {
                    sendMessage(DOWNLOAD_FAIL);
                }else if (DataManager.getFileSize() == DataManager.getDownloadLength()){
                    sendMessage(DOWNLOAD_SUCCESS);
                }
                download(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DownloadThreadAdapter downloadThreadAdapter = new DownloadThreadAdapter();
        recyclerView.setAdapter(downloadThreadAdapter);

        initMyHandlerList();
    }

    private void initMyHandlerList() {
        for (int i = 0; i < DataManager.getThreadNum(); i++) {
            MyHandler handler = new MyHandler();
            handler.setId(i);
            myHandlerList.add(handler);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void download(File file) throws IOException{
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(DataManager.getURL())
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendMessage(DOWNLOAD_FAIL);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                DataManager.setFileSize(Objects.requireNonNull(response.body()).contentLength());
                verifyStoragePermissions(DownloadActivity.this);
                RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
                //设置本地文件的长度和下载文件相同
                accessFile.setLength(DataManager.getFileSize());
                accessFile.close();
                //每块线程下载数据
                blockSize = ((fileSize % threadNum) == 0) ? (fileSize / threadNum) : (fileSize / threadNum + 1);
                Log.e(TAG, "每个线程分别下载 ：" + blockSize);
                try {
                    for (int i = 0; i < DataManager.getThreadNum(); i++) {
                        threadPoolExecutor.execute(new DownloadThread(myHandlerList.get(i),blockSize,i,file));
                    }
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

    private long getContentLength(String downloadUrl){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        long contentLength = 0;
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                contentLength = Objects.requireNonNull(response.body()).contentLength();
                Objects.requireNonNull(response.body()).close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        Log.d(TAG, String.valueOf(contentLength));
        return contentLength;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    class MyHandler extends Handler {
        int id;

        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            DownloadThreadAdapter.ViewHolder viewHolder = (DownloadThreadAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(id);
            try {
                switch (msg.what){
                    case UPDATE_TEXT:
                        showResponse("下载完成");
                        break;
                    case DOWNLOAD_SUCCESS:
                        viewHolder.progressTextView.setText("下载成功");
                        break;
                    case DOWNLOAD_FAIL:
                        viewHolder.progressTextView.setText("下载失败");
                        break;
                    case DOWNLOAD_PROGRESS:
                        StringBuilder sb = new StringBuilder();
                        double numPro = (double) numProgress/100;
                        sb.append(numPro).append("%");
                        viewHolder.progressTextView.setText(sb.toString());
                        viewHolder.progressBar.setProgress(numProgress);
                    default:
                        break;
                }
            }catch (NullPointerException id){
                Toast.makeText(getApplicationContext(), "出错了。。", Toast.LENGTH_SHORT).show();
            }

        }

        public void setId(int id) {
            this.id = id;
        }
    }

}

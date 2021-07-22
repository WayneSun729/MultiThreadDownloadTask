package com.wayne.myapplication;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public int nowNumProgress;



//    private void download() {
//        String url = "https://downloads.gradle-dn.com/distributions/gradle-6.7.1-bin.zip";
//        String fileName = "TARGET_FILE";
//        String filesDirPath = getFilesDir().getPath();
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder()
//                .get()
//                .url(url)
//                .build();
//        Call call = okHttpClient.newCall(request);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                //
//                sendMessage(DOWNLOAD_FAIL);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                // Log.d(TAG, "onResponse.");
//
//                InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
//                File target = new File(filesDirPath, fileName);
//                FileOutputStream fileOutputStream = new FileOutputStream(target);
//                long total = Objects.requireNonNull(response.body()).contentLength();
////                Log.d(TAG, String.valueOf(response.body().contentLength()));
//
//                try {
//                    byte[] buffer = new byte[2048];
//                    int len;
//                    long sum = 0;
//                    while ((len = inputStream.read(buffer)) != -1) {
//                        fileOutputStream.write(buffer, 0, len);
//                        sum+=len;
//                        double temp = (double) sum/total;
//                        temp*=10000;
////                        Log.d(TAG, String.valueOf((int)temp));
//                        numProgress = (int) temp;
//                        sendMessage(DOWNLOAD_PROGRESS);
////                         Log.d(TAG, "read: " + len);
//                    }
//                    fileOutputStream.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                sendMessage(DOWNLOAD_SUCCESS);
//            }
//        });

//    }
}

package com.wayne.myapplication;

import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Wayne
 */
public class DataManager {

    private static String savePath,URL,fileName;
    private static long fileSize,downloadLength;
    private static int threadNum = 5;
    private static final int UPDATE_TEXT = 1;
    private static final int DOWNLOAD_SUCCESS = 2;
    private static final int DOWNLOAD_FAIL = 3;
    private static final int DOWNLOAD_PROGRESS = 4;
    public static final int FLAG_SINGLE_CONTROL = 0;
    public static final int FLAG_ALL_CONTROL = 1;
    public static final int FLAG_ALL_START= 0;
    public static final int FLAG_ALL_PAUSE = 1;
    public static final int STATUS_NEW= -1;
    public static final int STATUS_START= 0;
    public static final int STATUS_PAUSE = 1;
    public static final int STATUS_FINISH = 2;
    private static int threadsStatus = STATUS_NEW;
    private static final ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
            DataManager.getThreadNum(),
            DataManager.getThreadNum(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new DownloadThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    private static SharedPreferences sp;
    private static List<DownloadThread> downloadThreads = new ArrayList<>();
    private static File targetFile;

    public static int getUpdateText() {
        return UPDATE_TEXT;
    }

    public static int getDownloadSuccess() {
        return DOWNLOAD_SUCCESS;
    }

    public static int getDownloadFail() {
        return DOWNLOAD_FAIL;
    }

    public static int getDownloadProgress() {
        return DOWNLOAD_PROGRESS;
    }

    public static long getFileSize() {
        return fileSize;
    }

    public static void setFileSize(long fileSize) {
        DataManager.fileSize = fileSize;
    }

    public static int getThreadNum() {
        return threadNum;
    }

    public static void setThreadNum(int threadNum) {
        DataManager.threadNum = threadNum;
    }

    public static String getSavePath() {
        return savePath;
    }

    public static void setSavePath(String savePath) {
        DataManager.savePath = savePath;
    }

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        DataManager.URL = URL;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        DataManager.fileName = fileName;
    }

    public static long getDownloadLength() {
        return downloadLength;
    }

    public static void setDownloadLength(long downloadLength) {
        DataManager.downloadLength = downloadLength;
    }

    public static List<DownloadThread> getDownloadThreads() {
        return downloadThreads;
    }

    public static void setDownloadThreads(List<DownloadThread> downloadThreads) {
        DataManager.downloadThreads = downloadThreads;
    }

    public static int getThreadsStatus() {
        return threadsStatus;
    }

    public static void setThreadsStatus(int threadsStatus) {
        DataManager.threadsStatus = threadsStatus;
    }

    public static ExecutorService getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public static SharedPreferences getSp() {
        return sp;
    }

    public static void setSp(SharedPreferences Sp) {
        sp = Sp;
    }

    public static File getTargetFile() {
        return targetFile;
    }

    public static void setTargetFile(File targetFile) {
        DataManager.targetFile = targetFile;
    }
}

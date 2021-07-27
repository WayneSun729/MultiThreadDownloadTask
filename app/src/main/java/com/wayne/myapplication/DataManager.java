package com.wayne.myapplication;

public class DataManager {
    private static String savePath,URL,fileName;

    private static long fileSize;

    private static int threadNum = 5;

    private static final int UPDATE_TEXT = 1;

    private static final int DOWNLOAD_SUCCESS = 2;

    private static final int DOWNLOAD_FAIL = 3;

    private static final int DOWNLOAD_PROGRESS = 4;

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
}

package com.wayne.myapplication;

import android.app.Application;
import android.content.Context;

/**
 * <pre>
 *     author : 孙博文
 *     e-mail : sunbowen@migu.cn
 *     time   : 2021/08/05
 *     desc   : 主活动
 *     version: 1.0 {孙博文} 可以全局获得context了
 * </pre>
 */
public class MyApplication extends Application {
    @SuppressWarnings("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}

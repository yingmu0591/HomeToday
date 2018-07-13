package com.topstar;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * 需要在AndroidManifest.xml文件中注册
 */
public class TSApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 获取APP上下文对象
     */
    public static Context getContext() {
        return instance;
    }

    /**
     * 获取资源中的颜色
     */
    public static int getResourcesColor(int colorId) {
        return ContextCompat.getColor(instance, colorId);
    }

    /**
     * 获取资源中的字符串
     */
    public static String getResourcesString(int stringId){
        return instance.getString(stringId);
    }
}
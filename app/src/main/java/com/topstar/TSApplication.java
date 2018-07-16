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
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
    }

    /**
     * 获取APP上下文对象
     */
    public static Context getContext() {
        return sContext;
    }

    /**
     * 获取资源中的颜色
     */
    public static int getResourcesColor(int colorId) {
        return ContextCompat.getColor(sContext, colorId);
    }

    /**
     * 获取资源中的字符串
     */
    public static String getResourcesString(int stringId){
        return sContext.getString(stringId);
    }
}
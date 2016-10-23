package com.sollyu.android.appenv;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

/**
 * 作者: Sollyu
 * 时间: 16/10/23
 * 联系: sollyu@qq.com
 * 说明:
 */
public class MainApplication extends Application {

    private static MainApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Android-Bootstrap 图标注册
        TypefaceProvider.registerDefaultIconSets();
    }

    public static MainApplication getInstance() {
        return instance;
    }

    /**
     * @return 检查XPOSED是否工作
     */
    public boolean isXposedWork() {
        return false;
    }
}

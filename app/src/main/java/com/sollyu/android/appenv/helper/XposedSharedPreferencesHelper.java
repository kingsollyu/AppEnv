package com.sollyu.android.appenv.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.sollyu.android.appenv.BuildConfig;
import com.sollyu.android.appenv.MainApplication;
import com.sollyu.android.appenv.module.AppInfo;

import java.io.File;

/**
 * 作者: Sollyu
 * 时间: 16/10/23
 * 联系: sollyu@qq.com
 * 说明:
 */
public class XposedSharedPreferencesHelper {
    private static final String TAG = "AppEnv";

    public static final String KEY_ALL  = "ALL";
    public static final String KEY_USER = "USER";

    private static final XposedSharedPreferencesHelper instance = new XposedSharedPreferencesHelper();

    private XposedSharedPreferencesHelper() {
    }

    public static XposedSharedPreferencesHelper getInstance() {
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        return MainApplication.getInstance().getSharedPreferences("XPOSED", Context.MODE_PRIVATE);
    }

    public AppInfo get(String packageName) {
        return JSON.toJavaObject(JSON.parseObject(getSharedPreferences().getString(packageName, null)), AppInfo.class);
    }

    public void set(String packageName, AppInfo appInfo) {
        getSharedPreferences().edit().putString(packageName, JSON.toJSONString(appInfo, false)).apply();
        resetFilePermission();
    }

    public void remove(String packageName) {
        getSharedPreferences().edit().remove(packageName).apply();
        resetFilePermission();
    }

    private void resetFilePermission() {
        File sharedPreferencesFile = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/", "XPOSED.xml");

        sharedPreferencesFile.setReadable(true, false);
        sharedPreferencesFile.setWritable(true, false);
    }
}

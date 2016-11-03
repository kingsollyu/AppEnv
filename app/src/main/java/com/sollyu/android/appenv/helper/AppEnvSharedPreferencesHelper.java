package com.sollyu.android.appenv.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sollyu.android.appenv.MainApplication;

/**
 * 作者: Sollyu
 * 时间: 16/10/23
 * 联系: sollyu@qq.com
 * 说明:
 */
public class AppEnvSharedPreferencesHelper {
    private static final AppEnvSharedPreferencesHelper instance = new AppEnvSharedPreferencesHelper();

    private static final String KEY_REPORT_PHONE    = "REPORT_PHONE";
    private static final String KEY_SHOW_SYSTEM_APP = "SHOW_SYSTEM_APP";

    private AppEnvSharedPreferencesHelper() {
    }

    public static AppEnvSharedPreferencesHelper getInstance() {
        return instance;
    }

    public boolean isReportPhone() {
        return getSharedPreferences().getBoolean(KEY_REPORT_PHONE, false);
    }

    public void setReportPhone(boolean value) {
        getSharedPreferences().edit().putBoolean(KEY_REPORT_PHONE, value).apply();
    }

    public boolean isShowSystemApp() {
        return getSharedPreferences().getBoolean(KEY_SHOW_SYSTEM_APP, false);
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance());
    }

}

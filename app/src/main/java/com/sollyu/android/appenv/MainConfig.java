package com.sollyu.android.appenv;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sollyu on 2017/5/1.
 */

public class MainConfig {
    private static final MainConfig instance = new MainConfig();

    public synchronized static MainConfig getInstance() {
        return instance;
    }

    private SharedPreferences sharedPreferences = null;

    public void init() {
        setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance()));
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isShowSystemApp() {
        return getSharedPreferences().getBoolean("show_system_app", false);
    }

    public void setShowSystemApp(boolean showSystemApp) {
        getSharedPreferences().edit().putBoolean("show_system_app", showSystemApp).apply();
    }

    public Boolean getRandomLanguage() {
        return getSharedPreferences().getBoolean("randomLanguage", false);
    }

    public void setRandomLanguage(Boolean randomLanguage) {
        getSharedPreferences().edit().putBoolean("randomLanguage", randomLanguage).apply();
    }
}

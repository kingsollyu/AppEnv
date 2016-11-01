package com.sollyu.android.appenv;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.sollyu.android.appenv.helper.PhoneHelper;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "558a1cb667e58e7649000228", BuildConfig.FLAVOR));
        MobclickAgent.setCatchUncaughtExceptions(true);
        MobclickAgent.enableEncrypt(true);

        // 释放文件
        try {
            File releaseFile = new File(this.getFilesDir(), "phone.json");
            if (!releaseFile.exists()) {
                FileUtils.writeByteArrayToFile(releaseFile, InputToByte(getAssets().open("phone.json")));
            }

            PhoneHelper.getInstance().reload(this);
        } catch (Exception e) {
            MobclickAgent.reportError(this, e);
        }
    }

    public static byte[] InputToByte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[]                buff       = new byte[100];
        int                   rc         = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
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

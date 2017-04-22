package com.sollyu.android.appenv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 作者: Sollyu
 * 时间: 16/10/23
 * 联系: sollyu@qq.com
 * 说明:
 */
public class SplashActivity extends AppCompatActivity {

    private final Handler uiHandler = new Handler();
    private final Integer splashTimeOut = 1 * 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (!MainApplication.getInstance().isXposedWork()) {
//                    startActivity(new Intent(SplashActivity.this, XposedNotWorkActivity.class));
//                    SplashActivity.this.finish();
//                    return;
//                }

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, splashTimeOut);
    }
}

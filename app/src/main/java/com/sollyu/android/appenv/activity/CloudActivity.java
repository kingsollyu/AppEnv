package com.sollyu.android.appenv.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.sollyu.android.appenv.MainApplication;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.TokenHelper;

public class CloudActivity extends AppCompatActivity {

    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 891;

    private static final String TAG = "AppEnv";

    private BootstrapEditText tokenBootstrapEditText  = null;
    private BootstrapButton   activateBootstrapButton = null;
    private RelativeLayout    tokenRelativeLayout     = null;

    private Handler uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tokenRelativeLayout = (RelativeLayout) findViewById(R.id.input_token);
        tokenBootstrapEditText = (BootstrapEditText) findViewById(R.id.edit_text_token);
        activateBootstrapButton = (BootstrapButton) findViewById(R.id.button_activate);

        if (ContextCompat.checkSelfPermission(MainApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            readSavedTokenToUi();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            switch (grantResults[0]) {
                case PackageManager.PERMISSION_GRANTED:
                    readSavedTokenToUi();
                    break;
            }
        }
    }

    public void readSavedTokenToUi() {
        try {
            tokenBootstrapEditText.setText(TokenHelper.getInstance().getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickOrder(View view) {
        OtherHelper.getInstance().openUrl(view.getContext(), "https://item.taobao.com/item.htm?id=540499753293");
    }

    public void onClickActivate(View view) {
        if (tokenBootstrapEditText.getText().toString().isEmpty()) {
            Snackbar.make(view, "激活码不能为空", Snackbar.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            TokenHelper.ServerResult serverResult = TokenHelper.getInstance().info(tokenBootstrapEditText.getText().toString());
            Log.d(TAG, "onClickActivate: " + serverResult);
            if (serverResult.getRet() != 200) {
                uiHandler.post(() -> {
                    tokenBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    activateBootstrapButton.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    Snackbar.make(view, serverResult.getMsg(), Snackbar.LENGTH_LONG).show();
                });

                uiHandler.postDelayed(() -> {
                    tokenBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    activateBootstrapButton.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                }, 5000);

                TokenHelper.getInstance().setActivate(false);
                return;
            }

            // 保存Token并设置状态为激活
            TokenHelper.getInstance().setToken(tokenBootstrapEditText.getText().toString());
            TokenHelper.getInstance().setActivate(true);
            uiHandler.post(() -> tokenRelativeLayout.setVisibility(View.GONE));

        }).start();
    }
}

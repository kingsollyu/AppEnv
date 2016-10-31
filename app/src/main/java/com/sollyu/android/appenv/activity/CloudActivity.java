package com.sollyu.android.appenv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.TokenHelper;

public class CloudActivity extends AppCompatActivity {

    private static final String TAG = "AppEnv";

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

        if (!TokenHelper.getInstance().isActivate()) {
            startActivityForResult(new Intent(this, LoginActivity.class), 0);
        }

        checkTokenIsWork();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 1) {
            this.finish();
        }
    }

    private void checkTokenIsWork() {
        new Thread(() -> {
            TokenHelper.ServerResult serverResult = TokenHelper.getInstance().info(TokenHelper.getInstance().getToken());
            Log.d(TAG, "checkTokenIsWork: " + serverResult);
            if (serverResult.getRet() != 200) {
                TokenHelper.getInstance().setActivate(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(CloudActivity.this);
                builder.setTitle("提示");
                builder.setMessage("您的激活码已经不可以继续使用\n原因: " + serverResult.getMsg());
                builder.setPositiveButton("重新输入激活码", (dialog, which) -> startActivityForResult(new Intent(CloudActivity.this, LoginActivity.class), 0));
                builder.setNegativeButton("关闭", (dialog, which) -> {
                    CloudActivity.this.finish();
                });
                builder.setCancelable(false);
                uiHandler.post(() -> builder.create().show());
                return;
            }


        }).start();
    }
}

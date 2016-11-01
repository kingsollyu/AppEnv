package com.sollyu.android.appenv.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.TokenHelper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "AppEnv";

    private BootstrapEditText tokenBootstrapEditText  = null;
    private BootstrapButton   activateBootstrapButton = null;

    private Handler uiHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenBootstrapEditText = (BootstrapEditText) findViewById(R.id.edit_text_token);
        activateBootstrapButton = (BootstrapButton) findViewById(R.id.button_activate);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tokenBootstrapEditText.setText(TokenHelper.getInstance().getToken());
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

    public void onClickOrder(View view) {
        OtherHelper.getInstance().openUrl(view.getContext(), "https://item.taobao.com/item.htm?id=540499753293");
    }

    public void onClickActivate(View view) {
        if (tokenBootstrapEditText.getText().toString().isEmpty()) {
            Snackbar.make(view, R.string.token_can_not_null, Snackbar.LENGTH_LONG).show();
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
            setResult(1);
            LoginActivity.this.finish();

        }).start();
    }
}

package com.sollyu.android.appenv.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.TokenHelper;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    private static final String TAG = "AppEnv";

    private BootstrapEditText tokenBootstrapEditText  = null;
    private BootstrapButton   activateBootstrapButton = null;

    private Handler uiHandler = new Handler();

    @ViewInject(R.id.activity_login) private RelativeLayout mLoginContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

        AsyncTask<Object, Object, TokenHelper.ServerResult> asyncTask = new AsyncTask<Object, Object, TokenHelper.ServerResult>() {
            private String inputTokenString = null;
            private ProgressDialog progressDialog = null;

            @Override
            protected void onPreExecute() {
                inputTokenString = tokenBootstrapEditText.getText().toString();
                progressDialog = ProgressDialog.show(getActivity(), getString(R.string.wait), getString(R.string.processing), true);
            }

            @Override
            protected TokenHelper.ServerResult doInBackground(Object... params) {
                return TokenHelper.getInstance().info(inputTokenString);
            }

            @Override
            protected void onPostExecute(TokenHelper.ServerResult serverResult) {
                progressDialog.dismiss();
                if (serverResult.getRet() != 200) {
                    tokenBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    activateBootstrapButton.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    Snackbar.make(mLoginContent, serverResult.getMsg(), Snackbar.LENGTH_LONG).show();

                    uiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tokenBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                            activateBootstrapButton.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                        }
                    }, 5000);

                    TokenHelper.getInstance().setActivate(false);
                } else {
                    // 保存Token并设置状态为激活
                    TokenHelper.getInstance().setToken(tokenBootstrapEditText.getText().toString());
                    TokenHelper.getInstance().setActivate(true);
                    setResult(1);
                    LoginActivity.this.finish();
                }
            }
        };

        asyncTask.execute();
    }
}

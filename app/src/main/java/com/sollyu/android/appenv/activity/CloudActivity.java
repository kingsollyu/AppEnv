package com.sollyu.android.appenv.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.sollyu.android.appenv.BuildConfig;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.SolutionHelper;
import com.sollyu.android.appenv.helper.TokenHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CloudActivity extends AppCompatActivity {

    private static final String TAG = "AppEnv";

    private AwesomeTextView infoAwesomeTextView = null;

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

        infoAwesomeTextView = (AwesomeTextView) findViewById(R.id.info);

        if (!TokenHelper.getInstance().isActivate()) {
            startActivityForResult(new Intent(this, LoginActivity.class), 0);
        }else{
            checkTokenIsWork();
        }
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
            return;
        }
        checkTokenIsWork();
    }

    private void checkTokenIsWork() {
        new Thread(() -> {
            TokenHelper.ServerResult serverResult = TokenHelper.getInstance().info(TokenHelper.getInstance().getToken());
            if (serverResult.getRet() != 200) {
                TokenHelper.getInstance().setActivate(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(CloudActivity.this);
                builder.setTitle("提示");
                builder.setMessage("您的激活码已经不可以继续使用\n原因: " + serverResult.getMsg());
                builder.setPositiveButton("重新输入激活码", (dialog, which) -> startActivityForResult(new Intent(CloudActivity.this, LoginActivity.class), 0));
                builder.setNegativeButton("关闭", (dialog, which) -> CloudActivity.this.finish());
                builder.setCancelable(false);
                uiHandler.post(() -> builder.create().show());
            } else {
                uiHandler.post(() -> infoAwesomeTextView.setText("令牌: " + TokenHelper.getInstance().getToken() + "\n" +
                        "已用: " + serverResult.getDataJson().getString("times") + "\n" +
                        "总共: " + serverResult.getDataJson().getString("range")));
            }


        }).start();
    }

    public void onClickUpload(View view) {
        if (!TokenHelper.getInstance().isActivate()) {
            CloudActivity.this.startActivity(new Intent(CloudActivity.this, LoginActivity.class));
            return;
        }

        ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), getString(R.string.wait), getString(R.string.processing), true);
        new Thread(() -> {
            try {
                String appSettingContent    = "";
                String xposedSettingContent = "";
                String solutionContent      = "";

                File appSettingFile    = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + BuildConfig.APPLICATION_ID + "_preferences.xml");
                File xposedSettingFile = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/XPOSED.xml");
                File solutionFile      = SolutionHelper.SOLUTION_FILE;

                if (appSettingFile.exists()) {
                    appSettingContent = FileUtils.readFileToString(appSettingFile, "UTF-8");
                }

                if (xposedSettingFile.exists()) {
                    xposedSettingContent = FileUtils.readFileToString(xposedSettingFile, "UTF-8");
                }

                if (solutionFile.exists()) {
                    solutionContent = FileUtils.readFileToString(solutionFile, "UTF-8");
                }


                TokenHelper.ServerResult serverResult = TokenHelper.getInstance().upload(appSettingContent, xposedSettingContent, solutionContent);

                if (serverResult.getRet() != 200) {
                    throw new RuntimeException(serverResult.getMsg());
                }

                Snackbar.make(view, R.string.upload_success, Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                Snackbar.make(view, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            } finally {
                progressDialog.dismiss();
            }
        }).start();

    }

    public void onClickDownload(View view) {
        if (!TokenHelper.getInstance().isActivate()) {
            CloudActivity.this.startActivity(new Intent(CloudActivity.this, LoginActivity.class));
            return;
        }

        ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), getString(R.string.wait), getString(R.string.processing), true);

        new Thread(() -> {
            try {
                TokenHelper.ServerResult serverResult = TokenHelper.getInstance().download();

                if (serverResult.getRet() != 200) {
                    throw new RuntimeException(serverResult.getMsg());
                }

                File appSettingFile    = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + BuildConfig.APPLICATION_ID + "_preferences.xml");
                File xposedSettingFile = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/XPOSED.xml");
                File solutionFile      = SolutionHelper.SOLUTION_FILE;

                String temp = "";
                if ((temp = serverResult.getDataJson().getString("app")) != null) {
                    if (!appSettingFile.delete()) {
                        throw new IOException(getString(R.string.delete_file_error));
                    }
                    FileUtils.writeStringToFile(appSettingFile, temp, "UTF-8");
                }
                if ((temp = serverResult.getDataJson().getString("xposed")) != null) {
                    FileUtils.writeStringToFile(xposedSettingFile, temp, "UTF-8");
                }
                if ((temp = serverResult.getDataJson().getString("solution")) != null) {
                    FileUtils.writeStringToFile(solutionFile, temp, "UTF-8");
                }

                Snackbar.make(view, R.string.download_success, Snackbar.LENGTH_LONG).setAction(R.string.exit, v -> System.exit(0)).show();
            } catch (Exception e) {
                Snackbar.make(view, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            } finally {
                progressDialog.dismiss();
            }
        }).start();
    }

    public void onClickPhone(View view) {
        if (!TokenHelper.getInstance().isActivate()) {
            CloudActivity.this.startActivity(new Intent(CloudActivity.this, LoginActivity.class));
            return;
        }

        ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), getString(R.string.wait), getString(R.string.processing), true);

        new Thread(() -> {
            try {
                TokenHelper.ServerResult serverResult = TokenHelper.getInstance().phone();
                progressDialog.dismiss();

                if (serverResult.getRet() != 200) {
                    throw new RuntimeException(serverResult.getMsg());
                }

                FileUtils.writeStringToFile(new File(view.getContext().getFilesDir(), "phone.json"), JSON.toJSONString(serverResult.getDataJson(), true), "UTF-8");
                Snackbar.make(view, R.string.get_last_phone_sccess, Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                Snackbar.make(view, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            } finally {
                progressDialog.dismiss();
            }
        }).start();
    }

    public void onClickShare(View view) {
        if (!TokenHelper.getInstance().isActivate()) {
            CloudActivity.this.startActivity(new Intent(CloudActivity.this, LoginActivity.class));
            return;
        }

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ViewHolder(R.layout.content_share))
                .setExpanded(true, (int) OtherHelper.getInstance().convertDpToPixel(view.getContext(), 260))
                .create();

        BootstrapButton   bootstrapButton               = (BootstrapButton) dialogPlus.getHolderView().findViewById(R.id.share);
        BootstrapEditText manufacturerBootstrapEditText = (BootstrapEditText) dialogPlus.findViewById(R.id.manufacturer);
        BootstrapEditText modelBootstrapEditText        = (BootstrapEditText) dialogPlus.findViewById(R.id.model);
        BootstrapEditText nameBootstrapEditText         = (BootstrapEditText) dialogPlus.findViewById(R.id.name);
        BootstrapEditText reasonBootstrapEditText       = (BootstrapEditText) dialogPlus.findViewById(R.id.reason);

        ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(R.string.share_devices);

        bootstrapButton.setOnClickListener(v -> {
            if (manufacturerBootstrapEditText.getText().toString().isEmpty()) {
                manufacturerBootstrapEditText.requestFocus();
                manufacturerBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                uiHandler.postDelayed(() -> manufacturerBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.INFO), 3000);
                return;
            }

            if (modelBootstrapEditText.getText().toString().isEmpty()) {
                modelBootstrapEditText.requestFocus();
                modelBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                uiHandler.postDelayed(() -> modelBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.INFO), 3000);
                return;
            }

            if (nameBootstrapEditText.getText().toString().isEmpty()) {
                nameBootstrapEditText.requestFocus();
                nameBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                uiHandler.postDelayed(() -> nameBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.INFO), 3000);
                return;
            }

            if (reasonBootstrapEditText.getText().toString().isEmpty()) {
                reasonBootstrapEditText.requestFocus();
                reasonBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                uiHandler.postDelayed(() -> reasonBootstrapEditText.setBootstrapBrand(DefaultBootstrapBrand.INFO), 3000);
                return;
            }

            ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), getString(R.string.wait), getString(R.string.processing), true);
            new Thread(() -> {
                try {
                    TokenHelper.ServerResult serverResult = TokenHelper.getInstance().share(
                            manufacturerBootstrapEditText.getText().toString(),
                            modelBootstrapEditText.getText().toString(),
                            nameBootstrapEditText.getText().toString(),
                            reasonBootstrapEditText.getText().toString()
                    );

                    if (serverResult.getRet() != 200) {
                        throw new RuntimeException(serverResult.getMsg());
                    }

                    uiHandler.post(dialogPlus::dismiss);
                    uiHandler.postDelayed(() -> Snackbar.make(view, R.string.share_device_success, Snackbar.LENGTH_LONG).show(), 500);
                } catch (Exception e) {
                    Snackbar.make(view, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                } finally {
                    progressDialog.dismiss();
                }
            }).start();
        });

        dialogPlus.show();
    }
}

package com.sollyu.android.appenv.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.sollyu.android.appenv.R;

public class XposedNotWorkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xposed_not_work);

        BootstrapButton bootstrapButton = (BootstrapButton) findViewById(R.id.button);
        AwesomeTextView awesomeTextView = (AwesomeTextView) findViewById(R.id.text);

        try {
            ApplicationInfo xposedInstallerApplicationInfo = getPackageManager().getApplicationInfo("de.robv.android.xposed.installer", PackageManager.GET_UNINSTALLED_PACKAGES);

            awesomeTextView.setMarkdownText(getString(R.string.s1));
            bootstrapButton.setOnClickListener(openXposedInstallerOnClickListener);
            bootstrapButton.setText(R.string.open_xposed);
        } catch (PackageManager.NameNotFoundException e) {
            awesomeTextView.setMarkdownText(getString(R.string.s2));
            bootstrapButton.setOnClickListener(installXposedOnClickListener);
            bootstrapButton.setText(R.string.install_xposed);
        }
    }

    private View.OnClickListener openXposedInstallerOnClickListener = v -> {
        Intent intent = getPackageManager().getLaunchIntentForPackage("de.robv.android.xposed.installer");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    };

    private View.OnClickListener installXposedOnClickListener = v -> {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=de.robv.android.xposed.installer")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=de.robv.android.xposed.installer")));
        }
    };
}

package com.sollyu.android.appenv.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;

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

    private View.OnClickListener openXposedInstallerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OtherHelper.getInstance().openApplication(v.getContext(), "de.robv.android.xposed.installer");
        }
    };
    private View.OnClickListener installXposedOnClickListener       = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OtherHelper.getInstance().openMarket(v.getContext(), "de.robv.android.xposed.installer");
        }
    };
}

package com.sollyu.android.appenv.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kyleduo.switchbutton.SwitchButton;
import com.sollyu.android.appenv.MainConfig;
import com.sollyu.android.appenv.R;
import com.tencent.bugly.beta.Beta;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 作者: Sollyu
 * 时间: 16/11/2
 * 联系: sollyu@qq.com
 * 说明:
 */
@ContentView(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {
    private boolean isChanged = false;

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;

    @ViewInject(R.id.sbShowSystemApp)
    private SwitchButton mSbShowSystemApp;


    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSbShowSystemApp.setCheckedImmediately(MainConfig.getInstance().isShowSystemApp());
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
    public void onBackPressed() {
        SettingsActivity.this.setResult(isChanged ? 1 : 0);
        SettingsActivity.this.finish();

        super.onBackPressed();
    }

    @SuppressWarnings("unused")
    @Event(R.id.oivCheckUpdate)
    private void onClickCheckUpdate(View view) {
        Beta.checkUpgrade();
    }

    @SuppressWarnings("unused")
    @Event(R.id.oivAbout)
    private void onClickAbout(View view) {
        getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    @SuppressWarnings("unused")
    @Event(R.id.sbShowSystemApp)
    private void onClickShowSystemApp(View view) {
        isChanged = true;
        MainConfig.getInstance().setShowSystemApp(mSbShowSystemApp.isChecked());
    }
}

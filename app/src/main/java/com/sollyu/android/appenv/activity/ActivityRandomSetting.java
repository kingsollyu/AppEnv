package com.sollyu.android.appenv.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kyleduo.switchbutton.SwitchButton;
import com.sollyu.android.appenv.MainConfig;
import com.sollyu.android.appenv.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import qiu.niorgai.StatusBarCompat;

@ContentView(R.layout.activity_random_setting)
public class ActivityRandomSetting extends BaseActivity {

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ActivityRandomSetting.class));
    }

    @ViewInject(R.id.toolbar)          Toolbar      mToolbar;
    @ViewInject(R.id.sbRandomLanguage) SwitchButton mSbRandomLanguage;

    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.random_setting);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            StatusBarCompat.setStatusBarColor(getActivity(), getActivity().getResources().getColor(R.color.colorPrimaryDark));
        }

        mSbRandomLanguage.setCheckedImmediately(MainConfig.getInstance().getRandomLanguage());
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

    @SuppressWarnings("unused")
    @Event(R.id.sbRandomLanguage)
    private void onClickShowSystemApp(View view) {
        MainConfig.getInstance().setRandomLanguage(mSbRandomLanguage.isChecked());
    }
}

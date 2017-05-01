package com.sollyu.android.appenv.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.xutils.x;

/**
 * Created by sollyu on 2017/4/22.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initView();
        initData();
    }

    protected void initView() {

    }

    protected void initData() {

    }

    protected Activity getActivity() {
        return this;
    }
}

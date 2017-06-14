package com.sollyu.android.appenv.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.sollyu.android.appenv.BuildConfig;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.OtherHelper;

import org.xutils.view.annotation.ContentView;

import qiu.niorgai.StatusBarCompat;

@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            try {
                OtherHelper.getInstance().openUrl(v.getContext(), "http://www.sollyu.com/hook-model/");
            } catch (Exception e) {
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            StatusBarCompat.setStatusBarColor(getActivity(), getActivity().getResources().getColor(R.color.colorPrimaryDark));
        }

        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);
        textView.setText(Html.fromHtml(getApplicationContext().getString(R.string.about_text, BuildConfig.VERSION_NAME)));
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
}

package com.sollyu.android.appenv.activity;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.AppEnvSharedPreferencesHelper;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.ServerHelper;
import com.sollyu.android.appenv.helper.XposedSharedPreferencesHelper;
import com.sollyu.android.appenv.module.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "AppEnv";

    private Handler         uiHandler       = new Handler();
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    private RecyclerView              _RecyclerView              = null;
    private NormalRecyclerViewAdapter _NormalRecyclerViewAdapter = null;
    private SwipeRefreshLayout        _SwipeRefreshLayout        = null;

    private ArrayList<ApplicationInfo> _DisplayApplicationInfo = new ArrayList<>();
    private List<ApplicationInfo>      applicationInfos        = null;

    private static final int READ_PHONE_STATE_REQUEST_CODE = 227;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).show());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        _RecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        _RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        _RecyclerView.setAdapter(_NormalRecyclerViewAdapter = new NormalRecyclerViewAdapter());

        _SwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeRefreshLayout);
        _SwipeRefreshLayout.setOnRefreshListener(this);


        AppInfo appInfo = new AppInfo();
        appInfo.buildBoard = "dasdfa";
        XposedSharedPreferencesHelper.getInstance().set(getPackageName(), appInfo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
        } else {
            reportPhoneInfo();
        }

        new Thread(() -> {
            applicationInfos = MainActivity.this.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            Collections.sort(applicationInfos, new ApplicationInfo.DisplayNameComparator(MainActivity.this.getPackageManager()));
            uiHandler.post(this::onRefresh);
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_PHONE_STATE_REQUEST_CODE) {
            switch (grantResults[0]) {
                case PackageManager.PERMISSION_GRANTED:
                    reportPhoneInfo();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_refresh) {
            // onRefresh();
        } else if (id == R.id.action_about) {
            // startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_donate) {
            OtherHelper.getInstance().openUrl(this, "https://mobilecodec.alipay.com/client_download.htm?qrcode=apynckrfcfi5atfy45");
        } else if (id == R.id.nav_score) {
            OtherHelper.getInstance().openMarket(this, getPackageName());
        } else if (id == R.id.action_settings) {
            // startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_cloud) {
            // startActivity(new Intent(this, CloudActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onRefresh() {

        _NormalRecyclerViewAdapter.notifyItemRangeRemoved(0, _DisplayApplicationInfo.size());
        _DisplayApplicationInfo.clear();

        int hasConfigIndex = 0;
        for (ApplicationInfo applicationInfo : applicationInfos) {
            if (OtherHelper.getInstance().isUserAppllication(applicationInfo)) {
                if (XposedSharedPreferencesHelper.getInstance().get(applicationInfo.packageName) != null) {
                    _DisplayApplicationInfo.add(hasConfigIndex++, applicationInfo);
                } else {
                    _DisplayApplicationInfo.add(applicationInfo);
                }
            }
        }
        _NormalRecyclerViewAdapter.notifyDataSetChanged();
        _SwipeRefreshLayout.setRefreshing(false);
    }

    private void reportPhoneInfo() {
        new Thread(() -> {
            if (!AppEnvSharedPreferencesHelper.getInstance().isReportPhone())
                AppEnvSharedPreferencesHelper.getInstance().setReportPhone(ServerHelper.getInstance().devices(MainActivity.this).getCode() == 400);
        }).start();
    }

    private class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {

        @Override
        public NormalRecyclerViewAdapter.NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.content_listview, parent, false));
        }

        @Override
        public void onBindViewHolder(NormalRecyclerViewAdapter.NormalTextViewHolder holder, int position) {
            ApplicationInfo applicationInfo = _DisplayApplicationInfo.get(position);
            holder.textView1.setText(applicationInfo.loadLabel(getPackageManager()));
            holder.textView2.setText(applicationInfo.packageName);
            holder.imageView.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));

            AppInfo appInfo = XposedSharedPreferencesHelper.getInstance().get(applicationInfo.packageName);
            Log.d(TAG, "onBindViewHolder: " + JSON.toJSONString(appInfo));
            holder.textView1.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), appInfo != null ? R.color.bootstrap_brand_success : android.R.color.primary_text_light));

            holder.itemView.setOnClickListener(v -> {
                Snackbar.make(holder.itemView, holder.textView1.getText().toString(), Snackbar.LENGTH_LONG).show();
            });

            holder.itemView.setOnLongClickListener(v -> {
                List<HashMap<String, Object>> mapList = new ArrayList<>();

                HashMap<String, Object> openItem = new HashMap<>();
                openItem.put("title", getString(R.string.open));
                openItem.put("icon", R.drawable.ic_delete);
                mapList.add(openItem);

                openItem = new HashMap<>();
                openItem.put("title", getString(R.string.random));
                openItem.put("icon", R.drawable.ic_refresh);
                mapList.add(openItem);

                openItem = new HashMap<>();
                openItem.put("title", getString(R.string.detail));
                openItem.put("icon", R.drawable.ic_info);
                mapList.add(openItem);

                openItem = new HashMap<>();
                openItem.put("title", getString(R.string.delete));
                openItem.put("icon", R.drawable.ic_delete);
                mapList.add(openItem);

                SimpleAdapter simpleAdapter = new SimpleAdapter(
                        holder.itemView.getContext(),
                        mapList,
                        R.layout.dialog_plus_content,
                        new String[]{"title", "icon"},
                        new int[]{R.id.text_view, R.id.image_view});

                DialogPlus dialogPlus = DialogPlus.newDialog(holder.itemView.getContext())
                        .setExpanded(false)
                        .setContentHolder(new ListHolder())
                        .setHeader(R.layout.dialog_plus_header)
                        .setAdapter(simpleAdapter)
                        .setOnItemClickListener((dialog, item, view, position1) -> {
                            dialog.dismiss();
                            switch (position1) {
                                case 0: // Open
                                    holder.itemView.performClick();
                                    break;
                                case 1: // Random
                                    // RandomHelper.getInstance().randomAll(holder.textView2.getText().toString());
                                    // Snackbar.make(holder.itemView, "一键随机完成", Snackbar.LENGTH_LONG).setAction("强制停止", v1 -> {}).show();
                                    break;
                                case 2: // Detail
                                    OtherHelper.getInstance().openAppDetails(holder.itemView.getContext(), holder.textView2.getText().toString());
                                    break;
                                case 3: // Delete
                                    XposedSharedPreferencesHelper.getInstance().remove(holder.textView2.getText().toString());
                                    uiHandler.post(MainActivity.this::onRefresh);
                                    break;
                            }
                        })
                        .create();

                ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(holder.textView1.getText());
                ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view2)).setText(holder.textView2.getText());

                dialogPlus.show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return _DisplayApplicationInfo.size();
        }

        class NormalTextViewHolder extends RecyclerView.ViewHolder {
            private TextView  textView1;
            private TextView  textView2;
            private ImageView imageView;

            NormalTextViewHolder(View itemView) {
                super(itemView);

                textView1 = (TextView) itemView.findViewById(R.id.text1);
                textView2 = (TextView) itemView.findViewById(R.id.text2);
                imageView = (ImageView) itemView.findViewById(R.id.image_view);
            }
        }
    }
}

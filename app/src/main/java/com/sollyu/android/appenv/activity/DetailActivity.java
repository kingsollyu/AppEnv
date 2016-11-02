package com.sollyu.android.appenv.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.LibSuHelper;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.PhoneHelper;
import com.sollyu.android.appenv.helper.RandomHelper;
import com.sollyu.android.appenv.helper.TokenHelper;
import com.sollyu.android.appenv.helper.XposedSharedPreferencesHelper;
import com.sollyu.android.appenv.module.AppInfo;
import com.sollyu.android.appenv.view.DetailItem;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private ApplicationInfo applicationInfo    = null;
    private Integer         activityResultCode = 0;

    private Handler uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        applicationInfo = getIntent().getParcelableExtra("applicationInfo");

        try {
            PhoneHelper.getInstance().reload(this);
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error);
            builder.setMessage("程序出现严重错误: \n" + Log.getStackTraceString(e));
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> DetailActivity.this.finish());
            builder.setCancelable(false);
            builder.create().show();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            switch (applicationInfo.packageName) {
                case "all":
                    getSupportActionBar().setTitle("全局拦截");
                    findViewById(R.id.menu_run_app).setEnabled(false);
                    findViewById(R.id.menu_clear_app).setEnabled(false);
                    findViewById(R.id.menu_force_stop).setEnabled(false);
                    break;
                case "user":
                    getSupportActionBar().setTitle("第三方拦截");
                    findViewById(R.id.menu_run_app).setEnabled(false);
                    findViewById(R.id.menu_clear_app).setEnabled(false);
                    findViewById(R.id.menu_force_stop).setEnabled(false);
                    break;
                default:
                    getSupportActionBar().setTitle(applicationInfo.loadLabel(getPackageManager()));
                    break;
            }
        }

        if (!OtherHelper.getInstance().isUserAppllication(applicationInfo)) {
            Snackbar.make(findViewById(R.id.content_main), "不建议修改系统应用，修改系统应用可能会手机无法开机", Snackbar.LENGTH_LONG).show();
        }

        if (applicationInfo.packageName.equals("com.sina.weibo")) {
            Snackbar.make(findViewById(R.id.content_main), "微博显示的机型有点少，如不能正常显示请更改再尝试", Snackbar.LENGTH_LONG).show();
        }
        if (applicationInfo.packageName.equals("com.qzone")) {
            Snackbar.make(findViewById(R.id.content_main), "如果您随意填写机型QQ空间将会把您填写的变成小写", Snackbar.LENGTH_LONG).show();
        }

        appInfoToUi(XposedSharedPreferencesHelper.getInstance().get(applicationInfo.packageName));
        getOverflowMenu();
    }

    //强制显示菜单中的更多按钮
    private void getOverflowMenu() {
        try {
            ViewConfiguration config       = ViewConfiguration.get(this);
            Field             menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            MobclickAgent.reportError(this, e);
        }
    }

    /**
     * 强制让目录显示图标
     *
     * @param featureId 1
     * @param menu      2
     * @return 1
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    MobclickAgent.reportError(this, e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 强制让目录显示图标
     *
     * @param view v
     * @param menu m
     * @return b
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    MobclickAgent.reportError(this, e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
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
        DetailActivity.this.setResult(activityResultCode);
        DetailActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void appInfoToUi(AppInfo appInfo) {
        if (appInfo == null)
            return;

        DetailItem manufacturer          = (DetailItem) findViewById(R.id.manufacturer);
        DetailItem model                 = (DetailItem) findViewById(R.id.model);
        DetailItem serial                = (DetailItem) findViewById(R.id.serial);
        DetailItem phone_number          = (DetailItem) findViewById(R.id.phone_number);
        DetailItem phone_network_type    = (DetailItem) findViewById(R.id.phone_network_type);
        DetailItem phone_device_id       = (DetailItem) findViewById(R.id.phone_device_id);
        DetailItem sim_serial_number     = (DetailItem) findViewById(R.id.sim_serial_number);
        DetailItem wifi_info_ssid        = (DetailItem) findViewById(R.id.wifi_info_ssid);
        DetailItem wifi_info_mac_address = (DetailItem) findViewById(R.id.wifi_info_mac_address);

        manufacturer.getEditText().setText(appInfo.buildManufacturer);
        model.getEditText().setText(appInfo.buildModel);
        serial.getEditText().setText(appInfo.buildSerial);
        phone_number.getEditText().setText(appInfo.telephonyGetLine1Number);
        phone_network_type.getEditText().setText(appInfo.telephonyGetNetworkType);
        phone_device_id.getEditText().setText(appInfo.telephonyGetDeviceId);
        sim_serial_number.getEditText().setText(appInfo.telephonyGetSimSerialNumber);
        wifi_info_ssid.getEditText().setText(appInfo.wifiInfoGetSSID);
        wifi_info_mac_address.getEditText().setText(appInfo.wifiInfoGetMacAddress);
    }

    private AppInfo uiToAppInfo() {
        DetailItem manufacturer          = (DetailItem) findViewById(R.id.manufacturer);
        DetailItem model                 = (DetailItem) findViewById(R.id.model);
        DetailItem serial                = (DetailItem) findViewById(R.id.serial);
        DetailItem phone_number          = (DetailItem) findViewById(R.id.phone_number);
        DetailItem phone_network_type    = (DetailItem) findViewById(R.id.phone_network_type);
        DetailItem phone_device_id       = (DetailItem) findViewById(R.id.phone_device_id);
        DetailItem sim_serial_number     = (DetailItem) findViewById(R.id.sim_serial_number);
        DetailItem wifi_info_ssid        = (DetailItem) findViewById(R.id.wifi_info_ssid);
        DetailItem wifi_info_mac_address = (DetailItem) findViewById(R.id.wifi_info_mac_address);

        AppInfo appInfo = new AppInfo();
        appInfo.buildManufacturer = manufacturer.getEditText().getText().toString();
        appInfo.buildModel = model.getEditText().getText().toString();
        appInfo.buildSerial = serial.getEditText().getText().toString();
        appInfo.telephonyGetLine1Number = phone_number.getEditText().getText().toString();
        appInfo.telephonyGetNetworkType = phone_network_type.getEditText().getText().toString();
        appInfo.telephonyGetDeviceId = phone_device_id.getEditText().getText().toString();
        appInfo.telephonyGetSimSerialNumber = sim_serial_number.getEditText().getText().toString();
        appInfo.wifiInfoGetSSID = wifi_info_ssid.getEditText().getText().toString();
        appInfo.wifiInfoGetMacAddress = wifi_info_mac_address.getEditText().getText().toString();

        return appInfo;
    }

    public void onClickRandomAll(View view) {
        appInfoToUi(RandomHelper.getInstance().randomAll());
    }

    public void onClickRunApp(View view) {
        LibSuHelper.getInstance().addCommand("monkey -p " + applicationInfo.packageName + " -c android.intent.category.LAUNCHER 1", 0, (commandCode, exitCode, output) -> {
            if (exitCode != 0) {
                Snackbar.make(view, getString(R.string.start_app_error) + exitCode, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void onClickClearApp(View view) {
        LibSuHelper.getInstance().addCommand("pm clear " + applicationInfo.packageName, 0, (commandCode, exitCode, output) -> {
            if (exitCode != 0)
                Snackbar.make(view, getString(R.string.wipe_data_error) + exitCode, Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(view, R.string.wipe_data_sccess, Snackbar.LENGTH_LONG).show();
        });
    }

    public void onClickForceStopApp(View view) {
        LibSuHelper.getInstance().addCommand("am force-stop " + applicationInfo.packageName, 0, (commandCode, exitCode, output) -> {
            if (exitCode != 0)
                Snackbar.make(view, getString(R.string.force_stop_error) + exitCode, Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(view, R.string.force_stop_success, Snackbar.LENGTH_LONG).show();
        });
    }

    public void onClickSaveConfig(View view) {
        activityResultCode = 1;
        XposedSharedPreferencesHelper.getInstance().set(applicationInfo.packageName, uiToAppInfo());
        Snackbar.make(view, R.string.save_config_success, Snackbar.LENGTH_LONG).show();
    }

    public void onClickManufacturer(View view) {
        ArrayList<String> selectStringArrayList = PhoneHelper.getInstance().getManufacturerList();

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ListHolder())
                .setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, selectStringArrayList))
                .setOnItemClickListener((dialog, item, view1, position) -> {
                    DetailItem detailItem = (DetailItem) view;
                    detailItem.getEditText().setText(selectStringArrayList.get(position));
                    dialog.dismiss();
                })
                .setExpanded(true)
                .create();

        ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(R.string.manufacturer);

        dialogPlus.show();
    }

    public void onClickModel(View view) {
        DetailItem              buildManufacturer = (DetailItem) findViewById(R.id.manufacturer);
        HashMap<String, String> hashMap           = PhoneHelper.getInstance().getModelList(buildManufacturer.getEditText().getText().toString());

        ArrayList<String> selectStringArrayList = new ArrayList<>();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            if (!selectStringArrayList.contains(entry.getKey())) {
                selectStringArrayList.add(entry.getKey());
            }
        }
        Collections.sort(selectStringArrayList, String.CASE_INSENSITIVE_ORDER);

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ListHolder())
                .setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, selectStringArrayList))
                .setOnItemClickListener((dialog, item, view1, position) -> {
                    DetailItem detailItem = (DetailItem) view;
                    detailItem.getEditText().setText(hashMap.get(selectStringArrayList.get(position)));
                    dialog.dismiss();
                })
                .setExpanded(true)
                .create();

        ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(R.string.manufacturer);

        dialogPlus.show();
    }

    public void onClickSerial(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomBuildSerial());
    }

    public void onClickLineNumber(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomTelephonyGetLine1Number());
    }

    public void onClickNetworkType(View view) {
        HashMap<String, Object> hashMap = RandomHelper.getInstance().getTelephonyGetNetworkTypeList();

        ArrayList<String> displayArrayList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            displayArrayList.add(entry.getKey());
        }

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ListHolder())
                .setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, displayArrayList))
                .setOnItemClickListener((dialog, item, view1, position) -> {
                    DetailItem detailItem = (DetailItem) view;
                    detailItem.getEditText().setText(String.valueOf(hashMap.get(displayArrayList.get(position))));
                    dialog.dismiss();
                })
                .setExpanded(true)
                .create();

        ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(R.string.phone_network_type);

        dialogPlus.show();
    }

    public void onClickDeviceId(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomTelephonyGetDeviceId());
    }

    public void onClickSimSerialNumber(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomTelephonySimSerialNumber());
    }

    public void onClickWifiInfoSSID(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomWifiInfoSSID());
    }

    public void onClickWifiInfoMacAddress(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomWifiInfoMacAddress());
    }

    public void onMenuClearConfig(MenuItem item) {
        XposedSharedPreferencesHelper.getInstance().remove(applicationInfo.packageName);
        activityResultCode = 1;
        DetailActivity.this.setResult(activityResultCode);
        DetailActivity.this.finish();
    }

    public void onMenuRemoteRandom(MenuItem item) {
        if (!TokenHelper.getInstance().isActivate()) {
            DetailActivity.this.startActivity(new Intent(DetailActivity.this, LoginActivity.class));
            return;
        }

        View           view           = findViewById(R.id.fab);
        ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), getString(R.string.wait), getString(R.string.processing), true);

        new Thread(() -> {
            try {
                TokenHelper.ServerResult serverResult = TokenHelper.getInstance().random();

                progressDialog.dismiss();
                if (serverResult.getRet() != 200) {
                    throw new RuntimeException(serverResult.getMsg());
                }

                AppInfo appInfo = new AppInfo();
                appInfo.buildManufacturer = serverResult.getDataJson().getString("buildManufacturer");
                appInfo.buildModel = serverResult.getDataJson().getString("buildModel");
                appInfo.buildSerial = serverResult.getDataJson().getString("buildSerial");
                appInfo.telephonyGetLine1Number = serverResult.getDataJson().getString("telephonyGetLine1Number");
                appInfo.telephonyGetSimOperator = serverResult.getDataJson().getString("telephonyGetSimOperator");
                appInfo.telephonyGetNetworkType = serverResult.getDataJson().getString("telephonyGetNetworkType");
                appInfo.telephonyGetDeviceId = serverResult.getDataJson().getString("telephonyGetDeviceId");
                appInfo.telephonyGetSimSerialNumber = serverResult.getDataJson().getString("telephonyGetSimSerialNumber");
                appInfo.wifiInfoGetSSID = serverResult.getDataJson().getString("wifiInfoGetSSID");
                appInfo.wifiInfoGetMacAddress = serverResult.getDataJson().getString("wifiInfoGetMacAddress");
                appInfo.settingsSecureAndroidId = serverResult.getDataJson().getString("settingsSecureAndroidId");
                appInfo.webUserAgent = serverResult.getDataJson().getString("webUserAgent");
                appInfo.displayDip = serverResult.getDataJson().getString("displayDip");

                uiHandler.post(() -> appInfoToUi(appInfo));
                Snackbar.make(view, "远程随机成功！可用点数 -2！", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                TokenHelper.getInstance().setActivate(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("错误");
                builder.setMessage(e.getMessage());
                builder.setPositiveButton("重新输入激活码", (dialog, which) -> startActivityForResult(new Intent(DetailActivity.this, LoginActivity.class), 0));
                builder.setNegativeButton("取消", null);
                builder.setCancelable(false);
                uiHandler.post(() -> builder.create().show());
            }

        }).start();
    }
}

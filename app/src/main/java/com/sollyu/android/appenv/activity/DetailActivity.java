package com.sollyu.android.appenv.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.sollyu.android.appenv.R;
import com.sollyu.android.appenv.helper.LibSuHelper;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.PhoneHelper;
import com.sollyu.android.appenv.helper.RandomHelper;
import com.sollyu.android.appenv.helper.SolutionHelper;
import com.sollyu.android.appenv.helper.TokenHelper;
import com.sollyu.android.appenv.helper.XposedSharedPreferencesHelper;
import com.sollyu.android.appenv.module.AppInfo;
import com.sollyu.android.appenv.view.DetailItem;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import eu.chainfire.libsuperuser.Shell;

@ContentView(R.layout.activity_detail)
public class DetailActivity extends BaseActivity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 45;

    private ApplicationInfo applicationInfo                      = null;
    private Integer         activityResultCode                   = 0;
    private Boolean         wipeDataConfirm                      = false;

    private Handler uiHandler = new Handler();

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;

    @ViewInject(R.id.content_detail)
    private ScrollView mDetailContent;

    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            switch (applicationInfo.packageName) {
                case XposedSharedPreferencesHelper.KEY_ALL:
                    getSupportActionBar().setTitle(R.string.hook_all);
                    findViewById(R.id.menu_run_app).setEnabled(false);
                    findViewById(R.id.menu_clear_app).setEnabled(false);
                    findViewById(R.id.menu_force_stop).setEnabled(false);
                    break;
                case XposedSharedPreferencesHelper.KEY_USER:
                    getSupportActionBar().setTitle(R.string.hook_user);
                    findViewById(R.id.menu_run_app).setEnabled(false);
                    findViewById(R.id.menu_clear_app).setEnabled(false);
                    findViewById(R.id.menu_force_stop).setEnabled(false);
                    break;
                default:
                    getSupportActionBar().setTitle(applicationInfo.loadLabel(getPackageManager()));
                    break;
            }
        }

        getOverflowMenu();
    }

    @Override
    protected void initData() {
        applicationInfo = getIntent().getParcelableExtra("applicationInfo");

        try {
            PhoneHelper.getInstance().reload(this);
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error);
            builder.setMessage("程序出现严重错误: \n" + Log.getStackTraceString(e));
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
            return;
        }

        if (!OtherHelper.getInstance().isUserAppllication(applicationInfo)) {
            Snackbar.make(findViewById(R.id.content_main), "不建议修改系统应用，修改系统应用可能会手机无法启动", Snackbar.LENGTH_LONG).show();
        }

        if (applicationInfo.packageName.equals("com.sina.weibo")) {
            Snackbar.make(findViewById(R.id.content_main), "微博显示的机型有点少，如不能正常显示请更改再尝试", Snackbar.LENGTH_LONG).show();
        }
        if (applicationInfo.packageName.equals("com.qzone")) {
            Snackbar.make(findViewById(R.id.content_main), "如果您随意填写机型QQ空间将会把您填写的变成小写", Snackbar.LENGTH_LONG).show();
        }

        appInfoToUi(XposedSharedPreferencesHelper.getInstance().get(applicationInfo.packageName));
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

        DetailItem manufacturer            = (DetailItem) findViewById(R.id.manufacturer);
        DetailItem model                   = (DetailItem) findViewById(R.id.model);
        DetailItem serial                  = (DetailItem) findViewById(R.id.serial);
        DetailItem versionName             = (DetailItem) findViewById(R.id.versionName);
        DetailItem phone_number            = (DetailItem) findViewById(R.id.phone_number);
        DetailItem phone_network_type      = (DetailItem) findViewById(R.id.phone_network_type);
        DetailItem phone_device_id         = (DetailItem) findViewById(R.id.phone_device_id);
        DetailItem sim_serial_number       = (DetailItem) findViewById(R.id.sim_serial_number);
        DetailItem sim_subscriber_id       = (DetailItem) findViewById(R.id.sim_subscriber_id);
        DetailItem wifi_info_ssid          = (DetailItem) findViewById(R.id.wifi_info_ssid);
        DetailItem wifi_info_mac_address   = (DetailItem) findViewById(R.id.wifi_info_mac_address);
        DetailItem settingsSecureAndroidId = (DetailItem) findViewById(R.id.settingsSecureAndroidId);

        manufacturer.getEditText()           .setText(appInfo.buildManufacturer);
        model.getEditText()                  .setText(appInfo.buildModel);
        serial.getEditText()                 .setText(appInfo.buildSerial);
        versionName.getEditText()            .setText(appInfo.buildVersionRelease);
        phone_number.getEditText()           .setText(appInfo.telephonyGetLine1Number);
        phone_network_type.getEditText()     .setText(appInfo.telephonyGetNetworkType);
        phone_device_id.getEditText()        .setText(appInfo.telephonyGetDeviceId);
        sim_serial_number.getEditText()      .setText(appInfo.telephonyGetSimSerialNumber);
        sim_subscriber_id.getEditText()      .setText(appInfo.telephonyGetSubscriberId);
        wifi_info_ssid.getEditText()         .setText(appInfo.wifiInfoGetSSID);
        wifi_info_mac_address.getEditText()  .setText(appInfo.wifiInfoGetMacAddress);
        settingsSecureAndroidId.getEditText().setText(appInfo.settingsSecureAndroidId);
    }

    private AppInfo uiToAppInfo() {
        DetailItem manufacturer            = (DetailItem) findViewById(R.id.manufacturer);
        DetailItem model                   = (DetailItem) findViewById(R.id.model);
        DetailItem serial                  = (DetailItem) findViewById(R.id.serial);
        DetailItem versionName             = (DetailItem) findViewById(R.id.versionName);
        DetailItem phone_number            = (DetailItem) findViewById(R.id.phone_number);
        DetailItem phone_network_type      = (DetailItem) findViewById(R.id.phone_network_type);
        DetailItem phone_device_id         = (DetailItem) findViewById(R.id.phone_device_id);
        DetailItem sim_serial_number       = (DetailItem) findViewById(R.id.sim_serial_number);
        DetailItem sim_subscriber_id       = (DetailItem) findViewById(R.id.sim_subscriber_id);
        DetailItem wifi_info_ssid          = (DetailItem) findViewById(R.id.wifi_info_ssid);
        DetailItem wifi_info_mac_address   = (DetailItem) findViewById(R.id.wifi_info_mac_address);
        DetailItem settingsSecureAndroidId = (DetailItem) findViewById(R.id.settingsSecureAndroidId);

        AppInfo appInfo = new AppInfo();
        appInfo.buildManufacturer           = manufacturer.getEditText().getText().toString();
        appInfo.buildModel                  = model.getEditText().getText().toString();
        appInfo.buildSerial                 = serial.getEditText().getText().toString();
        appInfo.buildVersionRelease         = versionName.getEditText().getText().toString();
        appInfo.telephonyGetLine1Number     = phone_number.getEditText().getText().toString();
        appInfo.telephonyGetNetworkType     = phone_network_type.getEditText().getText().toString();
        appInfo.telephonyGetDeviceId        = phone_device_id.getEditText().getText().toString();
        appInfo.telephonyGetSimSerialNumber = sim_serial_number.getEditText().getText().toString();
        appInfo.telephonyGetSubscriberId    = sim_subscriber_id.getEditText().getText().toString();
        appInfo.wifiInfoGetSSID             = wifi_info_ssid.getEditText().getText().toString();
        appInfo.wifiInfoGetMacAddress       = wifi_info_mac_address.getEditText().getText().toString();
        appInfo.settingsSecureAndroidId     = settingsSecureAndroidId.getEditText().getText().toString();

        return appInfo;
    }

    public void onClickRandomAll(View view) {
        appInfoToUi(RandomHelper.getInstance().randomAll());
    }

    public void onClickRunApp(View view) {
        LibSuHelper.getInstance().addCommand("monkey -p " + applicationInfo.packageName + " -c android.intent.category.LAUNCHER 1", 0, new Shell.OnCommandResultListener() {
            @Override
            public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                if (exitCode != 0) {
                    Snackbar.make(mDetailContent, getString(R.string.start_app_error) + exitCode, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onClickClearApp(View view) {
        if (wipeDataConfirm) {
            wipeDataConfirm = false;
            LibSuHelper.getInstance().addCommand("pm clear " + applicationInfo.packageName, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                    if (exitCode != 0)
                        Snackbar.make(mDetailContent, getString(R.string.wipe_data_error) + exitCode, Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(mDetailContent, R.string.wipe_data_success, Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            wipeDataConfirm = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (wipeDataConfirm) {
                        Snackbar.make(mDetailContent, "清除数据为敏感操作，请在2秒内连续点击次。", Snackbar.LENGTH_LONG).show();
                    }
                    wipeDataConfirm = false;
                }
            }, 2000);
        }
    }

    public void onClickForceStopApp(View view) {
        LibSuHelper.getInstance().addCommand("am force-stop " + applicationInfo.packageName, 0, new Shell.OnCommandResultListener() {
            @Override
            public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                if (exitCode != 0)
                    Snackbar.make(mDetailContent, getString(R.string.force_stop_error) + exitCode, Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(mDetailContent, R.string.force_stop_success, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void onClickSaveConfig(View view) {
        activityResultCode = 1;
        XposedSharedPreferencesHelper.getInstance().set(applicationInfo.packageName, uiToAppInfo());
        Snackbar.make(view, R.string.save_config_success, Snackbar.LENGTH_LONG).show();
    }

    public void onClickManufacturer(View view) {
        final ArrayList<String> selectStringArrayList = PhoneHelper.getInstance().getManufacturerList();

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ListHolder())
                .setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, selectStringArrayList))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        DetailItem detailItem = (DetailItem) view;
                        detailItem.getEditText().setText(selectStringArrayList.get(position));
                        dialog.dismiss();
                    }
                })
                .setExpanded(true)
                .create();

        ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(R.string.manufacturer);

        dialogPlus.show();
    }

    public void onClickModel(View view) {
        DetailItem                    buildManufacturer = (DetailItem) findViewById(R.id.manufacturer);
        final HashMap<String, String> hashMap           = PhoneHelper.getInstance().getModelList(buildManufacturer.getEditText().getText().toString());

        final ArrayList<String> selectStringArrayList = new ArrayList<>();
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
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        DetailItem detailItem = (DetailItem) view;
                        detailItem.getEditText().setText(hashMap.get(selectStringArrayList.get(position)));
                        dialog.dismiss();
                    }
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
        final HashMap<String, Object> hashMap = RandomHelper.getInstance().getTelephonyGetNetworkTypeList();
        final ArrayList<String> displayArrayList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            displayArrayList.add(entry.getKey());
        }

        Collections.sort(displayArrayList, String.CASE_INSENSITIVE_ORDER);

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ListHolder())
                .setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, displayArrayList))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        DetailItem detailItem = (DetailItem) view;
                        detailItem.getEditText().setText(String.valueOf(hashMap.get(displayArrayList.get(position))));
                        dialog.dismiss();
                    }
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

        AsyncTask<Object, Object, Object> asyncTask = new AsyncTask<Object, Object, Object>() {
            private ProgressDialog progressDialog = null;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(getActivity(), getString(R.string.wait), getString(R.string.processing), true);
            }

            @Override
            protected Object doInBackground(Object... params) {
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
                    appInfo.settingsSecureAndroidId = serverResult.getDataJson().getString("settingsSecureAndroidId");

                    return appInfo;

                } catch (Exception e) {
                    TokenHelper.getInstance().setActivate(false);
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                if (o != null && o instanceof AppInfo) {
                    appInfoToUi((AppInfo) o);
                    Snackbar.make(mDetailContent, "远程随机成功！可用点数 -2！", Snackbar.LENGTH_LONG).show();
                }

                if (o != null && o instanceof Throwable) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    builder.setTitle(R.string.error);
                    builder.setMessage(((Throwable) o).getMessage());
                    builder.setPositiveButton("重新输入激活码", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(DetailActivity.this, LoginActivity.class), 0);
                        }
                    });;
                    builder.setNegativeButton("取消", null);
                    builder.setCancelable(false);
                    builder.create().show();
                }
            }
        };
        asyncTask.execute();
    }

    public void onMenuSaveSolution(MenuItem item) {
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }

        final AppInfo appInfo = uiToAppInfo();
        if (appInfo.isEmpty()) {
            Snackbar.make(findViewById(R.id.content_detail), "您没有界面中填写任何的内容，保存有何用。", Snackbar.LENGTH_LONG).show();
            return;
        }

        final EditText editText = new EditText(DetailActivity.this);
        editText.setHint("保存方案的名称");

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setIcon(R.drawable.ic_save);
        builder.setTitle("保存方案");
        builder.setView(editText);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (editText.getText().toString().length() == 0)
                        throw new RuntimeException("保存方案名称为空");

                    SolutionHelper.getInstance().put(editText.getText().toString(), appInfo);
                    Snackbar.make(findViewById(R.id.content_detail), "保存方案成功: " + editText.getText().toString(), Snackbar.LENGTH_LONG).show();
                } catch (Throwable throwable) {
                    Snackbar.make(findViewById(R.id.content_detail), "保存方案失败: " + throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    public void onMenuLoadSolution(MenuItem item) {
        final View              view             = findViewById(R.id.content_detail);
        final ArrayList<String> displayArrayList = SolutionHelper.getInstance().list();
        if (displayArrayList.size() == 0) {
            Snackbar.make(view, "没有任何方案", Snackbar.LENGTH_LONG).show();
            return;
        }

        Collections.sort(displayArrayList, String.CASE_INSENSITIVE_ORDER);

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ListHolder())
                .setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, displayArrayList))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        appInfoToUi(SolutionHelper.getInstance().get(displayArrayList.get(position)));
                    }
                })
                .setExpanded(true)
                .create();

        ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(R.string.load_solution);

        dialogPlus.show();
    }

    public void onMenuDeleteSolution(MenuItem item) {
        final View              view             = findViewById(R.id.content_detail);
        final ArrayList<String> displayArrayList = SolutionHelper.getInstance().list();
        if (displayArrayList.size() == 0) {
            Snackbar.make(view, "没有任何方案", Snackbar.LENGTH_LONG).show();
            return;
        }

        Collections.sort(displayArrayList, String.CASE_INSENSITIVE_ORDER);

        DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                .setHeader(R.layout.dialog_plus_header)
                .setContentHolder(new ListHolder())
                .setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, displayArrayList))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, final View view, final int position) {
                        dialog.dismiss();
                        SolutionHelper.getInstance().remove(displayArrayList.get(position));
                        uiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(mDetailContent, "删除方案成功: " + displayArrayList.get(position), Snackbar.LENGTH_LONG).show();
                            }
                        }, 250);
                    }
                })
                .setExpanded(true)
                .create();

        ((TextView) dialogPlus.getHeaderView().findViewById(R.id.text_view1)).setText(R.string.delete_solution);

        dialogPlus.show();

    }

    public void onMenuDownloadSolution(MenuItem item) {
    }

    public void onClickAndroidId(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomAndroidId());
    }

    public void onClickVersionName(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomBuildVersionName());
    }

    public void onClickSimSubscriberId(View view) {
        DetailItem detailItem = (DetailItem) view;
        detailItem.getEditText().setText(RandomHelper.getInstance().randomSimSubscriberId());
    }
}

package com.sollyu.android.appenv.helper;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sollyu.android.appenv.BuildConfig;
import com.sollyu.android.appenv.MainApplication;
import com.sollyu.android.appenv.module.AppInfo;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Field;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * 作者: Sollyu
 * 时间: 16/10/31
 * 联系: sollyu@qq.com
 * 说明:
 */
public class TokenHelper {
    private static final String       TAG        = "AppEnv";
    private static final TokenHelper  instance   = new TokenHelper();
    private static final ServerResult TOKEN_NULL = new ServerResult("{\"ret\":400,\"msg\":\"Token is null.\",\"data\":\"\"}");

    private OkHttpClient okHttpClient = new OkHttpClient();

    private TokenHelper() {
    }

    public static TokenHelper getInstance() {
        return instance;
    }

    public String getToken() {
        String token = PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance()).getString("TOKEN", null);
        if (token == null || token.isEmpty())
            throw new RuntimeException("Token is null");
        return token;
    }

    public void setToken(String token) {
        getDefaultSharedPreferences(MainApplication.getInstance()).edit().putString("TOKEN", token).apply();
    }

    public Boolean isActivate() {
        return PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance()).getBoolean("ACTIVATE", false);
    }

    public void setActivate(Boolean activate) {
        getDefaultSharedPreferences(MainApplication.getInstance()).edit().putBoolean("TOKEN", activate).apply();
    }

    public ServerResult info(String token) {
        try {
            RequestBody requestBody = new FormEncodingBuilder()
                    .add("service", "Default.Info")
                    .add("token", token)
                    .build();

            return new ServerResult(okHttpClient.newCall(new Request.Builder().url(BuildConfig.SERVER_HOST).post(requestBody).build()).execute().body().string());
        } catch (Exception e) {
            Log.d(TAG, "info: " + e.getLocalizedMessage(), e);
            return new ServerResult(400, e.getLocalizedMessage(), null);
        }
    }

    public ServerResult devices(Context context) {
        try {
            AppInfo          appInfo          = new AppInfo();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            WifiInfo         wifiInfo         = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();

            appInfo.buildDisplay = Build.DISPLAY;
            appInfo.buildProduct = Build.PRODUCT;
            appInfo.buildDevice = Build.DEVICE;
            appInfo.buildBoard = Build.BOARD;
            appInfo.buildManufacturer = Build.MANUFACTURER;
            appInfo.buildBrand = Build.BRAND;
            appInfo.buildModel = Build.MODEL;
            appInfo.buildBootloader = Build.BOOTLOADER;
            appInfo.buildRadio = Build.RADIO;
            appInfo.buildHardware = Build.HARDWARE;
            appInfo.buildSerial = Build.SERIAL;
            appInfo.buildFingerprint = Build.FINGERPRINT;

            appInfo.buildVersionIncremental = Build.VERSION.INCREMENTAL;
            appInfo.buildVersionRelease = Build.VERSION.RELEASE;
            appInfo.buildVersionSdk = Build.VERSION.SDK;
            appInfo.buildVersionCodeName = Build.VERSION.CODENAME;
            appInfo.buildVersionSdkInt = String.valueOf(Build.VERSION.SDK_INT);

            appInfo.telephonyGetDeviceId = telephonyManager.getDeviceId();
            appInfo.telephonyGetDeviceSoftwareVersion = telephonyManager.getDeviceSoftwareVersion();
            appInfo.telephonyGetLine1Number = telephonyManager.getLine1Number();
            appInfo.telephonyGetNetworkCountryISO = telephonyManager.getNetworkCountryIso();
            appInfo.telephonyGetNetworkOperator = telephonyManager.getNetworkOperator();
            appInfo.telephonyGetNetworkOperatorName = telephonyManager.getNetworkOperatorName();
            appInfo.telephonyGetSimCountryISO = telephonyManager.getSimCountryIso();
            appInfo.telephonyGetSimOperator = telephonyManager.getSimOperator();
            appInfo.telephonyGetSimOperatorName = telephonyManager.getSimOperatorName();
            appInfo.telephonyGetSimSerialNumber = telephonyManager.getSimSerialNumber();
            appInfo.telephonyGetSubscriberId = telephonyManager.getSubscriberId();
            appInfo.telephonyGetPhoneType = String.valueOf(telephonyManager.getPhoneType());
            appInfo.telephonyGetNetworkType = String.valueOf(telephonyManager.getNetworkType());
            appInfo.telephonyGetSimState = String.valueOf(telephonyManager.getSimState());

            appInfo.wifiInfoGetSSID = wifiInfo.getSSID();
            appInfo.wifiInfoGetBSSID = wifiInfo.getBSSID();
            appInfo.wifiInfoGetMacAddress = wifiInfo.getMacAddress();
            appInfo.wifiInfoGetNetworkId = String.valueOf(wifiInfo.getNetworkId());
            appInfo.wifiInfoGetIpAddress = OtherHelper.getInstance().intToIp(wifiInfo.getIpAddress());

            appInfo.settingsSecureAndroidId = Settings.Secure.getString(context.getContentResolver(), "android_id");

            appInfo.systemCpuInfo = FileUtils.readFileToString(new File("/proc/cpuinfo"));

            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            formEncodingBuilder.add("service", "Default.Devices");
            formEncodingBuilder.add("token", "INIT_TOKEN");

            for (Field field : appInfo.getClass().getFields()) {
                try {
                    if (field.get(appInfo) != null)
                        formEncodingBuilder.add(field.getName(), field.get(appInfo).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            return new ServerResult(okHttpClient.newCall(new Request.Builder().url(BuildConfig.SERVER_HOST).post(formEncodingBuilder.build()).build()).execute().body().string());
        } catch (Exception e) {
            Log.d(TAG, "devices: " + e.getLocalizedMessage(), e);
            return new ServerResult(400, e.getLocalizedMessage(), "{}");
        }
    }


    public static class ServerResult {
        private int        ret           = 400;
        private String     msg           = "unknown";
        private String     data          = "";
        private JSONObject dataJson      = null;
        private JSONObject rawJsonObject = null;

        public ServerResult(String rawString) {
            rawJsonObject = JSONObject.parseObject(rawString);
            if (rawJsonObject == null) {
                ret = 400;
                msg = "error json data";
            } else {
                ret = rawJsonObject.getInteger("ret");
                msg = rawJsonObject.getString("msg");
                data = rawJsonObject.getString("data");

                if (rawJsonObject.get("data") instanceof JSONObject)
                    dataJson = rawJsonObject.getJSONObject("data");
            }
        }

        public ServerResult(int code, String msg, String data) {
            this.ret = code;
            this.msg = msg;
            this.data = data;
        }

        public int getRet() {
            return ret;
        }

        public String getMsg() {
            return msg;
        }

        public String getData() {
            return data;
        }

        public JSONObject getDataJson() {
            return dataJson;
        }

        public JSONObject getRawJsonObject() {
            return rawJsonObject;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

}

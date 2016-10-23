package com.sollyu.android.appenv.helper;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sollyu.android.appenv.BuildConfig;
import com.sollyu.android.appenv.module.AppInfo;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;


/**
 * 作者: Sollyu
 * 时间: 16/10/23
 * 联系: sollyu@qq.com
 * 说明:
 */
public class ServerHelper {
    private static final ServerHelper instance = new ServerHelper();

    private OkHttpClient okHttpClient = new OkHttpClient();

    private ServerHelper() {
    }

    public static ServerHelper getInstance() {
        return instance;
    }

    public Response devices(Context context) {
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

            return new Response(okHttpClient.newCall(new Request.Builder().url(BuildConfig.SERVER_HOST).post(formEncodingBuilder.build()).build()).execute().body().string());
        } catch (IOException e) {
            MobclickAgent.reportError(context, e);
            return new Response(400, e.getLocalizedMessage(), "{}");
        }
    }

    public class Response {
        private int        code     = 400;
        private String     msg      = null;
        private String     data     = null;
        private JSONObject dataJson = null;

        public Response(String jsonString) {
            JSONObject jsonObject = JSON.parseObject(jsonString);

            code = jsonObject.getInteger("code");
            msg = jsonObject.getString("msg");
            data = jsonObject.getString("data");
            dataJson = jsonObject.getJSONObject("data");
        }

        public Response(int code, String msg, String data) {
            this.code = code;
            this.msg = msg;
            this.data = data;

            dataJson = JSON.parseObject(data);
        }

        public int getCode() {
            return code;
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
    }

}

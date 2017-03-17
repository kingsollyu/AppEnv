package com.sollyu.android.appenv;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.XposedHookHelper;
import com.sollyu.android.appenv.helper.XposedSharedPreferencesHelper;
import com.sollyu.android.appenv.module.AppInfo;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 作者: Sollyu
 * 时间: 16/11/1
 * 联系: sollyu@qqom
 * 说明:
 */
public class MainXposed implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final String TAG = "AppEnv";

    private XSharedPreferences xSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, "XPOSED");

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        try {
            if (loadPackageParam.packageName.equals("android") || loadPackageParam.packageName.equals("de.robv.android.xposed.installer")) {
                return;
            }

            if (loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) {
                XposedHelpers.findAndHookMethod("com.sollyu.android.appenv.MainApplication", loadPackageParam.classLoader, "isXposedWork", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        return true;
                    }
                });
                return;
            }

            xSharedPreferences.reload();

            AppInfo packageAppInfo = JSON.toJavaObject(JSON.parseObject(xSharedPreferences.getString(loadPackageParam.packageName, "{}")), AppInfo.class);

            // 当前应用是一个用户程序
            if (OtherHelper.getInstance().isUserAppllication(loadPackageParam.appInfo)) {
                packageAppInfo.merge(JSON.toJavaObject(JSON.parseObject(xSharedPreferences.getString(XposedSharedPreferencesHelper.KEY_USER, "{}")), AppInfo.class));
            }

            // 最后合并全部拦截配置
            packageAppInfo.merge(JSON.toJavaObject(JSON.parseObject(xSharedPreferences.getString(XposedSharedPreferencesHelper.KEY_ALL, "{}")), AppInfo.class));

            Log.d(TAG, "handleLoadPackage: " + packageAppInfo);

            // 拦截应用
            if (!OtherHelper.getInstance().isNull(packageAppInfo.buildManufacturer)) {
                XposedHookHelper.getInstances(loadPackageParam).Build.MANUFACTURER(packageAppInfo.buildManufacturer);
                XposedHookHelper.getInstances(loadPackageParam).Build.BRAND(packageAppInfo.buildManufacturer);
                XposedHookHelper.getInstances(loadPackageParam).Build.PRODUCT(packageAppInfo.buildManufacturer);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.buildModel)) {
                XposedHookHelper.getInstances(loadPackageParam).Build.MODEL(packageAppInfo.buildModel);
                XposedHookHelper.getInstances(loadPackageParam).Build.DEVICE(packageAppInfo.buildModel);
                XposedHookHelper.getInstances(loadPackageParam).Build.HARDWARE(packageAppInfo.buildModel);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.buildSerial)) {
                XposedHookHelper.getInstances(loadPackageParam).Build.SERIAL(packageAppInfo.buildSerial);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.buildVersionRelease)) {
                XposedHookHelper.getInstances(loadPackageParam).Build.Version.RELEASE(packageAppInfo.buildVersionRelease);
            }

            if (!OtherHelper.getInstance().isNull(packageAppInfo.telephonyGetLine1Number)) {
                XposedHookHelper.getInstances(loadPackageParam).Telephony.getLine1Number(packageAppInfo.telephonyGetLine1Number);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.telephonyGetSimOperatorName)) {
                XposedHookHelper.getInstances(loadPackageParam).Telephony.getSimOperatorName(packageAppInfo.telephonyGetSimOperatorName);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.telephonyGetPhoneType)) {
                // 拦截网络状态
                XposedBridge.hookAllMethods(NetworkInfo.class, "getType", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(packageAppInfo.telephonyGetPhoneType.equals("99") ? ConnectivityManager.TYPE_WIFI : ConnectivityManager.TYPE_MOBILE);
                    }
                });
                // 设置手机SIM网络状态
                if (!packageAppInfo.telephonyGetPhoneType.equals("99"))
                    XposedHookHelper.getInstances(loadPackageParam).Telephony.getPhoneType(Integer.parseInt(packageAppInfo.telephonyGetPhoneType));
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.telephonyGetDeviceId)) {
                XposedHookHelper.getInstances(loadPackageParam).Telephony.getDeviceId(packageAppInfo.telephonyGetDeviceId);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.telephonyGetSimSerialNumber)) {
                XposedHookHelper.getInstances(loadPackageParam).Telephony.getSimSerialNumber(packageAppInfo.telephonyGetSimSerialNumber);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.telephonyGetSubscriberId)) {
                XposedHookHelper.getInstances(loadPackageParam).Telephony.getSubscriberId(packageAppInfo.telephonyGetSubscriberId);
            }


            if (!OtherHelper.getInstance().isNull(packageAppInfo.wifiInfoGetSSID)) {
                XposedHookHelper.getInstances(loadPackageParam).Wifi.Info.getSSID(packageAppInfo.wifiInfoGetSSID);
            }
            if (!OtherHelper.getInstance().isNull(packageAppInfo.wifiInfoGetMacAddress)) {
                XposedHookHelper.getInstances(loadPackageParam).Wifi.Info.getMacAddress(packageAppInfo.wifiInfoGetMacAddress);
            }

            if (!OtherHelper.getInstance().isNull(packageAppInfo.settingsSecureAndroidId)) {
                XposedHookHelper.getInstances(loadPackageParam).Settings.System.getString(Settings.Secure.ANDROID_ID, packageAppInfo.settingsSecureAndroidId);
            }


        } catch (Throwable throwable) {
            Log.e(TAG, "handleLoadPackage: " + throwable.getLocalizedMessage(), throwable);
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        xSharedPreferences.makeWorldReadable();
    }
}

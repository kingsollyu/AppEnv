package com.sollyu.android.appenv;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sollyu.android.appenv.helper.OtherHelper;
import com.sollyu.android.appenv.helper.XposedHookHelper;
import com.sollyu.android.appenv.helper.XposedSharedPreferencesHelper;
import com.sollyu.android.appenv.module.AppInfo;

import java.util.Locale;

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

            final AppInfo packageAppInfo = JSON.toJavaObject(JSON.parseObject(xSharedPreferences.getString(loadPackageParam.packageName, "{}")), AppInfo.class);

            // 当前应用是一个用户程序
            if (OtherHelper.getInstance().isUserAppllication(loadPackageParam.appInfo)) {
                packageAppInfo.merge(JSON.toJavaObject(JSON.parseObject(xSharedPreferences.getString(XposedSharedPreferencesHelper.KEY_USER, "{}")), AppInfo.class));
            }

            // 最后合并全部拦截配置
            packageAppInfo.merge(JSON.toJavaObject(JSON.parseObject(xSharedPreferences.getString(XposedSharedPreferencesHelper.KEY_ALL, "{}")), AppInfo.class));

            // Log.d(TAG, "handleLoadPackage: " + packageAppInfo);

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


            if (!TextUtils.isEmpty(packageAppInfo.displayDip) || !TextUtils.isEmpty(packageAppInfo.systemLanguage)) {
                Class CompatibilityInfo = XposedHelpers.findClass("android.content.res.CompatibilityInfo", loadPackageParam.classLoader);
                XposedHelpers.findAndHookMethod(Resources.class, "updateConfiguration", Configuration.class, DisplayMetrics.class, CompatibilityInfo, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        Configuration configuration = null;

                        if (param.args[0] != null) {
                            configuration = new Configuration((Configuration) param.args[0]);
                        }

                        if (configuration == null) {
                            return;
                        }

                        // 拦截语言
                        if (!TextUtils.isEmpty(packageAppInfo.systemLanguage)) {
                            String[] localeParts = packageAppInfo.systemLanguage.split("_", 3);
                            String   language    = localeParts[0];
                            String   region      = (localeParts.length >= 2) ? localeParts[1] : "";
                            String   variant     = (localeParts.length >= 3) ? localeParts[2] : "";

                            Locale locale = new Locale(language, region, variant);
                            Locale.setDefault(locale);
                            configuration.locale = locale;
                            if (Build.VERSION.SDK_INT >= 17) {
                                configuration.setLayoutDirection(locale);
                            }
                        }

                        // 拦截DPI
                        if (!TextUtils.isEmpty(packageAppInfo.displayDip)) {
                            try {
                                int v0 = Integer.parseInt(packageAppInfo.displayDip);
                                DisplayMetrics displayMetrics;

                                if(param.args[1] != null) {
                                    displayMetrics = new DisplayMetrics();
                                    displayMetrics.setTo((DisplayMetrics) param.args[1]);
                                    param.args[1] = displayMetrics;
                                }
                                else {
                                    displayMetrics = ((Resources)param.thisObject).getDisplayMetrics();
                                }

                                if(v0 > 0) {
                                    displayMetrics.density = (((float)v0)) / 160f;
                                    displayMetrics.densityDpi = v0;
                                    if(Build.VERSION.SDK_INT >= 17) {
                                        XposedHelpers.setIntField(configuration, "densityDpi", v0);
                                    }
                                }
                            } catch (Throwable throwable) {
                                Log.e(TAG, throwable.getMessage(), throwable);
                            }
                        }

                        param.args[0] = configuration;
                    }
                });
            }

            XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    Log.d(TAG, activity.getResources().getConfiguration().locale.getLanguage());
                    super.afterHookedMethod(param);
                }
            });

        } catch (Throwable throwable) {
            Log.e(TAG, "handleLoadPackage: " + throwable.getLocalizedMessage(), throwable);
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        xSharedPreferences.makeWorldReadable();
    }
}

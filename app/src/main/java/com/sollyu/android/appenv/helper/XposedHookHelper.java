package com.sollyu.android.appenv.helper;

import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 作者: Sollyu
 * 时间: 16/10/20
 * 联系: sollyu@qq.com
 * 说明:
 */
public class XposedHookHelper {

    private static final String TAG = "AppEnv";

    public BuildImpl     Build     = null;
    public TelephonyImpl Telephony = null;
    public WifiImpl      Wifi      = null;
    public SettingsImpl  Settings  = null;

    private XC_LoadPackage.LoadPackageParam loadPackageParam = null;

    private static XposedHookHelper instances = null;

    private XposedHookHelper(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;

        Build     = new BuildImpl();
        Telephony = new TelephonyImpl();
        Wifi      = new WifiImpl();
        Settings  = new SettingsImpl();
    }

    public static XposedHookHelper getInstances(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (instances == null){
            instances = new XposedHookHelper(loadPackageParam);
        }
        return instances;
    }

    public XC_LoadPackage.LoadPackageParam getLoadPackageParam() {
        return loadPackageParam;
    }


    /**
     * android.os.Build 拦截
     */
    @SuppressWarnings("WeakerAccess")
    public class BuildImpl {
        public VersionImpl Version = new VersionImpl();

        public HashMap<String, String> hashMap = new HashMap<>();

        private BuildImpl() {
            XposedBridge.hookAllMethods(XposedHelpers.findClass("android.os.SystemProperties", getLoadPackageParam().classLoader), "get", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (hashMap.containsKey(param.args[0].toString())) {
                        param.setResult(hashMap.get(param.args[0].toString()));
                    }
                }
            });
        }

        public void MANUFACTURER(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "MANUFACTURER", value);
            hashMap.put("ro.product.manufacturer", value);
        }

        public void BRAND(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "BRAND", value);
            hashMap.put("ro.product.brand", value);
        }

        public void BOOTLOADER(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "BOOTLOADER", value);
            hashMap.put("ro.bootloader", value);
        }

        public void MODEL(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "MODEL", value);
            hashMap.put("ro.product.model", value);
        }

        public void DEVICE(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "DEVICE", value);
            hashMap.put("ro.product.device", value);
        }

        public void DISPLAY(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "DISPLAY", value);
            hashMap.put("ro.build.display.id", value);
        }

        public void PRODUCT(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "PRODUCT", value);
            hashMap.put("ro.product.name", value);
        }

        public void BOARD(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "BOARD", value);
            hashMap.put("ro.product.board", value);
        }

        public void HARDWARE(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "HARDWARE", value);
            hashMap.put("ro.hardware", value);
        }

        public void SERIAL(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "SERIAL", value);
            hashMap.put("ro.serialno", value);
        }

        public void TYPE(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "TYPE", value);
            hashMap.put("ro.build.type", value);
        }

        public void TAGS(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "TAGS", value);
            hashMap.put("ro.build.tags", value);
        }

        public void FINGERPRINT(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "FINGERPRINT", value);
            hashMap.put("ro.build.fingerprint", value);
        }

        public void USER(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "USER", value);
            hashMap.put("ro.build.user", value);
        }

        public void HOST(String value) {
            XposedHelpers.setStaticObjectField(android.os.Build.class, "HOST", value);
            hashMap.put("ro.build.host", value);
        }

        public class VersionImpl {
            private VersionImpl() {
            }

            public void INCREMENTAL(String value) {
                XposedHelpers.setStaticObjectField(android.os.Build.VERSION.class, "INCREMENTAL", value);
                hashMap.put("ro.build.version.incremental", value);
            }

            public void RELEASE(String value) {
                XposedHelpers.setStaticObjectField(android.os.Build.VERSION.class, "RELEASE", value);
                hashMap.put("ro.build.version.release", value);
            }

            public void BASE_OS(String value) {
                XposedHelpers.setStaticObjectField(android.os.Build.VERSION.class, "BASE_OS", value);
                hashMap.put("ro.build.version.base_os", value);
            }

            public void SECURITY_PATCH(String value) {
                XposedHelpers.setStaticObjectField(android.os.Build.VERSION.class, "SECURITY_PATCH", value);
                hashMap.put("ro.build.version.security_patch", value);
            }

            public void SDK(String value) {
                XposedHelpers.setStaticObjectField(android.os.Build.VERSION.class, "SDK", value);
                hashMap.put("ro.build.version.sdk", value);
            }

            public void CODENAME(String value) {
                XposedHelpers.setStaticObjectField(android.os.Build.VERSION.class, "CODENAME", value);
                hashMap.put("ro.build.version.all_codenames", value);
            }

            public void SDK_INT(int value) {
                XposedHelpers.setStaticIntField(android.os.Build.VERSION.class, "SDK_INT", value);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class TelephonyImpl {
        private TelephonyImpl() {
        }

        public void getDeviceId(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getDeviceId", new XC_ResultHook(value));
        }

        public void getDeviceSoftwareVersion(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getDeviceSoftwareVersion", new XC_ResultHook(value));
        }

        public void getLine1Number(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getLine1Number", new XC_ResultHook(value));
        }

        public void getMmsUAProfUrl(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getMmsUAProfUrl", new XC_ResultHook(value));
        }

        public void getMmsUserAgent(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getMmsUserAgent", new XC_ResultHook(value));
        }

        public void getNetworkCountryIso(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getNetworkCountryIso", new XC_ResultHook(value));
        }

        public void getNetworkOperator(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getNetworkOperator", new XC_ResultHook(value));
        }

        public void getNetworkOperatorName(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getNetworkOperatorName", new XC_ResultHook(value));
        }

        public void getSimCountryIso(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getSimCountryIso", new XC_ResultHook(value));
        }

        public void getSimOperator(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getSimOperator", new XC_ResultHook(value));
        }

        public void getSimOperatorName(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getSimOperatorName", new XC_ResultHook(value));
        }

        public void getSimSerialNumber(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getSimSerialNumber", new XC_ResultHook(value));
        }

        public void getSubscriberId(String value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getSubscriberId", new XC_ResultHook(value));
        }

        public void getCallState(int value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getCallState", new XC_ResultHook(value));
        }

        public void getDataNetworkType(int value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getDataNetworkType", new XC_ResultHook(value));
        }

        public void getDataState(int value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getDataState", new XC_ResultHook(value));
        }

        public void getNetworkType(int value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getNetworkType", new XC_ResultHook(value));
        }

        public void getPhoneCount(int value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getPhoneCount", new XC_ResultHook(value));
        }

        public void getPhoneType(int value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getPhoneType", new XC_ResultHook(value));
        }

        public void getSimState(int value) {
            XposedBridge.hookAllMethods(android.telephony.TelephonyManager.class, "getSimState", new XC_ResultHook(value));
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class WifiImpl {
        public InfoImpl Info = new InfoImpl();

        private WifiImpl() {
        }

        public void enableNetwork(boolean returnStatus) {
            XposedBridge.hookAllMethods(android.net.wifi.WifiManager.class, "enableNetwork", new XC_ResultHook(returnStatus));
        }

        public void disableNetwork(boolean returnStatus) {
            XposedBridge.hookAllMethods(android.net.wifi.WifiManager.class, "disableNetwork", new XC_ResultHook(returnStatus));
        }

        public void getWifiState(int returnStatus) {
            XposedBridge.hookAllMethods(android.net.wifi.WifiManager.class, "getWifiState", new XC_ResultHook(returnStatus));
        }

        public static class InfoImpl {
            private InfoImpl() {
            }

            public void getSSID(String value) {
                XposedBridge.hookAllMethods(android.net.wifi.WifiInfo.class, "getSSID", new XC_ResultHook(value));
            }

            public void getBSSID(String value) {
                XposedBridge.hookAllMethods(android.net.wifi.WifiInfo.class, "getBSSID", new XC_ResultHook(value));
            }

            public void getMacAddress(String value) {
                XposedBridge.hookAllMethods(android.net.wifi.WifiInfo.class, "getMacAddress", new XC_ResultHook(value));
            }

            public void getIpAddress(int value) {
                XposedBridge.hookAllMethods(android.net.wifi.WifiInfo.class, "getIpAddress", new XC_ResultHook(value));
            }

            public void getFrequency(int value) {
                XposedBridge.hookAllMethods(android.net.wifi.WifiInfo.class, "getFrequency", new XC_ResultHook(value));
            }

            public void getLinkSpeed(int value) {
                XposedBridge.hookAllMethods(android.net.wifi.WifiInfo.class, "getLinkSpeed", new XC_ResultHook(value));
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class SettingsImpl {
        public SystemImpl System = new SystemImpl();

        private SettingsImpl() {
        }

        @SuppressWarnings("WeakerAccess")
        public static class SystemImpl {
            private HashMap<String, String> hashMap = new HashMap<>();

            private SystemImpl() {
                XposedBridge.hookAllMethods(android.provider.Settings.System.class, "getString", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args.length > 1 && param.args[1] != null && hashMap.containsKey(param.args[1].toString())) {
                            param.setResult(hashMap.get(param.args[1].toString()));
                        }
                    }
                });

                XposedBridge.hookAllMethods(android.provider.Settings.Secure.class, "getString", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args.length > 1 && param.args[1] != null && hashMap.containsKey(param.args[1].toString())) {
                            param.setResult(hashMap.get(param.args[1].toString()));
                        }
                    }
                });
            }

            public void getString(String keyName, String value) {
                hashMap.put(keyName, value);
            }
        }
    }

    private static class XC_ResultHook extends XC_MethodHook {
        private Object resultObject = null;

        XC_ResultHook(Object resultObject) {
            this.resultObject = resultObject;
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (resultObject != null)
                param.setResult(resultObject);
        }
    }
}

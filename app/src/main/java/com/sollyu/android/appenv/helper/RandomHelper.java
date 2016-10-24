package com.sollyu.android.appenv.helper;

import android.telephony.TelephonyManager;

import com.sollyu.android.appenv.module.AppInfo;
import com.sollyu.android.appenv.utils.IMEIGen;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by king.sollyu
 * email: sollyu.king@foxmail.com
 */

public class RandomHelper {
    private static final String TAG = "RandomHelper";
    private static final RandomHelper instance = new RandomHelper();

    public static RandomHelper getInstance() {
        return instance;
    }

    private Random random = new Random();

    public String randomString(int length, boolean lowEnglish, boolean upperEnglish, boolean number) {
        String baseString = "";
        if (lowEnglish) baseString += "abcdefghijklmnopqrstuvwxyz";
        if (upperEnglish) baseString += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (number) baseString += "0123456789";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(baseString.charAt(random.nextInt(baseString.length())));
        }
        return sb.toString();
    }

    public int randomInt(int min, int max) {
        if (min == max) return min;
        return random.nextInt(max) + min;
    }

    public String randomBuildSerial() {
        return randomString(randomInt(10, 20), true, false, true);
    }

    public String randomTelephonyGetLine1Number() {
        String[] telFirst    = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
        String   line1Number = "";

        boolean isUserArea = RandomHelper.getInstance().randomInt(0, 100) < 30;
        if (isUserArea) line1Number += "+86";

        return line1Number + telFirst[RandomHelper.getInstance().randomInt(0, telFirst.length - 1)] + RandomHelper.getInstance().randomString(8, false, false, true);
    }

    public HashMap<String, Object> getTelephonyGetNetworkTypeList() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("GPRS (2G)" , TelephonyManager.NETWORK_TYPE_GPRS);
        hashMap.put("CDMA (3G)" , TelephonyManager.NETWORK_TYPE_CDMA);
        hashMap.put("HSDPA(3G+)", TelephonyManager.NETWORK_TYPE_HSDPA);
        hashMap.put("HSUPA(3G+)", TelephonyManager.NETWORK_TYPE_HSUPA);
        hashMap.put("LTE  (4G)" , TelephonyManager.NETWORK_TYPE_LTE);
        hashMap.put("EDGE"      , TelephonyManager.NETWORK_TYPE_EDGE);
        hashMap.put("UMTS"      , TelephonyManager.NETWORK_TYPE_UMTS);
        hashMap.put("EVDO_0"    , TelephonyManager.NETWORK_TYPE_EVDO_0);
        hashMap.put("EVDO_A"    , TelephonyManager.NETWORK_TYPE_EVDO_A);
        hashMap.put("1xRTT"     , TelephonyManager.NETWORK_TYPE_1xRTT);
        hashMap.put("HSPA"      , TelephonyManager.NETWORK_TYPE_HSPA);
        hashMap.put("IDEN"      , TelephonyManager.NETWORK_TYPE_IDEN);
        hashMap.put("EVDO_B"    , TelephonyManager.NETWORK_TYPE_EVDO_B);
        hashMap.put("EHRPD"     , TelephonyManager.NETWORK_TYPE_EHRPD);
        hashMap.put("HSPAP"     , TelephonyManager.NETWORK_TYPE_HSPAP);
        hashMap.put("Unknown"   , TelephonyManager.NETWORK_TYPE_UNKNOWN);
        return hashMap;
    }

    public void randomAll(String packageName) {
        AppInfo appInfo = new AppInfo();
        appInfo.telephonyGetLine1Number = randomTelephonyGetLine1Number();
        appInfo.buildSerial = randomBuildSerial();
        appInfo.telephonyGetDeviceId = randomTelephonyGetDeviceId();
    }

    public String randomTelephonyGetDeviceId() {
        String imeiCode = "86" + randomString(12, false, false, true);
        return imeiCode + IMEIGen.genCode(imeiCode);
    }
}

package com.sollyu.android.appenv.helper;

import android.telephony.TelephonyManager;

import com.sollyu.android.appenv.module.AppInfo;
import com.sollyu.android.appenv.utils.IMEIGen;
import com.sollyu.android.appenv.utils.RandomMac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        hashMap.put("0. Unknown"   , TelephonyManager.NETWORK_TYPE_UNKNOWN);
        hashMap.put("1. GPRS (2G)" , TelephonyManager.NETWORK_TYPE_GPRS);
        hashMap.put("2. CDMA (3G)" , TelephonyManager.NETWORK_TYPE_CDMA);
        hashMap.put("3. HSDPA(3G+)", TelephonyManager.NETWORK_TYPE_HSDPA);
        hashMap.put("4. HSUPA(3G+)", TelephonyManager.NETWORK_TYPE_HSUPA);
        hashMap.put("5. LTE  (4G)" , TelephonyManager.NETWORK_TYPE_LTE);
        hashMap.put("6. EDGE"      , TelephonyManager.NETWORK_TYPE_EDGE);
        hashMap.put("7. UMTS"      , TelephonyManager.NETWORK_TYPE_UMTS);
        hashMap.put("8. EVDO_0"    , TelephonyManager.NETWORK_TYPE_EVDO_0);
        hashMap.put("9. EVDO_A"    , TelephonyManager.NETWORK_TYPE_EVDO_A);
        hashMap.put("10.1xRTT"     , TelephonyManager.NETWORK_TYPE_1xRTT);
        hashMap.put("11.HSPA"      , TelephonyManager.NETWORK_TYPE_HSPA);
        hashMap.put("12.IDEN"      , TelephonyManager.NETWORK_TYPE_IDEN);
        hashMap.put("13.EVDO_B"    , TelephonyManager.NETWORK_TYPE_EVDO_B);
        hashMap.put("14.EHRPD"     , TelephonyManager.NETWORK_TYPE_EHRPD);
        hashMap.put("15.HSPAP"     , TelephonyManager.NETWORK_TYPE_HSPAP);
        hashMap.put("16.WIFI"      , 99);
        return hashMap;
    }

    public AppInfo randomAll() {
        ArrayList<String>       manufacturerStringArrayList = PhoneHelper.getInstance().getManufacturerList();
        String                  randomManufacturer          = manufacturerStringArrayList.get(randomInt(0, manufacturerStringArrayList.size() - 1));
        HashMap<String, String> hashMap                     = PhoneHelper.getInstance().getModelList(randomManufacturer);

        ArrayList<String> selectStringArrayList = new ArrayList<>();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            if (!selectStringArrayList.contains(entry.getKey())) {
                selectStringArrayList.add(entry.getKey());
            }
        }

        AppInfo appInfo = new AppInfo();
        appInfo.buildManufacturer = randomManufacturer;
        appInfo.buildModel = hashMap.get(selectStringArrayList.get(randomInt(0, selectStringArrayList.size() - 1)));
        appInfo.buildSerial = randomBuildSerial();
        appInfo.telephonyGetLine1Number = randomTelephonyGetLine1Number();
        appInfo.telephonyGetDeviceId = randomTelephonyGetDeviceId();
        appInfo.telephonyGetNetworkType = String.valueOf(randomInt(0, 15));
        appInfo.telephonyGetSimSerialNumber = randomTelephonySimSerialNumber();
        appInfo.wifiInfoGetSSID = randomWifiInfoSSID();
        appInfo.wifiInfoGetMacAddress = randomWifiInfoMacAddress();

        return appInfo;
    }

    public String randomTelephonyGetDeviceId() {
        String imeiCode = "86" + randomString(12, false, false, true);
        return imeiCode + IMEIGen.genCode(imeiCode);
    }

    public String randomTelephonySimSerialNumber() {
        return randomString(20, false, false, true);
    }

    public String randomWifiInfoSSID() {
        String[] strings = new String[]{"TP-", "FAST_", "Tenda_", "TP-LINK_", "MERCURY_"};
        return strings[randomInt(0, strings.length - 1)] + randomString(randomInt(5, 8), false, true, true);
    }

    public String randomWifiInfoMacAddress() {
        return RandomMac.getMacAddrWithFormat(":");
    }
}

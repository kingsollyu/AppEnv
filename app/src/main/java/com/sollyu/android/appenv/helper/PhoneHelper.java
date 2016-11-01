package com.sollyu.android.appenv.helper;

import android.content.Context;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 作者: Sollyu
 * 时间: 16/10/16
 * 联系: sollyu@qq.com
 * 说明:
 * 手机型号保存的列表
 */
public class PhoneHelper {
    private static final String TAG = "AppEnv";

    private PhoneHelper() {

    }

    private static final PhoneHelper instance = new PhoneHelper();

    public static PhoneHelper getInstance() {
        return instance;
    }

    private JSONObject phoneJsonObject = null;

    public JSONObject getPhoneJsonObject() {
        return phoneJsonObject;
    }

    public void setPhoneJsonObject(JSONObject phoneJsonObject) {
        this.phoneJsonObject = phoneJsonObject;
    }

    /**
     * @return 获得厂商列表
     */
    public ArrayList<String> getManufacturerList() {
        Iterator<String> stringIterator = getPhoneJsonObject().keys();

        ArrayList<String> stringArrayList = new ArrayList<>();

        while (stringIterator.hasNext()) {
            JSONArray modelJsonArray = getPhoneJsonObject().optJSONArray(stringIterator.next());
            for (int i = 0; i < modelJsonArray.length(); i++) {
                JSONObject modelItem = modelJsonArray.optJSONObject(i);

                if (!stringArrayList.contains(modelItem.optString("manufacturer"))) {
                    stringArrayList.add(modelItem.optString("manufacturer"));
                }
            }
        }

        return stringArrayList;
    }

    public HashMap<String, String> getModelList(String manufacturerName) {
        HashMap<String, String> hashMap = new HashMap<>();

        Iterator<String> stringIterator = getPhoneJsonObject().keys();
        while (stringIterator.hasNext()) {
            JSONArray modelJsonArray = getPhoneJsonObject().optJSONArray(stringIterator.next());
            for (int i = 0; i < modelJsonArray.length(); i++) {
                JSONObject modelItem = modelJsonArray.optJSONObject(i);

                if (modelItem.optString("manufacturer").equals(manufacturerName)) {
                    hashMap.put(modelItem.optString("name"), modelItem.optString("model"));
                }
            }
        }

        return hashMap;
    }

    public void reload(Context context) throws IOException, JSONException {
        setPhoneJsonObject(new JSONObject(FileUtils.readFileToString(new File(context.getFilesDir(), "phone.json"), "UTF-8")));
    }
}

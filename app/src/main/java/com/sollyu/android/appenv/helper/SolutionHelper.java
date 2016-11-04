package com.sollyu.android.appenv.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sollyu.android.appenv.MainApplication;
import com.sollyu.android.appenv.module.AppInfo;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 作者: Sollyu
 * 时间: 16/11/4
 * 联系: sollyu@qq.com
 * 说明:
 */
public class SolutionHelper {
    public static final File SOLUTION_FILE = new File(MainApplication.getInstance().getExternalFilesDir("solution"), "solution.json");

    private static final String TAG = "AppEnv";
    private static final SolutionHelper instance = new SolutionHelper();

    private JSONObject solutionJsonObject = new JSONObject();

    private SolutionHelper() {
    }

    public static SolutionHelper getInstance() {
        return instance;
    }

    public void put(String solutionName, AppInfo appInfo) {
        reload();
        solutionJsonObject.put(solutionName, appInfo);
        save();
    }

    public ArrayList<String> list() {
        reload();
        ArrayList<String> a = new ArrayList<>();

        for (Map.Entry<String, Object> entry : solutionJsonObject.entrySet()) {
            a.add(entry.getKey());
        }

        return a;
    }

    public synchronized void reload() {
        try {
            if (!SOLUTION_FILE.exists())
                save();

            solutionJsonObject = JSON.parseObject(FileUtils.readFileToString(SOLUTION_FILE, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void save() {
        try {
            FileUtils.writeStringToFile(SOLUTION_FILE, JSON.toJSONString(solutionJsonObject, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AppInfo get(String s) {
        reload();
        return JSON.toJavaObject(solutionJsonObject.getJSONObject(s), AppInfo.class);
    }

    public void remove(String s) {
        reload();
        solutionJsonObject.remove(s);
        save();
    }
}

package com.sollyu.android.appenv.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 作者: Sollyu
 * 时间: 16/10/23
 * 联系: sollyu@qq.com
 * 说明:
 */
public class OtherHelper {
    private static final OtherHelper instance = new OtherHelper();

    private OtherHelper() {
    }

    public static OtherHelper getInstance() {
        return instance;
    }

    public void openMarket(Context context, String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    public void openApplication(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(intent);
    }

    public void openUrl(Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}

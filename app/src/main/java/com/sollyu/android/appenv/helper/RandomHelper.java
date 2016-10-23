package com.sollyu.android.appenv.helper;

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

}

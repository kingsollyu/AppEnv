package com.sollyu.android.appenv.utils;

/**
 * 作者: Sollyu
 * 时间: 16/10/21
 * 联系: sollyu@qq.com
 * 说明:
 */

import java.util.ArrayList;
import java.util.List;

public class IMEIGen {

    /**
     * @param args
     */
    // public static void main(String[] args) {
    //     String code    = "35254112521400";
    //     String newCode = genCode(code);
    //     System.out.println("======" + newCode);
    //     System.out.println(code + newCode);
    //     String endCode = "35254112521500";
    //     beachIMEI(code, endCode);
    // }

    /**
     * 批量生成IMEI
     *
     * @param begin
     * @param end
     * @return
     */
    static List<String> beachIMEI(String begin, String end) {
        List<String> imeis = new ArrayList<String>();
        try {
            long   count       = Long.parseLong(end) - Long.parseLong(begin);
            Long   currentCode = Long.parseLong(begin);
            String code;
            for (int i = 0; i <= count; i++) {
                code = currentCode.toString();
                code = code + genCode(code);
                imeis.add(code);
                System.out.println("code=====" + code);
                currentCode += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imeis;
    }

    /**
     * IMEI 校验码
     *
     * @param code
     * @return
     */
    public static String genCode(String code) {
        int    total = 0, sum1 = 0, sum2 = 0;
        int    temp  = 0;
        char[] chs   = code.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            int num = chs[i] - '0';    // ascii to num
            //System.out.println(num);
            /*(1)将奇数位数字相加(从1开始计数)*/
            if (i % 2 == 0) {
                sum1 = sum1 + num;
            } else {
                /*(2)将偶数位数字分别乘以2,分别计算个位数和十位数之和(从1开始计数)*/
                temp = num * 2;
                if (temp < 10) {
                    sum2 = sum2 + temp;
                } else {
                    sum2 = sum2 + temp + 1 - 10;
                }
            }
        }
        total = sum1 + sum2;
        /*如果得出的数个位是0则校验位为0,否则为10减去个位数 */
        if (total % 10 == 0) {
            return "0";
        } else {
            return (10 - (total % 10)) + "";
        }

    }

}
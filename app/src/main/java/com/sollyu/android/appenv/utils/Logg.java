package com.sollyu.android.appenv.utils;

import android.app.Application;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.IOException;

import de.mindpipe.android.logging.log4j.LogCatAppender;

/**
 * 作者：Sollyu
 * 日期：2017/3/16
 * 说明：
 */
public class Logg {
    public static Logger L = null;

    /**
     * ### log文件的格式
     * <p>
     * ### 输出格式解释：
     * ### [%-d{yyyy-MM-dd HH:mm:ss}][Class: %c.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n
     * <p>
     * ### %d{yyyy-MM-dd HH:mm:ss}: 时间，大括号内是时间格式
     * ### %c: 全类名
     * ### %M: 调用的方法名称
     * ### %F:%L  类名:行号（在控制台可以追踪代码）
     * ### %n: 换行
     * ### %p: 日志级别，这里%-5p是指定的5个字符的日志名称，为的是格式整齐
     * ### %m: 日志信息
     * <p>
     * ### 输出的信息大概如下：
     * ### [时间{时间格式}][信息所在的class.method(className：lineNumber)] 换行
     * ### [Level: 5个字符的等级名称] - Msg: 输出信息 换行
     */
    private static final String filePattern = "[%-d{yyyy-MM-dd HH:mm:ss}][Class: %C.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n%n";

    /**
     * ### LogCat控制台输出格式
     * <p>
     * ### [Class: 信息所在的class.method(className：lineNumber)] 换行
     * ### [Level: 5个字符的等级名称] - Msg: 输出信息 换行
     */
    private static final String logCatPattern = "[Class: %C.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n%n";

    private static boolean immediateFlush = true;
    private static boolean isUseFile      = false;
    private static boolean isUseLogcat    = true;

    public static void init(Application application, String name) {
        L = Logger.getLogger(name);
        L.getLoggerRepository().resetConfiguration();

        Logger root = Logger.getRootLogger();

        if (isUseLogcat()) {
            Layout         logCatLayout   = new PatternLayout(getLogCatPattern());
            LogCatAppender logCatAppender = new LogCatAppender(logCatLayout);
            root.addAppender(logCatAppender);
        }

        if (isUseFile()) {
            try {
                Layout                   fileLayout          = new PatternLayout(getFilePattern());
                DailyRollingFileAppender rollingFileAppender = new DailyRollingFileAppender(fileLayout, new File(application.getExternalFilesDir("log"), "runtime.log").getAbsolutePath(), "'.'yyyy-MM-dd'.log'");
                rollingFileAppender.setImmediateFlush(isImmediateFlush());

                root.addAppender(rollingFileAppender);
            } catch (IOException e) {
                getLogger().error("初始化日志文件失败: " + e.getLocalizedMessage());
            }
        }

        getLogger().debug("\n\n\n\n===========[SOFT START]===========");
    }

    public static Logger getLogger() {
        return L;
    }

    public static String getFilePattern() {
        return filePattern;
    }

    public static String getLogCatPattern() {
        return logCatPattern;
    }

    public static boolean isImmediateFlush() {
        return immediateFlush;
    }

    public static void setImmediateFlush(boolean immediateFlush) {
        Logg.immediateFlush = immediateFlush;
    }

    public static boolean isUseFile() {
        return isUseFile;
    }

    public static void setIsUseFile(boolean isUseFile) {
        Logg.isUseFile = isUseFile;
    }

    public static boolean isUseLogcat() {
        return isUseLogcat;
    }

    public static void setIsUseLogcat(boolean isUseLogcat) {
        Logg.isUseLogcat = isUseLogcat;
    }
}

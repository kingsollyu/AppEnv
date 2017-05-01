# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Volumes/Soft.Tools.OS/MacOs/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


# 代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 7

# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
 -dontpreverify

# 混淆时应用侵入式重载
-overloadaggressively

# 确定统一的混淆类的成员名称来增加混淆
-useuniqueclassmembernames

# 混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames

# 不混淆第三方jar
-dontskipnonpubliclibraryclasses

# 不使用大小写混合
-dontusemixedcaseclassnames

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping proguardMapping.txt

# 抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature,*Annotation*
-keepattributes EnclosingMethod

# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep class android.** {*;}
-keep class org.apache.log4j.** { *;}
-keep class org.xutils.** { *;}
-keep class okio.** { *;}
-keep class com.alibaba.fastjson.** { *;}
-keep class com.tencent.bugly.** { *;}
-keep class com.orhanobut.dialogplus.** { *;}
-keep class org.greenrobot.eventbus.** { *;}
-keep class com.github.clans.fab.** { *;}
-keep class com.lqr.optionitemview.** { *;}
-keep class com.kyleduo.switchbutton.** { *;}
-keep class eu.chainfire.libsuperuser.** { *;}

-dontwarn org.apache.log4j.**
-dontwarn okio.**

-keepclassmembers class com.sollyu.android.appenv.module.AppInfo {public <fields>;}
-keep class * implements de.robv.android.xposed.IXposedHookLoadPackage { public void handleLoadPackage(de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam); }
-keep class com.sollyu.android.appenv.MainApplication { public boolean isXposedWork();}
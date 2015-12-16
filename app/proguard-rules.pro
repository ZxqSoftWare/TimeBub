# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Aphasia/Aphasia/path/android-sdk-macosx/tools/proguard/proguard-android.txt
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
-ignorewarnings
-optimizationpasses 5
-dontpreverify
-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-libraryjars /libs/alipaySDK-20151014.jar
-libraryjars /libs/SMSSDK-1.3.1.jar

-dontwarn com.alipay.**
-keep class com.alipay.** { *;}

-dontwarn com.mob.**
-keep class com.mob.** { *;}

-dontwarn cn.smssdk.**
-keep class cn.smssdk.** { *;}





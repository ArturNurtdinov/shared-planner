# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# EventBus region
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# End EventBus region

# Network region
-keepclassmembers class ru.spbstu.common.network.model.* {
  *;
}
# End Network region

# Google services region
-keep class com.google.* {*;}
-keep class com.google.impl.* {*;}
-keep class com.google.firebase.* {*;}
-keep class com.google.googlesignin.** { *; }
-keepnames class com.google.googlesignin.* { *; }
-keep class com.google.gms.** {*;}
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.* {*;}
-keep class com.google.unity.* {*;}
# End Google services region

# Timber region
-assumenosideeffects class timber.log.Timber* {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** e(...);
    public static *** w(...);
}
# End Timber region


# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line number information for Crashlytics stack traces
-keepattributes SourceFile,LineNumberTable

# Keep original source file name (not obfuscated) for Crashlytics
-renamesourcefileattribute SourceFile

# R8 missing class suppressions
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn org.bouncycastle.**

# Gson — keep model classes and Gson internals for reflection
-keep class com.fahimshahriarv1.mtom.data.room.model.** { *; }
-keep class com.fahimshahriarv1.mtom.data.backup.** { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Google Drive API
-keep class com.google.api.services.drive.** { *; }
-keep class com.google.api.client.** { *; }
-dontwarn com.google.api.client.http.apache.v2.**
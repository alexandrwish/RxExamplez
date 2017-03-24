-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontoptimize
-dontobfuscate
-verbose
-optimizations !code/simplification/arithmetic 

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


-keep class com.google.inject.Binder

-keepclassmembers class * {
    @com.google.inject.Inject <init>(...);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
} 

-keepattributes *Annotation* 

-keep class **.Finalizer 
 -keepclassmembers class ** { *** startFinalizer( ... ); } 

-keepclassmembers class * { 
   void *(**On*Event); 

}

-keep class net.sf.** { *; }
-keep class roboguice.** { *; }

-dontwarn net.sf.**
-dontwarn roboguice.**

-keep public class com.magenta.maxunits.mobile.se.service.WorkflowServiceImpl { *; }
-keep public class com.magenta.maxunits.mobile.hd.utils.WorkflowUtils { *; }

-keepclassmembers class * {
    @com.magenta.mc.client.android.listener.MxBroadcastEvents *;
}

# adding this in to preserve line numbers so that the stack traces
# can be remapped
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
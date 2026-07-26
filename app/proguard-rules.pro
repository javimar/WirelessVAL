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

-ignorewarnings
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn java.lang.ClassValue
-dontwarn com.google.errorprone.annotations.**
-keep class **$$ViewInjector { *; }

# ============================================================================
# kotlinx.serialization — SIN estas reglas R8 elimina los serializers generados
# de los @Serializable y la app CRASHEA al parsear el JSON de la API en release.
# (Debug no usa R8, por eso el fallo no aparece hasta producción.)
# Mismo bloque que My-Fallas-Guide, ya probado en producción.
# ============================================================================
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Mantener el Companion de las clases @Serializable
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
# Mantener serializer() en los Companion
-if @kotlinx.serialization.Serializable class ** {
    static **$Companion *;
}
-keepclassmembers class <2>$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
# Mantener INSTANCE.serializer() de los objects serializables
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Blindar explícitamente los DTOs de la app y sus serializers generados
-keep,includedescriptorclasses class eu.javimar.wirelessval.**$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class eu.javimar.wirelessval.** {
    <fields>;
}

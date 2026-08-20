# Add project specific ProGuard rules here.

# Keep MediaPipe classes
-keep class com.google.mediapipe.** { *; }
-keep class com.google.protobuf.** { *; }

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Application classes that will be serialized/deserialized over Gson
-keep class com.posturemind.app.data.** { *; }

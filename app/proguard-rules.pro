# Lakbay Aral ProGuard Rules

# Keep ARCore
-keep class com.google.ar.** { *; }
-dontwarn com.google.ar.**

# Keep SceneView
-keep class io.github.sceneview.** { *; }
-dontwarn io.github.sceneview.**

# Keep Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep data models for Gson
-keep class com.example.lakbayaral.data.models.** { *; }

# Hilt
-dontwarn dagger.hilt.**

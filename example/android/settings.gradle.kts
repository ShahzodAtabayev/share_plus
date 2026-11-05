pluginManagement {
    val flutterSdkPath =
        run {
            val properties = java.util.Properties()
            val localPropertiesFile = file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { properties.load(it) }
            }

            fun isValidSdk(path: String?): Boolean =
                !path.isNullOrBlank() && file(path).exists()

            var flutterSdkPath: String? = properties.getProperty("flutter.sdk")
            if (!isValidSdk(flutterSdkPath)) {
                flutterSdkPath = System.getenv("FLUTTER_ROOT") ?: System.getenv("FLUTTER_HOME")
            }

            if (!isValidSdk(flutterSdkPath)) {
                throw GradleException(
                    "Flutter SDK not found. Set flutter.sdk in local.properties or set FLUTTER_ROOT/FLUTTER_HOME."
                )
            }

            flutterSdkPath!!
        }

    includeBuild("$flutterSdkPath/packages/flutter_tools/gradle")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.flutter.flutter-plugin-loader") version "1.0.0"
    id("com.android.application") version "8.12.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
}

include(":app")

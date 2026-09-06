plugins {
    id("com.android.application") version "8.2.0"
}
android {
    namespace = "com.martodosko.studio"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.martodosko.studio"
        minSdk = 24
        targetSdk = 34
        versionCode = 100001
        versionName = "1.0.1"
    }
    buildTypes { debug { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    externalNativeBuild { cmake { path "CMakeLists.txt" } }
}

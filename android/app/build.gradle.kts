plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.martodosko.studio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.martodosko.studio"
        minSdk = 24
        targetSdk = 34
        versionCode = 100001
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    // ✅ IDINAGDAG: Material3 — kailangan para sa Theme.Material3
    implementation("com.google.android.material:material:1.11.0")
}

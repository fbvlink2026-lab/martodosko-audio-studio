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
        versionCode = 10076       // ✅ v1.0.55 — PINAKABAGO!
        versionName = "1.0.76"    // ✅ v1.0.55 — PINAKABAGO!

        // ⏸️ IKOMENTO MUNA — WALANG C++ PA!
        // externalNativeBuild {
        //     cmake {
        //         cppFlags += "-std=c++17 -O3 -fvisibility=hidden"
        //     }
        // }
    }

    // ✅ OPISYAL NA PINIRMA — NANDOON PA RIN!
    signingConfigs {
        create("release") {
            storeFile = file("martodosko-release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "martodosko2026"
            keyAlias = System.getenv("KEY_ALIAS") ?: "martodosko-key"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "martodosko2026"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
        debug {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    // ⏸️ IKOMENTO MUNA — WALANG C++ PA!
    // externalNativeBuild {
    //     cmake {
    //         path = file("src/main/cpp/CMakeLists.txt")
    //     }
    // }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// ✅ SIDE MENU — TAMA LAHAT!
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
}

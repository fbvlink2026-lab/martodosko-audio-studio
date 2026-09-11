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
        versionCode = 10053       // ✅ Tumaas — v1.0.53
        versionName = "1.0.53"    // ✅ Tumaas — v1.0.53

        // ✅ Kailangan para sa C++ Audio Engine
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17 -O3 -fvisibility=hidden"
            }
        }
    }

    // ==================================================
    // ✅ OPISYAL NA PINIRMA — NANDOON PA RIN!
    // ==================================================
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

    // ✅ Dito nakaturo ang C++ — NANDOON PA RIN!
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
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

// ==================================================
// ✅ DADAGDAG LANG — 2 LINYA PARA SA SIDE MENU!
// ==================================================
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")              // ✅ KAILANGAN — AppCompatActivity
    implementation("com.google.android.material:material:1.11.0")       // ✅ KAILANGAN — Material Icons
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")          // ✅ KAILANGAN — Side Menu!
}

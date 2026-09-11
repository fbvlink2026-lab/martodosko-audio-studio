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
        versionCode = 10044       // ✅ Tumaas — v1.0.2
        versionName = "1.0.44"      // ✅ Tumaas — v1.0.2

        // ✅ Kailangan para sa C++ Audio Engine
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17 -O3 -fvisibility=hidden"
            }
        }
    }

    // ✅ TAMA — Dito nakaturo ang C++
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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
    implementation("com.google.android.material:material:1.11.0")  // ✅ Idinagdag — kailangan sa UI
}

// ==================================================
// FILE: app/build.gradle.kts
// VERSION: 2.1.0
// PURPOSE: Module Build Configuration for Martodosko Studio
// NOTE: Requires Root build.gradle.kts to define Plugin Versions!
// ==================================================

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // ✅ CRITICAL: Ensure these versions are defined in ROOT build.gradle.kts [plugins] block
    id("com.google.dagger.hilt.android") 
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.martodosko.studio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.martodosko.studio"
        minSdk = 29 // Required for modern Audio APIs (<10ms latency goal)
        targetSdk = 34
        versionCode = 10305
        versionName = "1.0.305"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // ✅ Inject Supabase Credentials from Environment Variables
        // If empty (local dev without secrets), they will be empty strings "".
        buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL") ?: ""}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${System.getenv("SUPABASE_ANON_KEY") ?: ""}\"")
    }

    // ✅ Signing Configuration
    signingConfigs {
        create("release") {
            // Check if Keystore exists. If not, fall back to Debug signing to prevent hard crash during initial tests.
            val storeFilePath = System.getenv("RELEASE_KEYSTORE_PATH") ?: "martodosko-release.keystore"
            val keyStoreFile = file(storeFilePath)
            
            if (keyStoreFile.exists()) {
                storeFile = keyStoreFile
                // Use Env Vars or throw error ONLY if we intend to sign a real release
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "default_pass_for_test_only_DO_NOT_USE_IN_PROD"
                keyAlias = System.getenv("KEY_ALIAS") ?: "androiddebugkey"
                keyPassword = System.getenv("KEY_PASSWORD") ?: "default_pass_for_test_only_DO_NOT_USE_IN_PROD"
            } else {
                logger.warn("⚠️ Release Keystore not found. Using Debug fallback for signing.")
                // Fallback to Android Debug Key so the build doesn't fail immediately
                storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Enable R8/ProGuard for size/security
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            
            // Re-inject BuildConfig for Release variant explicitly if needed
            buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL") ?: ""}\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"${System.getenv("SUPABASE_ANON_KEY") ?: ""}\"")
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release") // Uses same config, which falls back to debug key if missing
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        // ⚠️ IMPORTANT: This MUST match your Kotlin Version in root gradle.properties or build.gradle.kts
        // For Kotlin 1.9.20+, use 1.5.8. For Kotlin 1.9.0, use 1.5.1.
        kotlinCompilerExtensionVersion = "1.5.8"
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
    // --- CORE ANDROID ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    
    // --- LIFECYCLE & NAVIGATION ---
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // --- JETPACK COMPOSE UI ---
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // --- DEPENDENCY INJECTION (HILT) ---
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // --- LOCAL DATABASE (ROOM) ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // --- NETWORK & BACKEND (SUPABASE) ---
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.5.0")
    
    // HTTP Client (Required by Supabase SDK internally, but good to have explicit)
    implementation("io.ktor:ktor-client-okhttp:2.3.7")

    // --- BACKGROUND WORKERS (SYNC) ---
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

    // --- UTILITIES ---
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}

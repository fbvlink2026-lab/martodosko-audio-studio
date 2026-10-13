// ==================================================
// FILE: app/build.gradle.kts
// VERSION: 2.1.0 - FINAL CLEAN BUILD CONFIG
// PURPOSE: Module Build Configuration for Martodosko Studio
// NOTE: Removed duplicate blocks and fixed syntax errors
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
        versionCode = 10339
        versionName = "1.0.339"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // ✅ Inject Supabase Credentials from Environment Variables
        buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL") ?: ""}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${System.getenv("SUPABASE_ANON_KEY") ?: ""}\"")
    }

    // ✅ Signing Configuration
    signingConfigs {
        create("release") {
            val storeFilePath = System.getenv("RELEASE_KEYSTORE_PATH") ?: "martodosko-release.keystore"
            val keyStoreFile = file(storeFilePath)
            
            if (keyStoreFile.exists()) {
                storeFile = keyStoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "default_pass_for_test_only_DO_NOT_USE_IN_PROD"
                keyAlias = System.getenv("KEY_ALIAS") ?: "androiddebugkey"
                keyPassword = System.getenv("KEY_PASSWORD") ?: "default_pass_for_test_only_DO_NOT_USE_IN_PROD"
            } else {
                logger.warn("⚠️ Release Keystore not found. Using Debug fallback.")
                storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
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
    
    // --- JETPACK COMPOSE UI ---
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended") // Added for safety
    
    debugImplementation("androidx.compose.ui:ui-tooling")

    // --- LIFECYCLE & NAVIGATION ---
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // --- DEPENDENCY INJECTION (HILT) ---
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.50")

    // --- LOCAL DATABASE (ROOM) ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // --- BACKGROUND WORKERS (SYNC) ---
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

    // --- UTILITIES ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // NOTE: Supabase SDKs are COMMENTED OUT for Phase 2 Mock Build to avoid API mismatch errors.
    // They will be re-enabled in Phase 3 when we implement the real backend logic.
    /*
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.5.0")
    implementation("io.ktor:ktor-client-okhttp:2.3.7")
    */
}

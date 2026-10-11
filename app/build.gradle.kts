plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // ✅ NEW: For Dependency Injection (Hilt)
    id("com.google.dagger.hilt.android")
    // ✅ NEW: For Annotation Processing (Room, Hilt) - Faster than KAPT
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.martodosko.studio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.martodosko.studio"
        minSdk = 29 // ⚠️ INCREASED TO 29 FOR MODERN AUDIO API SUPPORT (<10ms Latency Goal)
        targetSdk = 34
        versionCode = 10297 // Incremented for new structure
        versionName = "1.0.297"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // ✅ CRITICAL: Inject Supabase Credentials from Environment Variables (GitHub Secrets)
        // These will be empty strings if not set in CI, preventing crashes during local dev without setup.
        buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL") ?: ""}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${System.getenv("SUPABASE_ANON_KEY") ?: ""}\"")
    }

    // ✅ SECURITY FIX: Remove hardcoded fallbacks. Fail fast if secrets are missing in Release.
    signingConfigs {
        create("release") {
            // Only load keystore if file exists AND env vars are present
            val storeFilePath = System.getenv("RELEASE_KEYSTORE_PATH") ?: "martodosko-release.keystore"
            val keyStoreFile = file(storeFilePath)
            
            if (keyStoreFile.exists()) {
                storeFile = keyStoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD") 
                    ?: throw GradleException("❌ KEYSTORE_PASSWORD secret is missing! Cannot sign release build.")
                keyAlias = System.getenv("KEY_ALIAS") 
                    ?: throw GradleException("❌ KEY_ALIAS secret is missing!")
                keyPassword = System.getenv("KEY_PASSWORD") 
                    ?: throw GradleException("❌ KEY_PASSWORD secret is missing!")
            } else {
                logger.warn("⚠️ Keystore file not found. Release builds will fail unless provided via CI Secrets.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // ✅ ENABLE PROGUARD/R8 FOR SECURITY & SIZE REDUCTION
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            
            // Ensure BuildConfig fields are available in Release too
            buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL") ?: ""}\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"${System.getenv("SUPABASE_ANON_KEY") ?: ""}\"")
        }
        debug {
            isMinifyEnabled = false
            // Debug builds don't need strict signing, but can use same config if available
            signingConfig = signingConfigs.getByName("release")
        }
    }

    // ✅ REQUIRED FOR BUILDCONFIG GENERATION IN AGP 8+
    buildFeatures {
        buildConfig = true
        compose = true // ✅ ENABLE JETPACK COMPOSE
    }

    // ✅ COMPOSE COMPILER CONFIGURATION
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Match your Kotlin version (1.9.20+)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // ⏸️ C++ ENGINE COMMENTED OUT UNTIL PHASE 2 STARTS
    /*
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    */
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
    // Official Supabase Android Client
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.5.0") // For Clan Chat
    
    // HTTP Client for Supabase (Ktor is used internally by Supabase SDK usually, but explicit dep helps)
    implementation("io.ktor:ktor-client-okhttp:2.3.7")

    // --- BACKGROUND WORKERS (SYNC) ---
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

    // --- UTILITIES ---
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // For debugging network calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}

// ==================================================
// FILE: build.gradle.kts (ROOT)
// VERSION: 2.1.0
// PURPOSE: Top-Level Build Configuration & Plugin Definitions
// NOTE: Defines plugin versions used by app/build.gradle.kts
// ==================================================

plugins {
    // Android Application Plugin
    id("com.android.application") version "8.2.0" apply false
    
    // Kotlin Android Plugin
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    
    // Hilt Dependency Injection Plugin
    id("com.google.dagger.hilt.android") version "2.50" apply false
    
    // KSP (Kotlin Symbol Processing) for Room/Hilt compilation speed
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}

// Optional: Task to clean all build directories
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

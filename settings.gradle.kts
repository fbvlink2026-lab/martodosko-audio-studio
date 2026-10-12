pluginManagement {
    repositories {
        google()
        mavenCentral() // ✅ CRITICAL FOR HILT/KSP
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral() // ✅ CRITICAL FOR LIBRARIES
    }
}

rootProject.name = "Martodosko Audio Studio"
include(":app")

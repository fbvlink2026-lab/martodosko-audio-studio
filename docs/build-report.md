# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-09-20 23:32 UTC
🏷️ **Bersyon:** v1.0.265
🔑 **Pinirma:** Opisyal — Walang 'Install Anyway'!
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
🔑 PINIRMA:    OPISYAL — WALANG 'Install Anyway'!
📂 LOKASYON:   app/build/outputs/apk/release
⏰ ORAS:       2026-09-20 23:32:50 UTC
🏷️ BERSYON:    v1.0.265
==================================================

❌ NABIGO — Hindi nabuo o hindi mahanap ang APK
🔑 Exit Code: 1

--------------------------------------------------
📋 BUONG LOG NG BUILD:
--------------------------------------------------
Downloading https://services.gradle.org/distributions/gradle-8.2-bin.zip
............10%............20%............30%.............40%............50%............60%............70%.............80%............90%............100%
To honour the JVM settings for this build a single-use Daemon process will be forked. For more on this, please refer to https://docs.gradle.org/8.2/userguide/gradle_daemon.html#sec:disabling_the_daemon in the Gradle documentation.
Daemon will be stopped at the end of the build 
> Task :app:clean UP-TO-DATE
> Task :app:buildKotlinToolingMetadata
> Task :app:checkKotlinGradlePluginConfigurationErrors
> Task :app:preBuild UP-TO-DATE
> Task :app:preReleaseBuild UP-TO-DATE
> Task :app:generateReleaseResValues
> Task :app:checkReleaseAarMetadata
> Task :app:mapReleaseSourceSetPaths
> Task :app:generateReleaseResources
> Task :app:packageReleaseResources
> Task :app:mergeReleaseResources
> Task :app:createReleaseCompatibleScreenManifests
> Task :app:extractDeepLinksRelease
> Task :app:parseReleaseLocalResources
> Task :app:processReleaseMainManifest
> Task :app:processReleaseManifest
> Task :app:javaPreCompileRelease
> Task :app:extractProguardFiles
> Task :app:mergeReleaseJniLibFolders
> Task :app:mergeReleaseNativeLibs NO-SOURCE
> Task :app:stripReleaseDebugSymbols NO-SOURCE
> Task :app:extractReleaseNativeSymbolTables NO-SOURCE
> Task :app:mergeReleaseNativeDebugMetadata NO-SOURCE
> Task :app:desugarReleaseFileDependencies
> Task :app:checkReleaseDuplicateClasses
> Task :app:mergeReleaseArtProfile
> Task :app:mergeReleaseShaders
> Task :app:processReleaseManifestForPackage
> Task :app:compileReleaseShaders NO-SOURCE
> Task :app:generateReleaseAssets UP-TO-DATE
> Task :app:mergeReleaseAssets
> Task :app:compressReleaseAssets
> Task :app:collectReleaseDependencies
> Task :app:sdkReleaseDependencyData
> Task :app:validateSigningRelease
> Task :app:writeReleaseAppMetadata
> Task :app:writeReleaseSigningConfigVersions
> Task :app:processReleaseResources
> Task :app:mergeExtDexRelease
> Task :app:optimizeReleaseResources

> Task :app:compileReleaseKotlin FAILED
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/AdminPanelActivity.kt:37:5 Platform declaration clash: The following declarations have the same JVM signature (getRepoOwner()Ljava/lang/String;):
    fun `<get-repoOwner>`(): String defined in com.martodosko.studio.AdminPanelActivity
    fun getRepoOwner(): String defined in com.martodosko.studio.AdminPanelActivity
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/AdminPanelActivity.kt:39:5 Platform declaration clash: The following declarations have the same JVM signature (getRepoName()Ljava/lang/String;):
    fun `<get-repoName>`(): String defined in com.martodosko.studio.AdminPanelActivity
    fun getRepoName(): String defined in com.martodosko.studio.AdminPanelActivity
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/AdminPanelActivity.kt:123:5 Platform declaration clash: The following declarations have the same JVM signature (getRepoOwner()Ljava/lang/String;):
    fun `<get-repoOwner>`(): String defined in com.martodosko.studio.AdminPanelActivity
    fun getRepoOwner(): String defined in com.martodosko.studio.AdminPanelActivity
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/AdminPanelActivity.kt:124:5 Platform declaration clash: The following declarations have the same JVM signature (getRepoName()Ljava/lang/String;):
    fun `<get-repoName>`(): String defined in com.martodosko.studio.AdminPanelActivity
    fun getRepoName(): String defined in com.martodosko.studio.AdminPanelActivity

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:compileReleaseKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 57s
32 actionable tasks: 31 executed, 1 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

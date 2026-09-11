# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-09-11 15:19 UTC
🏷️ **Bersyon:** v1.0.41
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
📂 LOKASYON:   app/build/outputs/apk/debug
⏰ ORAS:       2026-09-11 15:19:57 UTC
🏷️ BERSYON:    v1.0.41
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
> Task :app:externalNativeBuildCleanDebug
> Task :app:externalNativeBuildCleanRelease
> Task :app:clean UP-TO-DATE
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
> Task :app:checkKotlinGradlePluginConfigurationErrors
> Task :app:generateDebugResValues
> Task :app:checkDebugAarMetadata
> Task :app:mapDebugSourceSetPaths
> Task :app:generateDebugResources
> Task :app:packageDebugResources
> Task :app:mergeDebugResources
> Task :app:createDebugCompatibleScreenManifests
> Task :app:extractDeepLinksDebug
> Task :app:parseDebugLocalResources

> Task :app:processDebugMainManifest
package="com.martodosko.studio" found in source AndroidManifest.xml: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/AndroidManifest.xml.
Setting the namespace via the package attribute in the source AndroidManifest.xml is no longer supported, and the value is ignored.
Recommendation: remove package="com.martodosko.studio" from the source AndroidManifest.xml: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/AndroidManifest.xml.

> Task :app:processDebugManifest
> Task :app:javaPreCompileDebug
> Task :app:mergeDebugShaders
> Task :app:compileDebugShaders NO-SOURCE
> Task :app:generateDebugAssets UP-TO-DATE
> Task :app:mergeDebugAssets
> Task :app:compressDebugAssets
> Task :app:desugarDebugFileDependencies
> Task :app:checkDebugDuplicateClasses

> Task :app:configureCMakeDebug[arm64-v8a]
Checking the license for package CMake 3.22.1 in /usr/local/lib/android/sdk/licenses
License for package CMake 3.22.1 accepted.
Preparing "Install CMake 3.22.1 v.3.22.1".
"Install CMake 3.22.1 v.3.22.1" ready.
Installing CMake 3.22.1 in /usr/local/lib/android/sdk/cmake/3.22.1
"Install CMake 3.22.1 v.3.22.1" complete.
"Install CMake 3.22.1 v.3.22.1" finished.

> Task :app:mergeLibDexDebug
> Task :app:buildCMakeDebug[arm64-v8a]
> Task :app:configureCMakeDebug[armeabi-v7a]
> Task :app:buildCMakeDebug[armeabi-v7a]
> Task :app:configureCMakeDebug[x86]
> Task :app:buildCMakeDebug[x86]
> Task :app:configureCMakeDebug[x86_64]
> Task :app:processDebugManifestForPackage
> Task :app:mergeExtDexDebug
> Task :app:buildCMakeDebug[x86_64]
> Task :app:mergeDebugJniLibFolders
> Task :app:mergeDebugNativeLibs
> Task :app:processDebugResources FAILED
> Task :app:validateSigningDebug

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:processDebugResources'.
> A failure occurred while executing com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask$TaskAction
   > Android resource linking failed
     ERROR: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/AndroidManifest.xml:36:13-38:54: AAPT: error: resource xml/file_paths (aka com.martodosko.studio:xml/file_paths) not found.
         

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 56s
35 actionable tasks: 34 executed, 1 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

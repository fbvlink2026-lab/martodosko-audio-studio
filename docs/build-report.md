# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-09-08 00:10 UTC
🏷️ **Bersyon:** v1.0.6
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
📂 LOKASYON:   app/build/outputs/apk/debug
⏰ ORAS:       2026-09-08 00:10:36 UTC
🏷️ BERSYON:    v1.0.6
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

> Task :app:processDebugManifestForPackage
> Task :app:mergeLibDexDebug
> Task :app:buildCMakeDebug[arm64-v8a]
> Task :app:processDebugResources FAILED
> Task :app:configureCMakeDebug[armeabi-v7a]
> Task :app:buildCMakeDebug[armeabi-v7a]
> Task :app:mergeExtDexDebug

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:processDebugResources'.
> A failure occurred while executing com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask$TaskAction
   > Android resource linking failed
     ERROR: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/AndroidManifest.xml:12:5-34:19: AAPT: error: resource mipmap/ic_launcher (aka com.martodosko.studio:mipmap/ic_launcher) not found.
         

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 53s
28 actionable tasks: 27 executed, 1 up-to-date

==================================================


---
✅ **Katapusan ng Talaan**

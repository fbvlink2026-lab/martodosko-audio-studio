# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-09-08 23:34 UTC
🏷️ **Bersyon:** v1.0.16
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
📂 LOKASYON:   app/build/outputs/apk/debug
⏰ ORAS:       2026-09-08 23:34:30 UTC
🏷️ BERSYON:    v1.0.16
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
> Task :app:mergeDebugResources FAILED

> Task :app:parseDebugLocalResources FAILED
[Fatal Error] splash_screen.xml:3:45: Element type "LinearLayout" must be followed by either attribute specifications, ">" or "/>".

FAILURE: Build completed with 2 failures.

1: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:mergeDebugResources'.
> A failure occurred while executing com.android.build.gradle.internal.res.ResourceCompilerRunnable
   > Resource compilation failed (Failed to compile resource file: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/res/layout/splash_screen.xml: . Cause: javax.xml.stream.XMLStreamException: ParseError at [row,col]:[3,45]
     Message: Element type "LinearLayout" must be followed by either attribute specifications, ">" or "/>".). Check logs for more details.

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

2: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:parseDebugLocalResources'.
> A failure occurred while executing com.android.build.gradle.internal.res.ParseLibraryResourcesTask$ParseResourcesRunnable
   > Failed to parse XML file '/home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/build/intermediates/packaged_res/debug/layout/splash_screen.xml'

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

BUILD FAILED in 12s
10 actionable tasks: 9 executed, 1 up-to-date

==================================================


---
✅ **Katapusan ng Talaan**

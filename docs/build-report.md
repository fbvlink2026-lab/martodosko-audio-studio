# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-09-12 10:03 UTC
🏷️ **Bersyon:** v1.0.61
🔑 **Pinirma:** Opisyal — Walang 'Install Anyway'!
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
🔑 PINIRMA:    OPISYAL — WALANG 'Install Anyway'!
📂 LOKASYON:   app/build/outputs/apk/release
⏰ ORAS:       2026-09-12 10:03:10 UTC
🏷️ BERSYON:    v1.0.61
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
> Task :app:mergeReleaseResources FAILED

> Task :app:parseReleaseLocalResources FAILED
[Fatal Error] activity_mixer.xml:367:47: The entity name must immediately follow the '&' in the entity reference.

FAILURE: Build completed with 2 failures.

1: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:mergeReleaseResources'.
> A failure occurred while executing com.android.build.gradle.internal.res.ResourceCompilerRunnable
   > Resource compilation failed (Failed to compile resource file: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/res/layout/activity_mixer.xml: . Cause: javax.xml.stream.XMLStreamException: ParseError at [row,col]:[367,47]
     Message: The entity name must immediately follow the '&' in the entity reference.). Check logs for more details.

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

2: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:parseReleaseLocalResources'.
> A failure occurred while executing com.android.build.gradle.internal.res.ParseLibraryResourcesTask$ParseResourcesRunnable
   > Failed to parse XML file '/home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/build/intermediates/packaged_res/release/layout/activity_mixer.xml'

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

BUILD FAILED in 23s
9 actionable tasks: 8 executed, 1 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

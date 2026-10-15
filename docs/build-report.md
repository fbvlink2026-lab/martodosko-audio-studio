# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-10-15 08:31 UTC
🏷️ **Bersyon:** v1.0.366
🔑 **Pinirma:** Opisyal — Walang 'Install Anyway'!
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
🔑 PINIRMA:    OPISYAL — WALANG 'Install Anyway'!
📂 LOKASYON:   app/build/outputs/apk/release
⏰ ORAS:       2026-10-15 08:31:08 UTC
🏷️ BERSYON:    v1.0.366
==================================================

❌ NABIGO — Hindi nabuo o hindi mahanap ang APK
🔑 Exit Code: 1

--------------------------------------------------
📋 BUONG LOG NG BUILD:
--------------------------------------------------
Downloading https://services.gradle.org/distributions/gradle-8.2-bin.zip
............10%............20%............30%.............40%............50%............60%............70%.............80%............90%............100%

Welcome to Gradle 8.2!

Here are the highlights of this release:
 - Kotlin DSL: new reference documentation, assignment syntax by default
 - Kotlin DSL is now the default with Gradle init
 - Improved suggestions to resolve errors in console output

For more details see https://docs.gradle.org/8.2/release-notes.html

To honour the JVM settings for this build a single-use Daemon process will be forked. For more on this, please refer to https://docs.gradle.org/8.2/userguide/gradle_daemon.html#sec:disabling_the_daemon in the Gradle documentation.
Daemon will be stopped at the end of the build 
> Task :clean UP-TO-DATE
> Task :app:clean UP-TO-DATE
> Task :app:buildKotlinToolingMetadata
> Task :app:checkKotlinGradlePluginConfigurationErrors
> Task :app:preBuild UP-TO-DATE
> Task :app:preReleaseBuild UP-TO-DATE
> Task :app:generateReleaseBuildConfig
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
> Task :app:extractProguardFiles
> Task :app:javaPreCompileRelease
> Task :app:mergeReleaseJniLibFolders
> Task :app:mergeReleaseNativeLibs NO-SOURCE
> Task :app:stripReleaseDebugSymbols NO-SOURCE
> Task :app:extractReleaseNativeSymbolTables NO-SOURCE
> Task :app:mergeReleaseNativeDebugMetadata NO-SOURCE
> Task :app:checkReleaseDuplicateClasses
> Task :app:mergeReleaseArtProfile
> Task :app:mergeReleaseShaders
> Task :app:compileReleaseShaders NO-SOURCE
> Task :app:generateReleaseAssets UP-TO-DATE
> Task :app:mergeReleaseAssets
> Task :app:compressReleaseAssets
> Task :app:processApplicationManifestReleaseForBundle
> Task :app:collectReleaseDependencies
> Task :app:sdkReleaseDependencyData
> Task :app:validateSigningRelease
> Task :app:writeReleaseAppMetadata
> Task :app:writeReleaseSigningConfigVersions
> Task :app:processReleaseManifestForPackage
> Task :app:processReleaseResources
> Task :app:bundleReleaseResources
> Task :app:kspReleaseKotlin

> Task :app:compileReleaseKotlin
w: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/services/AssetSyncService.kt:57:34 Parameter 'asset' is never used
w: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/navigation/MainNavGraph.kt:53:49 'List: ImageVector' is deprecated. Use the AutoMirrored version at Icons.AutoMirrored.Filled.List
w: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/home/PedalBoardScreen.kt:65:9 Variable 'params' is never used
w: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/mixer/MixerMonitorScreen.kt:177:5 Parameter 'color' is never used
w: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/theme/Theme.kt:22:5 Parameter 'darkTheme' is never used

> Task :app:compileReleaseJavaWithJavac
> Task :app:hiltAggregateDepsRelease

> Task :app:hiltJavaCompileRelease
/home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/build/generated/hilt/component_sources/release/com/martodosko/studio/MartodoskoApp_HiltComponents.java:134: error: cannot find symbol
          AppModule.class,
          ^
  symbol:   class AppModule
  location: class MartodoskoApp_HiltComponents
error: ComponentProcessingStep was unable to process 'com.martodosko.studio.MartodoskoApp_HiltComponents.SingletonC' because '<error>' could not be resolved.
  
  Dependency trace:
      => element (CLASS): com.martodosko.studio.MartodoskoApp_HiltComponents.SingletonC
      => annotation type: dagger.Component
      => annotation: @dagger.Component(modules={<error>, dagger.hilt.android.internal.modules.ApplicationContextModule, com.martodosko.studio.di.DatabaseModule, dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule, androidx.hilt.work.HiltWrapper_WorkerFactoryModule, com.martodosko.studio.MartodoskoApp_HiltComponents.ActivityRetainedCBuilderModule, com.martodosko.studio.MartodoskoApp_HiltComponents.ServiceCBuilderModule, com.martodosko.studio.di.NetworkModule}, dependencies={})
      => annotation value (TYPE_ARRAY): modules={<error>, dagger.hilt.android.internal.modules.ApplicationContextModule, com.martodosko.studio.di.DatabaseModule, dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule, androidx.hilt.work.HiltWrapper_WorkerFactoryModule, com.martodosko.studio.MartodoskoApp_HiltComponents.ActivityRetainedCBuilderModule, com.martodosko.studio.MartodoskoApp_HiltComponents.ServiceCBuilderModule, com.martodosko.studio.di.NetworkModule}
      => annotation value (TYPE): modules=<error>
  
  If type '<error>' is a generated type, check above for compilation errors that may have prevented the type from being generated. Otherwise, ensure that type '<error>' is on your classpath.
2 errors

> Task :app:hiltJavaCompileRelease FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:hiltJavaCompileRelease'.
> Compilation failed; see the compiler error output for details.

* Try:
> Run with --info option to get more log output.
> Run with --scan to get full insights.

BUILD FAILED in 2m 4s
37 actionable tasks: 35 executed, 2 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

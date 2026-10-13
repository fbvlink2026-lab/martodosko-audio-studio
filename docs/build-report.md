# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-10-13 17:37 UTC
🏷️ **Bersyon:** v1.0.345
🔑 **Pinirma:** Opisyal — Walang 'Install Anyway'!
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
🔑 PINIRMA:    OPISYAL — WALANG 'Install Anyway'!
📂 LOKASYON:   app/build/outputs/apk/release
⏰ ORAS:       2026-10-13 17:37:09 UTC
🏷️ BERSYON:    v1.0.345
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
> Task :app:processReleaseManifestForPackage
> Task :app:checkReleaseDuplicateClasses
> Task :app:mergeReleaseArtProfile
> Task :app:mergeReleaseShaders
> Task :app:compileReleaseShaders NO-SOURCE
> Task :app:generateReleaseAssets UP-TO-DATE
> Task :app:mergeReleaseAssets
> Task :app:processApplicationManifestReleaseForBundle
> Task :app:compressReleaseAssets
> Task :app:collectReleaseDependencies
> Task :app:sdkReleaseDependencyData
> Task :app:validateSigningRelease
> Task :app:writeReleaseAppMetadata
> Task :app:writeReleaseSigningConfigVersions
> Task :app:processReleaseResources
> Task :app:bundleReleaseResources
> Task :app:kspReleaseKotlin

> Task :app:compileReleaseKotlin FAILED
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/components/ProAudioKnob.kt:90:42 None of the following functions can be called with the arguments supplied: 
public fun <T : Comparable<TypeVariable(T)>> maxOf(a: TypeVariable(T), b: TypeVariable(T)): TypeVariable(T) defined in kotlin.comparisons
public fun <T : Comparable<TypeVariable(T)>> maxOf(a: TypeVariable(T), vararg other: TypeVariable(T)): TypeVariable(T) defined in kotlin.comparisons
public inline fun maxOf(a: Byte, b: Byte): Byte defined in kotlin.comparisons
public fun maxOf(a: Byte, vararg other: Byte): Byte defined in kotlin.comparisons
public inline fun maxOf(a: Double, b: Double): Double defined in kotlin.comparisons
public fun maxOf(a: Double, vararg other: Double): Double defined in kotlin.comparisons
public inline fun maxOf(a: Float, b: Float): Float defined in kotlin.comparisons
public fun maxOf(a: Float, vararg other: Float): Float defined in kotlin.comparisons
public inline fun maxOf(a: Int, b: Int): Int defined in kotlin.comparisons
public fun maxOf(a: Int, vararg other: Int): Int defined in kotlin.comparisons
public inline fun maxOf(a: Long, b: Long): Long defined in kotlin.comparisons
public fun maxOf(a: Long, vararg other: Long): Long defined in kotlin.comparisons
public inline fun maxOf(a: Short, b: Short): Short defined in kotlin.comparisons
public fun maxOf(a: Short, vararg other: Short): Short defined in kotlin.comparisons
public fun maxOf(a: UByte, b: UByte): UByte defined in kotlin.comparisons
public fun maxOf(a: UByte, vararg other: UByte): UByte defined in kotlin.comparisons
public fun maxOf(a: UInt, b: UInt): UInt defined in kotlin.comparisons
public fun maxOf(a: UInt, vararg other: UInt): UInt defined in kotlin.comparisons
public fun maxOf(a: ULong, b: ULong): ULong defined in kotlin.comparisons
public fun maxOf(a: ULong, vararg other: ULong): ULong defined in kotlin.comparisons
public fun maxOf(a: UShort, b: UShort): UShort defined in kotlin.comparisons
public fun maxOf(a: UShort, vararg other: UShort): UShort defined in kotlin.comparisons
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/components/ProAudioKnob.kt:96:51 Overload resolution ambiguity: 
public final operator fun div(other: Byte): Float defined in kotlin.Float
public final operator fun div(other: Double): Double defined in kotlin.Float
public final operator fun div(other: Float): Float defined in kotlin.Float
public final operator fun div(other: Int): Float defined in kotlin.Float
public final operator fun div(other: Long): Float defined in kotlin.Float
public final operator fun div(other: Short): Float defined in kotlin.Float

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

BUILD FAILED in 1m 53s
34 actionable tasks: 32 executed, 2 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

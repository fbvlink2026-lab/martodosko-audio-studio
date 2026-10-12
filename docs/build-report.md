# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-10-12 04:26 UTC
🏷️ **Bersyon:** v1.0.303
🔑 **Pinirma:** Opisyal — Walang 'Install Anyway'!
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
🔑 PINIRMA:    OPISYAL — WALANG 'Install Anyway'!
📂 LOKASYON:   app/build/outputs/apk/release
⏰ ORAS:       2026-10-12 04:26:34 UTC
🏷️ BERSYON:    v1.0.303
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
> Task :app:javaPreCompileRelease
> Task :app:extractProguardFiles
> Task :app:mergeReleaseJniLibFolders
> Task :app:mergeReleaseNativeLibs NO-SOURCE
> Task :app:stripReleaseDebugSymbols NO-SOURCE
> Task :app:extractReleaseNativeSymbolTables NO-SOURCE
> Task :app:mergeReleaseNativeDebugMetadata NO-SOURCE
> Task :app:checkReleaseDuplicateClasses
> Task :app:processReleaseManifestForPackage
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
> Task :app:processReleaseResources
> Task :app:bundleReleaseResources
> Task :app:kspReleaseKotlin

> Task :app:compileReleaseKotlin FAILED
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/model/UserMapper.kt:4:43 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/model/UserMapper.kt:6:1 Conflicting overloads: public fun UserEntity.toDomain(): [Error type: Unresolved type for User] defined in com.martodosko.studio.data.mapper in file UserMapper.kt, public fun UserEntity.toDomain(): [Error type: Unresolved type for User] defined in com.martodosko.studio.data.mapper in file User.kt
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/model/UserMapper.kt:6:28 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/model/UserMapper.kt:7:12 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:6:43 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:9:31 Unresolved reference: auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:10:31 Unresolved reference: auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:22:41 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:24:72 Overload resolution ambiguity: 
public fun UserEntity.toDomain(): [Error type: Unresolved type for User] defined in com.martodosko.studio.data.mapper in file UserMapper.kt
public fun UserEntity.toDomain(): [Error type: Unresolved type for User] defined in com.martodosko.studio.data.mapper in file User.kt
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:29:37 Unresolved reference: auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:29:53 Unresolved reference: Auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:47:22 Unresolved reference: auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:61:25 Unresolved reference: auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/repository/UserRepositoryImpl.kt:64:51 Unresolved reference: UserInfo
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/services/AssetSyncService.kt:38:25 Type mismatch: inferred type is String but Columns was expected
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/services/AssetSyncService.kt:39:18 Unresolved reference: eq
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/data/services/AssetSyncService.kt:76:34 Unresolved reference: storage
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/di/NetworkModule.kt:9:31 Unresolved reference: auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/di/NetworkModule.kt:37:13 Not enough information to infer type variable Config
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/di/NetworkModule.kt:37:21 Unresolved reference: Auth
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/domain/model/User.kt:4:43 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/domain/model/User.kt:6:1 Conflicting overloads: public fun UserEntity.toDomain(): [Error type: Unresolved type for User] defined in com.martodosko.studio.data.mapper in file UserMapper.kt, public fun UserEntity.toDomain(): [Error type: Unresolved type for User] defined in com.martodosko.studio.data.mapper in file User.kt
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/domain/model/User.kt:6:28 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/domain/model/User.kt:7:12 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/domain/repository/UserRepository.kt:3:43 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/domain/repository/UserRepository.kt:7:32 Unresolved reference: User
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/components/ProAudioKnob.kt:74:38 Unresolved reference: width
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/components/ProAudioKnob.kt:74:54 Unresolved reference: height
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/components/ProAudioKnob.kt:75:32 Unresolved reference: minDimension
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/components/ProAudioKnob.kt:92:13 Unresolved reference: Text
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/components/ProAudioKnob.kt:95:31 Unresolved reference: sp
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/navigation/BottomNavItem.kt:6:47 Unresolved reference: QueueMusic
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/navigation/BottomNavItem.kt:7:47 Unresolved reference: GraphicEq
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/navigation/BottomNavItem.kt:19:43 Unresolved reference: GraphicEq
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/navigation/BottomNavItem.kt:20:49 Unresolved reference: QueueMusic
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/MainActivity.kt:15:47 Unresolved reference: GraphicEq
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/MainActivity.kt:16:47 Unresolved reference: QueueMusic
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/MainActivity.kt:44:21 Unresolved reference: MainNavGraph
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/MainActivity.kt:59:43 Unresolved reference: GraphicEq
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/MainActivity.kt:60:49 Unresolved reference: QueueMusic
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/home/PedalBoardScreen.kt:72:16 Unresolved reference: HorizontalDivider

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

BUILD FAILED in 1m 42s
34 actionable tasks: 32 executed, 2 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

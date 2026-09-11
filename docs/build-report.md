# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-09-11 19:24 UTC
🏷️ **Bersyon:** v1.0.55
🔑 **Pinirma:** Opisyal — Walang 'Install Anyway'!
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
🔑 PINIRMA:    OPISYAL — WALANG 'Install Anyway'!
📂 LOKASYON:   app/build/outputs/apk/release
⏰ ORAS:       2026-09-11 19:24:37 UTC
🏷️ BERSYON:    v1.0.55
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
package="com.martodosko.studio" found in source AndroidManifest.xml: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/AndroidManifest.xml.
Setting the namespace via the package attribute in the source AndroidManifest.xml is no longer supported, and the value is ignored.
Recommendation: remove package="com.martodosko.studio" from the source AndroidManifest.xml: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/AndroidManifest.xml.

> Task :app:processReleaseManifest
> Task :app:javaPreCompileRelease
> Task :app:extractProguardFiles

> Task :app:configureCMakeRelWithDebInfo[arm64-v8a]
Checking the license for package CMake 3.22.1 in /usr/local/lib/android/sdk/licenses
License for package CMake 3.22.1 accepted.
Preparing "Install CMake 3.22.1 v.3.22.1".
"Install CMake 3.22.1 v.3.22.1" ready.
Installing CMake 3.22.1 in /usr/local/lib/android/sdk/cmake/3.22.1
"Install CMake 3.22.1 v.3.22.1" complete.
"Install CMake 3.22.1 v.3.22.1" finished.

> Task :app:buildCMakeRelWithDebInfo[arm64-v8a] FAILED
C/C++: ninja: Entering directory `/home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/.cxx/RelWithDebInfo/m6f4m373/arm64-v8a'
C/C++: /usr/local/lib/android/sdk/ndk/25.1.8937393/toolchains/llvm/prebuilt/linux-x86_64/bin/clang++ --target=aarch64-none-linux-android24 --sysroot=/usr/local/lib/android/sdk/ndk/25.1.8937393/toolchains/llvm/prebuilt/linux-x86_64/sysroot -Dmartodosko_EXPORTS -I/home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/.cxx/RelWithDebInfo/m6f4m373/arm64-v8a/_deps/oboe-src/include -g -DANDROID -fdata-sections -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -D_FORTIFY_SOURCE=2 -Wformat -Werror=format-security  -std=c++17 -O3 -fvisibility=hidden -O2 -g -DNDEBUG -fPIC -MD -MT CMakeFiles/martodosko.dir/main.cpp.o -MF CMakeFiles/martodosko.dir/main.cpp.o.d -o CMakeFiles/martodosko.dir/main.cpp.o -c /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/cpp/main.cpp
C/C++: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/cpp/main.cpp:95:43: error: no member named 'InputOutput' in 'oboe::Direction'
C/C++:     builder.setDirection(oboe::Direction::InputOutput)
C/C++:                          ~~~~~~~~~~~~~~~~~^
C/C++: 1 error generated.

> Task :app:processReleaseManifestForPackage

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:buildCMakeRelWithDebInfo[arm64-v8a]'.
> com.android.ide.common.process.ProcessException: ninja: Entering directory `/home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/.cxx/RelWithDebInfo/m6f4m373/arm64-v8a'
  [1/65] Building CXX object _deps/oboe-build/CMakeFiles/oboe.dir/src/aaudio/AAudioLoader.cpp.o
  [2/65] Building CXX object _deps/oboe-build/CMakeFiles/oboe.dir/src/common/AdpfWrapper.cpp.o
  [3/65] Building CXX object CMakeFiles/martodosko.dir/main.cpp.o
  FAILED: CMakeFiles/martodosko.dir/main.cpp.o 
  /usr/local/lib/android/sdk/ndk/25.1.8937393/toolchains/llvm/prebuilt/linux-x86_64/bin/clang++ --target=aarch64-none-linux-android24 --sysroot=/usr/local/lib/android/sdk/ndk/25.1.8937393/toolchains/llvm/prebuilt/linux-x86_64/sysroot -Dmartodosko_EXPORTS -I/home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/.cxx/RelWithDebInfo/m6f4m373/arm64-v8a/_deps/oboe-src/include -g -DANDROID -fdata-sections -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -D_FORTIFY_SOURCE=2 -Wformat -Werror=format-security  -std=c++17 -O3 -fvisibility=hidden -O2 -g -DNDEBUG -fPIC -MD -MT CMakeFiles/martodosko.dir/main.cpp.o -MF CMakeFiles/martodosko.dir/main.cpp.o.d -o CMakeFiles/martodosko.dir/main.cpp.o -c /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/cpp/main.cpp
  /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/cpp/main.cpp:95:43: error: no member named 'InputOutput' in 'oboe::Direction'
      builder.setDirection(oboe::Direction::InputOutput)
                           ~~~~~~~~~~~~~~~~~^
  1 error generated.
  [4/65] Building CXX object _deps/oboe-build/CMakeFiles/oboe.dir/src/common/AudioSourceCaller.cpp.o
  [5/65] Building CXX object _deps/oboe-build/CMakeFiles/oboe.dir/src/aaudio/AudioStreamAAudio.cpp.o
  [6/65] Building CXX object _deps/oboe-build/CMakeFiles/oboe.dir/src/common/Utilities.cpp.o
  [7/65] Building CXX object _deps/oboe-build/CMakeFiles/oboe.dir/src/common/AudioStream.cpp.o
  [8/65] Building CXX object _deps/oboe-build/CMakeFiles/oboe.dir/src/common/AudioStreamBuilder.cpp.o
  ninja: build stopped: subcommand failed.
  
  C++ build system [build] failed while executing:
      /usr/local/lib/android/sdk/cmake/3.22.1/bin/ninja \
        -C \
        /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/.cxx/RelWithDebInfo/m6f4m373/arm64-v8a \
        martodosko
    from /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 33s
20 actionable tasks: 19 executed, 1 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

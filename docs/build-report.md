# 📋 Build Report — Martodosko Audio Studio

> Created & Developed by MartoDosko © Copyright 2026

---
📅 **Petsa:** 2026-10-12 03:47 UTC
🏷️ **Bersyon:** v1.0.299
🔑 **Pinirma:** Opisyal — Walang 'Install Anyway'!
---

==================================================
📦 APLIKASYON: Martodosko Audio Studio
🔑 PINIRMA:    OPISYAL — WALANG 'Install Anyway'!
📂 LOKASYON:   app/build/outputs/apk/release
⏰ ORAS:       2026-10-12 03:47:30 UTC
🏷️ BERSYON:    v1.0.299
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
> Task :app:mergeReleaseShaders
> Task :app:mergeReleaseArtProfile
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
> Task :app:processReleaseManifestForPackage
> Task :app:processReleaseResources
> Task :app:bundleReleaseResources

> Task :app:kspReleaseKotlin FAILED
e: [ksp] [Hilt] /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/build/generated/ksp/release/java/byRounds/1/com/martodosko/studio/ui/screens/MainActivity_GeneratedInjector.java: kotlin.io.FileAlreadyExistsException: /home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/build/generated/ksp/release/java/byRounds/1/com/martodosko/studio/ui/screens/MainActivity_GeneratedInjector.java
	at com.google.devtools.ksp.processing.impl.CodeGeneratorImpl.createNewFile(CodeGeneratorImpl.kt:116)
	at com.google.devtools.ksp.processing.impl.CodeGeneratorImpl.createNewFile(CodeGeneratorImpl.kt:67)
	at dagger.spi.internal.shaded.androidx.room.compiler.processing.ksp.KspFiler.createNewFile(KspFiler.kt:156)
	at dagger.spi.internal.shaded.androidx.room.compiler.processing.ksp.KspFiler.write(KspFiler.kt:46)
	at dagger.hilt.android.processor.internal.androidentrypoint.InjectorEntryPointGenerator.generate(InjectorEntryPointGenerator.java:69)
	at dagger.hilt.android.processor.internal.androidentrypoint.AndroidEntryPointProcessingStep.processEach(AndroidEntryPointProcessingStep.java:54)
	at dagger.hilt.processor.internal.BaseProcessingStep.process(BaseProcessingStep.java:118)
	at dagger.hilt.android.processor.internal.androidentrypoint.AndroidEntryPointProcessingStep.process(AndroidEntryPointProcessingStep.java:35)
	at dagger.spi.internal.shaded.androidx.room.compiler.processing.CommonProcessorDelegate.processRound(XBasicAnnotationProcessor.kt:132)
	at dagger.spi.internal.shaded.androidx.room.compiler.processing.ksp.KspBasicAnnotationProcessor.process(KspBasicAnnotationProcessor.kt:62)
	at com.google.devtools.ksp.AbstractKotlinSymbolProcessingExtension$doAnalysis$8$1.invoke(KotlinSymbolProcessingExtension.kt:305)
	at com.google.devtools.ksp.AbstractKotlinSymbolProcessingExtension$doAnalysis$8$1.invoke(KotlinSymbolProcessingExtension.kt:303)
	at com.google.devtools.ksp.AbstractKotlinSymbolProcessingExtension.handleException(KotlinSymbolProcessingExtension.kt:409)
	at com.google.devtools.ksp.AbstractKotlinSymbolProcessingExtension.doAnalysis(KotlinSymbolProcessingExtension.kt:303)
	at org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(TopDownAnalyzerFacadeForJVM.kt:112)
	at org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration$default(TopDownAnalyzerFacadeForJVM.kt:77)
	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler$analyze$1.invoke(KotlinToJVMBytecodeCompiler.kt:256)
	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler$analyze$1.invoke(KotlinToJVMBytecodeCompiler.kt:247)
	at org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport.analyzeAndReport(AnalyzerWithCompilerReport.kt:115)
	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.analyze(KotlinToJVMBytecodeCompiler.kt:247)
	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileModules$cli(KotlinToJVMBytecodeCompiler.kt:87)
	at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileModules$cli$default(KotlinToJVMBytecodeCompiler.kt:43)
	at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:165)
	at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:50)
	at org.jetbrains.kotlin.cli.common.CLICompiler.execImpl(CLICompiler.kt:104)
	at org.jetbrains.kotlin.cli.common.CLICompiler.execImpl(CLICompiler.kt:48)
	at org.jetbrains.kotlin.cli.common.CLITool.exec(CLITool.kt:101)
	at org.jetbrains.kotlin.daemon.CompileServiceImpl.compile(CompileServiceImpl.kt:1523)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at java.rmi/sun.rmi.server.UnicastServerRef.dispatch(UnicastServerRef.java:360)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:200)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:197)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:712)
	at java.rmi/sun.rmi.transport.Transport.serviceCall(Transport.java:196)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport.handleMessages(TCPTransport.java:587)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run0(TCPTransport.java:828)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.lambda$run$0(TCPTransport.java:705)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run(TCPTransport.java:704)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:840)

[1;31m[Hilt] Processing did not complete. See error above for details.[0m
e: Error occurred in KSP, check log for detail
e: file:///home/runner/work/martodosko-audio-studio/martodosko-audio-studio/app/src/main/java/com/martodosko/studio/ui/screens/MainScaffold.kt:93:2 imports are only allowed in the beginning of file

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:kspReleaseKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 1m 45s
33 actionable tasks: 31 executed, 2 up-to-date

==================================================

---
✅ **Katapusan ng Talaan**

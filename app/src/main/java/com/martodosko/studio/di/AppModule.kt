// ==================================================
// FILE: AppModule.kt
// VERSION: v2.3.0-phase2-step1-fix
// PURPOSE: Provides Core Dependencies (AudioStateManager)
// NOTE: Must be in 'com.martodosko.studio.di' package for Hilt discovery.
// ==================================================
package com.martodosko.studio.di // ✅ CRITICAL: Package must match folder structure exactly

import com.martodosko.studio.data.audio.AudioStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioStateManager(): AudioStateManager {
        return AudioStateManager()
    }
}

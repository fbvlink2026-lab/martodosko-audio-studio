// ==================================================
// FILE: AppModule.kt
// VERSION: v2.3.0-di-fix-context-injection
// PURPOSE: Provides Core Dependencies with Application Context
// NOTE: Fixed AudioStateManager provider to inject @ApplicationContext correctly.
// ==================================================
package com.martodosko.studio.di

import android.content.Context
import com.martodosko.studio.data.audio.AudioStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the singleton instance of AudioStateManager.
     * Injects the Application Context to allow SharedPreferences access.
     */
    @Provides
    @Singleton
    fun provideAudioStateManager(
        @ApplicationContext context: Context // ✅ CRITICAL: Use Qualifier for App Context
    ): AudioStateManager {
        return AudioStateManager(context)
    }
}

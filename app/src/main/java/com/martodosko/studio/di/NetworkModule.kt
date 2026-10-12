// ==================================================
// FILE: NetworkModule.kt
// VERSION: 2.1.0 - DISABLED FOR PHASE 2 BUILD SUCCESS
// PURPOSE: Placeholder for Future Supabase Integration
// NOTE: Real implementation returns in Phase 3 with correct SDK APIs
// ==================================================
package com.martodosko.studio.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ✅ COMMENTED OUT TO PREVENT COMPILE ERRORS WITH INCORRECT SDK IMPORTS
    /*
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        val url = BuildConfig.SUPABASE_URL
        val key = BuildConfig.SUPABASE_ANON_KEY
        
        if (url.isEmpty() || key.isEmpty()) {
            throw IllegalStateException("❌ Supabase Credentials Missing!")
        }

        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
    */
    
    // Dummy provider to satisfy Hilt graph if any other class accidentally requests it
    // But since we removed dependencies from Repos/Services, this shouldn't be called.
}

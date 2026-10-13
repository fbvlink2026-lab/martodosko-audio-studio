package com.martodosko.studio.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Empty for Phase 2. Supabase integration added in Phase 3.
}

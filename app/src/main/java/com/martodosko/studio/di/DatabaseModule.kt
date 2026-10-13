package com.martodosko.studio.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Empty for Phase 2. Real DB logic added in Phase 3.
}

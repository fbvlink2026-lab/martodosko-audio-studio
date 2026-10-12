package com.martodosko.studio.domain.repository

import com.martodosko.studio.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun syncProfileFromRemote(): Result<Unit>
}

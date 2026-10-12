// ==================================================
// FILE: UserRepository.kt
// VERSION: 2.1.0
// PURPOSE: Interface for User Operations
// ==================================================
package com.martodosko.studio.domain.repository // ✅ TAMA DAPAT ANG PACKAGE NAME

import com.martodosko.studio.domain.model.User // ✅ IMPORT FROM DOMAIN MODEL
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun syncProfileFromRemote(): Result<Unit>
}

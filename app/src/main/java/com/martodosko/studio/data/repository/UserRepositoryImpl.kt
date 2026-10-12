// ==================================================
// FILE: UserRepositoryImpl.kt
// VERSION: 2.1.0 - MOCKED FOR BUILD SUCCESS
// PURPOSE: Local User Management (No Network Calls Yet)
// NOTE: Real Supabase Auth integration returns in Phase 3
// ==================================================
package com.martodosko.studio.data.repository

import com.martodosko.studio.data.local.dao.UserDao
import com.martodosko.studio.data.local.entities.UserEntity
import com.martodosko.studio.domain.model.User
import com.martodosko.studio.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
    // ❌ TINANGGAL NA ANG SUPABASE DEPENDENCY DITO PARA MAIWASAN ANG COMPILE ERROR
) : UserRepository {

    override fun getCurrentUser(): Flow<User?> {
        // Return null for now since we aren't fetching from remote
        return userDao.getUserById("dummy-id").map { 
            if (it != null) {
                User(
                    id = it.id,
                    username = it.username ?: "Guest",
                    role = it.role,
                    isPaidMember = it.isPaidMember
                )
            } else {
                null
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        // ✅ MOCK LOGIN: Always succeeds for testing purposes
        println("🔐 [MOCK] Login attempted for: $email")
        
        // Save a dummy user to local DB so the app has state
        val dummyUser = UserEntity(
            id = "user-" + email.hashCode(),
            username = email.substringBefore("@"),
            role = "MEMBER", // Pretend they are a member for testing features
            isPaidMember = true
        )
        userDao.insertUser(dummyUser)
        
        return Result.success(Unit)
    }

    override suspend fun logout(): Result<Unit> {
        // ✅ MOCK LOGOUT: Clears local data
        println("🚪 [MOCK] Logout triggered")
        userDao.clearAllUsers()
        return Result.success(Unit)
    }

    override suspend fun syncProfileFromRemote(): Result<Unit> {
        // No-op for now
        return Result.success(Unit)
    }
}

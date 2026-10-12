package com.martodosko.studio.data.repository

import com.martodosko.studio.data.local.dao.UserDao
import com.martodosko.studio.data.local.entities.UserEntity
import com.martodosko.studio.data.mapper.toDomain
import com.martodosko.studio.domain.model.User
import com.martodosko.studio.domain.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val supabase: SupabaseClient
) : UserRepository {

    override fun getCurrentUser(): Flow<User?> {
        // Try getting from local first, then update if needed
        return userDao.getUserById(getCurrentUserId() ?: "").map { it?.toDomain() }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = supabase.auth.signInWith(Auth.Credentials.Email(email, password))
            if (response.session != null) {
                // Save user info to local DB
                val userInfo = response.user
                if (userInfo != null) {
                    saveUserInfoToLocal(userInfo)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Login failed: No session"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            userDao.clearAllUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncProfileFromRemote(): Result<Unit> {
        // Fetch latest profile from 'profiles' table and update local DB
        TODO("Implement fetching from Postgrest")
    }

    private fun getCurrentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    private suspend fun saveUserInfoToLocal(info: UserInfo) {
        // Map Supabase UserInfo to Local Entity
        val entity = UserEntity(
            id = info.id,
            username = info.email?.substringBefore("@") ?: "User",
            role = "GUEST", // Default until fetched from profiles table
            isPaidMember = false
        )
        userDao.insertUser(entity)
    }
}

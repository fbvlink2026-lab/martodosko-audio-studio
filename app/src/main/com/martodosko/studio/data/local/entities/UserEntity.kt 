package com.martodosko.studio.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_users")
data class UserEntity(
    @PrimaryKey val id: String, // UUID from Supabase
    val username: String?,
    val role: String, // GUEST, MEMBER, ADMIN...
    val isPaidMember: Boolean,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

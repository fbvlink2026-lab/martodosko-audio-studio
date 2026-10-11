package com.martodosko.studio.data.mapper

import com.martodosko.studio.data.local.entities.UserEntity
import com.martodosko.studio.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        username = this.username ?: "Unknown",
        role = this.role,
        isPaidMember = this.isPaidMember
    )
}

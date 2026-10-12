// ==================================================
// FILE: User.kt
// VERSION: 2.1.0
// PURPOSE: Domain Model for User Entity
// ==================================================
package com.martodosko.studio.domain.model // ✅ TAMA DAPAT ANG PACKAGE NAME

data class User(
    val id: String,
    val username: String,
    val role: String,
    val isPaidMember: Boolean
)

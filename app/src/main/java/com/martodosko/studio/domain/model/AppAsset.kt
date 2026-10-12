// ==================================================
// FILE: domain/model/AppAsset.kt
// VERSION: 2.1.0
// PURPOSE: Data Model for Native Library Assets
// ==================================================
package com.martodosko.studio.domain.model

data class AppAsset(
    val id: String,
    val type: String, // "NATIVE_LIB", "PRESET_PACK"
    val architecture: String?, // "arm64-v8a", null if universal
    val versionCode: Int,
    val filePath: String, // Path in Supabase Storage bucket
    val checksumSha256: String,
    val sizeBytes: Long,
    val isCritical: Boolean
)

// ==================================================
// FILE: data/services/AssetSyncService.kt
// VERSION: 2.1.0
// PURPOSE: Check and Download Latest Native Libraries
// ==================================================
package com.martodosko.studio.data.services

import android.content.Context
import com.martodosko.studio.BuildConfig
import com.martodosko.studio.domain.model.AppAsset
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetSyncService @Inject constructor(
    private val supabase: SupabaseClient,
    private val context: Context
) {

    companion object {
        private const val TABLE_NAME = "app_assets"
        private const val STORAGE_BUCKET = "native-libraries"
        private const val LOCAL_CACHE_DIR = "audio_engines"
    }

    /**
     * Fetches the latest critical assets from Supabase.
     */
    suspend fun fetchLatestAssets(): Result<List<AppAsset>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.from(TABLE_NAME)
                .select("*")
                .eq("is_critical", true)
                .order("version_code", ascending = false)
                .execute()
            
            val assets = response.decodeList<AppAsset>()
            Result.success(assets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Checks if a specific asset needs updating based on local cache.
     * Returns true if download is needed.
     */
    suspend fun isUpdateRequired(asset: AppAsset): Boolean = withContext(Dispatchers.IO) {
        val localFile = getLocalCacheFile(asset.filePath.substringAfterLast("/"))
        
        if (!localFile.exists()) return@withContext true
        
        // Compare Checksums
        val localChecksum = calculateSha256(localFile)
        return@withContext localChecksum != asset.checksumSha256
    }

    /**
     * Downloads the asset from Supabase Storage to local cache.
     */
    suspend fun downloadAsset(asset: AppAsset): Result<File> = withContext(Dispatchers.IO) {
        try {
            val fileName = asset.filePath.substringAfterLast("/")
            val targetFile = getLocalCacheFile(fileName)
            
            // Create parent dirs if not exist
            targetFile.parentFile?.mkdirs()

            // Download via Supabase SDK
            val bytes = supabase.storage.from(STORAGE_BUCKET).downloadAuthenticated(asset.filePath)
            
            // Write to disk
            targetFile.writeBytes(bytes)
            
            // Verify Integrity Immediately
            val newChecksum = calculateSha256(targetFile)
            if (newChecksum != asset.checksumSha256) {
                targetFile.delete() // Corrupted download, delete it
                throw SecurityException("❌ Checksum Mismatch! File corrupted during transfer.")
            }

            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getLocalCacheFile(filename: String): File {
        val dir = File(context.filesDir, LOCAL_CACHE_DIR)
        return File(dir, filename)
    }

    private fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

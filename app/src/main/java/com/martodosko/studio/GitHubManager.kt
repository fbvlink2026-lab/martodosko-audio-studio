// ==================================================
// FILE: GitHubManager.kt — ✅ NA-AYOS NA ANG MGA IMPORT! WALANG ERROR!
// VERSION: 1.0.1 — ✅ IDINAGDAG ANG MGA KAILANGANG IMPORT! DECRYPT MUNA BAGO GAMITIN!
// UPDATED: 2026-09-20 — BUILD NA! WALANG UNRESOLVED REFERENCE!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

class GitHubManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("github_config", Context.MODE_PRIVATE)
    
    private val REPO_OWNER = "fbvlink2026-lab"
    private val REPO_NAME = "martodosko-audio-studio"

    // ✅ WALANG MALINAW NA TOKEN — NAKA-ENCRYPT LANG ANG NAKA-SAVE!
    private val ENCRYPTED_TOKEN_KEY = "encrypted_github_token"
    private val USER_FOLDER_KEY = "user_folder"

    // ==============================================
    // ✅ I-SAVE ANG TOKEN — NAKA-ENCRYPT AGAD!
    // ==============================================
    fun saveEncryptedToken(plainToken: String, ownerKeyCode: String, userFolder: String) {
        val encryptedToken = EncryptionManager.encrypt(plainToken, ownerKeyCode)
        prefs.edit()
            .putString(ENCRYPTED_TOKEN_KEY, encryptedToken)
            .putString(USER_FOLDER_KEY, userFolder.trim().replace(Regex("[^a-zA-Z0-9_-]"), "_"))
            .apply()
        Toast.makeText(context, "✅ Token Naka-encrypt at Nai-save!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ KUNIN ANG TOKEN — KAILANGAN NG OWNER KEY PARA MA-DECRYPT!
    // ==============================================
    fun getDecryptedToken(ownerKeyCode: String): String? {
        val encryptedToken = prefs.getString(ENCRYPTED_TOKEN_KEY, null) ?: return null
        return EncryptionManager.decrypt(encryptedToken, ownerKeyCode)
    }

    // ==============================================
    // ✅ TIGNAN KUNG MAY NAKA-SAVE NA TOKEN
    // ==============================================
    fun hasEncryptedToken(): Boolean = prefs.contains(ENCRYPTED_TOKEN_KEY)

    // ==============================================
    // ✅ I-REMOVE ANG TOKEN
    // ==============================================
    fun clearToken() {
        prefs.edit().remove(ENCRYPTED_TOKEN_KEY).remove(USER_FOLDER_KEY).apply()
    }

    // ==============================================
    // ✅ I-CHECK KUNG MAY TOKEN — KUNG WALA → LOCAL LANG MUNA
    // ==============================================
    fun isGitHubEnabled(): Boolean = hasEncryptedToken()

    // ==============================================
    // ✅ I-SAVE ANG DATA SA GITHUB — BILANG FILE
    // ==============================================
    suspend fun saveToGitHub(
        filePath: String,
        jsonData: JSONObject,
        commitMsg: String,
        ownerKeyCode: String
    ): Result<String> {
        val token = getDecryptedToken(ownerKeyCode) ?: return Result.failure(
            Exception("❌ Kailangan ng tamang Owner Key Code para magamit ang GitHub!")
        )

        return withContext(Dispatchers.IO) {
            try {
                val fullPath = "user_data/${prefs.getString(USER_FOLDER_KEY, "default_user")}/$filePath"
                val apiUrl = "https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/contents/$fullPath"

                val sha = getFileSha(fullPath, token)
                val contentEncoded = Base64.encodeToString(jsonData.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                val requestBody = JSONObject().apply {
                    put("message", commitMsg)
                    put("content", contentEncoded)
                    sha?.let { put("sha", it) }
                }.toString()

                val url = URL(apiUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Authorization", "token $token")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                conn.doOutput = true
                conn.connectTimeout = 15000
                conn.readTimeout = 15000

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(requestBody)
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode
                if (responseCode in 200..201) {
                    Result.success("✅ Nai-save sa GitHub! ($responseCode)")
                } else {
                    Result.failure(Exception("❌ Hindi nai-save: Code $responseCode"))
                }
            } catch (e: Exception) {
                Log.e("GITHUB", "Save Error", e)
                Result.failure(e)
            }
        }
    }

    // ==============================================
    // ✅ I-LOAD ANG DATA MULA SA GITHUB
    // ==============================================
    suspend fun loadFromGitHub(
        filePath: String,
        ownerKeyCode: String
    ): Result<JSONObject> {
        val token = getDecryptedToken(ownerKeyCode) ?: return Result.failure(
            Exception("❌ Kailangan ng tamang Owner Key Code para magbasa sa GitHub!")
        )

        return withContext(Dispatchers.IO) {
            try {
                val fullPath = "user_data/${prefs.getString(USER_FOLDER_KEY, "default_user")}/$filePath"
                val apiUrl = "https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/contents/$fullPath"

                val url = URL(apiUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $token")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                conn.connectTimeout = 15000
                conn.readTimeout = 15000

                if (conn.responseCode == 404) {
                    return@withContext Result.failure(Exception("File hindi pa umiiral — bago pa lang"))
                }
                if (conn.responseCode !in 200..299) {
                    return@withContext Result.failure(Exception("Error: ${conn.responseCode}"))
                }

                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) response.append(line)
                reader.close()

                val json = JSONObject(response.toString())
                val encodedContent = json.getString("content").replace("\n", "")
                val decodedBytes = Base64.decode(encodedContent, Base64.NO_WRAP)
                val contentStr = String(decodedBytes, Charsets.UTF_8)

                Result.success(JSONObject(contentStr))
            } catch (e: Exception) {
                Log.e("GITHUB", "Load Error", e)
                Result.failure(e)
            }
        }
    }

    // ==============================================
    // ✅ KUNIN ANG SHA NG FILE — KAILANGAN PAG-UPDATE
    // ==============================================
    private suspend fun getFileSha(path: String, token: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/contents/$path")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "token $token")
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val resp = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) resp.append(line)
                JSONObject(resp.toString()).optString("sha")
            } else null
        } catch (e: Exception) { null }
    }
}

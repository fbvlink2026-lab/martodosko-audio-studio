// ==================================================
// FILE: EncryptionManager.kt — ✅ OWNER KEY = MASTER KEY!
// VERSION: 1.0.0 — AES-256 ENCRYPT/DECRYPT GITHUB TOKEN!
// ✅ WALANG NAKA-SAVE NA MALINAW NA TOKEN — NAKA-ENCRYPT LANG!
// UPDATED: 2026-09-20 — PINAKAMATAAS NA SEGURIDAD!
// ==================================================
package com.martodosko.studio

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom

object EncryptionManager {

    private const val TAG = "ENCRYPTION"
    
    // ✅ AES-256 — PINAKAMATAGAL NA URI NG ENCRYPTION
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_SIZE = 32 // 256 bits = AES-256
    private const val IV_SIZE = 16  // 128 bits = kailangan ng CBC mode

    // ==============================================
    // ✅ HAKBANG 1: GAWING 256-BIT KEY ANG OWNER KEY CODE
    // ==============================================
    // Ang Owner Key ay ginagawang SHA-256 hash → ito ang magiging AES Key!
    private fun deriveKeyFromOwnerKey(ownerKeyCode: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(ownerKeyCode.toByteArray(StandardCharsets.UTF_8))
        return SecretKeySpec(keyBytes, 0, KEY_SIZE, "AES")
    }

    // ==============================================
    // ✅ HAKBANG 2: ENCRYPT ANG GITHUB TOKEN — BAGO I-SAVE!
    // ==============================================
    fun encrypt(plainTextGitHubToken: String, ownerKeyCode: String): String {
        try {
            // 1. Gawing key ang Owner Key
            val secretKey = deriveKeyFromOwnerKey(ownerKeyCode)

            // 2. Gumawa ng random IV (Initialization Vector) — bawat beses iba ang resulta!
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            // 3. I-encrypt ang token
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encryptedBytes = cipher.doFinal(plainTextGitHubToken.toByteArray(StandardCharsets.UTF_8))

            // 4. Pagsamahin ang IV + Encrypted Data → Base64
            // KAILANGAN ANG IV PAG-DECRYPT — kaya kasama sa resulta
            val combined = iv + encryptedBytes
            val encryptedToken = Base64.encodeToString(combined, Base64.NO_WRAP)

            Log.d(TAG, "✅ Token na-encrypt — handa nang i-save!")
            return encryptedToken

        } catch (e: Exception) {
            Log.e(TAG, "❌ Encryption Error", e)
            throw RuntimeException("Hindi ma-encrypt ang token: ${e.message}")
        }
    }

    // ==============================================
    // ✅ HAKBANG 3: DECRYPT — PAG-IPASOK NG TAMANG OWNER KEY!
    // ==============================================
    fun decrypt(encryptedToken: String, ownerKeyCode: String): String? {
        try {
            // 1. Base64 → Bytes
            val combined = Base64.decode(encryptedToken, Base64.NO_WRAP)

            // 2. IHIWALAY ANG IV AT ENCRYPTED DATA
            val iv = combined.copyOfRange(0, IV_SIZE)
            val encryptedData = combined.copyOfRange(IV_SIZE, combined.size)

            // 3. Gawing key ang Owner Key
            val secretKey = deriveKeyFromOwnerKey(ownerKeyCode)
            val ivSpec = IvParameterSpec(iv)

            // 4. I-decrypt
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decryptedBytes = cipher.doFinal(encryptedData)
            val decryptedToken = String(decryptedBytes, StandardCharsets.UTF_8)

            Log.d(TAG, "✅ Token na-decrypt — handa nang gamitin!")
            return decryptedToken

        } catch (e: Exception) {
            Log.e(TAG, "❌ Decryption Error — Maling Owner Key!", e)
            return null // ❌ Maling Owner Key — hindi magbubukas!
        }
    }

    // ==============================================
    // ✅ TIGNAN KUNG TAMA ANG OWNER KEY BAGO GUMAMIT NG GITHUB
    // ==============================================
    fun verifyOwnerKey(encryptedTestToken: String, ownerKeyCode: String): Boolean {
        val result = decrypt(encryptedTestToken, ownerKeyCode)
        return result != null
    }
}

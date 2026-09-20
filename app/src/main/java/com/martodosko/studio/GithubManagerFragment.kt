// ==================================================
// FILE: GithubManagerFragment.kt — ✅ UNA! TOKEN SETUP + ENCRYPTION + VERIFICATION!
// VERSION: 1.0.0 — ✅ I-INPUT • I-ENCRYPT • I-SAVE • I-VERIFY • AUTO-FILL SA FILE EDITOR!
// UPDATED: 2026-09-21 — DAPAT ITO UNA — KAILANGAN NG FILE EDITOR!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import android.util.Base64

class GithubManagerFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var etToken: EditText
    private lateinit var etRepoOwner: EditText
    private lateinit var etRepoName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnVerify: Button
    private lateinit var btnClear: Button
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCurrentToken: TextView

    private val KEY_ALIAS = "martodosko_github_key"
    private val PREFS_NAME = "github_prefs"
    private val ENCRYPTED_TOKEN_KEY = "encrypted_github_token"
    private val REPO_OWNER_KEY = "repo_owner"
    private val REPO_NAME_KEY = "repo_name"
    private val TOKEN_VERIFIED = "token_verified"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_github_manager, container, false)
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        initViews(view)
        loadSavedConfig()
        setupButtons()

        return view
    }

    private fun initViews(view: View) {
        etToken = view.findViewById(R.id.et_github_token)
        etRepoOwner = view.findViewById(R.id.et_repo_owner)
        etRepoName = view.findViewById(R.id.et_repo_name)
        btnSave = view.findViewById(R.id.btn_save_token)
        btnVerify = view.findViewById(R.id.btn_verify_token)
        btnClear = view.findViewById(R.id.btn_clear_token)
        tvStatus = view.findViewById(R.id.github_status)
        progressBar = view.findViewById(R.id.github_progress)
        tvCurrentToken = view.findViewById(R.id.tv_current_token)
    }

    private fun loadSavedConfig() {
        val savedOwner = prefs.getString(REPO_OWNER_KEY, "")
        val savedName = prefs.getString(REPO_NAME_KEY, "")
        val hasToken = prefs.getString(ENCRYPTED_TOKEN_KEY, null) != null
        val isVerified = prefs.getBoolean(TOKEN_VERIFIED, false)

        etRepoOwner.setText(savedOwner)
        etRepoName.setText(savedName)

        if (hasToken) {
            tvCurrentToken.text = "✅ Token: Naka-save at naka-encrypt"
            tvCurrentToken.setTextColor(if (isVerified) 0xFF4CAF50.toInt() else 0xFFFFA500.toInt())
            etToken.hint = "●●●●●●●●●●●●●●●● (Naka-save — i-type para palitan)"
        } else {
            tvCurrentToken.text = "❌ Walang naka-save na Token"
            tvCurrentToken.setTextColor(0xFFFF5252.toInt())
        }
    }

    private fun setupButtons() {
        btnSave.setOnClickListener { saveConfig() }
        btnVerify.setOnClickListener { verifyToken() }
        btnClear.setOnClickListener { clearConfig() }
    }

    // ==============================================
    // 🔒 I-ENCRYPT AT I-SAVE ANG TOKEN — HINDI PLAIN TEXT!
    // ==============================================
    private fun saveConfig() {
        val tokenInput = etToken.text.toString().trim()
        val owner = etRepoOwner.text.toString().trim()
        val repo = etRepoName.text.toString().trim()

        if (owner.isEmpty() || repo.isEmpty()) {
            showStatus("❌ Ilagay ang Repository Owner at Name!", false)
            return
        }

        // ✅ KUNG MAY BAGONG TOKEN — I-ENCRYPT AT I-SAVE
        if (tokenInput.isNotEmpty()) {
            if (!tokenInput.startsWith("ghp_") && !tokenInput.startsWith("github_pat_")) {
                showStatus("⚠️ Hindi wastong format ng GitHub Token.\nDapat: ghp_... o github_pat_...", false)
                return
            }

            try {
                val encryptedToken = encryptData(tokenInput)
                prefs.edit()
                    .putString(ENCRYPTED_TOKEN_KEY, encryptedToken)
                    .putString(REPO_OWNER_KEY, owner)
                    .putString(REPO_NAME_KEY, repo)
                    .putBoolean(TOKEN_VERIFIED, false)
                    .apply()

                showStatus("✅ Nai-save at naka-encrypt ang Token!", true)
                Toast.makeText(context, "Token Saved & Encrypted!", Toast.LENGTH_SHORT).show()

                etToken.text.clear()
                loadSavedConfig()

            } catch (e: Exception) {
                showStatus("❌ Hindi ma-encrypt: ${e.message}", false)
            }
        } else {
            // ✅ WALANG BAGONG TOKEN — I-SAVE LANG ANG REPO DETAILS
            prefs.edit()
                .putString(REPO_OWNER_KEY, owner)
                .putString(REPO_NAME_KEY, repo)
                .apply()
            showStatus("✅ Na-update ang Repository Details!", true)
        }
    }

    // ==============================================
    // 🔍 I-VERIFY ANG TOKEN — KONEKTA SA GITHUB
    // ==============================================
    private fun verifyToken() {
        val encryptedToken = prefs.getString(ENCRYPTED_TOKEN_KEY, null)
        val owner = prefs.getString(REPO_OWNER_KEY, "")
        val repo = prefs.getString(REPO_NAME_KEY, "")

        if (encryptedToken == null) {
            showStatus("❌ Walang naka-save na Token! I-save muna.", false)
            return
        }
        if (owner.isEmpty() || repo.isEmpty()) {
            showStatus("❌ Kulang ang Repository Details!", false)
            return
        }

        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = decryptData(encryptedToken)
                val url = URL("https://api.github.com/repos/$owner/$repo")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $token")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                val responseCode = conn.responseCode

                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = reader.readText()
                    val json = JSONObject(response)
                    val repoName = json.getString("full_name")
                    val private = json.getBoolean("private")

                    withContext(Dispatchers.Main) {
                        prefs.edit().putBoolean(TOKEN_VERIFIED, true).apply()
                        showLoading(false)
                        showStatus("✅ VERIFIED! ✅\n\n📦 Repository: $repoName\n🔒 Private: $private\n✅ Token ay wasto at gumagana!", true)
                    }
                } else if (responseCode == 401) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        prefs.edit().putBoolean(TOKEN_VERIFIED, false).apply()
                        showStatus("❌ 401 — Hindi wastong Token o nag-expire na!", false)
                    }
                } else if (responseCode == 404) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        prefs.edit().putBoolean(TOKEN_VERIFIED, false).apply()
                        showStatus("❌ 404 — Hindi nahanap ang Repository!\nSuriin ang Owner at Name.", false)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showStatus("❌ Error: Code $responseCode", false)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Error: ${e.message}", false)
                }
            }
        }
    }

    // ==============================================
    // 🗑️ CLEAR — BURAHIN ANG LAHAT
    // ==============================================
    private fun clearConfig() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Burahin ang GitHub Config?")
            .setMessage("Burahin ang Token at Repository Details?\nKailangan itong i-setup ulit.")
            .setPositiveButton("Burahin") { _, _ ->
                prefs.edit()
                    .remove(ENCRYPTED_TOKEN_KEY)
                    .remove(REPO_OWNER_KEY)
                    .remove(REPO_NAME_KEY)
                    .remove(TOKEN_VERIFIED)
                    .apply()

                etToken.text.clear()
                etRepoOwner.text.clear()
                etRepoName.text.clear()
                loadSavedConfig()
                showStatus("🗑️ Burado na ang lahat!", true)
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    // ==============================================
    // 🔐 ENCRYPTION — ANDROID KEYSTORE
    // ==============================================
    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            return keyGenerator.generateKey()
        }

        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    private fun encryptData(plainText: String): String {
        val key = getSecretKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    private fun decryptData(encryptedText: String): String {
        val key = getSecretKey()
        val combined = Base64.decode(encryptedText, Base64.DEFAULT)
        val ivSize = 12 // GCM IV size
        val iv = combined.copyOfRange(0, ivSize)
        val data = combined.copyOfRange(ivSize, combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, javax.crypto.spec.GCMParameterSpec(128, iv))
        val decrypted = cipher.doFinal(data)
        return String(decrypted, Charsets.UTF_8)
    }

    // ==============================================
    // 📊 HELPER — STATUS + PROGRESS
    // ==============================================
    private fun showStatus(msg: String, success: Boolean) {
        tvStatus.text = msg
        tvStatus.setTextColor(if (success) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
        btnVerify.isEnabled = !show
        btnClear.isEnabled = !show
    }
}

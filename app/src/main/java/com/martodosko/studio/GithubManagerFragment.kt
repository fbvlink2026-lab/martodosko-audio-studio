// ==================================================
// FILE: GithubManagerFragment.kt — ✅ REWORKED! WALANG XML! BINUBUO LAHAT SA KOTLIN!
// VERSION: 2.0.0 — ✅ TOKEN ENCRYPT/DECRYPT • VERIFY • SAVE • CLEAR! WALANG FINDBYVIEWID!
// UPDATED: 2026-09-21 — LAHAT NG UI BINUO SA onCreateView! WALANG XML KAILANGAN!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.Gravity
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
    ): View {
        val root = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFF12121F.toInt())
            setPadding(20, 20, 20, 30)
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // ==============================================
        // 🎨 HEADER
        // ==============================================
        mainContainer.addView(createHeader())

        // ==============================================
        // 📌 CURRENT STATUS
        // ==============================================
        tvCurrentToken = TextView(requireContext()).apply {
            text = "⏰ Kinakarga..."
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setBackgroundColor(0xFF1E1E2F.toInt())
            setPadding(14, 12, 14, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainContainer.addView(tvCurrentToken)

        // ==============================================
        // 🐙 GITHUB TOKEN INPUT
        // ==============================================
        mainContainer.addView(createLabel("🔐 GITHUB TOKEN"))
        etToken = EditText(requireContext()).apply {
            hint = "ghp_xxxxxxxxxxxx o github_pat_xxxxxxxxxxxx"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF666666.toInt())
            setPadding(14, 14, 14, 14)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainContainer.addView(etToken)

        // ==============================================
        // 📦 REPOSITORY OWNER
        // ==============================================
        mainContainer.addView(createLabel("👤 REPOSITORY OWNER"))
        etRepoOwner = EditText(requireContext()).apply {
            hint = "hal: martodosko"
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF666666.toInt())
            setPadding(14, 14, 14, 14)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainContainer.addView(etRepoOwner)

        // ==============================================
        // 📂 REPOSITORY NAME
        // ==============================================
        mainContainer.addView(createLabel("📂 REPOSITORY NAME"))
        etRepoName = EditText(requireContext()).apply {
            hint = "hal: martodosko-audio-studio"
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF666666.toInt())
            setPadding(14, 14, 14, 14)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }
        mainContainer.addView(etRepoName)

        // ==============================================
        // 🔘 BUTTONS — SAVE • VERIFY • CLEAR
        // ==============================================
        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        btnSave = createButton("💾 SAVE", 0xFF2E7D32.toInt())
        btnVerify = createButton("🔍 VERIFY", 0xFF0288D1.toInt())
        btnClear = createButton("🗑️ CLEAR", 0xFFB71C1C.toInt())

        btnRow.addView(btnSave)
        btnRow.addView(btnVerify)
        btnRow.addView(btnClear)
        mainContainer.addView(btnRow)

        // ==============================================
        // 📊 STATUS + PROGRESS
        // ==============================================
        tvStatus = TextView(requireContext()).apply {
            text = "✅ Handa na — I-setup ang GitHub Token para makapag-connect"
            textSize = 13f
            setTextColor(0xFF4CAF50.toInt())
            setPadding(4, 8, 4, 8)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }
        mainContainer.addView(tvStatus)

        progressBar = ProgressBar(requireContext()).apply {
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        mainContainer.addView(progressBar)

        // ==============================================
        // ✅ SETUP
        // ==============================================
        loadSavedConfig()
        setupButtons()

        root.addView(mainContainer)
        return root
    }

    private fun createHeader(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }

        val title = TextView(requireContext()).apply {
            text = "🐙 GITHUB TOKEN SETUP"
            textSize = 22f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(requireContext()).apply {
            text = "I-encrypt at i-verify ang access sa GitHub Repository"
            textSize = 12f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 4, 0, 0)
        }

        card.addView(title)
        card.addView(subtitle)
        return card
    }

    private fun createLabel(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 14f
            setTextColor(0xFFCCCCCC.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 0, 0, 8)
        }
    }

    private fun createButton(text: String, color: Int): Button {
        return Button(requireContext()).apply {
            this.text = text
            textSize = 12f
            setBackgroundColor(color)
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 45, 1f).apply { setMargins(4, 0, 4, 0) }
        }
    }

    private fun loadSavedConfig() {
        val savedOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        val savedName = prefs.getString(REPO_NAME_KEY, "") ?: ""
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
        val ivSize = 12
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

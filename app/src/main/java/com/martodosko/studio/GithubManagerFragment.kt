// ==================================================
// FILE: GithubManagerFragment.kt — ✅ KUSANG MALALAGYAN! WALANG MANUAL EDIT!
// VERSION: 3.1.0 — ✅ NAKA-HOLDER ANG DEFAULT TOKEN! KUSANG NALO-LOAD MULA SA BUILD CONFIG!
// UPDATED: 2026-09-21 — WALANG KAILANGANG PALITAN! LAHAT AUTOMATIC!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
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
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class GithubManagerFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var etToken: EditText
    private lateinit var etRepoOwner: EditText
    private lateinit var etRepoName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnVerify: Button
    private lateinit var btnReset: Button
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCurrentToken: TextView

    private val PREFS_NAME = "github_prefs"
    private val ENCRYPTED_TOKEN_KEY = "encrypted_github_token"
    private val REPO_OWNER_KEY = "repo_owner"
    private val REPO_NAME_KEY = "repo_name"
    private val TOKEN_VERIFIED = "token_verified"

    // ==============================================
    // 🔑 APP GLOBAL KEY — NAKA-EMBED! APP LANG ANG ALAM!
    // ==============================================
    private val APP_GLOBAL_KEY = "MARTODOSKO-APP-KEY-2026-SECRET"
    private val APP_KEY_BYTES = APP_GLOBAL_KEY.toByteArray().copyOf(16)

    // ==============================================
    // 📦 HOLDER — KUSANG MALALAGYAN NG BUILD SCRIPT / CI/CD!
    // WAG BAGUHIN — AUTOMATICALLY REPLACED SA BUILD TIME!
    // ==============================================
    private val DEFAULT_ENCRYPTED_TOKEN = "@@DEFAULT_ENCRYPTED_TOKEN@@"
    private val DEFAULT_REPO_OWNER = "@@DEFAULT_REPO_OWNER@@"
    private val DEFAULT_REPO_NAME = "@@DEFAULT_REPO_NAME@@"

    // ==============================================
    // ✅ TIGNAN KUNG HOLDER PA — KUNG HINDI PA PALITAN, GUMAMIT NG FALLBACK
    // ==============================================
    private fun getEffectiveOwner(): String {
        return if (DEFAULT_REPO_OWNER.startsWith("@@")) "fbvlink2026-lab" else DEFAULT_REPO_OWNER
    }

    private fun getEffectiveName(): String {
        return if (DEFAULT_REPO_NAME.startsWith("@@")) "martodosko-audio-studio" else DEFAULT_REPO_NAME
    }

    private fun hasValidToken(): Boolean {
        return !DEFAULT_ENCRYPTED_TOKEN.startsWith("@@") && DEFAULT_ENCRYPTED_TOKEN.isNotEmpty()
    }

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
        // ✅ AUTO-SETUP — KUNG WALANG NAKA-SAVE, I-LOAD ANG DEFAULT!
        // ==============================================
        if (!prefs.contains(ENCRYPTED_TOKEN_KEY) && hasValidToken()) {
            prefs.edit()
                .putString(ENCRYPTED_TOKEN_KEY, DEFAULT_ENCRYPTED_TOKEN)
                .putString(REPO_OWNER_KEY, getEffectiveOwner())
                .putString(REPO_NAME_KEY, getEffectiveName())
                .putBoolean(TOKEN_VERIFIED, false)
                .apply()
        }

        mainContainer.addView(createHeader())
        tvCurrentToken = createStatusBar()
        mainContainer.addView(tvCurrentToken)

        mainContainer.addView(createLabel("🔐 GITHUB TOKEN (Opsyonal)"))
        etToken = createTokenInput()
        mainContainer.addView(etToken)

        mainContainer.addView(createLabel("👤 REPOSITORY OWNER"))
        etRepoOwner = createTextInput("hal: ${getEffectiveOwner()}")
        mainContainer.addView(etRepoOwner)

        mainContainer.addView(createLabel("📂 REPOSITORY NAME"))
        etRepoName = createTextInput("hal: ${getEffectiveName()}")
        mainContainer.addView(etRepoName)

        val btnRow = createButtonRow()
        btnSave = createButton("💾 SAVE", 0xFF2E7D32.toInt())
        btnVerify = createButton("🔍 VERIFY", 0xFF0288D1.toInt())
        btnReset = createButton("🔄 RESET", 0xFFFF8C00.toInt())
        btnRow.addView(btnSave)
        btnRow.addView(btnVerify)
        btnRow.addView(btnReset)
        mainContainer.addView(btnRow)

        tvStatus = createStatusText()
        mainContainer.addView(tvStatus)
        progressBar = createProgressBar()
        mainContainer.addView(progressBar)

        loadSavedConfig()
        setupButtons()

        root.addView(mainContainer)
        return root
    }

    // ==============================================
    // 🔐 ENCRYPTION — APP GLOBAL KEY
    // ==============================================
    private fun encryptData(plainText: String): String {
        val secretKey = SecretKeySpec(APP_KEY_BYTES, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    private fun decryptData(encryptedText: String): String {
        val secretKey = SecretKeySpec(APP_KEY_BYTES, "AES")
        val combined = Base64.decode(encryptedText, Base64.DEFAULT)
        val ivSize = 12
        val iv = combined.copyOfRange(0, ivSize)
        val data = combined.copyOfRange(ivSize, combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val decrypted = cipher.doFinal(data)
        return String(decrypted, Charsets.UTF_8)
    }

    // ==============================================
    // ✅ GLOBAL — PWEDE TAWAGIN KAHIT SAAN!
    // ==============================================
    companion object {
        private val APP_GLOBAL_KEY = "MARTODOSKO-APP-KEY-2026-SECRET"
        private val APP_KEY_BYTES = APP_GLOBAL_KEY.toByteArray().copyOf(16)

        fun getDecryptedToken(context: Context): String? {
            val prefs = context.getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
            val encrypted = prefs.getString("encrypted_github_token", null) ?: return null
            return try {
                val secretKey = SecretKeySpec(APP_KEY_BYTES, "AES")
                val combined = Base64.decode(encrypted, Base64.DEFAULT)
                val iv = combined.copyOfRange(0, 12)
                val data = combined.copyOfRange(12, combined.size)
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
                String(cipher.doFinal(data), Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }

        fun getRepoInfo(context: Context): Pair<String, String> {
            val prefs = context.getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
            val owner = prefs.getString("repo_owner", "") ?: ""
            val name = prefs.getString("repo_name", "") ?: ""
            return Pair(owner, name)
        }
    }

    // ==============================================
    // 🎨 UI FUNCTIONS
    // ==============================================
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
        card.addView(TextView(requireContext()).apply {
            text = "🐙 GITHUB TOKEN SETUP"
            textSize = 22f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        card.addView(TextView(requireContext()).apply {
            text = "Kusang naka-set — hindi na kailangang ilagay!"
            textSize = 12f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 4, 0, 0)
        })
        return card
    }

    private fun createStatusBar(): TextView = TextView(requireContext()).apply {
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

    private fun createLabel(text: String): TextView = TextView(requireContext()).apply {
        this.text = text
        textSize = 14f
        setTextColor(0xFFCCCCCC.toInt())
        setTypeface(null, android.graphics.Typeface.BOLD)
        setPadding(4, 0, 0, 8)
    }

    private fun createTokenInput(): EditText = EditText(requireContext()).apply {
        hint = "I-type lang para palitan — opsyonal!"
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

    private fun createTextInput(hintText: String): EditText = EditText(requireContext()).apply {
        hint = hintText
        setBackgroundColor(0xFF1A1A2E.toInt())
        setTextColor(0xFFFFFFFF.toInt())
        setHintTextColor(0xFF666666.toInt())
        setPadding(14, 14, 14, 14)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 16) }
    }

    private fun createButtonRow(): LinearLayout = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 16) }
    }

    private fun createButton(text: String, color: Int): Button = Button(requireContext()).apply {
        this.text = text
        textSize = 14f
        setTextColor(0xFFFFFFFF.toInt())
        setBackgroundColor(color)
        setPadding(8, 12, 8, 12)
        setTypeface(null, android.graphics.Typeface.BOLD)
        layoutParams = LinearLayout.LayoutParams(0, 56, 1f).apply { setMargins(6, 0, 6, 0) }
    }

    private fun createStatusText(): TextView = TextView(requireContext()).apply {
        text = "✅ Handa na — Kusang naka-set ang GitHub Token!"
        textSize = 13f
        setTextColor(0xFF4CAF50.toInt())
        setPadding(4, 8, 4, 8)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 12) }
    }

    private fun createProgressBar(): ProgressBar = ProgressBar(requireContext()).apply {
        visibility = View.GONE
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    // ==============================================
    // 📊 LOGIC
    // ==============================================
    private fun loadSavedConfig() {
        val savedOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        val savedName = prefs.getString(REPO_NAME_KEY, "") ?: ""
        val hasToken = prefs.getString(ENCRYPTED_TOKEN_KEY, null) != null
        val isVerified = prefs.getBoolean(TOKEN_VERIFIED, false)

        etRepoOwner.setText(savedOwner)
        etRepoName.setText(savedName)

        if (hasToken) {
            tvCurrentToken.text = "✅ Token: Naka-set at naka-encrypt"
            tvCurrentToken.setTextColor(if (isVerified) 0xFF4CAF50.toInt() else 0xFFFFA500.toInt())
            etToken.hint = "●●●●●●●● (I-type para palitan)"
        } else {
            tvCurrentToken.text = "⚠️ Walang default token — I-setup muna"
            tvCurrentToken.setTextColor(0xFFFFA500.toInt())
        }
    }

    private fun setupButtons() {
        btnSave.setOnClickListener { saveConfig() }
        btnVerify.setOnClickListener { verifyToken() }
        btnReset.setOnClickListener { resetToDefault() }
    }

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
                showStatus("⚠️ Hindi wastong format ng GitHub Token.", false)
                return
            }
            val encryptedToken = encryptData(tokenInput)
            prefs.edit()
                .putString(ENCRYPTED_TOKEN_KEY, encryptedToken)
                .putString(REPO_OWNER_KEY, owner)
                .putString(REPO_NAME_KEY, repo)
                .putBoolean(TOKEN_VERIFIED, false)
                .apply()
            showStatus("✅ Nai-save at naka-encrypt ang Token!", true)
            etToken.text.clear()
        } else {
            prefs.edit()
                .putString(REPO_OWNER_KEY, owner)
                .putString(REPO_NAME_KEY, repo)
                .apply()
            showStatus("✅ Na-update ang Repository Details!", true)
        }
        loadSavedConfig()
    }

    private fun verifyToken() {
        val encryptedToken = prefs.getString(ENCRYPTED_TOKEN_KEY, null)
        val owner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        val repo = prefs.getString(REPO_NAME_KEY, "") ?: ""

        if (encryptedToken == null) {
            showStatus("❌ Walang naka-save na Token!", false)
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
                    val json = JSONObject(reader.readText())
                    val repoName = json.getString("full_name")
                    val isPrivate = json.getBoolean("private")
                    withContext(Dispatchers.Main) {
                        prefs.edit().putBoolean(TOKEN_VERIFIED, true).apply()
                        showLoading(false)
                        showStatus("✅ VERIFIED!\n📦 $repoName\n🔒 Private: $isPrivate", true)
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

    private fun resetToDefault() {
        AlertDialog.Builder(requireContext())
            .setTitle("🔄 I-reset sa Default?")
            .setMessage("Ibabalik sa default na GitHub Token at Repository Details?")
            .setPositiveButton("I-reset") { _, _ ->
                if (hasValidToken()) {
                    prefs.edit()
                        .putString(ENCRYPTED_TOKEN_KEY, DEFAULT_ENCRYPTED_TOKEN)
                        .putString(REPO_OWNER_KEY, getEffectiveOwner())
                        .putString(REPO_NAME_KEY, getEffectiveName())
                        .putBoolean(TOKEN_VERIFIED, false)
                        .apply()
                    etToken.text.clear()
                    etRepoOwner.setText(getEffectiveOwner())
                    etRepoName.setText(getEffectiveName())
                    loadSavedConfig()
                    showStatus("✅ Na-reset sa Default!", true)
                } else {
                    showStatus("⚠️ Walang default token na naka-set!", false)
                }
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    private fun showStatus(msg: String, success: Boolean) {
        tvStatus.text = msg
        tvStatus.setTextColor(if (success) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
        btnVerify.isEnabled = !show
        btnReset.isEnabled = !show
    }
}

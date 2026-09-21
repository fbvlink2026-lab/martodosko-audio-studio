// ==================================================
// FILE: GithubManagerFragment.kt — ✅ SARILI NANG GUMAGAWA NG PUSH/PULL/COMMIT!
// VERSION: 3.3.0 — ✅ WALANG BUILD SCRIPT NA GINAGALAW! LOCAL + ONLINE INJECTION LANG!
// UPDATED: 2026-09-22 — KUNG MAY TOKEN → KUSANG I-SE-SAVE SA LAHAT NG BAHAGI NG APP!
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
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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
    private lateinit var btnPull: Button
    private lateinit var btnPush: Button
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
    // 🔑 PAREHONG KEY SA BUONG APP — WALANG GAGALAWIN!
    // ==============================================
    private val APP_GLOBAL_KEY = "MARTODOSKO-APP-KEY-2026-SECRET"
    private val APP_KEY_BYTES = APP_GLOBAL_KEY.toByteArray().copyOf(16)

    // ==============================================
    // 📦 DEFAULT — HINDI NA KAILANGANG PALITAN SA BUILD!
    // ITO ANG GAGAMITIN KUNG WALANG INILAGAY NA BAGO
    // ==============================================
    private val DEFAULT_REPO_OWNER = "fbvlink2026-lab"
    private val DEFAULT_REPO_NAME = "martodosko-audio-studio"

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
        // ✅ AUTO-SETUP — KUNG WALANG NAKA-SAVE, ILAGAY ANG DEFAULT REPO
        // ==============================================
        if (!prefs.contains(REPO_OWNER_KEY)) {
            prefs.edit()
                .putString(REPO_OWNER_KEY, DEFAULT_REPO_OWNER)
                .putString(REPO_NAME_KEY, DEFAULT_REPO_NAME)
                .apply()
        }

        mainContainer.addView(createHeader())
        tvCurrentToken = createStatusBar()
        mainContainer.addView(tvCurrentToken)

        mainContainer.addView(createLabel("🔐 GITHUB TOKEN"))
        etToken = createTokenInput()
        mainContainer.addView(etToken)

        mainContainer.addView(createLabel("👤 REPOSITORY OWNER"))
        etRepoOwner = createTextInput("hal: $DEFAULT_REPO_OWNER")
        mainContainer.addView(etRepoOwner)

        mainContainer.addView(createLabel("📂 REPOSITORY NAME"))
        etRepoName = createTextInput("hal: $DEFAULT_REPO_NAME")
        mainContainer.addView(etRepoName)

        val btnRow1 = createButtonRow()
        btnSave = createButton("💾 SAVE", 0xFF2E7D32.toInt())
        btnVerify = createButton("🔍 VERIFY", 0xFF0288D1.toInt())
        btnReset = createButton("🔄 RESET", 0xFFFF8C00.toInt())
        btnRow1.addView(btnSave)
        btnRow1.addView(btnVerify)
        btnRow1.addView(btnReset)
        mainContainer.addView(btnRow1)

        val btnRow2 = createButtonRow()
        btnPull = createButton("📥 PULL → Local", 0xFF1976D2.toInt())
        btnPush = createButton("📤 PUSH → GitHub", 0xFF7B1FA2.toInt())
        btnRow2.addView(btnPull)
        btnRow2.addView(btnPush)
        mainContainer.addView(btnRow2)

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
    // 🔐 PAREHONG ENCRYPTION SA BUONG APP
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

    private fun decryptData(encryptedText: String): String? {
        return try {
            val secretKey = SecretKeySpec(APP_KEY_BYTES, "AES")
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)
            val iv = combined.copyOfRange(0, 12)
            val data = combined.copyOfRange(12, combined.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
            String(cipher.doFinal(data), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    // ==============================================
    // ✅ GLOBAL — KAYANG TAWAGIN NG FILE EDITOR KAHIT SAAN!
    // ==============================================
    companion object {
        private val APP_GLOBAL_KEY = "MARTODOSKO-APP-KEY-2026-SECRET"
        private val APP_KEY_BYTES = APP_GLOBAL_KEY.toByteArray().copyOf(16)
        private const val PREFS_NAME = "github_prefs"

        fun getDecryptedToken(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val owner = prefs.getString("repo_owner", "") ?: ""
            val name = prefs.getString("repo_name", "") ?: ""
            return Pair(owner, name)
        }
    }

    // ==============================================
    // 🎨 UI
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
            text = "🐙 GITHUB MANAGER — SARILI NANG GUMAGAWA!"
            textSize = 20f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        card.addView(TextView(requireContext()).apply {
            text = "Ilagay ang token → SAVE → VERIFY → PULL/PUSH — Walang build script na kailangan!"
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
        hint = "ghp_xxxxxxxxxxxx o github_pat_..."
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
        ).apply { setMargins(0, 0, 0, 12) }
    }

    private fun createButton(text: String, color: Int): Button = Button(requireContext()).apply {
        this.text = text
        textSize = 13f
        setTextColor(0xFFFFFFFF.toInt())
        setBackgroundColor(color)
        setPadding(8, 12, 8, 12)
        layoutParams = LinearLayout.LayoutParams(0, 52, 1f).apply { setMargins(4, 0, 4, 0) }
    }

    private fun createStatusText(): TextView = TextView(requireContext()).apply {
        text = "✅ Handa na — Ilagay ang GitHub Token sa itaas"
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
            tvCurrentToken.text = "⚠️ Walang token — Ilagay sa itaas → SAVE"
            tvCurrentToken.setTextColor(0xFFFFA500.toInt())
        }
    }

    private fun setupButtons() {
        btnSave.setOnClickListener { saveConfig() }
        btnVerify.setOnClickListener { verifyToken() }
        btnPull.setOnClickListener { showPullMenu() }
        btnPush.setOnClickListener { showPushMenu() }
        btnReset.setOnClickListener { resetConfig() }
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
            showStatus("✅ Nai-save at naka-encrypt! Handa na ang PULL/PUSH!", true)
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
            showStatus("❌ Walang Token — I-save muna!", false)
            return
        }

        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = decryptData(encryptedToken) ?: throw Exception("Hindi mabasa ang token")
                val url = URL("https://api.github.com/repos/$owner/$repo")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $token")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                if (conn.responseCode == 200) {
                    val json = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                    prefs.edit().putBoolean(TOKEN_VERIFIED, true).apply()
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showStatus("✅ VERIFIED — Handa na ang PULL/PUSH!", true)
                    }
                } else {
                    throw Exception("Code ${conn.responseCode}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Nabigo: ${e.message}", false)
                }
            }
        }
    }

    // ==============================================
    // 📥 PULL — GitHub → Local
    // ==============================================
    private fun showPullMenu() {
        val token = getValidToken() ?: return
        val owner = prefs.getString(REPO_OWNER_KEY, "") ?: return
        val repo = prefs.getString(REPO_NAME_KEY, "") ?: return

        val paths = arrayOf(
            "app/src/main/java/com/martodosko/studio/SideMenu.kt",
            "app/src/main/res/layout/side_menu.xml",
            "app/src/main/java/com/martodosko/studio/FileEditorFragment.kt",
            "app/src/main/java/com/martodosko/studio/GithubManagerFragment.kt",
            "app/src/main/AndroidManifest.xml",
            "docs/index.html",
            "assets/abiso.html",
            "Iba pang file..."
        )

        AlertDialog.Builder(requireContext())
            .setTitle("📥 PULL — GitHub → Local")
            .setItems(paths) { _, which ->
                if (paths[which] == "Iba pang file...") {
                    showCustomPathDialog("pull")
                } else {
                    pullFile(paths[which])
                }
            }
            .show()
    }

    private fun pullFile(path: String) {
        val token = getValidToken() ?: return
        val owner = prefs.getString(REPO_OWNER_KEY, "") ?: return
        val repo = prefs.getString(REPO_NAME_KEY, "") ?: return

        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://api.github.com/repos/$owner/$repo/contents/$path")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $token")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")

                val json = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                val content = String(Base64.decode(json.getString("content").replace("\n",""), Base64.DEFAULT))
                val localFile = File(requireContext().filesDir, "project/${path.substringAfterLast('/')}")
                localFile.parentFile?.mkdirs()
                localFile.writeText(content)

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("✅ NA-DOWNLOAD: ${localFile.name}", true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Nabigo: ${e.message}", false)
                }
            }
        }
    }

    // ==============================================
    // 📤 PUSH — Local → GitHub
    // ==============================================
    private fun showPushMenu() {
        val token = getValidToken() ?: return

        val localDir = File(requireContext().filesDir, "project")
        if (!localDir.exists() || localDir.listFiles().isNullOrEmpty()) {
            showStatus("⚠️ Walang file sa Local — Pumunta sa File Editor at mag-save muna!", false)
            return
        }

        val files = localDir.listFiles()?.map { it.name }?.toTypedArray() ?: emptyArray()
        if (files.isEmpty()) {
            showStatus("⚠️ Walang file — Mag-save muna sa File Editor!", false)
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("📤 PUSH — Local → GitHub")
            .setItems(files) { _, which ->
                showCommitMessageDialog(files[which])
            }
            .show()
    }

    private fun showCommitMessageDialog(fileName: String) {
        val input = EditText(requireContext()).apply {
            hint = "Commit message (hal: Inayos ang token)"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("📝 Commit Message")
            .setView(input)
            .setPositiveButton("PUSH") { _, _ ->
                val msg = input.text.toString().trim()
                pushFile(fileName, msg.ifEmpty { "Update: $fileName" })
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    private fun pushFile(fileName: String, message: String) {
        val token = getValidToken() ?: return
        val owner = prefs.getString(REPO_OWNER_KEY, "") ?: return
        val repo = prefs.getString(REPO_NAME_KEY, "") ?: return
        val localFile = File(requireContext().filesDir, "project/$fileName")
        if (!localFile.exists()) {
            showStatus("❌ Wala sa Local: $fileName", false)
            return
        }

        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val content = localFile.readText()
                val encodedContent = Base64.encodeToString(content.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                // Kunin ang SHA kung mayroon na
                var sha: String? = null
                try {
                    val checkUrl = URL("https://api.github.com/repos/$owner/$repo/contents/$fileName")
                    val checkConn = checkUrl.openConnection() as HttpURLConnection
                    checkConn.setRequestProperty("Authorization", "token $token")
                    sha = JSONObject(BufferedReader(InputStreamReader(checkConn.inputStream)).readText()).optString("sha", null)
                } catch (_: Exception) {}

                val url = URL("https://api.github.com/repos/$owner/$repo/contents/$fileName")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Authorization", "token $token")
                conn.setRequestProperty("Content-Type", "application/json")

                val body = JSONObject().apply {
                    put("message", message)
                    put("content", encodedContent)
                    if (!sha.isNullOrEmpty()) put("sha", sha)
                }.toString()

                OutputStreamWriter(conn.outputStream).use { it.write(body) }

                if (conn.responseCode in 200..201) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showStatus("✅ NA-PUSH: $fileName", true)
                    }
                } else {
                    throw Exception("Code ${conn.responseCode}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Nabigo: ${e.message}", false)
                }
            }
        }
    }

    private fun showCustomPathDialog(action: String) {
        val input = EditText(requireContext()).apply {
            hint = "hal: app/src/main/File.kt"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Ipasok ang path")
            .setView(input)
            .setPositiveButton("Tuloy") { _, _ ->
                val path = input.text.toString().trim()
                if (path.isNotEmpty()) {
                    if (action == "pull") pullFile(path)
                }
            }
            .show()
    }

    private fun getValidToken(): String? {
        val encrypted = prefs.getString(ENCRYPTED_TOKEN_KEY, null)
        if (encrypted == null) {
            showStatus("❌ I-save muna ang Token!", false)
            return null
        }
        val token = decryptData(encrypted)
        if (token == null) {
            showStatus("❌ Hindi mabasa ang Token — I-save uli!", false)
            return null
        }
        return token
    }

    private fun resetConfig() {
        AlertDialog.Builder(requireContext())
            .setTitle("🔄 I-reset?")
            .setMessage("Burahin ang naka-save na Token at Repository?")
            .setPositiveButton("Oo") { _, _ ->
                prefs.edit().clear().apply()
                etToken.text.clear()
                etRepoOwner.setText(DEFAULT_REPO_OWNER)
                etRepoName.setText(DEFAULT_REPO_NAME)
                loadSavedConfig()
                showStatus("✅ Na-reset — Ilagay uli ang Token", true)
            }
            .setNegativeButton("Hindi", null)
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
        btnPull.isEnabled = !show
        btnPush.isEnabled = !show
        btnReset.isEnabled = !show
    }
}

// ==================================================
// FILE: FileEditorFragment.kt — ✅ GINAYA ANG admin-edit.html! SIMPLE LANG!
// VERSION: 3.0.0 — ✅ KOPYA NG TAMANG DALOY: PUMILI → BASAHIN → I-EDIT → I-SAVE/PUSH!
// UPDATED: 2026-09-22 — WALANG KUMplikADO — GUMAGANA AGAD!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import android.util.Base64

class FileEditorFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var fileSelect: Spinner
    private lateinit var codeEditor: EditText
    private lateinit var btnLoad: Button
    private lateinit var btnSaveLocal: Button
    private lateinit var btnPushGithub: Button
    private lateinit var btnRefresh: Button
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar

    private var githubToken: String? = null
    private var repoOwner = ""
    private var repoName = ""
    private var selectedFilePath = ""
    private var currentSha: String? = null
    private var originalContent = ""

    companion object {
        const val PREFS_NAME = "github_prefs"
        const val REPO_OWNER_KEY = "repo_owner"
        const val REPO_NAME_KEY = "repo_name"
        const val BASE_URL = "https://api.github.com/repos/"
        const val BRANCH = "main"

        val DEFAULT_FILES = arrayOf(
            "app/src/main/java/com/martodosko/studio/SideMenu.kt",
            "app/src/main/res/layout/side_menu.xml",
            "app/src/main/java/com/martodosko/studio/AdminPanelActivity.kt",
            "app/src/main/AndroidManifest.xml",
            "README.md"
        )
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

        val main = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(main)

        // ===== HEADER =====
        main.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 24)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }

            addView(TextView(requireContext()).apply {
                text = "✏️ FILE EDITOR"
                textSize = 22f
                setTextColor(0xFF40E0D0.toInt())
                setTypeface(null, android.graphics.Typeface.BOLD)
            })
            addView(TextView(requireContext()).apply {
                text = "Pumili → I-load → I-edit → I-save"
                textSize = 13f
                setTextColor(0xFF888888.toInt())
                setPadding(0, 6, 0, 0)
            })
        })

        // ===== PILIAN NG FILE =====
        main.addView(TextView(requireContext()).apply {
            text = "📂 Piliin ang File:"
            textSize = 14f
            setTextColor(0xFFCCCCCC.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 0, 0, 8)
        })

        fileSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                DEFAULT_FILES
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            setBackgroundColor(0xFF1A1A2E.toInt())
        }
        main.addView(fileSelect)

        // ===== BUTTON: I-LOAD =====
        btnLoad = Button(requireContext()).apply {
            text = "📥 I-LOAD MULA SA GITHUB"
            setBackgroundColor(0xFF1976D2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                52
            ).apply { setMargins(0, 0, 0, 12) }
            setOnClickListener { loadFromGithub() }
        }
        main.addView(btnLoad)

        // ===== EDITOR =====
        main.addView(TextView(requireContext()).apply {
            text = "✏️ Nilalaman ng File:"
            textSize = 14f
            setTextColor(0xFFCCCCCC.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 8, 0, 8)
        })

        codeEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFE0E0E0.toInt())
            setHintTextColor(0xFF666666.toInt())
            textSize = 12f
            setPadding(14, 14, 14, 14)
            setHint("Pindutin ang \"I-LOAD\" para makita ang laman...")
            minHeight = 400
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        main.addView(codeEditor)

        // ===== ROW NG MGA BUTTON =====
        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        btnSaveLocal = Button(requireContext()).apply {
            text = "💾 LOKAL"
            setBackgroundColor(0xFF2E7D32.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 50, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { saveLocal() }
        }
        btnPushGithub = Button(requireContext()).apply {
            text = "☁️ I-PUSH"
            setBackgroundColor(0xFF7B1FA2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 50, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { pushToGithub() }
        }
        btnRefresh = Button(requireContext()).apply {
            text = "🔄"
            setBackgroundColor(0xFF00BFA5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 50, 0.3f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { loadFromGithub() }
        }

        btnRow.addView(btnSaveLocal)
        btnRow.addView(btnPushGithub)
        btnRow.addView(btnRefresh)
        main.addView(btnRow)

        // ===== STATUS =====
        statusText = TextView(requireContext()).apply {
            text = "⏳ Handa na — Pumili ng file at pindutin \"I-LOAD\""
            textSize = 13f
            setTextColor(0xFFFFA500.toInt())
            setPadding(0, 16, 0, 8)
        }
        main.addView(statusText)

        progressBar = ProgressBar(requireContext()).apply {
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        main.addView(progressBar)

        // ===== I-LOAD ANG CONFIG =====
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadConfig()

        return root
    }

    private fun loadConfig() {
        repoOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        repoName = prefs.getString(REPO_NAME_KEY, "") ?: ""
        githubToken = GithubManagerFragment.getDecryptedToken(requireContext())

        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ Kulang ang GitHub Token o Repository — Lokal lang muna", false)
        } else {
            showStatus("✅ Konektado: $repoOwner/$repoName — Handa na!", true)
        }
    }

    // ==============================================
    // ✅ 1. I-LOAD MULA SA GITHUB — KOPYA SA HTML!
    // ==============================================
    private fun loadFromGithub() {
        selectedFilePath = fileSelect.selectedItem.toString()
        if (selectedFilePath.isBlank()) {
            showStatus("⚠️ Pumili muna ng file!", false)
            return
        }
        if (githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kailangan ng GitHub Token — I-setup muna sa Admin Panel", false)
            return
        }

        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$selectedFilePath")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")

                val json = JSONObject(
                    BufferedReader(InputStreamReader(conn.inputStream)).readText()
                )
                currentSha = json.getString("sha") // ✅ I-SAVE ANG SHA!
                originalContent = String(
                    Base64.decode(
                        json.getString("content").replace("\n", ""),
                        Base64.DEFAULT
                    )
                )

                withContext(Dispatchers.Main) {
                    codeEditor.setText(originalContent)
                    showLoading(false)
                    showStatus("✅ Nai-load: $selectedFilePath — Handa nang i-edit!", true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Hindi mabasa: ${e.message}", false)
                }
            }
        }
    }

    // ==============================================
    // ✅ 2. I-SAVE SA LOKAL — SIMPLE LANG
    // ==============================================
    private fun saveLocal() {
        val content = codeEditor.text.toString()
        if (content.isBlank()) {
            showStatus("⚠️ Walang laman na ise-save!", false)
            return
        }
        val file = File(requireContext().filesDir, selectedFilePath.substringAfterLast('/'))
        file.parentFile?.mkdirs()
        file.writeText(content)
        originalContent = content
        showStatus("✅ Nai-save sa LOKAL: ${file.name}", true)
        Toast.makeText(context, "✅ Nai-save!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ 3. I-PUSH SA GITHUB — GINAYA ANG HTML NA PARAAN!
    // ==============================================
    private fun pushToGithub() {
        val content = codeEditor.text.toString()
        if (content.isBlank() || selectedFilePath.isBlank()) {
            showStatus("⚠️ I-load muna at i-edit ang file!", false)
            return
        }
        if (githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kailangan ng GitHub Token!", false)
            return
        }

        // Kumuha ng Commit Message
        val input = EditText(requireContext()).apply {
            hint = "Commit message (hal: Inayos ang istilo)"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("📤 I-PUSH SA GITHUB")
            .setMessage("I-upload ang pagbabago sa:\n$selectedFilePath")
            .setView(input)
            .setPositiveButton("I-PUSH") { _, _ ->
                val msg = input.text.toString().trim().ifEmpty { "Na-update: $selectedFilePath" }
                performPush(content, msg)
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    private fun performPush(content: String, message: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ✅ NO_WRAP = WALANG BAGONG LINYA — ITO ANG TAMA!
                val encoded = Base64.encodeToString(content.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                // ✅ BUILD REQUEST — GAYA SA HTML!
                val body = JSONObject().apply {
                    put("message", message)
                    put("content", encoded)
                    put("branch", BRANCH)
                    if (!currentSha.isNullOrEmpty()) {
                        put("sha", currentSha) // ✅ I-SAMA ANG SHA KUNG MERON!
                    }
                }.toString()

                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$selectedFilePath")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Content-Type", "application/json")

                OutputStreamWriter(conn.outputStream).use { it.write(body) }

                if (conn.responseCode in 200..201) {
                    val resp = JSONObject(
                        BufferedReader(InputStreamReader(conn.inputStream)).readText()
                    )
                    currentSha = resp.optJSONObject("commit")?.optString("sha")
                    originalContent = content
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showStatus("☁️✅ MATAGUMPAY NA-UPLOAD SA GITHUB!", true)
                    }
                } else {
                    throw Exception("HTTP ${conn.responseCode}: ${conn.responseMessage}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Nabigo: ${e.message}", false)
                }
            }
        }
    }

    private fun showStatus(msg: String, success: Boolean) {
        statusText.text = msg
        statusText.setTextColor(if (success) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLoad.isEnabled = !show
        btnSaveLocal.isEnabled = !show
        btnPushGithub.isEnabled = !show
        btnRefresh.isEnabled = !show
    }
}

// ==================================================
//try  FILE: FileEditorFragment.kt — ✅ PUTI ANG TEKSTO SA DROPDOWN! MAKIKITA NA!
// VERSION: 3.1.1 — ✅ HINDI NA ITIM — LUMINAW NA ANG LAHAT NG PILIAN!
// UPDATED: 2026-09-22 — TAMA ANG KULAY NG TEKSTO SA SPINNER!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import org.json.JSONArray
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
    private lateinit var folderSelect: Spinner
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
    private var currentFolderPath = "app/"
    private var selectedFilePath = ""
    private var currentSha: String? = null
    private var originalContent = ""
    private val allFiles = mutableListOf<FileItem>()

    data class FileItem(val path: String, val name: String, val type: String)

    companion object {
        const val PREFS_NAME = "github_prefs"
        const val REPO_OWNER_KEY = "repo_owner"
        const val REPO_NAME_KEY = "repo_name"
        const val BASE_URL = "https://api.github.com/repos/"
        const val BRANCH = "main"
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
            setPadding(16, 16, 16, 30)
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
            ).apply { setMargins(0, 0, 0, 16) }

            addView(TextView(requireContext()).apply {
                text = "✏️ FILE EDITOR"
                textSize = 22f
                setTextColor(0xFF40E0D0.toInt())
                setTypeface(null, android.graphics.Typeface.BOLD)
            })
            addView(TextView(requireContext()).apply {
                text = "I-load → I-edit → I-save/Push"
                textSize = 13f
                setTextColor(0xFF888888.toInt())
                setPadding(0, 4, 0, 0)
            })
        })

        // ===== FOLDER LABEL =====
        main.addView(TextView(requireContext()).apply {
            text = "📁 Piliin ang Folder:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt()) // ✅ PUTI!
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 0, 0, 6)
        })

        // ===== FOLDER SPINNER — PUTI ANG TEKSTO =====
        folderSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
            setBackgroundColor(0xFF1A1A2E.toInt())
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                    currentFolderPath = getItemAtPosition(pos).toString()
                    loadFileListFromFolder()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
        main.addView(folderSelect)

        // ===== FILE LABEL =====
        main.addView(TextView(requireContext()).apply {
            text = "📄 Piliin ang File:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt()) // ✅ PUTI!
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 4, 0, 6)
        })

        // ===== FILE SPINNER — PUTI ANG TEKSTO =====
        fileSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
            setBackgroundColor(0xFF1A1A2E.toInt())
        }
        main.addView(fileSelect)

        // ===== BUTTON: I-LOAD =====
        btnLoad = Button(requireContext()).apply {
            text = "📥 I-LOAD MULA SA GITHUB"
            setBackgroundColor(0xFF1976D2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            minHeight = 56
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
            setOnClickListener { loadFromGithub() }
        }
        main.addView(btnLoad)

        // ===== EDITOR =====
        codeEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt()) // ✅ PUTI ANG TEKSTO SA EDITOR
            setHintTextColor(0xFF888888.toInt())
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

        // ===== BUTTON ROW =====
        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        btnSaveLocal = Button(requireContext()).apply {
            text = "💾 I-SAVE LOKAL"
            setBackgroundColor(0xFF2E7D32.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 54
            setPadding(12, 10, 12, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { saveLocal() }
        }

        btnPushGithub = Button(requireContext()).apply {
            text = "☁️ I-PUSH SA GITHUB"
            setBackgroundColor(0xFF7B1FA2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 54
            setPadding(12, 10, 12, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { pushToGithub() }
        }

        btnRefresh = Button(requireContext()).apply {
            text = "🔄 I-REFRESH"
            setBackgroundColor(0xFF00BFA5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 54
            setPadding(12, 10, 12, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { loadFileListFromFolder() }
        }

        btnRow.addView(btnSaveLocal)
        btnRow.addView(btnPushGithub)
        btnRow.addView(btnRefresh)
        main.addView(btnRow)

        // ===== STATUS =====
        statusText = TextView(requireContext()).apply {
            text = "⏳ Kinakarga ang listahan..."
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

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadConfig()
        initFolderList()

        return root
    }

    // ===== CUSTOM ADAPTER — PUTI ANG TEKSTO SA DROPDOWN =====
    private fun createWhiteTextAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE) // ✅ PUTI SA NAKAPILING
                v.textSize = 14f
                v.setPadding(16, 12, 16, 12)
                return v
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE) // ✅ PUTI SA LAHAT NG PILIAN
                v.setBackgroundColor(0xFF1E1E2F.toInt())
                v.textSize = 14f
                v.setPadding(16, 14, 16, 14)
                return v
            }
        }
    }

    private fun loadConfig() {
        repoOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        repoName = prefs.getString(REPO_NAME_KEY, "") ?: ""
        githubToken = GithubManagerFragment.getDecryptedToken(requireContext())

        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ Kulang ang GitHub Token/Repo — Lokal lang muna", false)
        } else {
            showStatus("✅ Konektado: $repoOwner/$repoName", true)
        }
    }

    private fun initFolderList() {
        val folders = listOf(
            "app/",
            "app/src/",
            "app/src/main/",
            "app/src/main/java/",
            "app/src/main/java/com/martodosko/studio/",
            "app/src/main/res/",
            "app/src/main/res/layout/",
            "app/src/main/assets/",
            "docs/"
        )
        folderSelect.adapter = createWhiteTextAdapter(folders) // ✅ PUTI ANG TEKSTO!
    }

    // ==============================================
    // ✅ KUKUHA NG LAHAT NG FILE SA NAPILING FOLDER
    // ==============================================
    private fun loadFileListFromFolder() {
        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ I-setup muna ang GitHub Token", false)
            return
        }

        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$currentFolderPath")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")

                val response = BufferedReader(InputStreamReader(conn.inputStream)).readText()
                val jsonArray = JSONArray(response)

                allFiles.clear()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val type = item.getString("type")
                    val name = item.getString("name")
                    val path = item.getString("path")

                    if (type == "file") {
                        allFiles.add(FileItem(path, getFileIcon(name) + " " + name, type))
                    }
                }

                withContext(Dispatchers.Main) {
                    val displayNames = allFiles.map { it.name }
                    if (displayNames.isEmpty()) {
                        fileSelect.adapter = createWhiteTextAdapter(listOf("— Walang file —"))
                        showStatus("📭 Walang file sa napiling folder", false)
                    } else {
                        fileSelect.adapter = createWhiteTextAdapter(displayNames) // ✅ PUTI ANG TEKSTO!
                        showStatus("✅ ${allFiles.size} file nakita sa $currentFolderPath", true)
                    }
                    showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Hindi mabasa ang listahan: ${e.message}", false)
                }
            }
        }
    }

    private fun getFileIcon(name: String): String {
        return when {
            name.endsWith(".kt") -> "🔷"
            name.endsWith(".xml") -> "🔶"
            name.endsWith(".html") -> "🌐"
            name.endsWith(".json") -> "📋"
            name.endsWith(".md") -> "📝"
            name.endsWith(".gradle") -> "⚙️"
            name.endsWith(".css") -> "🎨"
            name.endsWith(".js") -> "⚡"
            else -> "📄"
        }
    }

    // ==============================================
    // ✅ I-LOAD ANG NAPILING FILE
    // ==============================================
    private fun loadFromGithub() {
        val pos = fileSelect.selectedItemPosition
        if (pos == AdapterView.INVALID_POSITION || pos >= allFiles.size) {
            showStatus("⚠️ Pumili muna ng file!", false)
            return
        }

        selectedFilePath = allFiles[pos].path
        if (githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kailangan ng GitHub Token!", false)
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
                currentSha = json.getString("sha")
                originalContent = String(
                    Base64.decode(
                        json.getString("content").replace("\n", ""),
                        Base64.DEFAULT
                    )
                )

                withContext(Dispatchers.Main) {
                    codeEditor.setText(originalContent)
                    showLoading(false)
                    showStatus("✅ Nai-load: $selectedFilePath", true)
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
    // ✅ I-SAVE SA LOKAL
    // ==============================================
    private fun saveLocal() {
        val content = codeEditor.text.toString()
        if (content.isBlank() || selectedFilePath.isBlank()) {
            showStatus("⚠️ I-load muna ang file!", false)
            return
        }
        val file = File(requireContext().filesDir, selectedFilePath.substringAfterLast('/'))
        file.parentFile?.mkdirs()
        file.writeText(content)
        originalContent = content
        showStatus("✅ Nai-save lokal: ${file.name}", true)
        Toast.makeText(context, "✅ Nai-save!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ I-PUSH SA GITHUB
    // ==============================================
    private fun pushToGithub() {
        val content = codeEditor.text.toString()
        if (content.isBlank() || selectedFilePath.isBlank()) {
            showStatus("⚠️ I-load at i-edit muna ang file!", false)
            return
        }
        if (githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kailangan ng GitHub Token!", false)
            return
        }

        val input = EditText(requireContext()).apply {
            hint = "Commit message"
            setTextColor(Color.WHITE)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("📤 I-PUSH SA GITHUB")
            .setMessage("Papalitan ang:\n$selectedFilePath")
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
                val encoded = Base64.encodeToString(content.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                val body = JSONObject().apply {
                    put("message", message)
                    put("content", encoded)
                    put("branch", BRANCH)
                    if (!currentSha.isNullOrEmpty()) {
                        put("sha", currentSha)
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
                        showStatus("☁️✅ MATAGUMPAY NA-UPLOAD!", true)
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

// ==================================================
// FILE: FileEditorFragment.kt — ✅ MALINAW NA ANG LAHAT NG BUTTON LABEL!
// VERSION: 2.0.2 — ✅ MAS MALUWAG NA BUTTONS! HINDI NA TUMATAGO ANG TEKSTO!
// UPDATED: 2026-09-21 — createButton() LANG ANG INAYOS! LAHAT NG IBA GANOON PA RIN!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
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
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class FileEditorFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var fileBrowser: LinearLayout
    private lateinit var codeEditor: EditText
    private lateinit var currentPath: TextView
    private lateinit var btnSave: Button
    private lateinit var btnCommit: Button
    private lateinit var btnPull: Button
    private lateinit var btnPush: Button
    private lateinit var btnRefresh: Button
    private lateinit var btnBack: Button
    private lateinit var btnLocalToGithub: Button
    private lateinit var btnGithubToLocal: Button
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar

    private var currentDir: File? = null
    private var selectedFile: File? = null
    private var githubToken: String? = null
    private var repoOwner = ""
    private var repoName = ""
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    companion object {
        const val PREFS_NAME = "github_prefs"
        const val ENCRYPTED_TOKEN_KEY = "encrypted_github_token"
        const val REPO_OWNER_KEY = "repo_owner"
        const val REPO_NAME_KEY = "repo_name"
        const val BASE_URL = "https://api.github.com/repos/"
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
            setPadding(16, 16, 16, 24)
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(mainContainer)

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // ==============================================
        // 🎨 HEADER
        // ==============================================
        mainContainer.addView(createHeader())

        // ==============================================
        // 📁 CURRENT PATH
        // ==============================================
        currentPath = TextView(requireContext()).apply {
            text = "📁 Nagkakarga..."
            textSize = 13f
            setTextColor(0xFF40E0D0.toInt())
            setBackgroundColor(0xFF1E1E2F.toInt())
            setPadding(12, 10, 12, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }
        mainContainer.addView(currentPath)

        // ==============================================
        // 📁 FILE BROWSER LABEL
        // ==============================================
        val browserLabel = TextView(requireContext()).apply {
            text = "📂 MGA FILE"
            textSize = 14f
            setTextColor(0xFFCCCCCC.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 0, 0, 8)
        }
        mainContainer.addView(browserLabel)

        // ==============================================
        // 📁 FILE BROWSER AREA
        // ==============================================
        fileBrowser = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1A1A2E.toInt())
            setPadding(8, 8, 8, 8)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainContainer.addView(fileBrowser)

        // ==============================================
        // ✏️ EDITOR LABEL
        // ==============================================
        val editorLabel = TextView(requireContext()).apply {
            text = "✏️ EDITOR"
            textSize = 14f
            setTextColor(0xFFCCCCCC.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 0, 0, 8)
        }
        mainContainer.addView(editorLabel)

        // ==============================================
        // ✏️ EDITOR
        // ==============================================
        codeEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFE0E0E0.toInt())
            setHintTextColor(0xFF666666.toInt())
            textSize = 13f
            setPadding(12, 12, 12, 12)
            setHint("Pumili ng file mula sa itaas...")
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                350
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainContainer.addView(codeEditor)

        // ==============================================
        // 🔘 BUTTON ROW 1
        // ==============================================
        val btnRow1 = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }

        btnSave = createButton("💾 SAVE", 0xFF2E7D32.toInt())
        btnCommit = createButton("📝 COMMIT", 0xFFFF9800.toInt())
        btnPull = createButton("📥 PULL", 0xFF1976D2.toInt())
        btnPush = createButton("📤 PUSH", 0xFF7B1FA2.toInt())
        btnRefresh = createButton("🔄", 0xFF00BFA5.toInt())
        btnBack = createButton("⬆️", 0xFF757575.toInt())

        btnRow1.addView(btnSave)
        btnRow1.addView(btnCommit)
        btnRow1.addView(btnPull)
        btnRow1.addView(btnPush)
        btnRow1.addView(btnRefresh)
        btnRow1.addView(btnBack)
        mainContainer.addView(btnRow1)

        // ==============================================
        // 🔘 BUTTON ROW 2
        // ==============================================
        val btnRow2 = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        btnLocalToGithub = createButton("📤 LOCAL→GITHUB", 0xFF512DA8.toInt())
        btnGithubToLocal = createButton("📥 GITHUB→LOCAL", 0xFF00796B.toInt())

        btnRow2.addView(btnLocalToGithub)
        btnRow2.addView(btnGithubToLocal)
        mainContainer.addView(btnRow2)

        // ==============================================
        // 📊 STATUS + PROGRESS
        // ==============================================
        statusText = TextView(requireContext()).apply {
            text = "⏳ Kinakarga ang GitHub config..."
            textSize = 13f
            setTextColor(0xFFFFA500.toInt())
            setPadding(0, 8, 0, 8)
        }
        mainContainer.addView(statusText)

        progressBar = ProgressBar(requireContext()).apply {
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        mainContainer.addView(progressBar)

        loadGithubConfig()
        setupButtons()
        openBaseDirectory()

        return root
    }

    // ==============================================
    // ✅ HEADER
    // ==============================================
    private fun createHeader(): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 24)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }

            addView(TextView(requireContext()).apply {
                text = "📂 FILE EDITOR"
                textSize = 22f
                setTextColor(0xFF40E0D0.toInt())
                setTypeface(null, android.graphics.Typeface.BOLD)
            })
            addView(TextView(requireContext()).apply {
                text = "I-edit at pamahalaan ang mga file — Local + GitHub"
                textSize = 12f
                setTextColor(0xFF888888.toInt())
                setPadding(0, 4, 0, 0)
            })
        }
    }

    // ==============================================
    // ✅ INAYOS NA — MALINAW NA ANG LAHAT NG LABEL!
    // ==============================================
    private fun createButton(text: String, color: Int): Button {
        return Button(requireContext()).apply {
            this.text = text
            textSize = 10.5f // ✅ Kasya lahat ng label
            setTextColor(0xFFFFFFFF.toInt()) // ✅ Puti — laging makikita!
            setBackgroundColor(color)
            setPadding(6, 12, 6, 12) // ✅ Maluwag sa loob
            
            // ✅ Pantay-pantay na lapad — hindi siksikan
            layoutParams = LinearLayout.LayoutParams(
                0, 48, 1f
            ).apply { setMargins(3, 6, 3, 6) } // ✅ Hiwalay ang bawat button
        }
    }

    // ==============================================
    // ✅ GITHUB CONFIG
    // ==============================================
    private fun loadGithubConfig() {
        repoOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        repoName = prefs.getString(REPO_NAME_KEY, "") ?: ""

        githubToken = try {
            val activity = activity as? AdminPanelActivity
            activity?.getGithubToken()
        } catch (e: Exception) {
            null
        }

        when {
            githubToken.isNullOrEmpty() -> {
                showStatus("⚠️ GitHub Token hindi naka-setup — Local mode lang", false)
            }
            repoOwner.isEmpty() || repoName.isEmpty() -> {
                showStatus("⚠️ Repository Owner/Name kulang — Local mode lang", false)
            }
            else -> {
                showStatus("✅ Handa na — GitHub konektado: $repoOwner/$repoName", true)
            }
        }
    }

    private fun setupButtons() {
        btnSave.setOnClickListener { saveCurrentFile() }
        btnCommit.setOnClickListener { showCommitDialog() }
        btnPull.setOnClickListener { pullFromGithub() }
        btnPush.setOnClickListener { pushToGithub() }
        btnRefresh.setOnClickListener { refreshFileList() }
        btnBack.setOnClickListener { navigateUp() }
        btnLocalToGithub.setOnClickListener { uploadLocalToGithub() }
        btnGithubToLocal.setOnClickListener { downloadGithubToLocal() }
    }

    // ==============================================
    // 📁 FILE SYSTEM
    // ==============================================
    private fun openBaseDirectory() {
        val baseDir = File(requireContext().filesDir, "project")
        if (!baseDir.exists()) baseDir.mkdirs()
        currentDir = baseDir
        refreshFileList()
    }

    private fun refreshFileList() {
        fileBrowser.removeAllViews()
        selectedFile = null
        codeEditor.visibility = View.GONE
        codeEditor.setText("")

        val dir = currentDir ?: return
        currentPath.text = "📁 ${dir.absolutePath}"

        // Parent folder
        if (dir.parentFile != null) {
            addFileItem("📂 ..", isDir = true, isUp = true)
        }

        // Folders first
        dir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name.lowercase() }
            ?.forEach { addFileItem("📂 ${it.name}", isDir = true, file = it) }

        // Files next
        dir.listFiles()
            ?.filter { it.isFile }
            ?.sortedBy { it.name.lowercase() }
            ?.forEach { addFileItem(getFileIcon(it.name) + " " + it.name, isDir = false, file = it) }
    }

    private fun getFileIcon(name: String): String {
        return when {
            name.endsWith(".kt") -> "🔷"
            name.endsWith(".xml") -> "🔶"
            name.endsWith(".html") -> "🌐"
            name.endsWith(".json") -> "📋"
            name.endsWith(".md") -> "📝"
            name.endsWith(".gradle") -> "⚙️"
            else -> "📄"
        }
    }

    private fun addFileItem(label: String, isDir: Boolean, isUp: Boolean = false, file: File? = null) {
        val item = TextView(requireContext()).apply {
            text = label
            textSize = 15f
            setPadding(32, 16, 16, 16)
            setTextColor(if (isDir) 0xFF40E0D0.toInt() else 0xFFFFFFFF.toInt())
            setBackgroundColor(0x00000000)
            isClickable = true
            isFocusable = true

            setOnClickListener {
                when {
                    isUp -> navigateUp()
                    isDir && file != null -> {
                        currentDir = file
                        refreshFileList()
                    }
                    file != null -> openFile(file)
                }
            }
        }
        fileBrowser.addView(item)
    }

    private fun navigateUp() {
        val parent = currentDir?.parentFile
        if (parent != null) {
            currentDir = parent
            refreshFileList()
        }
    }

    private fun openFile(file: File) {
        selectedFile = file
        codeEditor.visibility = View.VISIBLE
        codeEditor.setText(file.readText())
        showStatus("✅ Nabuksan: ${file.name}", true)
    }

    private fun saveCurrentFile() {
        val file = selectedFile
        if (file == null) {
            Toast.makeText(context, "⚠️ Pumili muna ng file!", Toast.LENGTH_SHORT).show()
            return
        }
        file.writeText(codeEditor.text.toString())
        showStatus("✅ Nai-save: ${file.name}", true)
        Toast.makeText(context, "✅ Nai-save!", Toast.LENGTH_SHORT).show()
    }

    private fun showCommitDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Commit message (hal: Inayos ang bug)"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("📝 Commit Message")
            .setView(input)
            .setPositiveButton("I-Commit") { _, _ ->
                val msg = input.text.toString().trim()
                if (msg.isNotEmpty()) {
                    showStatus("✅ Commit message: $msg", true)
                }
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    // ==============================================
    // 🌐 GITHUB — DOWNLOAD / UPLOAD
    // ==============================================
    private fun downloadGithubToLocal() {
        if (githubToken.isNullOrEmpty()) {
            Toast.makeText(context, "⚠️ I-setup muna ang GitHub Token!", Toast.LENGTH_SHORT).show()
            return
        }

        val files = arrayOf(
            "app/src/main/java/com/martodosko/studio/SideMenu.kt",
            "app/src/main/res/layout/side_menu.xml",
            "app/src/main/java/com/martodosko/studio/AdminPanelActivity.kt",
            "app/src/main/AndroidManifest.xml",
            "README.md"
        )

        AlertDialog.Builder(requireContext())
            .setTitle("📥 Piliin ang file mula GitHub")
            .setItems(files) { _, which ->
                downloadFileFromGithub(files[which])
            }
            .show()
    }

    private fun downloadFileFromGithub(path: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$path")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")

                val response = BufferedReader(InputStreamReader(conn.inputStream))
                val json = JSONObject(response.readText())
                val content = android.util.Base64.decode(
                    json.getString("content").replace("\n", ""),
                    android.util.Base64.DEFAULT
                )

                val localFile = File(currentDir, path.substringAfterLast('/'))
                localFile.parentFile?.mkdirs()
                localFile.writeBytes(content)

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    refreshFileList()
                    showStatus("✅ Na-download: ${localFile.name}", true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Nabigo ang download: ${e.message}", false)
                }
            }
        }
    }

    private fun uploadLocalToGithub() {
        val file = selectedFile
        if (file == null || githubToken.isNullOrEmpty()) {
            Toast.makeText(context, "⚠️ Pumili ng file at siguraduhing naka-setup ang GitHub!", Toast.LENGTH_SHORT).show()
            return
        }
        showStatus("ℹ️ Ihanda ang upload sa GitHub...", true)
    }

    private fun pullFromGithub() {
        downloadGithubToLocal()
    }

    private fun pushToGithub() {
        uploadLocalToGithub()
    }

    private fun showStatus(msg: String, success: Boolean) {
        statusText.text = msg
        statusText.setTextColor(if (success) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}

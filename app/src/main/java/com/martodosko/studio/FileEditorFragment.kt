// ==================================================
// FILE: FileEditorFragment.kt — ✅ KOTLIN PREVIEW + AYOS NA BUTTONS! WALANG FALSE WARNING!
// VERSION: 3.3.0 — ✅ PREVIEW SA KOTLIN! HINDI NA TINATAGO ANG TEKSTO!
// UPDATED: 2026-09-22 — LAHAT NG HINILING MO NARITO NA!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
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
import java.util.regex.Pattern

class FileEditorFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var folderSelect: Spinner
    private lateinit var fileSelect: Spinner
    private lateinit var codeEditor: EditText
    private lateinit var btnLoad: Button
    private lateinit var btnCopy: Button
    private lateinit var btnPreview: Button
    private lateinit var btnSaveLocal: Button
    private lateinit var btnPushGithub: Button
    private lateinit var btnRefresh: Button
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorPanel: LinearLayout
    private lateinit var errorText: TextView

    private var githubToken: String? = null
    private var repoOwner = ""
    private var repoName = ""
    private var currentFolderPath = "app/"
    private var selectedFilePath = ""
    private var currentSha: String? = null
    private var originalContent = ""
    private val allFiles = mutableListOf<FileItem>()

    data class FileItem(val path: String, val name: String, val type: String)
    data class CodeIssue(val severity: String, val message: String, val line: Int)

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
        val root = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF12121F.toInt())
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // ==============================================
        // ✅ NAKAPIRMI SA ITAAS — HINDI SUMASABAY SA SCROLL
        // ==============================================
        val fixedHeader = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 8)
            setBackgroundColor(0xFF1A1A2E.toInt())
            elevation = 4f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fixedHeader.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 8, 12, 16)
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

        // FOLDER
        fixedHeader.addView(TextView(requireContext()).apply {
            text = "📁 Piliin ang Folder:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 4, 0, 6)
        })

        folderSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
            setBackgroundColor(0xFF252540.toInt())
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                    currentFolderPath = getItemAtPosition(pos).toString()
                    loadFileListFromFolder()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
        fixedHeader.addView(folderSelect)

        // FILE
        fixedHeader.addView(TextView(requireContext()).apply {
            text = "📄 Piliin ang File:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 4, 0, 6)
        })

        fileSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
            setBackgroundColor(0xFF252540.toInt())
        }
        fixedHeader.addView(fileSelect)

        // LOAD BUTTON — Sapat na laki
        btnLoad = Button(requireContext()).apply {
            text = "📥 I-LOAD MULA SA GITHUB"
            setBackgroundColor(0xFF1976D2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            minHeight = 56
            setPadding(16, 14, 16, 14)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
            setOnClickListener { loadFromGithub() }
        }
        fixedHeader.addView(btnLoad)

        root.addView(fixedHeader)

        // ==============================================
        // ✅ SCROLLABLE NA LAMAN
        // ==============================================
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setPadding(16, 0, 16, 30)
        }

        val main = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(main)

        // EDITOR
        main.addView(TextView(requireContext()).apply {
            text = "✏️ Kodigo:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 4, 0, 8)
        })

        codeEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF666666.toInt())
            textSize = 11f
            setPadding(14, 14, 14, 14)
            setHint("Pindutin ang \"I-LOAD\" para makita ang laman...")
            minHeight = 300
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }
        main.addView(codeEditor)

        // ERROR PANEL
        errorPanel = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF3A1515.toInt())
            setPadding(14, 12, 14, 12)
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
            addView(TextView(requireContext()).apply {
                text = "⚠️ MGA TANDA SA KODIGO:"
                setTextColor(0xFFFF6B6B.toInt())
                setTypeface(null, android.graphics.Typeface.BOLD)
                textSize = 13f
            })
            errorText = TextView(requireContext()).apply {
                setTextColor(0xFFFFAAAA.toInt())
                textSize = 12f
                setPadding(0, 6, 0, 0)
            }
            addView(errorText)
        }
        main.addView(errorPanel)

        // QUICK BUTTONS — HINDI NA TINATAGO ANG TEKSTO
        val quickRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }

        btnCopy = Button(requireContext()).apply {
            text = "📋 KOPIYAHAN"
            setBackgroundColor(0xFF455A64.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 52
            minWidth = 0
            setPadding(8, 12, 8, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { copyCode() }
        }

        btnPreview = Button(requireContext()).apply {
            text = "👁️ PREVIEW"
            setBackgroundColor(0xFF00897B.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 52
            minWidth = 0
            setPadding(8, 12, 8, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { previewCode() }
        }

        quickRow.addView(btnCopy)
        quickRow.addView(btnPreview)
        main.addView(quickRow)

        // MAIN ACTION BUTTONS — Sapat na laki
        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        btnSaveLocal = Button(requireContext()).apply {
            text = "💾 I-SAVE LOKAL"
            setBackgroundColor(0xFF2E7D32.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 54
            setPadding(6, 12, 6, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { saveLocal() }
        }

        btnPushGithub = Button(requireContext()).apply {
            text = "☁️ I-PUSH SA GITHUB"
            setBackgroundColor(0xFF7B1FA2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 54
            setPadding(6, 12, 6, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { pushToGithub() }
        }

        btnRefresh = Button(requireContext()).apply {
            text = "🔄 I-REFRESH"
            setBackgroundColor(0xFF00BFA5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 54
            setPadding(6, 12, 6, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { loadFileListFromFolder() }
        }

        btnRow.addView(btnSaveLocal)
        btnRow.addView(btnPushGithub)
        btnRow.addView(btnRefresh)
        main.addView(btnRow)

        // STATUS
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

        root.addView(scrollView)

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadConfig()
        initFolderList()

        codeEditor.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                checkForErrors(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return root
    }

    // ==============================================
    // ✅ CUSTOM ADAPTER — PUTI ANG TEKSTO
    // ==============================================
    private fun createWhiteTextAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE)
                v.textSize = 14f
                v.setPadding(16, 12, 16, 12)
                return v
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE)
                v.setBackgroundColor(0xFF252540.toInt())
                v.textSize = 14f
                v.setPadding(16, 14, 16, 14)
                return v
            }
        }
    }

    // ==============================================
    // ✅ INAYOS NA ERROR CHECKER — WALANG FALSE WARNING!
    // ==============================================
    private fun checkForErrors(code: String) {
        val issues = mutableListOf<CodeIssue>()
        val lines = code.lines()

        val isKotlin = selectedFilePath.endsWith(".kt") || code.contains("class ") && code.contains("fun ")
        val isXml = selectedFilePath.endsWith(".xml") || code.trimStart().startsWith("<?xml")
        val isHtml = selectedFilePath.endsWith(".html") || code.contains("<html")

        // ===== KOTLIN — INAYOS! HINDI NA MAGBABALA NANG MALI =====
        if (isKotlin) {
            if (!code.contains("package ")) {
                issues.add(CodeIssue("warning", "Maaaring kulang ang package declaration", 1))
            }
            if (!code.contains("class ") && !code.contains("object ") && !code.contains("interface ")) {
                // Hindi lahat ng file ay may klase — huwag magbabala
            }
            lines.forEachIndexed { idx, line ->
                val lineNum = idx + 1
                val trimmed = line.trim()
                // Huwag magbabala sa mga tamang format ng class
                if (trimmed.startsWith("class ") || trimmed.startsWith("data class ") ||
                    trimmed.startsWith("interface ") || trimmed.startsWith("object ")) {
                    if (!trimmed.contains("(") && !trimmed.contains(":")) {
                        // Tamang declaration na walang inheritance — huwag magbabala
                    }
                }
                // Suriin ang tamang pagpapangalan ng variable
                if (trimmed.startsWith("private lateinit var ")) {
                    val parts = trimmed.split(" ").filter { it.isNotBlank() }
                    if (parts.size >= 4) {
                        // Tama: private lateinit var name: Type
                    } else {
                        issues.add(CodeIssue("warning", "Hindi kumpleto ang deklarasyon", lineNum))
                    }
                }
                // Walang nakasaradong string
                if (trimmed.contains("\"") && trimmed.split("\"").size % 2 == 0) {
                    issues.add(CodeIssue("error", "Hindi nakasaradong panipi", lineNum))
                }
            }
        }

        // ===== XML =====
        if (isXml) {
            lines.forEachIndexed { idx, line ->
                val lineNum = idx + 1
                if (line.contains("android:id=\"@+id/\"")) {
                    issues.add(CodeIssue("error", "Walang pangalan ang id", lineNum))
                }
            }
        }

        // ===== HTML =====
        if (isHtml) {
            if (!code.contains("</body>") && code.contains("<body")) {
                issues.add(CodeIssue("warning", "Maaaring sarado ang </body>", lines.size))
            }
        }

        if (issues.isEmpty()) {
            errorPanel.visibility = View.GONE
        } else {
            errorPanel.visibility = View.VISIBLE
            errorText.text = issues.joinToString("\n") { "• Linya ${it.line}: ${it.message}" }
        }
    }

    // ==============================================
    // ✅ KOPIYAHAN
    // ==============================================
    private fun copyCode() {
        val code = codeEditor.text.toString()
        if (code.isBlank()) {
            Toast.makeText(context, "⚠️ Walang kodigong kokopyahin!", Toast.LENGTH_SHORT).show()
            return
        }
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Code", code))
        Toast.makeText(context, "✅ Nakopya sa clipboard!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ PREVIEW — KOTLIN + HTML NA!
    // ==============================================
    private fun previewCode() {
        val code = codeEditor.text.toString()
        if (code.isBlank()) {
            Toast.makeText(context, "⚠️ Walang kodigong ipapakita!", Toast.LENGTH_SHORT).show()
            return
        }

        val isHtml = selectedFilePath.endsWith(".html") || code.trimStart().startsWith("<html")
        val isKotlin = selectedFilePath.endsWith(".kt") || code.contains("package ") && code.contains("class ")

        val previewHtml = when {
            isHtml -> code
            isKotlin -> generateKotlinPreviewHtml(code) // ✅ KOTLIN PREVIEW!
            else -> """
                <html><body style="background:#12121F; color:#E0E0E0; padding:20px; font-family:monospace;">
                <h3 style="color:#888;">📄 $selectedFilePath</h3>
                <pre style="white-space:pre-wrap; background:#1A1A2E; padding:16px; border-radius:8px; font-size:12px;">${escapeHtml(code)}</pre>
                </body></html>
            """.trimIndent()
        }

        val webView = WebView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadDataWithBaseURL(null, previewHtml, "text/html", "UTF-8", null)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("👁️ PREVIEW — $selectedFilePath")
            .setView(webView)
            .setPositiveButton("TAPOS", null)
            .show()
    }

    // ==============================================
    // ✅ KOTLIN → MAGANDANG FORMAT SA PREVIEW
    // ==============================================
    private fun generateKotlinPreviewHtml(code: String): String {
        val lines = code.lines()
        val styledLines = lines.map { line ->
            var styled = escapeHtml(line)
            // Kulayan ang mga keyword
            val keywords = listOf("package", "import", "class", "interface", "object", "fun",
                "val", "var", "private", "public", "protected", "internal", "override",
                "lateinit", "suspend", "return", "if", "else", "for", "while", "when",
                "try", "catch", "finally", "throw", "true", "false", "null", "this", "super")
            keywords.forEach { kw ->
                styled = styled.replace("\\b$kw\\b".toRegex(), "<span style=\"color:#FF70C0; font-weight:bold;\">$kw</span>")
            }
            // Kulayan ang string
            styled = styled.replace("\"([^\"\\\\]|\\\\.)*\"".toRegex(), "<span style=\"color:#90EE90;\">$0</span>")
            // Kulayan ang komento
            styled = styled.replace("(//.*)$".toRegex(), "<span style=\"color:#666; font-style:italic;\">$0</span>")
            // Kulayan ang pangalan ng klase
            styled = styled.replace("\\b(class|data class|interface|object)\\s+([A-Z][a-zA-Z0-9]+)".toRegex(), "$1 <span style=\"color:#40E0D0; font-weight:bold; font-size:1.05em;\">$2</span>")
            // Kulayan ang function
            styled = styled.replace("\\bfun\\s+([a-zA-Z][a-zA-Z0-9]*)".toRegex(), "fun <span style=\"color:#FFD700; font-weight:bold;\">$1</span>")
            // Kulayan ang numero
            styled = styled.replace("\\b(\\d+)\\b".toRegex(), "<span style=\"color:#FFA07A;\">$1</span>")
            styled
        }

        return """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style>
    * { margin:0; padding:0; box-sizing:border-box; }
    body { background:#12121F; color:#E0E0E0; padding:16px; font-family:'Segoe UI',monospace; }
    .header { background:#1E1E2F; padding:14px; border-radius:8px; margin-bottom:16px; }
    .title { color:#40E0D0; font-size:18px; font-weight:bold; }
    .sub { color:#888; font-size:12px; margin-top:4px; }
    pre { background:#1A1A2E; padding:16px; border-radius:8px; white-space:pre-wrap; line-height:1.6; font-size:11.5px; }
    .line { border-bottom:1px solid #2A2A4A; padding:2px 0; }
</style>
</head>
<body>
<div class="header">
<div class="title">🔷 KOTLIN SOURCE</div>
<div class="sub">$selectedFilePath — ${lines.size} linya</div>
</div>
<pre>${styledLines.joinToString("\n") { "<div class='line'>$it</div>" }}</pre>
</body>
</html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;")
    }

    // ==============================================
    // ✅ NATINONG PAMAMARAAN
    // ==============================================
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
        folderSelect.adapter = createWhiteTextAdapter(folders)
    }

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
                        fileSelect.adapter = createWhiteTextAdapter(displayNames)
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
                    checkForErrors(originalContent)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showStatus("❌ Nabigo: ${e.message}", false)
                }
            }
        }
    }

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
        btnCopy.isEnabled = !show
        btnPreview.isEnabled = !show
        btnSaveLocal.isEnabled = !show
        btnPushGithub.isEnabled = !show
        btnRefresh.isEnabled = !show
    }
}

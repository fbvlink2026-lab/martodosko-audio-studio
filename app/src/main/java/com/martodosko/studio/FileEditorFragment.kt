// ==================================================
// FILE: FileEditorFragment.kt — ✅ NAKAPIRMI ANG ITAAS AT IBABA! BUONG SCREEN PREVIEW!
// VERSION: 4.0.0 — ✅ NAKA-FIXED HEADER + FOOTER! CLICK PREVIEW BUTTON → PUMUNTA SA KODIGO!
// UPDATED: 2026-09-22 — AYON SA LAHAT NG UTOS MO!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
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
    lateinit var codeEditor: EditText   // ✅ Public para ma-access mula sa preview
    private lateinit var btnLoad: Button
    private lateinit var btnCopy: Button
    private lateinit var btnPaste: Button
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
    var selectedFilePath = ""   // ✅ Public
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
        // ✅ NAKAPIRMI SA ITAAS — HINDI GUMAGALAW!
        // ==============================================
        val fixedHeader = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 12, 16, 12)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fixedHeader.addView(TextView(requireContext()).apply {
            text = "✏️ FILE EDITOR"
            textSize = 22f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 4)
        })
        fixedHeader.addView(TextView(requireContext()).apply {
            text = "I-load → I-edit → I-save/Push"
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 0, 0, 12)
        })

        fixedHeader.addView(TextView(requireContext()).apply {
            text = "📁 Folder:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 4)
        })
        folderSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 48
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

        fixedHeader.addView(TextView(requireContext()).apply {
            text = "📄 File:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 4)
        })
        fileSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 48
            ).apply { setMargins(0, 0, 0, 12) }
            setBackgroundColor(0xFF252540.toInt())
        }
        fixedHeader.addView(fileSelect)

        btnLoad = Button(requireContext()).apply {
            text = "📥 I-LOAD"
            setBackgroundColor(0xFF1976D2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            minHeight = 48
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
            setOnClickListener { loadFromGithub() }
        }
        fixedHeader.addView(btnLoad)

        root.addView(fixedHeader)

        // ==============================================
        // ✅ GITNANG BAHAGI — LANG ANG NAG-I-SCROLL!
        // ==============================================
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f   // ✅ Kunin ang natirang espasyo
            )
            setPadding(16, 0, 16, 0)
            isFillViewport = true
        }

        val mainContent = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(mainContent)

        // Kodigo Box — Nasa gitna
        mainContent.addView(TextView(requireContext()).apply {
            text = "✏️ Kodigo:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        })

        codeEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF666666.toInt())
            textSize = 11f
            setPadding(14, 14, 14, 14)
            setHint("Pindutin ang \"I-LOAD\"...")
            minHeight = 280
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }
        mainContent.addView(codeEditor)

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
                setTypeface(null, Typeface.BOLD)
                textSize = 13f
            })
            errorText = TextView(requireContext()).apply {
                setTextColor(0xFFFFAAAA.toInt())
                textSize = 12f
                setPadding(0, 6, 0, 0)
            }
            addView(errorText)
        }
        mainContent.addView(errorPanel)

        // Quick Row — KOPIYA / PASTE / PREVIEW
        val quickRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }
        btnCopy = Button(requireContext()).apply {
            text = "📋 KOPIYA"
            setBackgroundColor(0xFF455A64.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 48, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { copyCode() }
        }
        btnPaste = Button(requireContext()).apply {
            text = "📌 PASTE"
            setBackgroundColor(0xFF558B2F.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 48, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { pasteCode() }
        }
        btnPreview = Button(requireContext()).apply {
            text = "👁️ PREVIEW"
            setBackgroundColor(0xFF00897B.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 48, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { previewCode() }
        }
        quickRow.addView(btnCopy)
        quickRow.addView(btnPaste)
        quickRow.addView(btnPreview)
        mainContent.addView(quickRow)

        statusText = TextView(requireContext()).apply {
            text = "⏳ Handa na..."
            textSize = 13f
            setTextColor(0xFFFFA500.toInt())
            setPadding(0, 4, 0, 8)
        }
        mainContent.addView(statusText)
        progressBar = ProgressBar(requireContext()).apply {
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        mainContent.addView(progressBar)

        root.addView(scrollView)

        // ==============================================
        // ✅ NAKAPIRMI SA ILALIM — HINDI GUMAGALAW!
        // ==============================================
        val fixedFooter = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 12, 16, 16)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        btnSaveLocal = Button(requireContext()).apply {
            text = "💾 SAVE"
            setBackgroundColor(0xFF2E7D32.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 52, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { saveLocal() }
        }
        btnPushGithub = Button(requireContext()).apply {
            text = "☁️ PUSH"
            setBackgroundColor(0xFF7B1FA2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 52, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { pushToGithub() }
        }
        btnRefresh = Button(requireContext()).apply {
            text = "🔄 REFRESH"
            setBackgroundColor(0xFF00BFA5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 52, 0.7f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { loadFileListFromFolder() }
        }
        fixedFooter.addView(btnSaveLocal)
        fixedFooter.addView(btnPushGithub)
        fixedFooter.addView(btnRefresh)
        root.addView(fixedFooter)

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadConfig()
        initFolderList()

        codeEditor.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { checkForErrors(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return root
    }

    private fun createWhiteTextAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE); v.textSize = 14f; v.setPadding(16, 12, 16, 12)
                return v
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE); v.setBackgroundColor(0xFF252540.toInt())
                v.textSize = 14f; v.setPadding(16, 14, 16, 14)
                return v
            }
        }
    }

    private fun checkForErrors(code: String) {
        val issues = mutableListOf<CodeIssue>()
        val lines = code.lines()
        if (selectedFilePath.endsWith(".kt")) {
            if (!code.contains("package ")) issues.add(CodeIssue("warning", "Kulang ang package", 1))
            lines.forEachIndexed { idx, line ->
                val s = line.trim()
                if (s.contains("\"") && s.split("\"").size % 2 == 0)
                    issues.add(CodeIssue("error", "Hindi saradong panipi", idx + 1))
            }
        }
        errorPanel.visibility = if (issues.isEmpty()) View.GONE else View.VISIBLE
        if (issues.isNotEmpty()) errorText.text = issues.joinToString("\n") { "• Linya ${it.line}: ${it.message}" }
    }

    private fun copyCode() {
        val text = codeEditor.text.toString()
        if (text.isBlank()) {
            Toast.makeText(context, "⚠️ Walang kokopyahin!", Toast.LENGTH_SHORT).show()
            return
        }
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Code", text))
        Toast.makeText(context, "✅ Nakopya!", Toast.LENGTH_SHORT).show()
    }

    private fun pasteCode() {
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = cm.primaryClip
        val text = clip?.getItemAt(0)?.text?.toString()
        if (!text.isNullOrBlank()) {
            codeEditor.setText(text)
            Toast.makeText(context, "✅ Nakapaste!", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(context, "⚠️ Walang laman ang clipboard!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ BUONG SCREEN PREVIEW + CLICK → PUMUNTA SA KODIGO!
    // ==============================================
    private fun previewCode() {
        val code = codeEditor.text.toString()
        if (code.isBlank()) {
            Toast.makeText(context, "⚠️ Walang kodigong ipapakita!", Toast.LENGTH_SHORT).show()
            return
        }

        val previewView = when {
            selectedFilePath.endsWith(".html") -> {
                android.webkit.WebView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    loadDataWithBaseURL(null, code, "text/html", "UTF-8", null)
                }
            }
            selectedFilePath.endsWith(".kt") -> buildKotlinPreview(code)
            selectedFilePath.endsWith(".xml") -> buildXmlPreview(code)
            else -> ScrollView(requireContext()).apply {
                setBackgroundColor(0xFF12121F.toInt()); setPadding(20, 20, 20, 20)
                addView(TextView(requireContext()).apply {
                    text = code; setTextColor(0xFFCCCCCC.toInt()); textSize = 11f
                    typeface = Typeface.MONOSPACE
                })
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("👁️ PREVIEW — $selectedFilePath")
            .setView(previewView)
            .setPositiveButton("TAPOS", null)
            .show()
    }

    private fun buildKotlinPreview(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFF12121F.toInt())
            setPadding(8, 8, 8, 8)
        }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scroll.addView(container)

        val className = Regex("class\\s+(\\w+)").find(code)?.groupValues?.get(1) ?: "Screen"
        val isEditor = className.contains("Editor", ignoreCase = true)
        val isLogin = className.contains("Login", ignoreCase = true)
        val isAdmin = className.contains("Admin", ignoreCase = true)

        // Header
        container.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1A1A2E.toInt())
            setPadding(20, 16, 20, 16)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(TextView(requireContext()).apply {
                text = when {
                    isAdmin -> "🔐 ADMIN PANEL"
                    isLogin -> "🔐 LOGIN"
                    isEditor -> "✏️ FILE EDITOR"
                    else -> "📱 $className"
                }
                textSize = 22f; setTextColor(0xFF40E0D0.toInt()); setTypeface(null, Typeface.BOLD)
                setGravity(Gravity.CENTER)
            })
        })

        // Content — may pindutan na pumupunta sa kodigo!
        when {
            isEditor -> {
                addClickableButton(container, "📁 Folder Selector", code, "folderSelect")
                addClickableButton(container, "📄 File Selector", code, "fileSelect")
                addClickableButton(container, "📥 I-LOAD MULA SA GITHUB", code, "btnLoad")
                addClickableButton(container, "📋 KOPIYA", code, "btnCopy")
                addClickableButton(container, "📌 PASTE", code, "btnPaste")
                addClickableButton(container, "👁️ PREVIEW", code, "btnPreview")
                addClickableButton(container, "💾 I-SAVE LOKAL", code, "btnSaveLocal")
                addClickableButton(container, "☁️ I-PUSH SA GITHUB", code, "btnPushGithub")
            }
            isLogin -> {
                addClickableButton(container, "🔐 MAG-LOGIN", code, "setPositiveButton")
            }
            isAdmin -> {
                addClickableButton(container, "👑 Owner Controls", code, "Owner")
                addClickableButton(container, "🔑 Key Management", code, "Key")
                addClickableButton(container, "👤 Users", code, "User")
            }
            else -> {
                container.addView(TextView(requireContext()).apply {
                    text = "📱 $className\n\nPindutin ang mga bahagi sa aktuwal na pagtakbo."
                    setTextColor(0xFF888888.toInt()); setGravity(Gravity.CENTER)
                    setPadding(20, 40, 20, 40)
                })
            }
        }
        return scroll
    }

    // ✅ PINDUTIN SA PREVIEW → PUMUNTA SA LINYA SA KODIGO!
    private fun addClickableButton(container: LinearLayout, label: String, code: String, searchKey: String) {
        val btn = Button(requireContext()).apply {
            text = label
            setBackgroundColor(0xFF252540.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                56
            ).apply { setMargins(16, 6, 16, 6) }
            setOnClickListener {
                val lineNo = findLineNumber(code, searchKey)
                if (lineNo > 0) {
                    moveEditorToLine(lineNo)
                } else {
                    Toast.makeText(context, "🔍 Nahanap sa kodigo: $label", Toast.LENGTH_SHORT).show()
                }
            }
        }
        container.addView(btn)
    }

    private fun findLineNumber(code: String, keyword: String): Int {
        val lines = code.lines()
        lines.forEachIndexed { idx, line ->
            if (line.contains(keyword, ignoreCase = true)) return idx + 1
        }
        return -1
    }

    private fun moveEditorToLine(lineNo: Int) {
        val text = codeEditor.text
        var pos = 0
        for (i in 1 until lineNo) {
            pos = text.indexOf('\n', pos) + 1
            if (pos <= 0) break
        }
        if (pos > 0) {
            codeEditor.setSelection(pos)
            codeEditor.requestFocus()
            Toast.makeText(context, "✅ Nalipat sa Linya $lineNo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildXmlPreview(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFF12121F.toInt()); setPadding(16, 16, 16, 16)
        }
        val container = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(container)
        container.addView(TextView(requireContext()).apply {
            text = "🔶 LAYOUT PREVIEW"; textSize = 18f; setTextColor(0xFFFFB74D.toInt())
            setTypeface(null, Typeface.BOLD); setPadding(0, 0, 0, 16)
        })
        val btnCount = Regex("<Button").findAll(code).count()
        val tvCount = Regex("<TextView").findAll(code).count()
        val etCount = Regex("<EditText").findAll(code).count()
        container.addView(LinearLayout(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt()); setPadding(20, 20, 20, 20)
            orientation = LinearLayout.VERTICAL
            addView(TextView(requireContext()).apply {
                text = "📋 Mga Elemento:"; setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD); setPadding(0, 0, 0, 12)
            })
            addView(TextView(requireContext()).apply {
                text = "• Button: $btnCount"; setTextColor(0xFF64B5F6.toInt()); textSize = 13f
            })
            addView(TextView(requireContext()).apply {
                text = "• TextView: $tvCount"; setTextColor(0xFFCCCCCC.toInt()); textSize = 13f
            })
            addView(TextView(requireContext()).apply {
                text = "• EditText: $etCount"; setTextColor(0xFF40E0D0.toInt()); textSize = 13f
            })
        })
        return scroll
    }

    private fun loadConfig() {
        repoOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        repoName = prefs.getString(REPO_NAME_KEY, "") ?: ""
        githubToken = GithubManagerFragment.getDecryptedToken(requireContext())
        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ Kulang ang GitHub Token", false)
        } else {
            showStatus("✅ Konektado: $repoOwner/$repoName", true)
        }
    }

    private fun initFolderList() {
        val folders = listOf(
            "app/", "app/src/", "app/src/main/", "app/src/main/java/",
            "app/src/main/java/com/martodosko/studio/", "app/src/main/res/",
            "app/src/main/res/layout/", "app/src/main/assets/", "docs/"
        )
        folderSelect.adapter = createWhiteTextAdapter(folders)
    }

    private fun loadFileListFromFolder() {
        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ I-setup muna ang GitHub", false)
            return
        }
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$currentFolderPath")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                val jsonArray = JSONArray(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                allFiles.clear()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    if (item.getString("type") == "file") {
                        val name = item.getString("name")
                        allFiles.add(FileItem(item.getString("path"), getFileIcon(name) + " " + name, "file"))
                    }
                }
                withContext(Dispatchers.Main) {
                    val names = allFiles.map { it.name }
                    fileSelect.adapter = createWhiteTextAdapter(if (names.isEmpty()) listOf("— Walang file —") else names)
                    showStatus("✅ ${allFiles.size} file nakita", true)
                    showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false)
                    showLoading(false)
                }
            }
        }
    }

    private fun getFileIcon(name: String) = when {
        name.endsWith(".kt") -> "🔷"
        name.endsWith(".xml") -> "🔶"
        name.endsWith(".html") -> "🌐"
        name.endsWith(".json") -> "📋"
        else -> "📄"
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
                val json = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                currentSha = json.getString("sha")
                originalContent = String(Base64.decode(json.getString("content").replace("\n", ""), Base64.DEFAULT))
                withContext(Dispatchers.Main) {
                    codeEditor.setText(originalContent)
                    showStatus("✅ Nai-load", true)
                    checkForErrors(originalContent)
                    showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false)
                    showLoading(false)
                }
            }
        }
    }

    private fun saveLocal() {
        val content = codeEditor.text.toString()
        if (content.isBlank() || selectedFilePath.isBlank()) {
            showStatus("⚠️ I-load muna!", false)
            return
        }
        val file = File(requireContext().filesDir, selectedFilePath.substringAfterLast('/'))
        file.parentFile?.mkdirs()
        file.writeText(content)
        originalContent = content
        showStatus("✅ Nai-save: ${file.name}", true)
        Toast.makeText(context, "✅ Nai-save!", Toast.LENGTH_SHORT).show()
    }

    private fun pushToGithub() {
        val content = codeEditor.text.toString()
        if (content.isBlank() || selectedFilePath.isBlank() || githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kulang ang impormasyon", false)
            return
        }
        val input = EditText(requireContext()).apply {
            hint = "Mensahe ng pagbabago..."
            setTextColor(0xFF000000.toInt())
            setHintTextColor(0xFF888888.toInt())
            setBackgroundColor(0xFFFFFFFF.toInt())
            textSize = 14f
            setPadding(24, 18, 24, 18)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("📤 I-PUSH SA GITHUB")
            .setMessage("Papalitan:\n$selectedFilePath")
            .setView(input)
            .setPositiveButton("I-PUSH") { _, _ ->
                performPush(content, input.text.toString().trim().ifEmpty { "Na-update" })
            }
            .setNegativeButton("KANSELAHIN", null)
            .show()
    }

    private fun performPush(content: String, msg: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = JSONObject().apply {
                    put("message", msg)
                    put("content", Base64.encodeToString(content.toByteArray(Charsets.UTF_8), Base64.NO_WRAP))
                    put("branch", BRANCH)
                    if (!currentSha.isNullOrEmpty()) put("sha", currentSha)
                }.toString()
                val conn = URL("${BASE_URL}$repoOwner/$repoName/contents/$selectedFilePath").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use { it.write(body) }
                if (conn.responseCode in 200..201) {
                    currentSha = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                        .optJSONObject("commit")?.optString("sha")
                    originalContent = content
                    withContext(Dispatchers.Main) {
                        showStatus("☁️✅ MATAGUMPAY NA-UPLOAD!", true)
                        showLoading(false)
                    }
                } else throw Exception("HTTP ${conn.responseCode}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false)
                    showLoading(false)
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
        listOf(btnLoad, btnCopy, btnPaste, btnPreview, btnSaveLocal, btnPushGithub, btnRefresh).forEach {
            it.isEnabled = !show
        }
    }
}

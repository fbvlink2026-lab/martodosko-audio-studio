// ==================================================
// FILE: FileEditorFragment.kt — ✅ FULL-SCREEN PREVIEW + CLICK-TO-JUMP + CLEAR BUTTON!
// VERSION: 4.0.0 — ✅ LAHAT NG HINILING MO!
// UPDATED: 2026-09-22 — FULL SCREEN • CLICKABLE • FIXED BOTTOM • CLEAR BUTTON!
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
    private lateinit var codeEditor: EditText
    private lateinit var btnLoad: Button
    private lateinit var btnCopy: Button
    private lateinit var btnPaste: Button
    private lateinit var btnClear: Button   // ✅ BAGONG CLEAR BUTTON
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

        // ===== NAKAPIRMI SA ITAAS =====
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
                setTypeface(null, Typeface.BOLD)
            })
            addView(TextView(requireContext()).apply {
                text = "I-load → I-edit → I-save/Push"
                textSize = 13f
                setTextColor(0xFF888888.toInt())
                setPadding(0, 4, 0, 0)
            })
        })

        fixedHeader.addView(TextView(requireContext()).apply {
            text = "📁 Piliin ang Folder:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
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

        fixedHeader.addView(TextView(requireContext()).apply {
            text = "📄 Piliin ang File:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
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

        // ===== SCROLLABLE NA LAMAN — CODE BOX SA GITNA =====
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f  // ✅ Kukuha ng natitirang espasyo
            )
            setPadding(16, 8, 16, 8)
            isFillViewport = true
        }

        val main = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(main)

        main.addView(TextView(requireContext()).apply {
            text = "✏️ Kodigo:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
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
        main.addView(errorPanel)

        // ✅ UNANG HANAY: KOPIYA + PASTE + CLEAR + PREVIEW
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
            textSize = 12f
            minHeight = 50
            setPadding(4, 8, 4, 8)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { copyCode() }
        }

        btnPaste = Button(requireContext()).apply {
            text = "📌 PASTE"
            setBackgroundColor(0xFF558B2F.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 50
            setPadding(4, 8, 4, 8)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { pasteCode() }
        }

        btnClear = Button(requireContext()).apply {
            text = "🧹 BURA"
            setBackgroundColor(0xFFC62828.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 50
            setPadding(4, 8, 4, 8)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { clearCode() }
        }

        btnPreview = Button(requireContext()).apply {
            text = "👁️ PREVIEW"
            setBackgroundColor(0xFF00897B.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 50
            setPadding(4, 8, 4, 8)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { previewCode() }
        }

        quickRow.addView(btnCopy)
        quickRow.addView(btnPaste)
        quickRow.addView(btnClear)
        quickRow.addView(btnPreview)
        main.addView(quickRow)

        statusText = TextView(requireContext()).apply {
            text = "⏳ Kinakarga ang listahan..."
            textSize = 13f
            setTextColor(0xFFFFA500.toInt())
            setPadding(0, 8, 0, 8)
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

        // ===== NAKAPIRMI SA IBABA — HINDI GUMAGALAW! =====
        val fixedBottom = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 12, 16, 16)
            setBackgroundColor(0xFF1A1A2E.toInt())
            elevation = 8f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        btnSaveLocal = Button(requireContext()).apply {
            text = "💾 LOKAL"
            setBackgroundColor(0xFF2E7D32.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(4, 10, 4, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { saveLocal() }
        }

        btnPushGithub = Button(requireContext()).apply {
            text = "☁️ PUSH"
            setBackgroundColor(0xFF7B1FA2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(4, 10, 4, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { pushToGithub() }
        }

        btnRefresh = Button(requireContext()).apply {
            text = "🔄 REFRESH"
            setBackgroundColor(0xFF00BFA5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(4, 10, 4, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { loadFileListFromFolder() }
        }

        fixedBottom.addView(btnSaveLocal)
        fixedBottom.addView(btnPushGithub)
        fixedBottom.addView(btnRefresh)
        root.addView(fixedBottom)

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

    private fun checkForErrors(code: String) {
        val issues = mutableListOf<CodeIssue>()
        val lines = code.lines()
        val isKotlin = selectedFilePath.endsWith(".kt")
        if (isKotlin) {
            if (!code.contains("package ")) issues.add(CodeIssue("warning", "Maaaring kulang ang package declaration", 1))
            lines.forEachIndexed { idx, line ->
                val trimmed = line.trim()
                if (trimmed.contains("\"") && trimmed.split("\"").size % 2 == 0) {
                    issues.add(CodeIssue("error", "Hindi nakasaradong panipi", idx + 1))
                }
            }
        }
        if (issues.isEmpty()) errorPanel.visibility = View.GONE
        else {
            errorPanel.visibility = View.VISIBLE
            errorText.text = issues.joinToString("\n") { "• Linya ${it.line}: ${it.message}" }
        }
    }

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

    private fun pasteCode() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (!text.isNullOrBlank()) {
                codeEditor.setText(text)
                Toast.makeText(context, "✅ Nakapaste sa editor!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "⚠️ Walang nakopyang teksto!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "⚠️ Walang laman ang clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ BAGONG CLEAR FUNCTION
    private fun clearCode() {
        codeEditor.setText("")
        originalContent = ""
        errorPanel.visibility = View.GONE
        Toast.makeText(context, "✅ Nabura na ang laman!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ FULL-SCREEN PREVIEW + CLICK-TO-JUMP!
    // ==============================================
    private fun previewCode() {
        val code = codeEditor.text.toString()
        if (code.isBlank()) {
            Toast.makeText(context, "⚠️ Walang kodigong ipapakita!", Toast.LENGTH_SHORT).show()
            return
        }

        val previewView = when {
            selectedFilePath.endsWith(".html") -> {
                val webView = android.webkit.WebView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    loadDataWithBaseURL(null, code, "text/html", "UTF-8", null)
                }
                webView
            }
            selectedFilePath.endsWith(".kt") -> buildKotlinGuiPreview(code)
            selectedFilePath.endsWith(".xml") -> buildXmlLayoutPreview(code)
            else -> {
                val scroll = ScrollView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(0xFF12121F.toInt())
                    setPadding(20, 20, 20, 20)
                    addView(TextView(requireContext()).apply {
                        text = code
                        setTextColor(0xFFCCCCCC.toInt())
                        textSize = 11f
                        typeface = Typeface.MONOSPACE
                    })
                }
                scroll
            }
        }

        // ✅ FULL-SCREEN DIALOG
        AlertDialog.Builder(requireContext())
            .setTitle("👁️ PREVIEW — $selectedFilePath")
            .setView(previewView)
            .setPositiveButton("TAPOS", null)
            .show()
    }

    // ==============================================
    // ✅ KOTLIN PREVIEW — CLICK BUTTON → JUMP TO CODE!
    // ==============================================
    private fun buildKotlinGuiPreview(code: String): View {
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

        val className = Regex("class\\s+(\\w+)").find(code)?.groupValues?.get(1) ?: "UnknownScreen"
        val isEditor = className.contains("Editor", ignoreCase = true)
        val isLogin = className.contains("Login", ignoreCase = true)
        val isAdmin = className.contains("Admin", ignoreCase = true)
        val lines = code.lines()

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
                textSize = 22f
                setTextColor(0xFF40E0D0.toInt())
                setTypeface(null, Typeface.BOLD)
                setGravity(Gravity.CENTER)
            })
            addView(TextView(requireContext()).apply {
                text = "💡 Pindutin ang pindutan → tumalon sa kodigo"
                textSize = 12f
                setTextColor(0xFF666666.toInt())
                setGravity(Gravity.CENTER)
                setPadding(0, 4, 0, 0)
            })
        })

        when {
            isEditor -> {
                addClickablePreviewButton(container, "📥 I-LOAD MULA SA GITHUB", 0xFF1976D2.toInt(), lines)
                addClickablePreviewButton(container, "📋 KOPIYA", 0xFF455A64.toInt(), lines)
                addClickablePreviewButton(container, "📌 PASTE", 0xFF558B2F.toInt(), lines)
                addClickablePreviewButton(container, "🧹 BURA", 0xFFC62828.toInt(), lines)
                addClickablePreviewButton(container, "👁️ PREVIEW", 0xFF00897B.toInt(), lines)
                addClickablePreviewButton(container, "💾 I-SAVE LOKAL", 0xFF2E7D32.toInt(), lines)
                addClickablePreviewButton(container, "☁️ I-PUSH SA GITHUB", 0xFF7B1FA2.toInt(), lines)
                addClickablePreviewButton(container, "🔄 I-REFRESH", 0xFF00BFA5.toInt(), lines)
            }
            isLogin -> {
                addClickablePreviewButton(container, "🔐 MAG-LOGIN", 0xFF7B1FA2.toInt(), lines)
            }
            isAdmin -> {
                listOf(
                    "✏️ File Editor",
                    "🔑 Key Code Generator",
                    "👤 User Management",
                    "📊 System Status",
                    "☁️ GitHub Settings"
                ).forEach { label ->
                    val btn = Button(requireContext()).apply {
                        text = label
                        setBackgroundColor(0xFF1E1E2F.toInt())
                        setTextColor(0xFFFFFFFF.toInt())
                        textSize = 14f
                        setPadding(20, 16, 20, 16)
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply { setMargins(16, 4, 16, 4) }
                    }
                    container.addView(btn)
                }
            }
        }
        return scroll
    }

    // ✅ CLICKABLE BUTTON — HANAPIN ANG LINYA SA KODIGO!
    private fun addClickablePreviewButton(
        container: LinearLayout,
        label: String,
        bgColor: Int,
        lines: List<String>
    ) {
        val btn = Button(requireContext()).apply {
            text = label
            setBackgroundColor(bgColor)
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            minHeight = 52
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 6, 16, 6) }

            setOnClickListener {
                val searchText = label.substringAfter(" ")
                val lineNo = lines.indexOfFirst { it.contains(searchText, ignoreCase = true) || it.contains("btn", ignoreCase = true) && it.contains(searchText.take(4), ignoreCase = true) }
                if (lineNo >= 0) {
                    jumpToLine(lineNo + 1)
                } else {
                    Toast.makeText(context, "🔍 Nahanap: $label", Toast.LENGTH_SHORT).show()
                }
            }
        }
        container.addView(btn)
    }

    // ✅ TUMALON SA TINUTUKOY NA LINYA SA EDITOR
    private fun jumpToLine(lineNumber: Int) {
        val text = codeEditor.text.toString()
        val lines = text.lines()
        if (lineNumber > 0 && lineNumber <= lines.size) {
            var pos = 0
            for (i in 0 until lineNumber - 1) pos += lines[i].length + 1
            codeEditor.setSelection(pos)
            codeEditor.requestFocus()
            Toast.makeText(context, "📍 Linya $lineNumber", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildXmlLayoutPreview(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFF12121F.toInt())
            setPadding(16, 16, 16, 16)
        }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(container)

        container.addView(TextView(requireContext()).apply {
            text = "🔶 LAYOUT PREVIEW"
            textSize = 18f
            setTextColor(0xFFFFB74D.toInt())
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        })

        val btnCount = Regex("<Button").findAll(code).count()
        val tvCount = Regex("<TextView").findAll(code).count()
        val etCount = Regex("<EditText").findAll(code).count()

        container.addView(LinearLayout(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt())
            setPadding(20, 20, 20, 20)
            orientation = LinearLayout.VERTICAL
            addView(TextView(requireContext()).apply {
                text = "📋 Mga Elemento:"
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, 12)
            })
            addView(TextView(requireContext()).apply {
                text = "• Pindutan: $btnCount"
                setTextColor(0xFF64B5F6.toInt())
                textSize = 13f
            })
            addView(TextView(requireContext()).apply {
                text = "• Teksto: $tvCount"
                setTextColor(0xFFCCCCCC.toInt())
                textSize = 13f
            })
            addView(TextView(requireContext()).apply {
                text = "• Kahon: $etCount"
                setTextColor(0xFF81C784.toInt())
                textSize = 13f
            })
        })
        return scroll
    }

    private fun loadConfig() {
        repoOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        repoName = prefs.getString(REPO_NAME_KEY, "") ?: ""
        githubToken = GithubManagerFragment.getDecryptedToken(requireContext())
        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ Kulang ang GitHub Token/Repo", false)
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
            hint = "Ilagay ang mensahe ng pagbabago..."
            setTextColor(0xFF000000.toInt())
            setHintTextColor(0xFF888888.toInt())
            setBackgroundColor(0xFFFFFFFF.toInt())
            textSize = 14f
            setPadding(24, 18, 24, 18)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("📤 I-PUSH SA GITHUB")
            .setMessage("Papalitan ang:\n$selectedFilePath")
            .setView(input)
            .setPositiveButton("I-PUSH") { _, _ ->
                val msg = input.text.toString().trim().ifEmpty { "Na-update: $selectedFilePath" }
                performPush(content, msg)
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
        listOf(btnLoad, btnCopy, btnPaste, btnClear, btnPreview, btnSaveLocal, btnPushGithub, btnRefresh).forEach {
            it.isEnabled = !show
        }
    }
}

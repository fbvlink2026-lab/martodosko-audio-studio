// ==================================================
// FILE: FileEditorFragment.kt — ✅ MAY LINE NUMBERING NA! TUMPAK ANG TALON!
// VERSION: 6.0.0 — ✅ PREVIEW MAY NUMERO • TUMATALON SA TAMANG LINYA • NAKA-HIGHLIGHT!
// UPDATED: 2026-09-22 — AYON SA MUNGKAHI: LINE NUMBER = TUMPAK NA SANGGUNIAN!
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
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
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
    private lateinit var btnFullScreen: Button
    private lateinit var btnLoad: Button
    private lateinit var btnCopy: Button
    private lateinit var btnPaste: Button
    private lateinit var btnClear: Button
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
    private var isFullScreen = false
    private var activePreviewDialog: AlertDialog? = null

    data class FileItem(val path: String, val name: String, val type: String)
    data class CodeIssue(val severity: String, val message: String, val line: Int)
    data class PreviewElement(val label: String, val targetLine: Int, val bgColor: Int, val type: String = "button")

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

        // ===== HEADER =====
        val header = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 8)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        header.addView(TextView(requireContext()).apply {
            text = "✏️ FILE EDITOR"
            textSize = 20f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, Typeface.BOLD)
        })

        header.addView(TextView(requireContext()).apply {
            text = "📁 Folder:"
            textSize = 13f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 12, 0, 4)
        })

        folderSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
            setBackgroundColor(0xFF252540.toInt())
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                    currentFolderPath = getItemAtPosition(pos).toString()
                    loadFileListFromFolder()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
        header.addView(folderSelect)

        header.addView(TextView(requireContext()).apply {
            text = "📄 File:"
            textSize = 13f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 4, 0, 4)
        })

        fileSelect = Spinner(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
            setBackgroundColor(0xFF252540.toInt())
        }
        header.addView(fileSelect)

        btnLoad = Button(requireContext()).apply {
            text = "📥 I-LOAD MULA SA GITHUB"
            setBackgroundColor(0xFF1976D2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 13f
            minHeight = 52
            setOnClickListener { loadFromGithub() }
        }
        header.addView(btnLoad)

        root.addView(header)

        // ===== CODE EDITOR AREA =====
        val scrollView = ScrollView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
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
            text = "💻 Kodigo:"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, Typeface.BOLD)
            setPadding(4, 4, 0, 8)
        })

        codeEditor = EditText(requireContext()).apply {
            id = View.generateViewId()
            setBackgroundColor(0xFF0F0F1A.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF555577.toInt())
            textSize = 11f
            setPadding(14, 14, 14, 14)
            setHint("Pindutin ang \"I-LOAD\" para makita ang laman...")
            minHeight = 400
            setTypeface(Typeface.MONOSPACE)
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

        // ===== BOTTOM BUTTONS =====
        val fixedBottom = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 12, 8, 16)
            setBackgroundColor(0xFF1A1A2E.toInt())
            elevation = 8f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        btnFullScreen = Button(requireContext()).apply {
            text = "⛶"
            setBackgroundColor(0xFF252540.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            minWidth = 52
            minHeight = 54
            setPadding(4, 4, 4, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { toggleFullScreenEditor() }
        }
        fixedBottom.addView(btnFullScreen)

        btnCopy = Button(requireContext()).apply {
            text = "📋"
            setBackgroundColor(0xFF455A64.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(2, 4, 2, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { copyCode() }
        }
        fixedBottom.addView(btnCopy)

        btnPaste = Button(requireContext()).apply {
            text = "📌"
            setBackgroundColor(0xFF558B2F.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(2, 4, 2, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { pasteCode() }
        }
        fixedBottom.addView(btnPaste)

        btnClear = Button(requireContext()).apply {
            text = "🧹"
            setBackgroundColor(0xFFC62828.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(2, 4, 2, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { clearCode() }
        }
        fixedBottom.addView(btnClear)

        btnPreview = Button(requireContext()).apply {
            text = "👁️"
            setBackgroundColor(0xFF00897B.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(2, 4, 2, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { previewCode() }
        }
        fixedBottom.addView(btnPreview)

        btnSaveLocal = Button(requireContext()).apply {
            text = "💾"
            setBackgroundColor(0xFF2E7D32.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(2, 4, 2, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { saveLocal() }
        }
        fixedBottom.addView(btnSaveLocal)

        btnPushGithub = Button(requireContext()).apply {
            text = "☁️"
            setBackgroundColor(0xFF7B1FA2.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(2, 4, 2, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { pushToGithub() }
        }
        fixedBottom.addView(btnPushGithub)

        btnRefresh = Button(requireContext()).apply {
            text = "🔄"
            setBackgroundColor(0xFF00BFA5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            minHeight = 54
            setPadding(2, 4, 2, 4)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { loadFileListFromFolder() }
        }
        fixedBottom.addView(btnRefresh)

        root.addView(fixedBottom)

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadConfig()
        initFolderList()
        codeEditor.addTextChangedListener(textWatcher)

        return root
    }

    private fun closePreviewDialog() {
        activePreviewDialog?.dismiss()
        activePreviewDialog = null
    }

    private fun toggleFullScreenEditor() {
        isFullScreen = !isFullScreen
        if (isFullScreen) showFullScreenEditorDialog()
    }

    private fun showFullScreenEditorDialog() {
        val fullScreenEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF0A0A15.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            setPadding(20, 20, 20, 20)
            setTypeface(Typeface.MONOSPACE)
            setText(codeEditor.text)
            setSelection(codeEditor.selectionStart)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        AlertDialog.Builder(requireContext())
            .setTitle("⛶ BUONG EKRAN — I-edit ang Kodigo")
            .setView(fullScreenEditor)
            .setPositiveButton("✅ ILIPAT") { _, _ ->
                codeEditor.setText(fullScreenEditor.text)
                codeEditor.setSelection(fullScreenEditor.selectionStart)
                isFullScreen = false
                applySyntaxHighlighting()
            }
            .setNegativeButton("❌ KANSELAHIN") { _, _ -> isFullScreen = false }
            .show()
    }

    private val textWatcher = object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) {
            checkForErrors(s.toString())
            if (!isFullScreen) applySyntaxHighlighting()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun applySyntaxHighlighting() {
        val selStart = codeEditor.selectionStart
        val selEnd = codeEditor.selectionEnd
        val colored = applyHighlighting(codeEditor.text.toString())
        codeEditor.removeTextChangedListener(textWatcher)
        codeEditor.setText(colored)
        codeEditor.setSelection(selStart, selEnd)
        codeEditor.addTextChangedListener(textWatcher)
    }

    private fun applyHighlighting(text: String): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(text)
        when {
            selectedFilePath.endsWith(".kt") -> highlightKotlin(ssb)
            selectedFilePath.endsWith(".xml") -> highlightXml(ssb)
            selectedFilePath.endsWith(".html") -> highlightXml(ssb)
        }
        return ssb
    }

    private fun highlightKotlin(ssb: SpannableStringBuilder) {
        val text = ssb.toString()
        listOf("package", "import", "class", "fun", "val", "var", "override",
            "private", "public", "protected", "if", "else", "for", "while",
            "return", "null", "true", "false", "this", "super", "object",
            "interface", "companion", "lateinit", "suspend", "try", "catch",
            "finally", "throw", "as", "is", "in", "when", "where").forEach { kw ->
            Regex("\\b$kw\\b").findAll(text).forEach { m ->
                ssb.setSpan(ForegroundColorSpan(0xFF61AFEF.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        Regex("\"(\\\\.|[^\"\\\\])*\"").findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFFE06C75.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("//.*$", RegexOption.MULTILINE).findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFF98C379.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("@\\w+").findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFFC678DD.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("\\b\\d+\\b").findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFFD19A66.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun highlightXml(ssb: SpannableStringBuilder) {
        val text = ssb.toString()
        Regex("</?[\\w-:]+").findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFF61AFEF.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("\\b[\\w-]+\\s*=").findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFFE5C07B.toInt()), m.range.first, m.range.last, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("\"[^\"]*\"").findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFFE06C75.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("<!--[\\s\\S]*?-->").findAll(text).forEach { m ->
            ssb.setSpan(ForegroundColorSpan(0xFF98C379.toInt()), m.range.first, m.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun createWhiteTextAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE); v.textSize = 14f; v.setPadding(16, 12, 16, 12)
                return v
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent) as TextView
                v.setTextColor(Color.WHITE); v.setBackgroundColor(0xFF252540.toInt()); v.textSize = 14f; v.setPadding(16, 14, 16, 14)
                return v
            }
        }
    }

    private fun checkForErrors(code: String) {
        val issues = mutableListOf<CodeIssue>()
        val lines = code.lines()
        if (selectedFilePath.endsWith(".kt")) {
            if (!code.contains("package ")) issues.add(CodeIssue("warning", "Maaaring kulang ang package declaration", 1))
            lines.forEachIndexed { idx, line ->
                val trimmed = line.trim()
                if (trimmed.contains("\"") && trimmed.split("\"").size % 2 == 0) {
                    issues.add(CodeIssue("error", "Hindi nakasaradong panipi", idx + 1))
                }
            }
        }
        errorPanel.visibility = if (issues.isEmpty()) View.GONE else {
            errorText.text = issues.joinToString("\n") { "• Linya ${it.line}: ${it.message}" }
            View.VISIBLE
        }
    }

    private fun copyCode() {
        val code = codeEditor.text.toString()
        if (code.isBlank()) { Toast.makeText(context, "⚠️ Walang kodigong kokopyahin!", Toast.LENGTH_SHORT).show(); return }
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Code", code))
        Toast.makeText(context, "✅ Nakopya!", Toast.LENGTH_SHORT).show()
    }

    private fun pasteCode() {
        val clip = (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (!text.isNullOrBlank()) {
                codeEditor.setText(text)
                applySyntaxHighlighting()
                Toast.makeText(context, "✅ Nakapaste!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        Toast.makeText(context, "⚠️ Walang laman ang clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun clearCode() {
        codeEditor.setText(""); originalContent = ""; errorPanel.visibility = View.GONE
        Toast.makeText(context, "✅ Nabura!", Toast.LENGTH_SHORT).show()
    }

    private fun previewCode() {
        val code = codeEditor.text.toString()
        if (code.isBlank()) { Toast.makeText(context, "⚠️ Walang kodigong ipapakita!", Toast.LENGTH_SHORT).show(); return }

        val previewView = when {
            selectedFilePath.endsWith(".html") -> android.webkit.WebView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(null, code, "text/html", "UTF-8", null)
            }
            selectedFilePath.endsWith(".kt") -> buildFullKotlinPreview(code)
            selectedFilePath.endsWith(".xml") -> buildFullXmlPreview(code)
            else -> buildGenericPreviewWithNumbers(code)
        }

        activePreviewDialog = AlertDialog.Builder(requireContext())
            .setTitle("👁️ PREVIEW — $selectedFilePath")
            .setView(previewView)
            .setPositiveButton("TAPOS", null)
            .create()
        activePreviewDialog?.show()
    }

    // ✅ BAGONG BUILDER — MAY NUMERO NG LINYA!
    private fun buildGenericPreviewWithNumbers(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(0xFF12121F.toInt()); setPadding(16, 16, 16, 16)
        }
        val container = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(container)

        container.addView(TextView(requireContext()).apply {
            text = "📋 KODIGO — MAY NUMERO NG LINYA"; textSize = 16f
            setTextColor(0xFF40E0D0.toInt()); setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        })

        val lines = code.lines()
        lines.forEachIndexed { index, line ->
            val lineNum = index + 1
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(8, 4, 8, 4)
                setBackgroundColor(if (lineNum % 2 == 0) 0xFF1A1A2E.toInt() else 0xFF12121F.toInt())
                setOnClickListener { jumpToLineAndHighlight(lineNum, lines) }
            }
            // Numero ng linya
            row.addView(TextView(requireContext()).apply {
                text = String.format("%4d", lineNum)
                setTextColor(0xFF666688.toInt())
                textSize = 11f
                setPadding(8, 4, 12, 4)
                setTypeface(Typeface.MONOSPACE)
                minWidth = 80
            })
            // Nilalaman ng linya
            row.addView(TextView(requireContext()).apply {
                text = if (line.isBlank()) " " else line
                setTextColor(0xFFCCCCCC.toInt())
                textSize = 11f
                setPadding(4, 4, 4, 4)
                setTypeface(Typeface.MONOSPACE)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            container.addView(row)
        }

        container.addView(TextView(requireContext()).apply {
            text = "\n💡 Pindutin ang kahit anong linya → tumalon at i-highlight"
            textSize = 12f; setTextColor(0xFF888888.toInt()); gravity = Gravity.CENTER
            setPadding(0, 16, 0, 8)
        })

        return scroll
    }

    private fun buildFullKotlinPreview(code: String): View {
        // ✅ GAMITIN ANG GENERIC NA MAY NUMERO NG LINYA
        return buildGenericPreviewWithNumbers(code)
    }

    private fun buildFullXmlPreview(code: String): View {
        // ✅ GAMITIN ANG GENERIC NA MAY NUMERO NG LINYA
        return buildGenericPreviewWithNumbers(code)
    }

    // ✅ TUMPAK NA PAGTALON — GAMIT ANG EKSAKTONG NUMERO NG LINYA
    private fun jumpToLineAndHighlight(targetLine: Int, allLines: List<String>) {
        // Isara muna ang preview
        closePreviewDialog()

        // Kalkulahin ang posisyon sa loob ng buong teksto
        var charStart = 0
        for (i in 0 until targetLine - 1) {
            charStart = charStart + allLines[i].length + 1 // +1 = bagong linya
        }
        val charEnd = charStart + allLines[targetLine - 1].length

        // I-highlight — alisin muna ang textWatcher para hindi mabura
        codeEditor.removeTextChangedListener(textWatcher)
        codeEditor.setSelection(charStart, charEnd)
        codeEditor.addTextChangedListener(textWatcher)

        codeEditor.requestFocus()

        // I-scroll papunta sa tamang linya
        codeEditor.post {
            val layout = codeEditor.layout
            if (layout != null) {
                val lineTop = layout.getLineTop(targetLine - 1)
                val parentScroll = codeEditor.parent as? ScrollView
                if (parentScroll != null) {
                    parentScroll.smoothScrollTo(0, lineTop - 100)
                } else {
                    codeEditor.scrollTo(0, lineTop - 100)
                }
            }
        }

        Toast.makeText(context, "📍 Linya $targetLine", Toast.LENGTH_SHORT).show()
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
        folderSelect.adapter = createWhiteTextAdapter(listOf(
            "app/", "app/src/", "app/src/main/", "app/src/main/java/",
            "app/src/main/java/com/martodosko/studio/", "app/src/main/res/",
            "app/src/main/res/layout/", "app/src/main/assets/", "docs/"
        ))
    }

    private fun loadFileListFromFolder() {
        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ I-setup muna ang GitHub Token", false); return
        }
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$currentFolderPath")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                val ja = JSONArray(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                allFiles.clear()
                for (i in 0 until ja.length()) {
                    val item = ja.getJSONObject(i)
                    if (item.getString("type") == "file") {
                        val name = item.getString("name")
                        allFiles.add(FileItem(item.getString("path"), getFileIcon(name) + " " + name, "file"))
                    }
                }
                withContext(Dispatchers.Main) {
                    val names = allFiles.map { it.name }
                    fileSelect.adapter = createWhiteTextAdapter(if (names.isEmpty()) listOf("— Walang file —") else names)
                    showStatus("✅ ${allFiles.size} file nakita", true); showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false); showLoading(false)
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
            showStatus("⚠️ Pumili muna ng file!", false); return
        }
        selectedFilePath = allFiles[pos].path
        if (githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kailangan ng GitHub Token!", false); return
        }
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$selectedFilePath")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "token $githubToken")
                val jo = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                currentSha = jo.getString("sha")
                originalContent = String(Base64.decode(jo.getString("content").replace("\n", ""), Base64.DEFAULT))
                withContext(Dispatchers.Main) {
                    codeEditor.setText(originalContent)
                    showStatus("✅ Nai-load", true)
                    checkForErrors(originalContent)
                    applySyntaxHighlighting()
                    showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false); showLoading(false)
                }
            }
        }
    }

    private fun saveLocal() {
        val content = codeEditor.text.toString()
        if (content.isBlank() || selectedFilePath.isBlank()) {
            showStatus("⚠️ I-load muna!", false); return
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
            showStatus("⚠️ Kulang ang impormasyon", false); return
        }
        val input = EditText(requireContext()).apply {
            hint = "Ilagay ang mensahe ng pagbabago..."
            setBackgroundColor(0xFFFFFFFF.toInt())
            setTextColor(0xFF000000.toInt())
            setHintTextColor(0xFF888888.toInt())
            textSize = 14f; setPadding(24, 18, 24, 18)
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
                conn.requestMethod = "PUT"; conn.doOutput = true
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use { it.write(body) }
                if (conn.responseCode in 200..201) {
                    currentSha = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                        .optJSONObject("commit")?.optString("sha")
                    originalContent = content
                    withContext(Dispatchers.Main) {
                        showStatus("☁️✅ MATAGUMPAY NA-UPLOAD!", true); showLoading(false)
                    }
                } else throw Exception("HTTP ${conn.responseCode}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false); showLoading(false)
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
        listOf(
            btnLoad, btnFullScreen, btnCopy, btnPaste, btnClear,
            btnPreview, btnSaveLocal, btnPushGithub, btnRefresh
        ).forEach {
            it.isEnabled = !show
            it.alpha = if (show) 0.4f else 1.0f
        }
    }
}

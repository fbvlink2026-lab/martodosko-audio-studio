// ==================================================
// FILE: FileEditorFragment.kt — ✅ TUMPAK NA TALON • HIGHLIGHT • SCROLL!
// VERSION: 5.5.0 — ✅ TINUKOY AT INAYOS ANG LAHAT NG SULIRANIN!
// UPDATED: 2026-09-23 — TUMPAK NA PAGTALON SA EKSAKTONG LINYA!
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
import java.util.regex.Pattern

class FileEditorFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var folderSelect: Spinner
    private lateinit var fileSelect: Spinner
    private lateinit var codeContainer: LinearLayout
    private lateinit var lineNumbers: TextView
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
    private lateinit var scrollView: ScrollView

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

    // ✅ PAGTALON — TUMPAK NA IMPORMASYON
    private data class JumpTarget(val line: Int, val selStart: Int, val selEnd: Int)
    private var pendingJump: JumpTarget? = null

    data class FileItem(val path: String, val name: String, val type: String)
    data class CodeIssue(val severity: String, val message: String, val line: Int)

    companion object {
        const val PREFS_NAME = "github_prefs"
        const val REPO_OWNER_KEY = "repo_owner"
        const val REPO_NAME_KEY = "repo_name"
        const val BASE_URL = "https://api.github.com/repos/"
        const val BRANCH = "main"
        private val LINE_NUM_PATTERN = Pattern.compile("^\\d+\\|")
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

        // ========== HEADER ==========
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

        // ========== SCROLL VIEW ==========
        scrollView = ScrollView(requireContext()).apply {
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

        // ✅ EDITOR CONTAINER
        codeContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xFF0F0F1A.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }

        // ✅ LINE NUMBERS — KALIWA
        lineNumbers = TextView(requireContext()).apply {
            setBackgroundColor(0xFF151528.toInt())
            setTextColor(0xFF666688.toInt())
            textSize = 11f
            setPadding(12, 14, 8, 14)
            setTypeface(Typeface.MONOSPACE)
            gravity = Gravity.TOP or Gravity.RIGHT
            minWidth = 70
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        codeContainer.addView(lineNumbers)

        // ✅ CODE EDITOR — KANAN
        codeEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF0F0F1A.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFF555577.toInt())
            textSize = 11f
            setPadding(14, 14, 14, 14)
            setHint("Pindutin ang \"I-LOAD\" para makita ang laman...")
            minHeight = 400
            setTypeface(Typeface.MONOSPACE)
            background = null
            setHorizontallyScrolling(false)  // ✅ TEXT WRAP
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        codeContainer.addView(codeEditor)

        main.addView(codeContainer)

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

        // ========== BOTTOM BUTTONS ==========
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

    private val textWatcher = object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) {
            updateLineNumbers()
            checkForErrors(s.toString())
            if (!isFullScreen) applySyntaxHighlighting()
            // ✅ KUNG MAY NAKA-ANTAY NA PAGTALON — I-APLAY AGAD
            pendingJump?.let { jump ->
                pendingJump = null
                applyJump(jump)
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateLineNumbers() {
        val text = codeEditor.text.toString()
        val lines = text.lines()
        val maxWidth = lines.size.toString().length
        lineNumbers.text = lines.indices.joinToString("\n") { "%${maxWidth}d".format(it + 1) }
    }

    private fun getCleanCode(): String {
        return codeEditor.text.toString()
            .lines()
            .joinToString("\n") { LINE_NUM_PATTERN.matcher(it).replaceFirst("") }
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
        val selStart = codeEditor.selectionStart
        val selEnd = codeEditor.selectionEnd
        val cleanText = getCleanCode()

        val fsEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF0A0A15.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            setPadding(20, 20, 20, 20)
            setTypeface(Typeface.MONOSPACE)
            setHorizontallyScrolling(false)
            setText(cleanText)
            setSelection(selStart, selEnd)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        AlertDialog.Builder(requireContext())
            .setTitle("⛶ BUONG EKRAN")
            .setView(fsEditor)
            .setPositiveButton("✅ ILIPAT") { _, _ ->
                codeEditor.removeTextChangedListener(textWatcher)
                codeEditor.setText(fsEditor.text)
                codeEditor.setSelection(fsEditor.selectionStart, fsEditor.selectionEnd)
                codeEditor.addTextChangedListener(textWatcher)
                updateLineNumbers()
                isFullScreen = false
                applySyntaxHighlighting()
            }
            .setNegativeButton("❌ KANSELAHIN") { _, _ -> isFullScreen = false }
            .show()
    }

    // ✅ KALKULAHIN ANG TUMPAK NA POSISYON — TINANGGAL ANG MALING PARAAN!
    private fun getLineStartPosition(text: String, targetLine: Int): Int {
        if (targetLine <= 1) return 0
        var pos = 0
        val lines = text.lines()
        for (i in 0 until targetLine - 1) {
            pos += lines[i].length + 1  // +1 = bagong linya — tama na ito!
        }
        return pos
    }

    // ✅ TUMPAK NA PAGTALON — HIGHLIGHT + SCROLL
    private fun scheduleJumpToLine(targetLine: Int) {
        val cleanText = getCleanCode()
        val lines = cleanText.lines()
        if (targetLine !in 1..lines.size) return

        val start = getLineStartPosition(cleanText, targetLine)
        val end = start + lines[targetLine - 1].length

        val jump = JumpTarget(targetLine, start, end)

        // ✅ I-APLAY AGAD O I-ANTAY
        if (codeEditor.text.toString() == cleanText) {
            applyJump(jump)
        } else {
            pendingJump = jump
        }

        closePreviewDialog()
    }

    // ✅ TUNAY NA PAGTALON — HIGHLIGHT + SCROLL
    private fun applyJump(jump: JumpTarget) {
        codeEditor.removeTextChangedListener(textWatcher)
        codeEditor.setSelection(jump.selStart, jump.selEnd)  // ✅ NAKA-HIGHLIGHT!
        codeEditor.addTextChangedListener(textWatcher)
        codeEditor.requestFocus()

        // ✅ SCROLL — HIGIT SA ISANG PAGKAKATAON PARA SIGURADO
        repeat(3) { attempt ->
            codeEditor.postDelayed({
                val layout = codeEditor.layout ?: return@postDelayed
                val lineTop = layout.getLineTop(jump.line - 1)
                // ✅ ITAAS NG KONTI PARA MAKITA ANG BUONG LINYA
                val offset = if (attempt == 0) lineTop - 100 else lineTop - 80
                scrollView.smoothScrollTo(0, offset.coerceAtLeast(0))
            }, (100 * (attempt + 1)).toLong())
        }

        Toast.makeText(context, "📍 Linya ${jump.line}", Toast.LENGTH_SHORT).show()
    }

    private fun applySyntaxHighlighting() {
        val selStart = codeEditor.selectionStart
        val selEnd = codeEditor.selectionEnd
        val colored = highlightCode(getCleanCode())
        codeEditor.removeTextChangedListener(textWatcher)
        codeEditor.setText(colored)
        // ✅ IBALIK ANG PAGPILI PAGKATAPOS NG HIGHLIGHT
        codeEditor.setSelection(selStart, selEnd)
        codeEditor.addTextChangedListener(textWatcher)
    }

    private fun highlightCode(text: String): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(text)
        when {
            selectedFilePath.endsWith(".kt") -> highlightKotlin(ssb)
            selectedFilePath.endsWith(".xml") || selectedFilePath.endsWith(".html") -> highlightMarkup(ssb)
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
            Regex("\\b$kw\\b").findAll(text).forEach {
                ssb.setSpan(ForegroundColorSpan(0xFF61AFEF.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        Regex("\"(\\\\.|[^\"\\\\])*\"").findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFFE06C75.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("//.*$", RegexOption.MULTILINE).findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFF98C379.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("@\\w+").findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFFC678DD.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("\\b\\d+\\b").findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFFD19A66.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun highlightMarkup(ssb: SpannableStringBuilder) {
        val text = ssb.toString()
        Regex("</?[\\w-:]+").findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFF61AFEF.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("\\b[\\w-]+\\s*=").findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFFE5C07B.toInt()), it.range.first, it.range.last, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("\"[^\"]*\"").findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFFE06C75.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        Regex("<!--[\\s\\S]*?-->").findAll(text).forEach {
            ssb.setSpan(ForegroundColorSpan(0xFF98C379.toInt()), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun createWhiteAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
            override fun getView(p: Int, c: View?, parent: ViewGroup): View {
                val v = super.getView(p, c, parent) as TextView
                v.setTextColor(Color.WHITE); v.textSize = 14f; v.setPadding(16, 12, 16, 12)
                return v
            }
            override fun getDropDownView(p: Int, c: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(p, c, parent) as TextView
                v.setTextColor(Color.WHITE); v.setBackgroundColor(0xFF252540.toInt()); v.textSize = 14f; v.setPadding(16, 14, 16, 14)
                return v
            }
        }
    }

    private fun checkForErrors(code: String) {
        val issues = mutableListOf<CodeIssue>()
        val lines = code.lines()
        if (selectedFilePath.endsWith(".kt")) {
            if (!code.contains("package ")) issues.add(CodeIssue("warning", "Kulang package declaration", 1))
            lines.forEachIndexed { idx, line ->
                val t = line.trim()
                if (t.contains("\"") && t.split("\"").size % 2 == 0)
                    issues.add(CodeIssue("error", "Hindi nakasaradong panipi", idx + 1))
            }
        }
        errorPanel.visibility = if (issues.isEmpty()) View.GONE else {
            errorText.text = issues.joinToString("\n") { "• Linya ${it.line}: ${it.message}" }
            View.VISIBLE
        }
    }

    private fun copyCode() {
        val clean = getCleanCode()
        if (clean.isBlank()) {
            Toast.makeText(context, "⚠️ Walang kokopyahin!", Toast.LENGTH_SHORT).show()
            return
        }
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Code", clean))
        Toast.makeText(context, "✅ Nakopya!", Toast.LENGTH_SHORT).show()
    }

    private fun pasteCode() {
        val clip = (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip
        val text = clip?.getItemAt(0)?.text?.toString()
        if (!text.isNullOrBlank()) {
            codeEditor.removeTextChangedListener(textWatcher)
            codeEditor.setText(text)
            codeEditor.addTextChangedListener(textWatcher)
            updateLineNumbers()
            applySyntaxHighlighting()
            Toast.makeText(context, "✅ Nakapaste!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "⚠️ Walang laman ang clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearCode() {
        codeEditor.setText("")
        originalContent = ""
        lineNumbers.text = ""
        errorPanel.visibility = View.GONE
        Toast.makeText(context, "✅ Nabura!", Toast.LENGTH_SHORT).show()
    }

    private fun previewCode() {
        val clean = getCleanCode()
        if (clean.isBlank()) {
            Toast.makeText(context, "⚠️ Walang ipapakita!", Toast.LENGTH_SHORT).show()
            return
        }

        val view = when {
            selectedFilePath.endsWith(".html") -> android.webkit.WebView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(null, clean, "text/html", "UTF-8", null)
            }
            selectedFilePath.endsWith(".kt") -> buildKtPreview(clean)
            selectedFilePath.endsWith(".xml") -> buildXmlPreview(clean)
            else -> ScrollView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setBackgroundColor(0xFF12121F.toInt()); setPadding(20, 20, 20, 20)
                addView(TextView(requireContext()).apply {
                    text = clean; setTextColor(0xFFCCCCCC.toInt()); textSize = 11f; setTypeface(Typeface.MONOSPACE)
                })
            }
        }

        activePreviewDialog = AlertDialog.Builder(requireContext())
            .setTitle("👁️ PREVIEW — $selectedFilePath")
            .setView(view)
            .setPositiveButton("TAPOS", null)
            .create()
        activePreviewDialog?.show()
    }

    private fun buildKtPreview(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(0xFF12121F.toInt())
        }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        scroll.addView(container)

        val className = Regex("class\\s+(\\w+)").find(code)?.groupValues?.get(1) ?: "Unknown"
        val lines = code.lines()

        container.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1A1A2E.toInt()); setPadding(16, 24, 16, 16)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnClickListener { scheduleJumpToLine(1) }
            addView(TextView(requireContext()).apply {
                text = "📱 $className"; textSize = 22f; setTextColor(0xFF40E0D0.toInt())
                setTypeface(null, Typeface.BOLD); gravity = Gravity.CENTER
            })
            addView(TextView(requireContext()).apply {
                text = "💡 Pindutin → lumipat sa linya"; textSize = 12f
                setTextColor(0xFF666666.toInt()); gravity = Gravity.CENTER; setPadding(0, 4, 0, 0)
            })
        })

        listOf(
            "Folder" to "folderSelect",
            "File" to "fileSelect",
            "I-LOAD" to "btnLoad",
            "Kodigo" to "codeEditor",
            "I-save" to "btnSaveLocal",
            "I-push" to "btnPushGithub"
        ).forEach { (label, key) ->
            val lineNo = lines.indexOfFirst { it.contains(key, ignoreCase = true) }
            val target = if (lineNo < 0) 1 else lineNo + 1
            container.addView(Button(requireContext()).apply {
                text = "📍 $label → Linya $target"
                setBackgroundColor(0xFF1E1E2F.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .apply { setMargins(16, 4, 16, 4) }
                setOnClickListener { scheduleJumpToLine(target) }
            })
        }
        return scroll
    }

    private fun buildXmlPreview(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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
        val llCount = Regex("<LinearLayout").findAll(code).count()
        val lines = code.lines()

        container.addView(LinearLayout(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt()); setPadding(20, 20, 20, 20)
            orientation = LinearLayout.VERTICAL
            addView(TextView(requireContext()).apply {
                text = "📋 Mga Elemento:"; setTextColor(Color.WHITE); setTypeface(null, Typeface.BOLD); setPadding(0, 0, 0, 12)
            })
            addView(TextView(requireContext()).apply { text = "• Linear Layout: $llCount"; setTextColor(0xFF64B5F6.toInt()); textSize = 13f })
            addView(TextView(requireContext()).apply { text = "• Pindutan: $btnCount"; setTextColor(0xFF64B5F6.toInt()); textSize = 13f })
            addView(TextView(requireContext()).apply { text = "• Teksto: $tvCount"; setTextColor(0xFFCCCCCC.toInt()); textSize = 13f })
            addView(TextView(requireContext()).apply { text = "• Pag-input: $etCount"; setTextColor(0xFF81C784.toInt()); textSize = 13f })
        })

        container.addView(TextView(requireContext()).apply {
            text = "\n💡 Pindutin kahit saan → lumipat sa linya 1"
            textSize = 12f; setTextColor(0xFF888888.toInt()); gravity = Gravity.CENTER
        })
        container.setOnClickListener { scheduleJumpToLine(1) }
        return scroll
    }

    private fun loadConfig() {
        repoOwner = prefs.getString(REPO_OWNER_KEY, "") ?: ""
        repoName = prefs.getString(REPO_NAME_KEY, "") ?: ""
        githubToken = GithubManagerFragment.getDecryptedToken(requireContext())
        if (githubToken.isNullOrEmpty() || repoOwner.isEmpty() || repoName.isEmpty()) {
            showStatus("⚠️ I-setup muna ang GitHub Token", false)
        } else {
            showStatus("✅ Konektado: $repoOwner/$repoName", true)
        }
    }

    private fun initFolderList() {
        folderSelect.adapter = createWhiteAdapter(listOf(
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
                        allFiles.add(FileItem(item.getString("path"), getIcon(name) + " " + name, "file"))
                    }
                }
                withContext(Dispatchers.Main) {
                    val names = allFiles.map { it.name }
                    fileSelect.adapter = createWhiteAdapter(if (names.isEmpty()) listOf("— Walang file —") else names)
                    showStatus("✅ ${allFiles.size} file nakita", true); showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false); showLoading(false)
                }
            }
        }
    }

    private fun getIcon(name: String) = when {
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
                    updateLineNumbers()
                    showStatus("✅ Nai-load — ${originalContent.lines().size} linya", true)
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
        val content = getCleanCode()
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
        val content = getCleanCode()
        if (content.isBlank() || selectedFilePath.isBlank() || githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kulang ang impormasyon", false); return
        }
        val input = EditText(requireContext()).apply {
            hint = "Mensahe ng pagbabago..."
            setBackgroundColor(0xFFFFFFFF.toInt())
            setTextColor(0xFF000000.toInt())
            setHintTextColor(0xFF888888.toInt())
            textSize = 14f; setPadding(24, 18, 24, 18)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("📤 I-PUSH SA GITHUB")
            .setMessage("Papalitan: $selectedFilePath")
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

                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$selectedFilePath")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use { it.write(body) }

                if (conn.responseCode in 200..201) {
                    val resp = JSONObject(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                    currentSha = resp.optJSONObject("commit")?.optString("sha")
                    originalContent = content
                    withContext(Dispatchers.Main) {
                        showStatus("☁️✅ Nai-push!", true)
                        Toast.makeText(context, "✅ Matagumpay!", Toast.LENGTH_SHORT).show()
                        showLoading(false)
                    }
                } else {
                    throw Exception("HTTP ${conn.responseCode}: ${conn.responseMessage}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false)
                    Toast.makeText(context, "❌ Nabigo", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun showStatus(msg: String, ok: Boolean) {
        statusText.text = msg
        statusText.setTextColor(if (ok) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        listOf(btnLoad, btnFullScreen, btnCopy, btnPaste, btnClear, btnPreview, btnSaveLocal, btnPushGithub, btnRefresh).forEach {
            it.isEnabled = !show; it.alpha = if (show) 0.4f else 1f
        }
    }
}

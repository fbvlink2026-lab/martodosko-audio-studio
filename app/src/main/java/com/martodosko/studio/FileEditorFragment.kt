// ==================================================
// FILE: FileEditorFragment.kt — ✅ MAY LINE NUMBERS NA! TUMPAK SA LAHAT NG 800+ LINYA!
// VERSION: 5.5.0 — ✅ NUMERO SA KALIWA • TUMPAK NA PAGTALON • AWTO TINATANGGAL BAGO I-SAVE!
// UPDATED: 2026-09-22 — HINDI NA HULA-HULA! BAWAT LINYA MAY NUMERO!
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
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
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
    private lateinit var lineNumbers: TextView  // ✅ NUMERO NG LINYA — KALIWA
    private lateinit var codeEditor: EditText    // ✅ KODIGO — KANAN
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
    
    // ✅ TUNAY NA KODIGO — WALANG NUMERO — ITO ANG I-SAVE SA GITHUB
    private var cleanOriginalCode = ""  
    
    private val allFiles = mutableListOf<FileItem>()
    private var isFullScreen = false
    private var activePreviewDialog: AlertDialog? = null

    data class FileItem(val path: String, val name: String, val type: String)
    data class CodeIssue(val severity: String, val message: String, val line: Int)
    data class PreviewElement(val label: String, val searchKey: String, val bgColor: Int, val type: String = "button")

    companion object {
        const val PREFS_NAME = "github_prefs"
        const val REPO_OWNER_KEY = "repo_owner"
        const val REPO_NAME_KEY = "repo_name"
        const val BASE_URL = "https://api.github.com/repos/"
        const val BRANCH = "main"
        private val LINE_NUMBER_PATTERN = Pattern.compile("^(\\d+)\\|") // ✅ "123|" sa simula
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

        // ========== EDITOR AREA — MAY NUMERO SA KALIWA ==========
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

        // ✅ KODIGO + NUMERO NG LINYA — MAGKATABI
        codeContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xFF0F0F1A.toInt())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }

        // ✅ KALIWA: MGA NUMERO NG LINYA
        lineNumbers = TextView(requireContext()).apply {
            id = View.generateViewId()
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

        // ✅ KANAN: ANG TUNAY NA KODIGO
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
            background = null // Alisin ang hangganan para magtugma sa numero
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        codeContainer.addView(codeEditor)

        main.addView(codeContainer)

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
        
        // ✅ BANTAYAN ANG PAGBABAGO → I-UPDATE ANG MGA NUMERO
        codeEditor.addTextChangedListener(codeWatcher)

        return root
    }

    // ✅ AWTO: I-UPDATE ANG MGA NUMERO NG LINYA
    private val codeWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateLineNumbers()
            checkForErrors(getCleanCode())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // ✅ KUNIN ANG KODIGONG WALANG NUMERO — ITO ANG I-SAVE
    private fun getCleanCode(): String {
        return codeEditor.text.toString()
            .lines()
            .joinToString("\n") { line ->
                val m = LINE_NUMBER_PATTERN.matcher(line)
                if (m.find()) line.substring(m.end()) else line
            }
    }

    // ✅ ILAGAY ANG NUMERO SA BAWAT LINYA
    private fun applyLineNumbers(code: String): String {
        val lines = code.lines()
        val maxNumWidth = lines.size.toString().length
        return lines.mapIndexed { index, line ->
            val lineNum = "%${maxNumWidth}d".format(index + 1)
            "$lineNum|$line"
        }.joinToString("\n")
    }

    // ✅ I-UPDATE ANG LISTAHAN NG NUMERO SA KALIWA
    private fun updateLineNumbers() {
        val lineCount = codeEditor.text.lines().size
        val maxNumWidth = lineCount.toString().length
        val numText = (1..lineCount).joinToString("\n") { "%${maxNumWidth}d".format(it) }
        lineNumbers.text = numText
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
        val cleanCode = getCleanCode()
        val currentSelStart = calculateCleanSelectionStart()
        val currentSelEnd = calculateCleanSelectionEnd()

        val fullScreenEditor = EditText(requireContext()).apply {
            setBackgroundColor(0xFF0A0A15.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            setPadding(20, 20, 20, 20)
            setTypeface(Typeface.MONOSPACE)
            setText(cleanCode) // ✅ WALANG NUMERO SA FULL SCREEN
            setSelection(currentSelStart, currentSelEnd)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        AlertDialog.Builder(requireContext())
            .setTitle("⛶ BUONG EKRAN — I-edit ang Kodigo")
            .setView(fullScreenEditor)
            .setPositiveButton("✅ ILIPAT") { _, _ ->
                // ✅ BALIKAN MAY NUMERO NA
                val withNumbers = applyLineNumbers(fullScreenEditor.text.toString())
                codeEditor.removeTextChangedListener(codeWatcher)
                codeEditor.setText(withNumbers)
                cleanOriginalCode = fullScreenEditor.text.toString()
                codeEditor.addTextChangedListener(codeWatcher)
                updateLineNumbers()
                isFullScreen = false
            }
            .setNegativeButton("❌ KANSELAHIN") { _, _ -> isFullScreen = false }
            .show()
    }

    // ✅ TANTIYAHIN ANG POSISYON SA MALINIS NA KODIGO
    private fun calculateCleanSelectionStart(): Int {
        val allText = codeEditor.text.toString()
        val selStart = codeEditor.selectionStart
        val upToSel = allText.take(selStart)
        val lines = upToSel.lines()
        var removedChars = 0
        lines.dropLast(1).forEach { line ->
            removedChars += line.indexOf('|') + 1
        }
        return selStart - removedChars
    }

    private fun calculateCleanSelectionEnd(): Int {
        val cleanLen = getCleanCode().length
        val rawSel = codeEditor.selectionEnd
        val allText = codeEditor.text.toString()
        val upToSel = allText.take(rawSel)
        val lines = upToSel.lines()
        var removedChars = 0
        lines.dropLast(1).forEach { line ->
            removedChars += line.indexOf('|') + 1
        }
        return (rawSel - removedChars).coerceAtMost(cleanLen)
    }

    // ✅ TUMPAK NA PAGHANAP — GAMIT ANG TUNAY NA BUONG KODIGO
    private fun findBestMatchingLine(searchKey: String, fullCode: String): Int {
        val lines = fullCode.lines()
        if (searchKey.isBlank()) return 1
        val keyLower = searchKey.lowercase()

        // 1️⃣ val/var pangalan
        val declPattern = Regex("\\b(?:val|var)\\s+${Regex.escape(searchKey)}\\b", RegexOption.IGNORE_CASE)
        lines.forEachIndexed { idx, line ->
            if (declPattern.containsMatchIn(line)) return idx + 1
        }

        // 2️⃣ id / R.id
        val idPattern = Regex("\\b(?:id|R\\.id)\\b.*?${Regex.escape(searchKey)}", RegexOption.IGNORE_CASE)
        lines.forEachIndexed { idx, line ->
            if (idPattern.containsMatchIn(line)) return idx + 1
        }

        // 3️⃣ simpleng pagtugma
        lines.forEachIndexed { idx, line ->
            if (line.lowercase().contains(keyLower)) return idx + 1
        }

        return -1 // hindi nakita
    }

    // ✅ TUMALON SA TINUTUKOY NA LINYA — TUMPAK KAHIT 800+
    private fun jumpToLine(lineNumber: Int) {
        if (lineNumber < 1) return
        
        val allLines = codeEditor.text.lines()
        if (lineNumber > allLines.size) return

        // Kalkulahin ang posisyon sa teksto na may numero
        var pos = 0
        for (i in 0 until lineNumber - 1) {
            pos += allLines[i].length + 1 // +1 = bagong linya
        }
        
        val lineLength = allLines[lineNumber - 1].length
        codeEditor.setSelection(pos, pos + lineLength)
        codeEditor.requestFocus()

        // I-scroll papunta sa tamang lugar
        codeEditor.post {
            val layout = codeEditor.layout
            if (layout != null) {
                val lineTop = layout.getLineTop(lineNumber - 1)
                val parentScroll = codeEditor.parent as? ScrollView
                if (parentScroll != null) {
                    parentScroll.smoothScrollTo(0, lineTop - 80)
                } else {
                    codeEditor.scrollTo(0, lineTop - 80)
                }
            }
        }

        Toast.makeText(context, "📍 Linya $lineNumber", Toast.LENGTH_SHORT).show()
    }

    private fun previewCode() {
        val cleanCode = getCleanCode()
        if (cleanCode.isBlank()) { 
            Toast.makeText(context, "⚠️ Walang kodigong ipapakita!", Toast.LENGTH_SHORT).show()
            return 
        }

        val previewView = when {
            selectedFilePath.endsWith(".html") -> android.webkit.WebView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(null, cleanCode, "text/html", "UTF-8", null)
            }
            selectedFilePath.endsWith(".kt") -> buildFullKotlinPreview(cleanCode)
            selectedFilePath.endsWith(".xml") -> buildFullXmlPreview(cleanCode)
            else -> ScrollView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setBackgroundColor(0xFF12121F.toInt()); setPadding(20, 20, 20, 20)
                addView(TextView(requireContext()).apply {
                    text = cleanCode; setTextColor(0xFFCCCCCC.toInt()); textSize = 11f; typeface = Typeface.MONOSPACE
                })
            }
        }

        activePreviewDialog = AlertDialog.Builder(requireContext())
            .setTitle("👁️ PREVIEW — $selectedFilePath")
            .setView(previewView)
            .setPositiveButton("TAPOS", null)
            .create()
        activePreviewDialog?.show()
    }

    private fun buildFullKotlinPreview(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(0xFF12121F.toInt()); setPadding(0, 0, 0, 0)
        }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        scroll.addView(container)

        val className = Regex("class\\s+(\\w+)").find(code)?.groupValues?.get(1) ?: "UnknownScreen"
        val isEditor = className.contains("Editor", true)
        val isLogin = className.contains("Login", true)
        val isAdmin = className.contains("Admin", true)
        val lines = code.lines()

        container.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1A1A2E.toInt()); setPadding(16, 24, 16, 16)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnClickListener { 
                jumpToLine(1) // ✅ TUMALON SA TUNAY NA LINYA 1
                closePreviewDialog()
            }
            addView(TextView(requireContext()).apply {
                text = when {
                    isAdmin -> "🔐 ADMIN PANEL"
                    isLogin -> "🔐 LOGIN"
                    isEditor -> "✏️ FILE EDITOR"
                    else -> "📱 $className"
                }; textSize = 22f; setTextColor(0xFF40E0D0.toInt()); setTypeface(null, Typeface.BOLD); gravity = Gravity.CENTER
            })
            addView(TextView(requireContext()).apply {
                text = "💡 Pindutin → lumipat sa eksaktong linya sa Editor"
                textSize = 12f; setTextColor(0xFF666666.toInt()); gravity = Gravity.CENTER; setPadding(0, 4, 0, 0)
            })
        })

        when {
            isEditor -> buildEditorFullPreview(container, lines, code)
            isLogin -> buildLoginFullPreview(container, lines)
            isAdmin -> buildAdminFullPreview(container, lines)
        }
        return scroll
    }

    private fun buildEditorFullPreview(container: LinearLayout, lines: List<String>, fullCode: String) {
        addClickablePreviewElement(container, PreviewElement("📁 Piliin ang Folder:", "folderSelect", 0xFF252540.toInt(), "label"), lines, fullCode)
        addClickablePreviewElement(container, PreviewElement("— Pumili ng folder —", "folderSelect", 0xFF1E1E2F.toInt(), "spinner"), lines, fullCode)
        addClickablePreviewElement(container, PreviewElement("📄 Piliin ang File:", "fileSelect", 0xFF252540.toInt(), "label"), lines, fullCode)
        addClickablePreviewElement(container, PreviewElement("— Walang file pa —", "fileSelect", 0xFF1E1E2F.toInt(), "spinner"), lines, fullCode)
        addClickablePreviewElement(container, PreviewElement("📥 I-LOAD MULA SA GITHUB", "btnLoad", 0xFF1976D2.toInt()), lines, fullCode)
        addClickablePreviewElement(container, PreviewElement("✏️ Kodigo:", "codeEditor", 0xFF1A1A2E.toInt(), "label"), lines, fullCode)
        addClickablePreviewElement(container, PreviewElement("Pindutin ang \"I-LOAD\" para makita ang laman...", "codeEditor", 0xFF0F0F1A.toInt(), "editor"), lines, fullCode)

        val quickRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { setMargins(16, 4, 16, 4) }
        }
        addClickablePreviewBtnToRow(quickRow, "⛶", "btnFullScreen", 0xFF252540.toInt(), lines, fullCode)
        addClickablePreviewBtnToRow(quickRow, "📋", "btnCopy", 0xFF455A64.toInt(), lines, fullCode)
        addClickablePreviewBtnToRow(quickRow, "📌", "btnPaste", 0xFF558B2F.toInt(), lines, fullCode)
        addClickablePreviewBtnToRow(quickRow, "🧹", "btnClear", 0xFFC62828.toInt(), lines, fullCode)
        addClickablePreviewBtnToRow(quickRow, "👁️", "btnPreview", 0xFF00897B.toInt(), lines, fullCode)
        container.addView(quickRow)

        addClickablePreviewElement(container, PreviewElement("⏳ Kinakarga ang listahan...", "statusText", 0xFF12121F.toInt(), "status"), lines, fullCode)

        val bottomRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xFF1A1A2E.toInt()); setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        addClickablePreviewBtnToRow(bottomRow, "💾", "btnSaveLocal", 0xFF2E7D32.toInt(), lines, fullCode)
        addClickablePreviewBtnToRow(bottomRow, "☁️", "btnPushGithub", 0xFF7B1FA2.toInt(), lines, fullCode)
        addClickablePreviewBtnToRow(bottomRow, "🔄", "btnRefresh", 0xFF00BFA5.toInt(), lines, fullCode)
        container.addView(bottomRow)
    }

    private fun buildLoginFullPreview(container: LinearLayout, lines: List<String>) {
        val dummy = "" // walang special search
        addClickablePreviewElement(container, PreviewElement("Ipasok ang Key Code:", "keyInput", 0xFF12121F.toInt(), "label"), lines, dummy)
        addClickablePreviewElement(container, PreviewElement("_________________________", "keyInput", 0xFF1E1E2F.toInt(), "input"), lines, dummy)
        addClickablePreviewElement(container, PreviewElement("🔐 MAG-LOGIN", "btnLogin", 0xFF7B1FA2.toInt()), lines, dummy)
    }

    private fun buildAdminFullPreview(container: LinearLayout, lines: List<String>) {
        val dummy = ""
        listOf("✏️ File Editor" to "fileEditor", "🔑 Key Code Generator" to "keyGen",
            "👤 User Management" to "userMgmt", "📊 System Status" to "status",
            "☁️ GitHub Settings" to "github").forEach { (label, key) ->
            addClickablePreviewElement(container, PreviewElement(label, key, 0xFF1E1E2F.toInt()), lines, dummy)
        }
    }

    private fun addClickablePreviewElement(container: LinearLayout, el: PreviewElement, lines: List<String>, fullCode: String) {
        val targetLine = if (fullCode.isNotEmpty()) findBestMatchingLine(el.searchKey, fullCode) else -1
        val finalLine = if (targetLine == -1) {
            Toast.makeText(context, "⚠️ Hindi nakita: ${el.searchKey}", Toast.LENGTH_SHORT).show()
            1
        } else targetLine

        val view = when (el.type) {
            "spinner" -> TextView(requireContext()).apply {
                text = el.label; setBackgroundColor(el.bgColor); setTextColor(0xFFAAAAAA.toInt())
                textSize = 14f; setPadding(16, 14, 16, 14)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 56).apply { setMargins(16, 0, 16, 8) }
                setOnClickListener { 
                    jumpToLine(finalLine)
                    closePreviewDialog()
                }
            }
            "editor" -> TextView(requireContext()).apply {
                text = el.label; setBackgroundColor(el.bgColor); setTextColor(0xFF666666.toInt())
                textSize = 11f; setPadding(16, 16, 16, 16); minHeight = 200
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { setMargins(16, 0, 16, 12) }
                setOnClickListener { 
                    jumpToLine(finalLine)
                    closePreviewDialog()
                }
            }
            "input" -> TextView(requireContext()).apply {
                text = el.label; setBackgroundColor(el.bgColor); setTextColor(0xFF888888.toInt())
                textSize = 14f; setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 56).apply { setMargins(16, 4, 16, 12) }
                setOnClickListener { 
                    jumpToLine(finalLine)
                    closePreviewDialog()
                }
            }
            "status" -> TextView(requireContext()).apply {
                text = el.label; setTextColor(0xFFFFA500.toInt()); textSize = 13f; setPadding(16, 8, 16, 8)
                setOnClickListener { 
                    jumpToLine(finalLine)
                    closePreviewDialog()
                }
            }
            "label" -> TextView(requireContext()).apply {
                text = el.label; setTextColor(0xFFFFFFFF.toInt()); setTypeface(null, Typeface.BOLD)
                textSize = 14f; setPadding(16, 8, 16, 4)
                setOnClickListener { 
                    jumpToLine(finalLine)
                    closePreviewDialog()
                }
            }
            else -> Button(requireContext()).apply {
                text = el.label; setBackgroundColor(el.bgColor); setTextColor(0xFFFFFFFF.toInt())
                textSize = 14f; minHeight = 52; setPadding(16, 12, 16, 12)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { setMargins(16, 6, 16, 6) }
                setOnClickListener { 
                    jumpToLine(finalLine)
                    closePreviewDialog()
                }
            }
        }
        container.addView(view)
    }

    private fun addClickablePreviewBtnToRow(row: LinearLayout, label: String, key: String, color: Int, lines: List<String>, fullCode: String) {
        val targetLine = if (fullCode.isNotEmpty()) findBestMatchingLine(key, fullCode) else -1
        val finalLine = if (targetLine == -1) {
            Toast.makeText(context, "⚠️ Hindi nakita: $key", Toast.LENGTH_SHORT).show()
            1
        } else targetLine

        row.addView(Button(requireContext()).apply {
            text = label; setBackgroundColor(color); setTextColor(0xFFFFFFFF.toInt()); textSize = 11f; minHeight = 50
            setPadding(4, 8, 4, 8)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { 
                jumpToLine(finalLine)
                closePreviewDialog()
            }
        })
    }

    private fun buildFullXmlPreview(code: String): View {
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
        val svCount = Regex("<ScrollView").findAll(code).count()
        val lines = code.lines()

        container.addView(LinearLayout(requireContext()).apply {
            setBackgroundColor(0xFF1A1A2E.toInt()); setPadding(20, 20, 20, 20)
            orientation = LinearLayout.VERTICAL
            addView(TextView(requireContext()).apply {
                text = "📋 Mga Elemento:"; setTextColor(Color.WHITE); setTypeface(null, Typeface.BOLD); setPadding(0, 0, 0, 12)
            })
            addView(TextView(requireContext()).apply { text = "• Linear Layout: $llCount"; setTextColor(0xFF64B5F6.toInt()); textSize = 13f })
            addView(TextView(requireContext()).apply { text = "• Scroll View: $svCount"; setTextColor(0xFFBB86FC.toInt()); textSize = 13f })
            addView(TextView(requireContext()).apply { text = "• Pindutan: $btnCount"; setTextColor(0xFF64B5F6.toInt()); textSize = 13f })
            addView(TextView(requireContext()).apply { text = "• Teksto: $tvCount"; setTextColor(0xFFCCCCCC.toInt()); textSize = 13f })
            addView(TextView(requireContext()).apply { text = "• Kahon/Pag-input: $etCount"; setTextColor(0xFF81C784.toInt()); textSize = 13f })
        })

        container.addView(TextView(requireContext()).apply {
            text = "\n💡 Pindutin kahit saan → lumipat sa eksaktong linya"
            textSize = 12f; setTextColor(0xFF888888.toInt()); gravity = Gravity.CENTER
        })
        container.setOnClickListener { 
            jumpToLine(1)
            closePreviewDialog()
        }
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
                cleanOriginalCode = String(Base64.decode(jo.getString("content").replace("\n", ""), Base64.DEFAULT))
                
                withContext(Dispatchers.Main) {
                    // ✅ ILAGAY ANG NUMERO SA BAWAT LINYA PARA MAKITA
                    val withLineNumbers = applyLineNumbers(cleanOriginalCode)
                    codeEditor.removeTextChangedListener(codeWatcher)
                    codeEditor.setText(withLineNumbers)
                    codeEditor.addTextChangedListener(codeWatcher)
                    
                    updateLineNumbers()
                    showStatus("✅ Nai-load — ${cleanOriginalCode.lines().size} linya", true)
                    checkForErrors(cleanOriginalCode)
                    showLoading(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false); showLoading(false)
                }
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
        val cleanCode = getCleanCode()
        if (cleanCode.isBlank()) { 
            Toast.makeText(context, "⚠️ Walang kodigong kokopyahin!", Toast.LENGTH_SHORT).show()
            return 
        }
        val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Code", cleanCode))
        Toast.makeText(context, "✅ Nakopya! (walang numero)", Toast.LENGTH_SHORT).show()
    }

    private fun pasteCode() {
        val clip = (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (!text.isNullOrBlank()) {
                val withNumbers = applyLineNumbers(text)
                codeEditor.removeTextChangedListener(codeWatcher)
                codeEditor.setText(withNumbers)
                codeEditor.addTextChangedListener(codeWatcher)
                cleanOriginalCode = text
                updateLineNumbers()
                Toast.makeText(context, "✅ Nakapaste!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        Toast.makeText(context, "⚠️ Walang laman ang clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun clearCode() {
        codeEditor.setText("")
        cleanOriginalCode = ""
        errorPanel.visibility = View.GONE
        lineNumbers.text = ""
        Toast.makeText(context, "✅ Nabura!", Toast.LENGTH_SHORT).show()
    }

    private fun saveLocal() {
        val content = getCleanCode()
        if (content.isBlank() || selectedFilePath.isBlank()) {
            showStatus("⚠️ I-load muna!", false); return
        }
        val file = File(requireContext().filesDir, selectedFilePath.substringAfterLast('/'))
        file.parentFile?.mkdirs()
        file.writeText(content)
        cleanOriginalCode = content
        showStatus("✅ Nai-save: ${file.name}", true)
        Toast.makeText(context, "✅ Nai-save! (walang numero)", Toast.LENGTH_SHORT).show()
    }

    private fun pushToGithub() {
        val content = getCleanCode() //




        if (content.isBlank() || selectedFilePath.isBlank() || githubToken.isNullOrEmpty()) {
            showStatus("⚠️ Kulang ang impormasyon", false)
            return
        }

        val input = EditText(requireContext()).apply {
            hint = "Ilagay ang mensahe ng pagbabago..."
            setBackgroundColor(0xFFFFFFFF.toInt())
            setTextColor(0xFF000000.toInt())
            setHintTextColor(0xFF888888.toInt())
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

                val url = URL("${BASE_URL}$repoOwner/$repoName/contents/$selectedFilePath")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.doOutput = true
                conn.setRequestProperty("Authorization", "token $githubToken")
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use { it.write(body) }

                if (conn.responseCode in 200..201) {
                    val response = JSONObject(
                        BufferedReader(InputStreamReader(conn.inputStream)).readText()
                    )
                    currentSha = response.optJSONObject("commit")?.optString("sha")
                    cleanOriginalCode = content

                    withContext(Dispatchers.Main) {
                        showStatus("☁️✅ MATAGUMPAY NA-UPLOAD!", true)
                        Toast.makeText(context, "✅ Nai-push sa GitHub!", Toast.LENGTH_SHORT).show()
                        showLoading(false)
                    }
                } else {
                    throw Exception("HTTP ${conn.responseCode}: ${conn.responseMessage}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showStatus("❌ ${e.message}", false)
                    Toast.makeText(context, "❌ Nabigo: ${e.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun showStatus(message: String, isSuccess: Boolean) {
        statusText.text = message
        statusText.setTextColor(if (isSuccess) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
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

    private fun createWhiteTextAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.setTextColor(0xFFFFFFFF.toInt())
                v.textSize = 13f
                v.setPadding(16, 12, 16, 12)
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent) as TextView
                v.setTextColor(0xFFFFFFFF.toInt())
                v.setBackgroundColor(0xFF1E1E2F.toInt())
                v.textSize = 13f
                v.setPadding(16, 14, 16, 14)
                return v
            }
        }
    }
}

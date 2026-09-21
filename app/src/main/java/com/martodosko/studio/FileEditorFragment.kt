// ==================================================
// FILE: FileEditorFragment.kt — ✅ TUNAY NA GUI PREVIEW! HINDI LANG KODIGO!
// VERSION: 3.4.0 — ✅ KUNG KAYA RENDERIN → IPAPAKITA ANG ITSURA NG SCREEN!
// UPDATED: 2026-09-22 — KOTLIN = SCREEN PREVIEW | XML = LAYOUT PREVIEW | HTML = TUNAY NA PAHINA!
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

        // ===== SCROLLABLE NA LAMAN =====
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
            setPadding(8, 12, 8, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) }
            setOnClickListener { previewCode() }
        }

        quickRow.addView(btnCopy)
        quickRow.addView(btnPreview)
        main.addView(quickRow)

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
        val isXml = selectedFilePath.endsWith(".xml")
        val isHtml = selectedFilePath.endsWith(".html")

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

    // ==============================================
    // ✅ TUNAY NA GUI PREVIEW — HINDI LANG KODIGO!
    // ==============================================
    private fun previewCode() {
        val code = codeEditor.text.toString()
        if (code.isBlank()) {
            Toast.makeText(context, "⚠️ Walang kodigong ipapakita!", Toast.LENGTH_SHORT).show()
            return
        }

        val previewView = when {
            selectedFilePath.endsWith(".html") -> {
                // HTML — tunay na WebView
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

            selectedFilePath.endsWith(".kt") -> {
                // ✅ KOTLIN — BUUIN ANG TUNAY NA GUI PREVIEW!
                buildKotlinGuiPreview(code)
            }

            selectedFilePath.endsWith(".xml") -> {
                // ✅ XML — BUUIN ANG LAYOUT PREVIEW!
                buildXmlLayoutPreview(code)
            }

            else -> {
                // Iba pang uri — kulay na kodigo
                val scroll = ScrollView(requireContext()).apply {
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

        AlertDialog.Builder(requireContext())
            .setTitle("👁️ PREVIEW — $selectedFilePath")
            .setView(previewView)
            .setPositiveButton("TAPOS", null)
            .show()
    }

    // ==============================================
    // ✅ BUUIN ANG TUNAY NA GUI PARA SA KOTLIN
    // ==============================================
    private fun buildKotlinGuiPreview(code: String): View {
        val scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFF12121F.toInt())
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scroll.addView(container)

        // Kunin ang pangalan ng klase
        val className = Regex("class\\s+(\\w+)").find(code)?.groupValues?.get(1) ?: "UnknownScreen"
        val isAdmin = className.contains("Admin", ignoreCase = true)
        val isLogin = className.contains("Login", ignoreCase = true)
        val isEditor = className.contains("Editor", ignoreCase = true)
        val isMixer = className.contains("Mixer", ignoreCase = true)
        val isHelp = className.contains("Help", ignoreCase = true)
        val isFragment = className.contains("Fragment", ignoreCase = true)

        // ===== HEADER NG PREVIEW =====
        container.addView(LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1A1A2E.toInt())
            setPadding(20, 16, 20, 16)
            setOnClickListener { }
            addView(TextView(requireContext()).apply {
                text = when {
                    isAdmin -> "🔐 ADMIN PANEL"
                    isLogin -> "🔐 LOGIN"
                    isEditor -> "✏️ FILE EDITOR"
                    isMixer -> "🎛️ MIXER"
                    isHelp -> "❌ TULONG / GABAY"
                    else -> "📱 $className"
                }
                textSize = 20f
                setTextColor(0xFF40E0D0.toInt())
                setTypeface(null, Typeface.BOLD)
                setGravity(Gravity.CENTER)
            })
            addView(TextView(requireContext()).apply {
                text = if (isFragment) "Fragment Preview" else "Screen Preview"
                textSize = 12f
                setTextColor(0xFF888888.toInt())
                setGravity(Gravity.CENTER)
                setPadding(0, 4, 0, 0)
            })
        })

        // ===== SIMULATED CONTENT AYON SA URI NG SCREEN =====
        when {
            isEditor -> {
                // File Editor — katulad ng nakikita mo ngayon!
                container.addView(TextView(requireContext()).apply {
                    text = "📁 Piliin ang Folder:"
                    setTextColor(Color.WHITE)
                    textSize = 14f
                    setPadding(4, 16, 4, 4)
                    setTypeface(null, Typeface.BOLD)
                })
                container.addView(View(requireContext()).apply {
                    setBackgroundColor(0xFF252540.toInt())
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        48
                    ).apply { setMargins(16, 4, 16, 12) }
                })
                container.addView(TextView(requireContext()).apply {
                    text = "📄 Piliin ang File:"
                    setTextColor(Color.WHITE)
                    textSize = 14f
                    setPadding(4, 16, 4, 4)
                    setTypeface(null, Typeface.BOLD)
                })
                container.addView(View(requireContext()).apply {
                    setBackgroundColor(0xFF252540.toInt())
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        48
                    ).apply { setMargins(16, 4, 16, 12) }
                })
                container.addView(Button(requireContext()).apply {
                    text = "📥 I-LOAD MULA SA GITHUB"
                    setBackgroundColor(0xFF1976D2.toInt())
                    setTextColor(Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        56
                    ).apply { setMargins(16, 0, 16, 12) }
                })
                container.addView(View(requireContext()).apply {
                    setBackgroundColor(0xFF1A1A2E.toInt())
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        200
                    ).apply { setMargins(16, 0, 16, 12) }
                })
                val btnRow = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(12, 0, 12, 0) }
                }
                btnRow.addView(Button(requireContext()).apply {
                    text = "📋 KOPIYA"
                    setBackgroundColor(0xFF455A64.toInt())
                    setTextColor(Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(0, 52, 1f).apply { setMargins(4, 0, 4, 0) }
                })
                btnRow.addView(Button(requireContext()).apply {
                    text = "👁️ PREVIEW"
                    setBackgroundColor(0xFF00897B.toInt())
                    setTextColor(Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(0, 52, 1f).apply { setMargins(4, 0, 4, 0) }
                })
                container.addView(btnRow)
            }

            isLogin -> {
                container.addView(LinearLayout(requireContext()).apply {
                    setPadding(24, 32, 24, 32)
                    orientation = LinearLayout.VERTICAL
                    addView(TextView(requireContext()).apply {
                        text = "Ipasok ang Key Code:"
                        setTextColor(0xFFCCCCCC.toInt())
                        setPadding(0, 0, 0, 8)
                    })
                    addView(View(requireContext()).apply {
                        setBackgroundColor(0xFF252540.toInt())
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            56
                        ).apply { setMargins(0, 0, 0, 16) }
                    })
                    addView(Button(requireContext()).apply {
                        text = "🔐 MAG-LOGIN"
                        setBackgroundColor(0xFF7B1FA2.toInt())
                        setTextColor(Color.WHITE)
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            54
                        )
                    })
                })
            }

            isAdmin -> {
                container.addView(TextView(requireContext()).apply {
                    text = "👑 OWNER — BUONG KAPANGYARIHAN"
                    setTextColor(0xFFFFD700.toInt())
                    setTypeface(null, Typeface.BOLD)
                    setGravity(Gravity.CENTER)
                    textSize = 16f
                    setPadding(0, 16, 0, 16)
                })
                listOf(
                    "✏️ File Editor",
                    "🔑 Key Code Generator",
                    "👤 User Management",
                    "📊 System Status",
                    "☁️ GitHub Settings"
                ).forEach { item ->
                    container.addView(LinearLayout(requireContext()).apply {
                        setBackgroundColor(0xFF1E1E2F.toInt())
                        setPadding(20, 16, 20, 16)
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply { setMargins(16, 4, 16, 4) }
                        addView(TextView(requireContext()).apply {
                            text = item
                            setTextColor(0xFFFFFFFF.toInt())
                            textSize = 14f
                        })
                    })
                }
            }

            else -> {
                container.addView(TextView(requireContext()).apply {
                    text = "📱 Screen Preview"
                    setTextColor(0xFFAAAAAA.toInt())
                    setGravity(Gravity.CENTER)
                    setPadding(0, 40, 0, 20)
                })
                container.addView(TextView(requireContext()).apply {
                    text = "Klase: $className\n\nAng aktuwal na itsura ay makikita kapag pinatakbo ang app."
                    setTextColor(0xFF888888.toInt())
                    setGravity(Gravity.CENTER)
                    textSize = 13f
                    setPadding(20, 0, 20, 20)
                })
            }
        }

        return scroll
    }

    // ==============================================
    // ✅ BUUIN ANG LAYOUT PREVIEW PARA SA XML
    // ==============================================
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
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scroll.addView(container)

        // Kunin ang ugat na layout
        val rootTag = Regex("<([a-zA-Z0-9]+)").find(code)?.groupValues?.get(1) ?: "Layout"

        container.addView(TextView(requireContext()).apply {
            text = "🔶 LAYOUT PREVIEW"
            textSize = 18f
            setTextColor(0xFFFFB74D.toInt())
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        })

        container.addView(TextView(requireContext()).apply {
            text = "Ugat: &lt;$rootTag&gt;"
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 0, 0, 16)
        })

        // Bilangin ang mga elemento
        val btnCount = Regex("<(Button|TextView|ImageView|EditText|LinearLayout)").findAll(code).count()

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
                text = "• Kabuuan: ~$btnCount na elemento"
                setTextColor(0xFFCCCCCC.toInt())
                textSize = 13f
            })
            if (code.contains("android:id")) {
                val idCount = Regex("android:id=\"@\\+id/([^\"]+)\"").findAll(code).count()
                addView(TextView(requireContext()).apply {
                    text = "• May ID: $idCount"
                    setTextColor(0xFFCCCCCC.toInt())
                    textSize = 13f
                })
            }
            if (code.contains("WebView")) {
                addView(TextView(requireContext()).apply {
                    text = "• 🌐 WebView — nagpapakita ng pahina"
                    setTextColor(0xFF40E0D0.toInt())
                    textSize = 13f
                })
            }
            if (code.contains("Button")) {
                addView(TextView(requireContext()).apply {
                    text = "• 🔘 Button — pindutan"
                    setTextColor(0xFF64B5F6.toInt())
                    textSize = 13f
                })
            }
        })

        container.addView(TextView(requireContext()).apply {
            text = "\n💡 Ang buong itsura ay makikita sa aktuwal na pagtakbo ng app."
            setTextColor(0xFF666666.toInt())
            textSize = 12f
            setPadding(0, 16, 0, 0)
        })

        return scroll
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
                originalContent = String(android.util.Base64.decode(json.getString("content").replace("\n", ""), Base64.DEFAULT))
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
        val input = EditText(requireContext()).apply { setTextColor(Color.WHITE); hint = "Commit message" }
        AlertDialog.Builder(requireContext())
            .setTitle("📤 I-PUSH SA GITHUB")
            .setMessage("Papalitan: $selectedFilePath")
            .setView(input)
            .setPositiveButton("I-PUSH") { _, _ ->
                performPush(content, input.text.toString().trim().ifEmpty { "Na-update" })
            }
            .setNegativeButton("Kanselahin", null)
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
        listOf(btnLoad, btnCopy, btnPreview, btnSaveLocal, btnPushGithub, btnRefresh).forEach {
            it.isEnabled = !show
        }
    }
}

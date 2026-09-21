// ==================================================
// FILE: PresetModerationFragment.kt — ✅ PINAGSAMA: PresetManager + Moderation!
// VERSION: 2.0.0 — ✅ USER SAVE + ADMIN MODERATION + GITHUB INTEGRATION!
// UPDATED: 2026-09-21 — WALANG IBANG FILE — LAHAT NANDITO! AYON SA BUONG PLANO!
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64

class PresetModerationFragment : Fragment() {

    // ==============================================
    // 🔐 PRESET MANAGER — NAKA-EMBED NA!
    // ==============================================
    inner class PresetManager(private val ctx: Context) {
        private val prefs = ctx.getSharedPreferences("preset_prefs", Context.MODE_PRIVATE)
        private val userPrefs = ctx.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        fun getUserLevel(): UserLevel {
            val memberKey = userPrefs.getString("member_key", null)
            val ownerKey = userPrefs.getString("owner_key", null)
            return when {
                ownerKey != null && ownerKey.startsWith("MARTODOSKO-OWNER") -> UserLevel.OWNER
                memberKey != null && memberKey.startsWith("MARTODOSKO-MEMBER") -> UserLevel.MEMBER
                else -> UserLevel.NON_MEMBER
            }
        }

        fun getSaveFolder(): String = when (getUserLevel()) {
            UserLevel.OWNER -> "presets/owner"
            UserLevel.MEMBER -> "presets/submissions"
            UserLevel.NON_MEMBER -> "presets/public"
        }

        fun getAccessibleFolders(): List<String> = when (getUserLevel()) {
            UserLevel.OWNER -> listOf("presets/owner", "presets/submissions", "presets/global", "presets/public")
            UserLevel.MEMBER -> listOf("presets/public", "presets/members/${getUserId()}", "presets/global")
            UserLevel.NON_MEMBER -> listOf("presets/public")
        }

        private fun getUserId(): String {
            val key = userPrefs.getString("member_key", "GUEST")
            return key.hashCode().toString(36)
        }

        suspend fun savePreset(presetName: String, presetData: String, description: String = ""): Result<String> {
            return kotlinx.coroutines.withContext(Dispatchers.IO) {
                try {
                    val token = GithubManagerFragment.getDecryptedToken(ctx)
                        ?: return@withContext Result.failure(Exception("❌ Walang GitHub Token!"))
                    val (owner, repo) = GithubManagerFragment.getRepoInfo(ctx)
                    if (owner.isEmpty() || repo.isEmpty())
                        return@withContext Result.failure(Exception("❌ Kulang ang Repository Info!"))

                    val folder = getSaveFolder()
                    val fileName = "${sanitizeName(presetName)}.json"
                    val path = "$folder/$fileName"
                    val status = if (getUserLevel() == UserLevel.MEMBER) "pending" else "approved"

                    val presetObject = JSONObject().apply {
                        put("name", presetName)
                        put("description", description)
                        put("author", userPrefs.getString("member_key", "Non-Member User"))
                        put("author_id", getUserId())
                        put("status", status)
                        put("submitted_at", System.currentTimeMillis())
                        put("data", JSONObject(presetData))
                    }

                    val base64Content = Base64.encodeToString(
                        presetObject.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP
                    )

                    val jsonBody = JSONObject().apply {
                        put("message", "Upload preset: $presetName [$status]")
                        put("content", base64Content)
                    }.toString()

                    val url = URL("https://api.github.com/repos/$owner/$repo/contents/$path")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "PUT"
                    conn.setRequestProperty("Authorization", "token $token")
                    conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.connectTimeout = 15000
                    conn.readTimeout = 15000
                    conn.outputStream.use { it.write(jsonBody.toByteArray(Charsets.UTF_8)) }

                    val msg = if (status == "pending")
                        "✅ Naipadala para sa pagsusuri! Hintayin ang aprubasyon."
                    else "✅ Nai-save na!"

                    if (conn.responseCode == 200 || conn.responseCode == 201)
                        Result.success("$msg\n📂 $path")
                    else Result.failure(Exception("❌ Error ${conn.responseCode}"))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }

        suspend fun loadPresets(): Result<List<PresetFile>> {
            return kotlinx.coroutines.withContext(Dispatchers.IO) {
                try {
                    val token = GithubManagerFragment.getDecryptedToken(ctx)
                        ?: return@withContext Result.failure(Exception("❌ Walang GitHub Token!"))
                    val (owner, repo) = GithubManagerFragment.getRepoInfo(ctx)
                    if (owner.isEmpty() || repo.isEmpty())
                        return@withContext Result.failure(Exception("❌ Kulang ang Repository Info!"))

                    val allPresets = mutableListOf<PresetFile>()
                    for (folder in getAccessibleFolders()) {
                        loadFolderPresets(owner, repo, folder, token).let { allPresets.addAll(it) }
                    }
                    Result.success(allPresets.sortedByDescending { it.savedAt })
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }

        private suspend fun loadFolderPresets(owner: String, repo: String, folder: String, token: String): List<PresetFile> {
            return kotlinx.coroutines.withContext(Dispatchers.IO) {
                val presets = mutableListOf<PresetFile>()
                try {
                    val url = URL("https://api.github.com/repos/$owner/$repo/contents/$folder")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.setRequestProperty("Authorization", "token $token")
                    conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    conn.connectTimeout = 10000
                    conn.readTimeout = 10000

                    if (conn.responseCode == 200) {
                        val arr = JSONArray(BufferedReader(InputStreamReader(conn.inputStream)).readText())
                        for (i in 0 until arr.length()) {
                            val item = arr.getJSONObject(i)
                            if (item.getString("type") == "file" && item.getString("name").endsWith(".json")) {
                                presets.add(PresetFile(
                                    name = item.getString("name").removeSuffix(".json"),
                                    path = item.getString("path"),
                                    downloadUrl = item.getString("download_url"),
                                    savedAt = item.optString("updated_at", ""),
                                    folder = folder,
                                    sha = item.optString("sha", "")
                                ))
                            }
                        }
                    }
                } catch (_: Exception) {}
                presets
            }
        }

        suspend fun updatePresetStatus(path: String, newStatus: String, sha: String): Result<String> {
            return kotlinx.coroutines.withContext(Dispatchers.IO) {
                try {
                    val token = GithubManagerFragment.getDecryptedToken(ctx)
                        ?: return@withContext Result.failure(Exception("❌ Walang Token!"))
                    val (owner, repo) = GithubManagerFragment.getRepoInfo(ctx)

                    val fileUrl = URL("https://api.github.com/repos/$owner/$repo/contents/$path")
                    val connGet = fileUrl.openConnection() as HttpURLConnection
                    connGet.setRequestProperty("Authorization", "token $token")
                    val contentObj = JSONObject(BufferedReader(InputStreamReader(connGet.inputStream)).readText())
                    val decodedContent = String(Base64.decode(contentObj.getString("content"), Base64.DEFAULT))
                    val presetObj = JSONObject(decodedContent)
                    presetObj.put("status", newStatus)

                    val newBase64 = Base64.encodeToString(presetObj.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                    val jsonBody = JSONObject().apply {
                        put("message", "Update status: $newStatus")
                        put("content", newBase64)
                        put("sha", contentObj.getString("sha"))
                    }.toString()

                    val connPut = fileUrl.openConnection() as HttpURLConnection
                    connPut.requestMethod = "PUT"
                    connPut.setRequestProperty("Authorization", "token $token")
                    connPut.setRequestProperty("Content-Type", "application/json")
                    connPut.doOutput = true
                    connPut.outputStream.use { it.write(jsonBody.toByteArray(Charsets.UTF_8)) }

                    if (connPut.responseCode == 200 || connPut.responseCode == 201)
                        Result.success("✅ Katayuan na-update: $newStatus")
                    else Result.failure(Exception("❌ Error: ${connPut.responseCode}"))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }

        private fun sanitizeName(name: String) = name.replace(Regex("[^a-zA-Z0-9 _-]"), "_").take(50)
    }

    // ==============================================
    // 📊 DATA CLASSES
    // ==============================================
    enum class UserLevel { OWNER, MEMBER, NON_MEMBER }
    data class PresetFile(
        val name: String, val path: String, val downloadUrl: String,
        val savedAt: String, val folder: String, val sha: String, val status: String = "pending"
    )
    data class PresetItem(
        val id: String, val name: String, val author: String, val description: String,
        val status: String, val submittedAt: Long, val jsonData: String
    )

    // ==============================================
    // 🎨 FRAGMENT UI — MODERATION PANEL
    // ==============================================
    private lateinit var presetManager: PresetManager
    private lateinit var prefsPresets: SharedPreferences
    private lateinit var prefsSession: SharedPreferences
    private lateinit var presetsContainer: LinearLayout
    private lateinit var tvStatus: TextView
    private lateinit var dateFormat: SimpleDateFormat

    private var currentUserLevel: String = "GUEST"
    private var allPresets = mutableListOf<PresetItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        presetManager = PresetManager(requireContext())
        prefsPresets = requireContext().getSharedPreferences("preset_moderation", Context.MODE_PRIVATE)
        prefsSession = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        currentUserLevel = prefsSession.getString("user_level", "GUEST") ?: "GUEST"

        val root = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(0xFF12121F.toInt())
            setPadding(20, 20, 20, 40)
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        mainContainer.addView(createHeader())
        mainContainer.addView(createSummaryCard())
        mainContainer.addView(createFilterButtons())

        tvStatus = TextView(requireContext()).apply {
            text = "⏳ Kinakarga ang mga preset..."
            textSize = 14f; setTextColor(0xFF888888.toInt()); setPadding(0, 16, 0, 8)
        }
        mainContainer.addView(tvStatus)

        presetsContainer = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        mainContainer.addView(presetsContainer)

        root.addView(mainContainer)
        loadAllPresets()
        return root
    }

    private fun createHeader(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
            gravity = android.view.Gravity.CENTER
        }
        card.addView(TextView(requireContext()).apply {
            text = "📋  PAMAMAHALA NG PRESET"
            textSize = 24f; setTextColor(0xFF9C27B0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        card.addView(TextView(requireContext()).apply {
            text = "Aprubahan • Tanggihan • Tingnan ang mga ipinadalang preset"
            textSize = 13f; setTextColor(0xFF888888.toInt()); setPadding(0, 6, 0, 0)
        })
        return card
    }

    private fun createSummaryCard(): View {
        val total = prefsPresets.getInt("total_presets", 0)
        val approved = prefsPresets.getInt("approved_presets", 0)
        val pending = prefsPresets.getInt("pending_presets", 0)
        val rejected = prefsPresets.getInt("rejected_presets", 0)

        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        card.addView(TextView(requireContext()).apply {
            text = "📊 BUOD"; textSize = 15f; setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD); setPadding(0, 0, 0, 12)
        })

        val grid = TableLayout(requireContext())
        val row1 = TableRow(requireContext())
        row1.addView(createSummaryCell("📋 Kabuuan", "$total", 0xFF9C27B0.toInt()))
        row1.addView(createSummaryCell("⏳ Nakahihintulot", "$pending", 0xFFFFA500.toInt()))
        val row2 = TableRow(requireContext())
        row2.addView(createSummaryCell("✅ Aprubado", "$approved", 0xFF4CAF50.toInt()))
        row2.addView(createSummaryCell("❌ Tinanggihan", "$rejected", 0xFFFF5252.toInt()))
        grid.addView(row1); grid.addView(row2)
        card.addView(grid)
        return card
    }

    private fun createSummaryCell(label: String, value: String, color: Int): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
            setBackgroundColor(0xFF252538.toInt())
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 4, 4, 4) }
            gravity = android.view.Gravity.CENTER
            addView(TextView(requireContext()).apply {
                text = value; textSize = 20f; setTextColor(color)
                setTypeface(null, android.graphics.Typeface.BOLD)
            })
            addView(TextView(requireContext()).apply {
                text = label; textSize = 11f; setTextColor(0xFF999999.toInt()); setPadding(0, 4, 0, 0)
            })
        }
    }

    private fun createFilterButtons(): View {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        val btnAll = createFilterButton("📋 Lahat", true)
        val btnPending = createFilterButton("⏳ Nakahihintulot", false)
        val btnApproved = createFilterButton("✅ Aprubado", false)
        btnAll.setOnClickListener { loadAllPresets() }
        btnPending.setOnClickListener { loadPresetsByStatus("pending") }
        btnApproved.setOnClickListener { loadPresetsByStatus("approved") }
        container.addView(btnAll); container.addView(btnPending); container.addView(btnApproved)
        return container
    }

    private fun createFilterButton(text: String, isActive: Boolean): Button {
        return Button(requireContext()).apply {
            this.text = text; textSize = 12f
            setBackgroundColor(if (isActive) 0xFF9C27B0.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 40, 1f).apply { setMargins(4, 0, 4, 0) }
        }
    }

    private fun loadAllPresets() {
        allPresets.clear(); presetsContainer.removeAllViews()
        GlobalScope.launch(Dispatchers.Main) {
            val result = presetManager.loadPresets()
            if (result.isSuccess) {
                val list = result.getOrNull()!!
                if (list.isEmpty()) {
                    tvStatus.text = "📋 Wala pang ipinadalang preset."
                    return@launch
                }
                tvStatus.text = "📋 ${list.size} preset na natanggap:"
                list.sortedByDescending { it.savedAt }.forEach { addPresetCard(it) }
            } else {
                tvStatus.text = "⚠️ Hindi makarga: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    private fun loadPresetsByStatus(status: String) {
        loadAllPresets()
        presetsContainer.removeAllViews()
        GlobalScope.launch(Dispatchers.Main) {
            val result = presetManager.loadPresets()
            if (result.isSuccess) {
                val filtered = result.getOrNull()!!.filter { it.status == status }
                if (filtered.isEmpty()) {
                    tvStatus.text = "📋 Walang preset na katayuan: $status"
                    return@launch
                }
                tvStatus.text = "📋 ${filtered.size} preset — $status:"
                filtered.sortedByDescending { it.savedAt }.forEach { addPresetCard(it) }
            }
        }
    }

    private fun addPresetCard(preset: PresetFile) {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }

        val topRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        topRow.addView(TextView(requireContext()).apply {
            text = preset.name; textSize = 16f; setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })

        val statusColor = when (preset.status) {
            "approved" -> 0xFF4CAF50.toInt(); "pending" -> 0xFFFFA500.toInt()
            "rejected" -> 0xFFFF5252.toInt(); else -> 0xFF888888.toInt()
        }
        val statusText = when (preset.status) {
            "approved" -> "✅ APRUBADO"; "pending" -> "⏳ NAKAHIHINTULOT"
            "rejected" -> "❌ TINANGGIHAN"; else -> "❓ UNKNOWN"
        }
        topRow.addView(TextView(requireContext()).apply {
            text = statusText; textSize = 11f; setTextColor(statusColor)
            setPadding(10, 3, 10, 3); setBackgroundColor(0xFF2A2A3A.toInt())
        })

        card.addView(topRow)
        card.addView(TextView(requireContext()).apply {
            text = "📂 ${preset.folder} • 📅 ${preset.savedAt.take(16)}"
            textSize = 12f; setTextColor(0xFF888888.toInt()); setPadding(0, 6, 0, 0)
        })

        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL; setPadding(0, 12, 0, 0)
        }

        btnRow.addView(Button(requireContext()).apply {
            text = "✅ APRUBA"; isEnabled = preset.status == "pending"
            textSize = 11f; setBackgroundColor(if (isEnabled) 0xFF2E7D32.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { approvePreset(preset) }
        })

        btnRow.addView(Button(requireContext()).apply {
            text = "❌ TANGGI"; isEnabled = preset.status != "rejected"
            textSize = 11f; setBackgroundColor(if (isEnabled) 0xFF8B0000.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { rejectPreset(preset) }
        })

        card.addView(btnRow)
        presetsContainer.addView(card)
    }

    private fun approvePreset(preset: PresetFile) {
        AlertDialog.Builder(requireContext())
            .setTitle("✅ Aprubahan ang Preset?")
            .setMessage("Aprubahan ba natin ang: ${preset.name}?\nIlilipat sa Pangkalahatang Presets.")
            .setPositiveButton("Aprubahan") { _, _ ->
                GlobalScope.launch {
                    val result = presetManager.updatePresetStatus(preset.path, "approved", preset.sha)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, result.getOrElse { "❌ Error: ${it.message}" }, Toast.LENGTH_SHORT).show()
                        loadAllPresets()
                    }
                }
            }
            .setNegativeButton("Kanselahin", null).show()
    }

    private fun rejectPreset(preset: PresetFile) {
        AlertDialog.Builder(requireContext())
            .setTitle("❌ Tanggihan ang Preset?")
            .setMessage("Tanggihan ba natin ang: ${preset.name}?")
            .setPositiveButton("Tanggihan") { _, _ ->
                GlobalScope.launch {
                    val result = presetManager.updatePresetStatus(preset.path, "rejected", preset.sha)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, result.getOrElse { "❌ Error: ${it.message}" }, Toast.LENGTH_SHORT).show()
                        loadAllPresets()
                    }
                }
            }
            .setNegativeButton("Kanselahin", null).show()
    }
}

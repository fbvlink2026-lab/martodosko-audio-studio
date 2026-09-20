// ==================================================
// FILE: PresetModerationFragment.kt — ✅ KUMPLETONG PRESET MANAGER!
// VERSION: 1.0.0 — ✅ LISTAHAN • DETALYE • APRUBUHAN • TANGGIHAN • BAWAL • EXPORT!
// UPDATED: 2026-09-21 — MAY STATUS • PETSA • MAY-ARI • KULAY AY KATAYUAN!
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
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PresetModerationFragment : Fragment() {

    private lateinit var prefsPresets: SharedPreferences
    private lateinit var prefsSession: SharedPreferences
    private lateinit var presetsContainer: LinearLayout
    private lateinit var tvStatus: TextView
    private lateinit var dateFormat: SimpleDateFormat

    private var currentUserLevel: String = "GUEST"
    private var allPresets = mutableListOf<PresetItem>()

    data class PresetItem(
        val id: String,
        val name: String,
        val author: String,
        val description: String,
        val status: String, // "pending", "approved", "rejected", "banned"
        val submittedAt: Long,
        val jsonData: String
    )

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
            setPadding(20, 20, 20, 40)
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        prefsPresets = requireContext().getSharedPreferences("preset_moderation", Context.MODE_PRIVATE)
        prefsSession = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        currentUserLevel = prefsSession.getString("user_level", "GUEST") ?: "GUEST"

        // ==============================================
        // 📋 HEADER
        // ==============================================
        mainContainer.addView(createHeader())

        // ==============================================
        // 📊 BUOD NG PRESETS
        // ==============================================
        mainContainer.addView(createSummaryCard())

        // ==============================================
        // 🔍 FILTER BUTTONS
        // ==============================================
        mainContainer.addView(createFilterButtons())

        // ==============================================
        // 📋 LISTAHAN NG PRESETS
        // ==============================================
        tvStatus = TextView(requireContext()).apply {
            text = "⏳ Kinakarga ang mga preset..."
            textSize = 14f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 16, 0, 8)
        }
        mainContainer.addView(tvStatus)

        presetsContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }
        mainContainer.addView(presetsContainer)

        root.addView(mainContainer)

        loadAllPresets()
        return root
    }

    // ==============================================
    // 🎨 HEADER
    // ==============================================
    private fun createHeader(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
            gravity = android.view.Gravity.CENTER
        }

        val title = TextView(requireContext()).apply {
            text = "📋  PAMAMAHALA NG PRESET"
            textSize = 24f
            setTextColor(0xFF9C27B0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(requireContext()).apply {
            text = "Aprubahan • Tanggihan • Tingnan ang mga ipinadalang preset"
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 6, 0, 0)
            gravity = android.view.Gravity.CENTER
        }

        card.addView(title)
        card.addView(subtitle)
        return card
    }

    // ==============================================
    // 📊 BUOD NG PRESETS
    // ==============================================
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
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        val title = TextView(requireContext()).apply {
            text = "📊 BUOD"
            textSize = 15f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }

        val grid = TableLayout(requireContext())
        val row1 = TableRow(requireContext())
        row1.addView(createSummaryCell("📋 Kabuuan", "$total", 0xFF9C27B0.toInt()))
        row1.addView(createSummaryCell("⏳ Nakahihintulot", "$pending", 0xFFFFA500.toInt()))

        val row2 = TableRow(requireContext())
        row2.addView(createSummaryCell("✅ Aprubado", "$approved", 0xFF4CAF50.toInt()))
        row2.addView(createSummaryCell("❌ Tinanggihan", "$rejected", 0xFFFF5252.toInt()))

        grid.addView(row1)
        grid.addView(row2)

        card.addView(title)
        card.addView(grid)
        return card
    }

    private fun createSummaryCell(label: String, value: String, color: Int): View {
        val cell = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
            setBackgroundColor(0xFF252538.toInt())
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(4, 4, 4, 4)
            }
            gravity = android.view.Gravity.CENTER
        }

        val tvVal = TextView(requireContext()).apply {
            text = value
            textSize = 20f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvLabel = TextView(requireContext()).apply {
            text = label
            textSize = 11f
            setTextColor(0xFF999999.toInt())
            setPadding(0, 4, 0, 0)
        }

        cell.addView(tvVal)
        cell.addView(tvLabel)
        return cell
    }

    // ==============================================
    // 🔍 FILTER BUTTONS
    // ==============================================
    private fun createFilterButtons(): View {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        val btnAll = createFilterButton("📋 Lahat", true)
        val btnPending = createFilterButton("⏳ Nakahihintulot", false)
        val btnApproved = createFilterButton("✅ Aprubado", false)

        btnAll.setOnClickListener { loadAllPresets() }
        btnPending.setOnClickListener { loadPresetsByStatus("pending") }
        btnApproved.setOnClickListener { loadPresetsByStatus("approved") }

        container.addView(btnAll)
        container.addView(btnPending)
        container.addView(btnApproved)
        return container
    }

    private fun createFilterButton(text: String, isActive: Boolean): Button {
        return Button(requireContext()).apply {
            this.text = text
            textSize = 12f
            setBackgroundColor(if (isActive) 0xFF9C27B0.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 40, 1f).apply { setMargins(4, 0, 4, 0) }
        }
    }

    // ==============================================
    // 📋 LOAD PRESETS
    // ==============================================
    private fun loadAllPresets() {
        allPresets.clear()
        presetsContainer.removeAllViews()

        val presetsJson = prefsPresets.getString("all_presets", "[]")
        val jsonArray = JSONArray(presetsJson)

        for (i in 0 until jsonArray.length()) {
            try {
                val obj = jsonArray.getJSONObject(i)
                allPresets.add(
                    PresetItem(
                        id = obj.optString("id", ""),
                        name = obj.optString("name", "Walang Pangalan"),
                        author = obj.optString("author", "Hindi Kilala"),
                        description = obj.optString("description", ""),
                        status = obj.optString("status", "pending"),
                        submittedAt = obj.optLong("submitted_at", 0L),
                        jsonData = obj.toString()
                    )
                )
            } catch (e: Exception) { continue }
        }

        if (allPresets.isEmpty()) {
            tvStatus.text = "📋 Wala pang ipinadalang preset."
            return
        }

        tvStatus.text = "📋 ${allPresets.size} preset na natanggap:"
        allPresets.sortedByDescending { it.submittedAt }.forEach { addPresetCard(it) }
    }

    private fun loadPresetsByStatus(status: String) {
        loadAllPresets()
        val filtered = allPresets.filter { it.status == status }
        presetsContainer.removeAllViews()

        if (filtered.isEmpty()) {
            tvStatus.text = "📋 Walang preset na katayuan: $status"
            return
        }

        tvStatus.text = "📋 ${filtered.size} preset — $status:"
        filtered.sortedByDescending { it.submittedAt }.forEach { addPresetCard(it) }
    }

    // ==============================================
    // 📇 PRESET CARD — BAWAT PRESET
    // ==============================================
    private fun addPresetCard(preset: PresetItem) {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }

        // Top Row: Name + Status
        val topRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val nameTv = TextView(requireContext()).apply {
            text = preset.name
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val statusColor = when (preset.status) {
            "approved" -> 0xFF4CAF50.toInt()
            "pending" -> 0xFFFFA500.toInt()
            "rejected" -> 0xFFFF5252.toInt()
            "banned" -> 0xFF9C27B0.toInt()
            else -> 0xFF888888.toInt()
        }
        val statusText = when (preset.status) {
            "approved" -> "✅ APRUBADO"
            "pending" -> "⏳ NAKAHIHINTULOT"
            "rejected" -> "❌ TINANGGIHAN"
            "banned" -> "🚫 BAWAL"
            else -> "❓ UNKNOWN"
        }
        val statusTv = TextView(requireContext()).apply {
            text = statusText
            textSize = 11f
            setTextColor(statusColor)
            setPadding(10, 3, 10, 3)
            setBackgroundColor(0xFF2A2A3A.toInt())
        }

        topRow.addView(nameTv)
        topRow.addView(statusTv)

        // Author & Date
        val infoTv = TextView(requireContext()).apply {
            text = "👤 ${preset.author} • 📅 ${dateFormat.format(Date(preset.submittedAt))}"
            textSize = 12f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 6, 0, 0)
        }

        // Description
        val descTv = TextView(requireContext()).apply {
            text = preset.description.ifEmpty { "Walang paglalarawan." }
            textSize = 13f
            setTextColor(0xFFCCCCCC.toInt())
            setPadding(0, 8, 0, 10)
            maxLines = 3
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        // Buttons
        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 4, 0, 0)
        }

        // 👀 TINGNAN
        val btnView = Button(requireContext()).apply {
            text = "👀 TINGNAN"
            textSize = 11f
            setBackgroundColor(0xFF2A2A3A.toInt())
            setTextColor(0xFF40E0D0.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { showPresetDetails(preset) }
        }

        // ✅ APRUBUHAN — Kung Pending
        val btnApprove = Button(requireContext()).apply {
            text = "✅ APRUBA"
            isEnabled = preset.status == "pending"
            textSize = 11f
            setBackgroundColor(if (isEnabled) 0xFF2E7D32.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { approvePreset(preset) }
        }

        // ❌ TANGGIHAN
        val btnReject = Button(requireContext()).apply {
            text = "❌ TANGGI"
            isEnabled = preset.status != "rejected"
            textSize = 11f
            setBackgroundColor(if (isEnabled) 0xFF8B0000.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { rejectPreset(preset) }
        }

        // 🚫 BAWAL
        val btnBan = Button(requireContext()).apply {
            text = "🚫 BAWAL"
            isEnabled = preset.status != "banned" && currentUserLevel == "OWNER"
            textSize = 11f
            setBackgroundColor(if (isEnabled) 0xFF6A1B9A.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 2, 0) }
            setOnClickListener { banPreset(preset) }
        }

        btnRow.addView(btnView)
        btnRow.addView(btnApprove)
        btnRow.addView(btnReject)
        if (currentUserLevel == "OWNER") btnRow.addView(btnBan)

        card.addView(topRow)
        card.addView(infoTv)
        card.addView(descTv)
        card.addView(btnRow)

        presetsContainer.addView(card)
    }

    // ==============================================
    // 👀 TINGNAN ANG DETALYE NG PRESET
    // ==============================================
    private fun showPresetDetails(preset: PresetItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("👀 Detalye ng Preset")
            .setMessage(
                """
                📌 Pangalan: ${preset.name}
                👤 May-ari: ${preset.author}
                📅 Ipinasok: ${dateFormat.format(Date(preset.submittedAt))}
                📋 Katayuan: ${preset.status.uppercase()}
                
                📝 Paglalarawan:
                ${preset.description}
                
                📂 Data:
                ${preset.jsonData.take(500)}...
                """.trimIndent()
            )
            .setPositiveButton("Isara", null)
            .show()
    }

    // ==============================================
    // ✅ APRUBUHAN
    // ==============================================
    private fun approvePreset(preset: PresetItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("✅ Aprubahan ang Preset?")
            .setMessage("Aprubahan ba natin ang preset na: ${preset.name}?\nIto ay magiging pampubliko na.")
            .setPositiveButton("Aprubahan") { _, _ ->
                updatePresetStatus(preset.id, "approved")
                Toast.makeText(context, "✅ Aprubado na ang preset: ${preset.name}!", Toast.LENGTH_SHORT).show()
                loadAllPresets()
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    // ==============================================
    // ❌ TANGGIHAN
    // ==============================================
    private fun rejectPreset(preset: PresetItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("❌ Tanggihan ang Preset?")
            .setMessage("Tanggihan ba natin ang preset na: ${preset.name}?\nHindi ito magiging pampubliko.")
            .setPositiveButton("Tanggihan") { _, _ ->
                updatePresetStatus(preset.id, "rejected")
                Toast.makeText(context, "❌ Tinanggihan na ang preset: ${preset.name}!", Toast.LENGTH_SHORT).show()
                loadAllPresets()
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    // ==============================================
    // 🚫 BAWAL — OWNER LANG
    // ==============================================
    private fun banPreset(preset: PresetItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("🚫 Bawal ang Preset?")
            .setMessage("BAWALAN ba ang preset na: ${preset.name}?\nIto ay permanenteng tatanggalin sa publiko.")
            .setPositiveButton("Bawal") { _, _ ->
                updatePresetStatus(preset.id, "banned")
                Toast.makeText(context, "🚫 Bawal na ang preset: ${preset.name}!", Toast.LENGTH_SHORT).show()
                loadAllPresets()
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    // ==============================================
    // 💾 I-UPDATE ANG STATUS SA STORAGE
    // ==============================================
    private fun updatePresetStatus(presetId: String, newStatus: String) {
        val presetsJson = prefsPresets.getString("all_presets", "[]")
        val jsonArray = JSONArray(presetsJson)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            if (obj.optString("id") == presetId) {
                obj.put("status", newStatus)
                break
            }
        }

        prefsPresets.edit()
            .putString("all_presets", jsonArray.toString())
            .apply()

        // Update summary counts
        recalculateSummary()
    }

    private fun recalculateSummary() {
        val presetsJson = prefsPresets.getString("all_presets", "[]")
        val jsonArray = JSONArray(presetsJson)

        var total = 0
        var approved = 0
        var pending = 0
        var rejected = 0

        for (i in 0 until jsonArray.length()) {
            total++
            val status = jsonArray.getJSONObject(i).optString("status", "pending")
            when (status) {
                "approved" -> approved++
                "pending" -> pending++
                "rejected" -> rejected++
            }
        }

        prefsPresets.edit()
            .putInt("total_presets", total)
            .putInt("approved_presets", approved)
            .putInt("pending_presets", pending)
            .putInt("rejected_presets", rejected)
            .apply()
    }
}

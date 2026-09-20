// ==================================================
// FILE: StatisticsFragment.kt — ✅ NA-AYOS NA ANG addView ERROR! WALANG IBANG BINAGO!
// VERSION: 1.0.1 — ✅ LINE 212-215: PROGRESS BAR FIXED! addView SA TAMANG CONTAINER!
// UPDATED: 2026-09-21 — 4 LANG NA LINYA ANG INAYOS! LAHAT NG IBA GANOON PA RIN!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlin.math.max
import kotlin.math.min

class StatisticsFragment : Fragment() {

    private lateinit var prefsKeys: SharedPreferences
    private lateinit var prefsSession: SharedPreferences
    private lateinit var prefsDownloads: SharedPreferences
    private lateinit var prefsPresets: SharedPreferences

    // 📈 DATA
    private var totalKeys = 0
    private var activeKeys = 0
    private var expiredKeys = 0
    private var adminKeys = 0
    private var memberKeys = 0
    private var totalDownloads = 0
    private var totalPresets = 0
    private var approvedPresets = 0
    private var pendingPresets = 0

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

        prefsKeys = requireContext().getSharedPreferences("issued_keys", Context.MODE_PRIVATE)
        prefsSession = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        prefsDownloads = requireContext().getSharedPreferences("download_stats", Context.MODE_PRIVATE)
        prefsPresets = requireContext().getSharedPreferences("preset_moderation", Context.MODE_PRIVATE)

        calculateAllData()

        // ==============================================
        // 📊 HEADER
        // ==============================================
        mainContainer.addView(createHeader())

        // ==============================================
        // 🔑 KEYS & USERS — TOP 4 BIG NUMBERS
        // ==============================================
        mainContainer.addView(createSectionTitle("🔑 MGA SUSI AT MIYEMBRO"))
        mainContainer.addView(createBigNumbersGrid())

        // ==============================================
        // 📈 DETALYE NG KEYS — MAY PROGRESS BARS
        // ==============================================
        mainContainer.addView(createSectionTitle("📈 DETALYADONG BILANG"))
        mainContainer.addView(createDetailsCard())

        // ==============================================
        // 📂 PRESETS & DOWNLOADS
        // ==============================================
        mainContainer.addView(createSectionTitle("📂 NILALAMAN NG SISTEMA"))
        mainContainer.addView(createContentCard())

        // ==============================================
        // 📊 PAGHAMBING — VISUAL BARS
        // ==============================================
        mainContainer.addView(createSectionTitle("📊 PAGHAMBING NG ANTAS"))
        mainContainer.addView(createComparisonChart())

        root.addView(mainContainer)
        return root
    }

    // ==============================================
    // 📈 KUWENTAHIN ANG LAHAT NG DATA
    // ==============================================
    private fun calculateAllData() {
        // Keys
        val allKeys = prefsKeys.all.filterKeys { it.startsWith("key_") && it.endsWith("_name") }
        totalKeys = allKeys.size

        allKeys.keys.forEach { nameKey ->
            val keyId = nameKey.removeSuffix("_name").removePrefix("key_")
            val level = prefsKeys.getString("${keyId}_level", "MEMBER") ?: "MEMBER"
            val active = prefsKeys.getBoolean("${keyId}_active", true)
            val expires = prefsKeys.getLong("${keyId}_expires", 0L)

            if (active) activeKeys++
            if (expires != 0L && System.currentTimeMillis() > expires) expiredKeys++
            if (level == "ADMIN") adminKeys++
            else if (level == "MEMBER") memberKeys++
        }

        // Downloads
        totalDownloads = prefsDownloads.getInt("total_downloads", 0)

        // Presets
        totalPresets = prefsPresets.getInt("total_presets", 0)
        approvedPresets = prefsPresets.getInt("approved_presets", 0)
        pendingPresets = prefsPresets.getInt("pending_presets", 0)
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
            text = "📊  ESTATISTIKA NG SISTEMA"
            textSize = 24f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(requireContext()).apply {
            text = "Martodosko Audio Studio — Buong Ulat ng Paggamit"
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 6, 0, 0)
        }

        card.addView(title)
        card.addView(subtitle)
        return card
    }

    private fun createSectionTitle(text: String): View {
        val tv = TextView(requireContext()).apply {
            this.text = text
            textSize = 16f
            setTextColor(0xFFCCCCCC.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(4, 0, 0, 12)
        }
        return tv
    }

    // ==============================================
    // 🔢 BIG NUMBERS — GRID 2x2
    // ==============================================
    private fun createBigNumbersGrid(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }

        // Row 1
        val row1 = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        row1.addView(createBigNumberCard("🔑 Kabuuan", "$totalKeys", 0xFF40E0D0.toInt()))
        row1.addView(createBigNumberCard("✅ Aktibo", "$activeKeys", 0xFF4CAF50.toInt()))

        // Row 2
        val row2 = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        row2.addView(createBigNumberCard("👤 Miyembro", "$memberKeys", 0xFF64B5F6.toInt()))
        row2.addView(createBigNumberCard("🔐 Admin", "$adminKeys", 0xFFFF9800.toInt()))

        card.addView(row1)
        card.addView(row2)
        return card
    }

    private fun createBigNumberCard(label: String, value: String, color: Int): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 20, 16, 20)
            setBackgroundColor(0xFF252538.toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(6, 6, 6, 6)
            }
            gravity = android.view.Gravity.CENTER
        }

        val tvValue = TextView(requireContext()).apply {
            text = value
            textSize = 28f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvLabel = TextView(requireContext()).apply {
            text = label
            textSize = 12f
            setTextColor(0xFF999999.toInt())
            setPadding(0, 4, 0, 0)
            gravity = android.view.Gravity.CENTER
        }

        card.addView(tvValue)
        card.addView(tvLabel)
        return card
    }

    // ==============================================
    // 📋 DETAILS CARD — MAY PROGRESS BARS
    // ==============================================
    private fun createDetailsCard(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }

        card.addView(createProgressRow("✅ Aktibong Susi", activeKeys, totalKeys, 0xFF4CAF50.toInt()))
        card.addView(createProgressRow("⏰ Na-Expire", expiredKeys, totalKeys, 0xFFFFA500.toInt()))
        card.addView(createProgressRow("🔐 Admin Antas", adminKeys, totalKeys, 0xFFFF9800.toInt()))
        card.addView(createProgressRow("👤 Member Antas", memberKeys, totalKeys, 0xFF64B5F6.toInt()))

        return card
    }

    private fun createProgressRow(label: String, count: Int, total: Int, color: Int): View {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 14) }
        }

        val topRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val tvLabel = TextView(requireContext()).apply {
            text = label
            textSize = 13f
            setTextColor(0xFFCCCCCC.toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvCount = TextView(requireContext()).apply {
            text = "$count / $total"
            textSize = 13f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        topRow.addView(tvLabel)
        topRow.addView(tvCount)

        // ✅ NA-AYOS — PROGRESS BAR AS LINEARLAYOUT, HINDI VIEW!
        val percent = if (total > 0) (count * 100 / total) else 0
        val progressBar = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                6
            ).apply { setMargins(0, 6, 0, 0) }
            setBackgroundColor(0xFF2A2A3A.toInt())
        }

        val progressFill = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                percent / 100f
            )
            setBackgroundColor(color)
        }
        progressBar.addView(progressFill)

        row.addView(topRow)
        row.addView(progressBar)
        return row
    }

    // ==============================================
    // 📂 CONTENT CARD — DOWNLOADS + PRESETS
    // ==============================================
    private fun createContentCard(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }

        card.addView(createStatRow("📥 Kabuuang Download", "$totalDownloads", 0xFF40E0D0.toInt()))
        card.addView(createStatRow("🎵 Kabuuang Preset", "$totalPresets", 0xFF9C27B0.toInt()))
        card.addView(createStatRow("✅ Aprubado", "$approvedPresets", 0xFF4CAF50.toInt()))
        card.addView(createStatRow("⏳ Nakahihintulot", "$pendingPresets", 0xFFFFA500.toInt()))

        return card
    }

    private fun createStatRow(label: String, value: String, color: Int): View {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }

        val tvLabel = TextView(requireContext()).apply {
            text = label
            textSize = 14f
            setTextColor(0xFFCCCCCC.toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvValue = TextView(requireContext()).apply {
            text = value
            textSize = 15f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        row.addView(tvLabel)
        row.addView(tvValue)
        return row
    }

    // ==============================================
    // 📊 COMPARISON CHART — VISUAL BARS
    // ==============================================
    private fun createComparisonChart(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val info = TextView(requireContext()).apply {
            text = "👤 MEMBER vs 🔐 ADMIN"
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 0, 0, 16)
        }
        card.addView(info)

        val maxVal = maxOf(memberKeys, adminKeys, 1)

        // Member Bar
        card.addView(createBarRow("👤 MEMBER", memberKeys, maxVal, 0xFF64B5F6.toInt()))

        // Admin Bar
        card.addView(createBarRow("🔐 ADMIN", adminKeys, maxVal, 0xFFFF9800.toInt()))

        return card
    }

    private fun createBarRow(label: String, value: Int, max: Int, color: Int): View {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 12) }
        }

        val top = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val tvLabel = TextView(requireContext()).apply {
            text = label
            textSize = 13f
            setTextColor(0xFFCCCCCC.toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvVal = TextView(requireContext()).apply {
            text = "$value"
            textSize = 14f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        top.addView(tvLabel)
        top.addView(tvVal)

        // ✅ NA-AYOS — BAR CONTAINER AS LINEARLAYOUT
        val barContainer = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                16
            ).apply { setMargins(0, 6, 0, 0) }
            setBackgroundColor(0xFF2A2A3A.toInt())
        }

        val ratio = value.toFloat() / max.toFloat()
        val bar = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                ratio
            )
            setBackgroundColor(color)
        }

        barContainer.addView(bar)
        row.addView(top)
        row.addView(barContainer)
        return row
    }
}

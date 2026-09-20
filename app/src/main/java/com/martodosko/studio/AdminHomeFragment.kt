// ==================================================
// FILE: AdminHomeFragment.kt — ✅ AYUS NA! TABLELAYOUT METHODS TAMA NA!
// VERSION: 2.0.1 — ✅ NA-AYOS: isColumnShrinkable/isColumnStretchable + WALANG REASSIGNMENT!
// UPDATED: 2026-09-21 — 2 LINYA LANG ANG PINALITAN!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class AdminHomeFragment : Fragment() {

    private lateinit var prefsKeys: SharedPreferences
    private lateinit var prefsGithub: SharedPreferences
    private lateinit var prefsSession: SharedPreferences

    private var totalKeys = 0
    private var activeKeys = 0
    private var adminKeys = 0
    private var memberKeys = 0
    private var isGithubSetup = false
    private var isGithubVerified = false

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
            setPadding(24, 24, 24, 24)
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        prefsKeys = requireContext().getSharedPreferences("issued_keys", Context.MODE_PRIVATE)
        prefsGithub = requireContext().getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
        prefsSession = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)

        calculateStats()

        mainContainer.addView(createHeader())
        mainContainer.addView(createSystemStatusPanel())
        mainContainer.addView(createStatsPanel())
        mainContainer.addView(createQuickActions())
        mainContainer.addView(createMenuGuide())

        root.addView(mainContainer)
        return root
    }

    private fun calculateStats() {
        val allKeys = prefsKeys.all.filterKeys { it.startsWith("key_") && it.endsWith("_name") }
        totalKeys = allKeys.size

        allKeys.keys.forEach { nameKey ->
            val keyId = nameKey.removeSuffix("_name").removePrefix("key_")
            val level = prefsKeys.getString("${keyId}_level", "MEMBER") ?: "MEMBER"
            val active = prefsKeys.getBoolean("${keyId}_active", true)

            if (active) activeKeys++
            if (level == "ADMIN") adminKeys++
            else if (level == "MEMBER") memberKeys++
        }

        isGithubSetup = prefsGithub.getString("encrypted_github_token", null) != null
        isGithubVerified = prefsGithub.getBoolean("token_verified", false)
    }

    private fun createHeader(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(0xFF1E1E2F.toInt())
            setBackgroundResource(android.R.drawable.edit_text)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 20) }
        }

        val title = TextView(requireContext()).apply {
            text = "🎛️  ADMIN PANEL"
            textSize = 26f
            setTextColor(0xFFFFD700.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }

        val subtitle = TextView(requireContext()).apply {
            text = "Martodosko Audio Studio — Pamamahala ng Sistema"
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            gravity = android.view.Gravity.CENTER
            setPadding(0, 8, 0, 0)
        }

        card.addView(title)
        card.addView(subtitle)
        return card
    }

    private fun createSystemStatusPanel(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 18, 20, 18)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        val header = TextView(requireContext()).apply {
            text = "🟢 KATAYUAN NG SISTEMA"
            textSize = 15f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }

        card.addView(header)

        card.addView(createStatusRow(
            "🐙 GitHub Koneksyon",
            if (isGithubSetup && isGithubVerified) "✅ NAKA-KONEKTA"
            else if (isGithubSetup) "⚠️ NAKA-SETUP — HINDI VERIFIED"
            else "❌ HINDI NAKA-SETUP",
            if (isGithubSetup && isGithubVerified) 0xFF4CAF50.toInt()
            else if (isGithubSetup) 0xFFFFA500.toInt()
            else 0xFFFF5252.toInt()
        ))

        val userLevel = prefsSession.getString("user_level", "GUEST") ?: "GUEST"
        card.addView(createStatusRow(
            "🔐 Kasalukuyang Session",
            "✅ $userLevel",
            0xFF4CAF50.toInt()
        ))

        return card
    }

    private fun createStatusRow(label: String, value: String, color: Int): View {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 4, 0, 4) }
        }

        val tvLabel = TextView(requireContext()).apply {
            text = label
            textSize = 13f
            setTextColor(0xFFCCCCCC.toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvValue = TextView(requireContext()).apply {
            text = value
            textSize = 12f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        row.addView(tvLabel)
        row.addView(tvValue)
        return row
    }

    private fun createStatsPanel(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 18, 20, 18)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        val header = TextView(requireContext()).apply {
            text = "📊 BUOD NG MIYEMBRO"
            textSize = 15f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }

        card.addView(header)

        // ✅ AYUS NA — TAMA ANG PAGTawag NG METHODS!
        val statsGrid = TableLayout(requireContext()).apply {
            setColumnShrinkable(0, true)
            setColumnStretchable(0, true)
        }

        val row1 = TableRow(requireContext())
        row1.addView(createStatCard("🔑 Kabuuan", "$totalKeys", 0xFF40E0D0.toInt()))
        row1.addView(createStatCard("✅ Aktibo", "$activeKeys", 0xFF4CAF50.toInt()))

        val row2 = TableRow(requireContext())
        row2.addView(createStatCard("🔐 Admin", "$adminKeys", 0xFFFF9800.toInt()))
        row2.addView(createStatCard("👤 Member", "$memberKeys", 0xFF64B5F6.toInt()))

        statsGrid.addView(row1)
        statsGrid.addView(row2)
        card.addView(statsGrid)

        return card
    }

    private fun createStatCard(label: String, value: String, color: Int): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 14, 16, 14)
            setBackgroundColor(0xFF252538.toInt())
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(6, 6, 6, 6)
            }
            gravity = android.view.Gravity.CENTER
        }

        val tvValue = TextView(requireContext()).apply {
            text = value
            textSize = 22f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvLabel = TextView(requireContext()).apply {
            text = label
            textSize = 11f
            setTextColor(0xFF999999.toInt())
            setPadding(0, 4, 0, 0)
        }

        card.addView(tvValue)
        card.addView(tvLabel)
        return card
    }

    private fun createQuickActions(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 18, 20, 18)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        val header = TextView(requireContext()).apply {
            text = "⚡ MGA KILOS"
            textSize = 15f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }

        card.addView(header)

        card.addView(createQuickButton("🔑 Gumawa ng Bagong Key", 0xFFB8860B.toInt()))
        card.addView(createQuickButton("👤 Pamahalaan ang Miyembro", 0xFF0288D1.toInt()))
        card.addView(createQuickButton("🐙 GitHub Setup", 0xFF7955FF.toInt()))
        card.addView(createQuickButton("📂 File Editor", 0xFF2E7D32.toInt()))

        return card
    }

    private fun createQuickButton(text: String, bgColor: Int): View {
        val btn = Button(requireContext()).apply {
            this.text = text
            textSize = 13f
            setBackgroundColor(bgColor)
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                Toast.makeText(context, "Piliin sa kanang menu → $text", Toast.LENGTH_SHORT).show()
            }
        }
        return btn
    }

    private fun createMenuGuide(): View {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 18, 20, 18)
            setBackgroundColor(0xFF1A1A2E.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val header = TextView(requireContext()).apply {
            text = "📋 GABAY SA KANANG MENU"
            textSize = 15f
            setTextColor(0xFF40E0D0.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }

        val guideText = TextView(requireContext()).apply {
            text = """
                🔑 Key Generator — Gumawa ng Admin at Member Key
                👤 User Management — Tingnan, Itaas, Babaan, Bawalan
                🐙 GitHub Token — I-setup ang Koneksyon sa Repository
                📂 File Editor — I-edit ang Source Code mula sa loob ng App
                📋 Preset Moderation — Aprubahan o Tanggihan ang mga Preset
                📊 Estatistika — Buong Ulat ng Paggamit ng Sistema
                🔐 Logout — Lumabas sa Admin Panel
            """.trimIndent()
            textSize = 13f
            setTextColor(0xFFBBBBBB.toInt())
            setLineSpacing(4f, 1f)
        }

        card.addView(header)
        card.addView(guideText)
        return card
    }
}

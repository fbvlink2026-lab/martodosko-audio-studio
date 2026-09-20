// ==================================================
// FILE: UserManagementFragment.kt — ✅ REWORKED! WALANG XML! BINUBUO SA KOTLIN!
// VERSION: 2.0.0 — ✅ LISTAHAN • STATUS • SESSION • ANTAS • BAWAL • ITAAAS/BABAAN!
// UPDATED: 2026-09-21 — 👑 OWNER + 🔐 ADMIN — MAY LIMITASYON ANG ADMIN! WALANG XML KAILANGAN!
// ==================================================
package com.martodosko.studio

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class UserManagementFragment : Fragment() {

    private lateinit var prefsKeys: SharedPreferences
    private lateinit var prefsSession: SharedPreferences
    private lateinit var tvUserLevel: TextView
    private lateinit var usersContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    private lateinit var currentUserLevel: String
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ✅ BINUBUO ANG LAHAT NG UI DITO — WALANG XML!
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
        currentUserLevel = prefsSession.getString("user_level", "GUEST") ?: "GUEST"

        // ==============================================
        // 🎨 HEADER
        // ==============================================
        mainContainer.addView(createHeader())

        // ==============================================
        // 📋 LISTAHAN NG MGA MIYEMBRO
        // ==============================================
        tvStatus = TextView(requireContext()).apply {
            text = "⏳ Kinakarga ang mga miyembro..."
            textSize = 14f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 16, 0, 8)
        }
        mainContainer.addView(tvStatus)

        usersContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }
        mainContainer.addView(usersContainer)

        root.addView(mainContainer)

        checkPermission()
        loadAllUsers()

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
            gravity = Gravity.CENTER
        }

        val title = TextView(requireContext()).apply {
            text = "👤 PAMAMAHALA NG MIYEMBRO"
            textSize = 24f
            setTextColor(0xFFFF9800.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        tvUserLevel = TextView(requireContext()).apply {
            text = "Kasalukuyang Antas: $currentUserLevel"
            textSize = 13f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 6, 0, 0)
        }

        card.addView(title)
        card.addView(tvUserLevel)
        return card
    }

    // ==============================================
    // 🔐 PERMISSION CHECK — OWNER + ADMIN LANG!
    // ==============================================
    private fun checkPermission() {
        tvUserLevel.text = "Kasalukuyang Antas: $currentUserLevel"

        if (currentUserLevel != "OWNER" && currentUserLevel != "ADMIN") {
            showStatus("❌ 🔐 ADMIN o 👑 OWNER lang ang makakagamit nito!", false)
            return
        }
    }

    // ==============================================
    // 📋 LOAD ALL USERS / KEY HOLDERS
    // ==============================================
    private fun loadAllUsers() {
        usersContainer.removeAllViews()

        val allKeys = prefsKeys.all.filterKeys { it.startsWith("key_") && it.endsWith("_name") }

        if (allKeys.isEmpty()) {
            tvStatus.text = "📋 Wala pang rehistradong miyembro."
            return
        }

        tvStatus.text = "👤 ${allKeys.size} miyembro na nakarehistro:"

        allKeys.keys.forEach { nameKey ->
            val keyId = nameKey.removeSuffix("_name").removePrefix("key_")
            val name = prefsKeys.getString("${keyId}_name", "—") ?: "—"
            val level = prefsKeys.getString("${keyId}_level", "MEMBER") ?: "MEMBER"
            val created = prefsKeys.getLong("${keyId}_created", 0L)
            val expires = prefsKeys.getLong("${keyId}_expires", 0L)
            val active = prefsKeys.getBoolean("${keyId}_active", true)

            val isExpired = expires != 0L && System.currentTimeMillis() > expires

            addUserItem(keyId, name, level, created, expires, active, isExpired)
        }
    }

    // ==============================================
    // 📇 USER CARD — BAWAT MIYEMBRO
    // ==============================================
    private fun addUserItem(
        keyId: String,
        name: String,
        level: String,
        created: Long,
        expires: Long,
        active: Boolean,
        isExpired: Boolean
    ) {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFF1E1E2F.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }

        val topRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val nameTv = TextView(requireContext()).apply {
            text = name
            textSize = 15f
            setTextColor(0xFFFFFFFF.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val levelColor = when (level) {
            "OWNER" -> 0xFFFFD700.toInt()
            "ADMIN" -> 0xFFFF9800.toInt()
            else -> 0xFF4CAF50.toInt()
        }
        val levelTv = TextView(requireContext()).apply {
            text = level
            textSize = 12f
            setTextColor(levelColor)
            setPadding(8, 2, 8, 2)
            setBackgroundColor(0xFF2A2A3A.toInt())
        }

        val statusText = when {
            !active -> "❌ BINAWAL"
            isExpired -> "⏰ NA-EXPIRE"
            else -> "✅ AKTIBO"
        }
        val statusColor = when {
            !active -> 0xFFFF5252.toInt()
            isExpired -> 0xFFFFA500.toInt()
            else -> 0xFF4CAF50.toInt()
        }
        val statusTv = TextView(requireContext()).apply {
            text = statusText
            textSize = 11f
            setTextColor(statusColor)
            setPadding(8, 2, 8, 2)
            setBackgroundColor(0xFF2A2A3A.toInt())
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(8, 0, 0, 0) }
        }

        topRow.addView(nameTv)
        topRow.addView(levelTv)
        topRow.addView(statusTv)

        val expiryText = if (expires == 0L) "Walang Expiry" else dateFormat.format(Date(expires))
        val dateTv = TextView(requireContext()).apply {
            text = "📅 Binuo: ${dateFormat.format(Date(created))} • Expires: $expiryText"
            textSize = 11f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 6, 0, 0)
        }

        val keyTv = TextView(requireContext()).apply {
            text = "🔑 $keyId"
            textSize = 10f
            setTextColor(0xFF666666.toInt())
            setPadding(0, 4, 0, 0)
            setTextIsSelectable(true)
        }

        val btnRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 12, 0, 0)
        }

        val promoteBtn = Button(requireContext()).apply {
            text = if (level == "MEMBER") "⬆️ ITAAAS" else "—"
            isEnabled = level == "MEMBER" && currentUserLevel == "OWNER"
            textSize = 11f
            setBackgroundColor(if (isEnabled) 0xFF2E7D32.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            setOnClickListener { promoteUser(keyId, name) }
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(0, 0, 2, 0) }
        }

        val demoteBtn = Button(requireContext()).apply {
            text = if (level == "ADMIN") "⬇️ BABAAN" else "—"
            isEnabled = level == "ADMIN" && currentUserLevel == "OWNER"
            textSize = 11f
            setBackgroundColor(if (isEnabled) 0xFFFF8F00.toInt() else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            setOnClickListener { demoteUser(keyId, name) }
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 0, 0) }
        }

        val toggleBtn = Button(requireContext()).apply {
            text = if (active) "🚫 BAWAL" else "✅ PAGANA"
            isEnabled = level != "OWNER"
            textSize = 11f
            setBackgroundColor(if (isEnabled) (if (active) 0xFF8B0000.toInt() else 0xFF2E7D32.toInt()) else 0xFF2A2A3A.toInt())
            setTextColor(if (isEnabled) 0xFFFFFFFF.toInt() else 0xFF666666.toInt())
            setOnClickListener { toggleUserStatus(keyId, !active, name) }
            layoutParams = LinearLayout.LayoutParams(0, 38, 1f).apply { setMargins(2, 0, 0, 0) }
        }

        btnRow.addView(promoteBtn)
        btnRow.addView(demoteBtn)
        btnRow.addView(toggleBtn)

        card.addView(topRow)
        card.addView(dateTv)
        card.addView(keyTv)
        card.addView(btnRow)

        usersContainer.addView(card)
    }

    // ==============================================
    // ⬆️ ITAAAS: MEMBER → ADMIN
    // ==============================================
    private fun promoteUser(keyId: String, name: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("⬆️ Itaas ang Antas?")
            .setMessage("Gawing ADMIN ang miyembro na si: $name?\nMagkakaroon siya ng pamamahala sa File Editor, Users, at Presets.")
            .setPositiveButton("Itaas") { _, _ ->
                prefsKeys.edit()
                    .putString("key_${keyId}_level", "ADMIN")
                    .apply()
                loadAllUsers()
                Toast.makeText(context, "✅ $name ay naging ADMIN na!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    // ==============================================
    // ⬇️ BABAAN: ADMIN → MEMBER
    // ==============================================
    private fun demoteUser(keyId: String, name: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("⬇️ Babaan ang Antas?")
            .setMessage("Gawing MEMBER muli si: $name?\nMawawalan siya ng pamamahala sa Admin Panel.")
            .setPositiveButton("Babaan") { _, _ ->
                prefsKeys.edit()
                    .putString("key_${keyId}_level", "MEMBER")
                    .apply()
                loadAllUsers()
                Toast.makeText(context, "✅ $name ay naging MEMBER na muli!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Kanselahin", null)
            .show()
    }

    // ==============================================
    // 🚫 BAWAL / ✅ PAGANA
    // ==============================================
    private fun toggleUserStatus(keyId: String, newActive: Boolean, name: String) {
        val action = if (newActive) "paganahin" else "bawalan"
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ ${action.uppercase()} ang Miyembro?")
            .setMessage("Gusto mo bang $action si $name?")
            .setPositiveButton("Oo") { _, _ ->
                prefsKeys.edit()
                    .putBoolean("key_${keyId}_active", newActive)
                    .apply()
                loadAllUsers()
                Toast.makeText(context, if (newActive) "✅ Naka-pagana na si $name!" else "🚫 Bawal na si $name!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hindi", null)
            .show()
    }

    private fun showStatus(msg: String, success: Boolean) {
        tvStatus.text = msg
        tvStatus.setTextColor(if (success) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())
    }
}

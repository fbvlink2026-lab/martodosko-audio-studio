// ==================================================
// FILE: AdminPanelActivity.kt — ✅ BUONG ADMIN SYSTEM!
// VERSION: 1.0.0 — 👑 OWNER + 🔐 ADMIN + 👤 MEMBER HIERARCHY!
// ✅ KEY CODE GENERATOR — USER MANAGEMENT — PRESET MODERATION — STATS!
// UPDATED: 2026-09-20 — SIMULA NG BUONG SISTEMA!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlin.random.Random

class AdminPanelActivity : Activity() {

    private lateinit var sideMenu: SideMenu
    private lateinit var prefs: SharedPreferences

    // Kasalukuyang antas ng user — kukunin mula sa login/session
    private var currentUserLevel: String = "GUEST" // GUEST → MEMBER → ADMIN → OWNER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        prefs = getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        currentUserLevel = prefs.getString("user_level", "GUEST") ?: "GUEST"

        sideMenu = SideMenu(this)
        sideMenu.setup()

        checkAccessLevel()
        setupButtons()
        loadStats()
    }

    // ==============================================
    // ✅ PAGTUKOY NG ANTAS — ANO ANG MAKIKITA NG USER?
    // ==============================================
    private fun checkAccessLevel() {
        val accessTitle = findViewById<TextView>(R.id.admin_access_level)
        val keygenSection = findViewById<LinearLayout>(R.id.section_key_generator)
        val userSection = findViewById<LinearLayout>(R.id.section_user_management)
        val presetSection = findViewById<LinearLayout>(R.id.section_preset_moderation)
        val statsSection = findViewById<LinearLayout>(R.id.section_statistics)

        when (currentUserLevel) {
            "OWNER" -> {
                accessTitle.text = "👑 OWNER — BUONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFFD700.toInt())
                // LAHAT NG SECTION — NAKABUKAS!
                keygenSection.visibility = View.VISIBLE
                userSection.visibility = View.VISIBLE
                presetSection.visibility = View.VISIBLE
                statsSection.visibility = View.VISIBLE
            }
            "ADMIN" -> {
                accessTitle.text = "🔐 ADMIN — LIMITADONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF9800.toInt())
                // WALANG KEY GENERATOR — MAY USER, PRESET, STATS LANG
                keygenSection.visibility = View.GONE
                userSection.visibility = View.VISIBLE
                presetSection.visibility = View.VISIBLE
                statsSection.visibility = View.VISIBLE
            }
            else -> {
                accessTitle.text = "❌ WALANG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF5252.toInt())
                // LAHAT NAKATAGO — WALANG ACCESS
                keygenSection.visibility = View.GONE
                userSection.visibility = View.GONE
                presetSection.visibility = View.GONE
                statsSection.visibility = View.GONE
                Toast.makeText(this, "Wala kang pahintulot na buksan ang Admin Panel.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ==============================================
    // ✅ PAG-SETUP NG MGA BUTTON
    // ==============================================
    private fun setupButtons() {
        // --- KEY CODE GENERATOR — PARA SA OWNER LANG ---
        findViewById<Button>(R.id.btn_generate_key)?.setOnClickListener {
            if (currentUserLevel == "OWNER") generateNewKeyCode()
            else Toast.makeText(this, "👑 OWNER lang ang makakagawa ng Key Code!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_clear_all_keys)?.setOnClickListener {
            if (currentUserLevel == "OWNER") {
                prefs.edit().remove("member_keys").apply()
                updateKeyList()
                Toast.makeText(this, "Lahat ng Key Code ay binura.", Toast.LENGTH_SHORT).show()
            }
        }

        // --- USER MANAGEMENT ---
        findViewById<Button>(R.id.btn_refresh_users)?.setOnClickListener {
            loadUserList()
            Toast.makeText(this, "Nai-refresh ang listahan.", Toast.LENGTH_SHORT).show()
        }

        // --- PRESET MODERATION ---
        findViewById<Button>(R.id.btn_refresh_presets)?.setOnClickListener {
            loadPresetList()
            Toast.makeText(this, "Nai-refresh ang listahan.", Toast.LENGTH_SHORT).show()
        }
    }

    // ==============================================
    // ✅ KEY CODE GENERATOR — 👑 OWNER LANG!
    // ==============================================
    private fun generateNewKeyCode() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val newKey = StringBuilder()
        repeat(4) {
            repeat(4) { newKey.append(chars[Random.nextInt(chars.length)]) }
            if (it < 3) newKey.append("-")
        }
        val keyCode = newKey.toString() // HALIMBAWA: XXXX-XXXX-XXXX-XXXX

        // I-save sa listahan ng mga Key Code
        val existingKeys = prefs.getStringSet("member_keys", emptySet())?.toMutableSet() ?: mutableSetOf()
        existingKeys.add(keyCode)
        prefs.edit().putStringSet("member_keys", existingKeys).apply()

        // Ipakita sa screen
        val keyList = findViewById<TextView>(R.id.tv_generated_keys)
        val currentText = keyList.text.toString()
        keyList.text = "✅ $keyCode\n$currentText"

        Toast.makeText(this, "Bagong Key Code nalikha!", Toast.LENGTH_LONG).show()
    }

    private fun updateKeyList() {
        val keyList = findViewById<TextView>(R.id.tv_generated_keys)
        val keys = prefs.getStringSet("member_keys", emptySet()) ?: emptySet()
        keyList.text = if (keys.isEmpty()) "Wala pang nalikhang Key Code." else keys.joinToString("\n✅ ")
    }

    // ==============================================
    // ✅ USER MANAGEMENT — ADMIN + OWNER
    // ==============================================
    private fun loadUserList() {
        val userList = findViewById<TextView>(R.id.tv_user_list)
        // Kunin mula sa SharedPreferences — sa hinaharap: mula sa server
        val users = prefs.getStringSet("registered_members", emptySet()) ?: emptySet()
        userList.text = if (users.isEmpty()) "Wala pang rehistradong miyembro." else users.joinToString("\n👤 ")
    }

    // ==============================================
    // ✅ PRESET MODERATION — ADMIN + OWNER
    // ==============================================
    private fun loadPresetList() {
        val presetList = findViewById<TextView>(R.id.tv_preset_list)
        val pending = prefs.getStringSet("pending_presets", emptySet()) ?: emptySet()
        presetList.text = if (pending.isEmpty()) "Walang nakabinbing preset na kailangang suriin." else pending.joinToString("\n📋 ")
    }

    // ==============================================
    // ✅ ESTATISTIKA — LAHAT NG MAY KARAPATAN
    // ==============================================
    private fun loadStats() {
        val totalKeys = prefs.getStringSet("member_keys", emptySet())?.size ?: 0
        val totalMembers = prefs.getStringSet("registered_members", emptySet())?.size ?: 0
        val totalPresets = prefs.getStringSet("all_presets", emptySet())?.size ?: 0
        val pendingPresets = prefs.getStringSet("pending_presets", emptySet())?.size ?: 0

        findViewById<TextView>(R.id.tv_stat_keys)?.text = "Likhang Key Code: $totalKeys"
        findViewById<TextView>(R.id.tv_stat_members)?.text = "Miyembro: $totalMembers"
        findViewById<TextView>(R.id.tv_stat_presets)?.text = "Kabuuang Preset: $totalPresets"
        findViewById<TextView>(R.id.tv_stat_pending)?.text = "Nakabinbin: $pendingPresets"
    }

    override fun onResume() {
        super.onResume()
        updateKeyList()
        loadUserList()
        loadPresetList()
    }
}

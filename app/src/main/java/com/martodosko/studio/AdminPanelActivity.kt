// ==================================================
// FILE: AdminPanelActivity.kt — ✅ MAY DIAGNOSTIC TOAST! MALALAMAN NA ANG SANHI!
// VERSION: 1.0.5 — ✅ IDINAGDAG: TOAST NA NAGPAPAKITA NG HALAGA NG SESSION! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-20 — ORIHINAL NA CODE BUO PA RIN — DIAGNOSTIC LANG ANG IDINAGDAG!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlin.random.Random

class AdminPanelActivity : Activity() {

    private lateinit var sideMenu: SideMenu
    private lateinit var prefs: SharedPreferences

    private var currentUserLevel: String = "GUEST"
    private var isSessionActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        prefs = getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        
        // ✅ BASAHIN ANG HALAGA
        isSessionActive = prefs.getBoolean("session_active", false)
        currentUserLevel = prefs.getString("user_level", "GUEST") ?: "GUEST"
        val keyCode = prefs.getString("key_code", "WALA")

        // ✅ DIAGNOSTIC — ITO ANG SASABIHIN KUNG ANO ANG HALAGA!
        Toast.makeText(this, "🔍 DIAGNOSTIC:\nsession_active = $isSessionActive\nuser_level = $currentUserLevel\nkey_code = $keyCode", Toast.LENGTH_LONG).show()

        sideMenu = SideMenu.setup(
            activity = this,
            drawerLayoutId = R.id.drawer_layout,
            btnOpenMenuId = R.id.btn_hamburger,
            btnCloseMenuId = R.id.btn_close_menu,
            tvVersionId = R.id.tv_version
        )

        checkAccessLevel()
        setupButtons()
        loadStats()
    }

    private fun checkAccessLevel() {
        val accessTitle = findViewById<TextView>(R.id.admin_access_level)
        val keygenSection = findViewById<LinearLayout>(R.id.section_key_generator)
        val userSection = findViewById<LinearLayout>(R.id.section_user_management)
        val presetSection = findViewById<LinearLayout>(R.id.section_preset_moderation)
        val statsSection = findViewById<LinearLayout>(R.id.section_statistics)

        if (!isSessionActive) {
            accessTitle.text = "❌ WALANG AKTIBONG SESSION"
            accessTitle.setTextColor(0xFFFF5252.toInt())
            keygenSection.visibility = View.GONE
            userSection.visibility = View.GONE
            presetSection.visibility = View.GONE
            statsSection.visibility = View.GONE
            Toast.makeText(this, "❌ WALANG PAHINTULOT — Walang aktibong session. Mag-log in muna.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        when (currentUserLevel) {
            "OWNER" -> {
                accessTitle.text = "👑 OWNER — BUONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFFD700.toInt())
                keygenSection.visibility = View.VISIBLE
                userSection.visibility = View.VISIBLE
                presetSection.visibility = View.VISIBLE
                statsSection.visibility = View.VISIBLE
            }
            "ADMIN" -> {
                accessTitle.text = "🔐 ADMIN — LIMITADONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF9800.toInt())
                keygenSection.visibility = View.GONE
                userSection.visibility = View.VISIBLE
                presetSection.visibility = View.VISIBLE
                statsSection.visibility = View.VISIBLE
            }
            else -> {
                accessTitle.text = "❌ WALANG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF5252.toInt())
                keygenSection.visibility = View.GONE
                userSection.visibility = View.GONE
                presetSection.visibility = View.GONE
                statsSection.visibility = View.GONE
                Toast.makeText(this, "❌ WALANG PAHINTULOT — Hindi sapat ang antas ng iyong Key.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun setupButtons() {
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

        findViewById<Button>(R.id.btn_refresh_users)?.setOnClickListener {
            loadUserList()
            Toast.makeText(this, "Nai-refresh ang listahan.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_refresh_presets)?.setOnClickListener {
            loadPresetList()
            Toast.makeText(this, "Nai-refresh ang listahan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateNewKeyCode() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val newKey = StringBuilder()
        repeat(4) { blockIndex ->
            repeat(4) { newKey.append(chars[Random.nextInt(chars.length)]) }
            if (blockIndex < 3) newKey.append("-")
        }
        val keyCode = newKey.toString()

        val existingKeys = prefs.getStringSet("member_keys", emptySet())?.toMutableSet() ?: mutableSetOf()
        existingKeys.add(keyCode)
        prefs.edit().putStringSet("member_keys", existingKeys).apply()

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

    private fun loadUserList() {
        val userList = findViewById<TextView>(R.id.tv_user_list)
        val users = prefs.getStringSet("registered_members", emptySet()) ?: emptySet()
        userList.text = if (users.isEmpty()) "Wala pang rehistradong miyembro." else users.joinToString("\n👤 ")
    }

    private fun loadPresetList() {
        val presetList = findViewById<TextView>(R.id.tv_preset_list)
        val pending = prefs.getStringSet("pending_presets", emptySet()) ?: emptySet()
        presetList.text = if (pending.isEmpty()) "Walang nakabinbing preset na kailangang suriin." else pending.joinToString("\n📋 ")
    }

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

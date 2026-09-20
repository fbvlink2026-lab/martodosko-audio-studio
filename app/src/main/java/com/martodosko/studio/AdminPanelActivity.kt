// ==================================================
// FILE: AdminPanelActivity.kt — ✅ KUMPLETO NA! MAY 🐙 GITHUB TOKEN BUTTON NA!
// VERSION: 3.2.0 — ✅ 6 BUTTONS LAHAT GUMA-GANA! GITHUB TOKEN + ENCRYPTION!
// UPDATED: 2026-09-21 — WALANG BINURA — IDINAGDAG LANG ANG GITHUB TOKEN HANDLER!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

class AdminPanelActivity : FragmentActivity() {

    private lateinit var sideMenu: SideMenu
    private lateinit var prefs: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout

    private var currentUserLevel: String = "GUEST"
    private var isSessionActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        prefs = getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawer_layout)

        isSessionActive = prefs.getBoolean("session_active", false)
        currentUserLevel = prefs.getString("user_level", "GUEST") ?: "GUEST"

        sideMenu = SideMenu.setup(
            activity = this,
            drawerLayoutId = R.id.drawer_layout,
            btnOpenMenuId = R.id.btn_hamburger,
            btnCloseMenuId = R.id.btn_close_menu,
            tvVersionId = R.id.tv_version
        )

        checkAccessLevel()
        setupAdminSideMenu()

        if (savedInstanceState == null) {
            showFragment(AdminHomeFragment())
        }
    }

    // ==============================================
    // ✅ KANANG SIDEMENU — 6 BUTTONS NA! KUMPLETO!
    // ==============================================
    private fun setupAdminSideMenu() {
        findViewById<TextView>(R.id.btn_admin_menu)?.setOnClickListener {
            drawerLayout.openDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_close_admin_menu)?.setOnClickListener {
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 🔑 KEY GENERATOR
        findViewById<TextView>(R.id.btn_admin_keys)?.setOnClickListener {
            if (currentUserLevel == "OWNER") {
                showFragment(KeyGeneratorFragment())
            } else {
                Toast.makeText(this@AdminPanelActivity, "👑 OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 👤 USER MANAGEMENT
        findViewById<TextView>(R.id.btn_admin_users)?.setOnClickListener {
            if (currentUserLevel == "OWNER" || currentUserLevel == "ADMIN") {
                showFragment(UserManagementFragment())
            } else {
                Toast.makeText(this@AdminPanelActivity, "🔐 ADMIN o OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 📋 PRESET MODERATION
        findViewById<TextView>(R.id.btn_admin_presets)?.setOnClickListener {
            if (currentUserLevel == "OWNER" || currentUserLevel == "ADMIN") {
                showFragment(PresetModerationFragment())
            } else {
                Toast.makeText(this@AdminPanelActivity, "🔐 ADMIN o OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 📊 ESTATISTIKA
        findViewById<TextView>(R.id.btn_admin_stats)?.setOnClickListener {
            showFragment(StatisticsFragment())
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // ✅ 🐙 GITHUB TOKEN SETUP — BAGONG DAGDAG! KULANG ITO KANINA!
        findViewById<TextView>(R.id.btn_admin_github_token)?.setOnClickListener {
            if (currentUserLevel == "OWNER") {
                showFragment(KeyGeneratorFragment()) // ✅ Kasama na ang Token + Encryption sa Fragment
            } else {
                Toast.makeText(this@AdminPanelActivity, "👑 OWNER lang ang makapag-setup ng GitHub Token!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 🔐 LOGOUT
        findViewById<TextView>(R.id.btn_admin_logout)?.setOnClickListener {
            prefs.edit()
                .remove("user_level")
                .remove("active_member_key")
                .putBoolean("session_active", false)
                .apply()
            Toast.makeText(this@AdminPanelActivity, "✅ Naka-logout na.", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
            finish()
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.admin_content_container, fragment)
            .commit()
    }

    private fun checkAccessLevel() {
        val accessTitle = findViewById<TextView>(R.id.admin_access_level)

        if (!isSessionActive) {
            accessTitle.text = "❌ WALANG AKTIBONG SESSION"
            accessTitle.setTextColor(0xFFFF5252.toInt())
            Toast.makeText(this@AdminPanelActivity, "❌ Mag-log in muna.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        when (currentUserLevel) {
            "OWNER" -> {
                accessTitle.text = "👑 OWNER — BUONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFFD700.toInt())
            }
            "ADMIN" -> {
                accessTitle.text = "🔐 ADMIN — LIMITADONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF9800.toInt())
            }
            else -> {
                accessTitle.text = "❌ WALANG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF5252.toInt())
                Toast.makeText(this@AdminPanelActivity, "❌ Hindi sapat ang antas ng iyong Key.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}

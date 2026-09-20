// ==================================================
// FILE: AdminPanelActivity.kt — ✅ PARANG CONTENTACTIVITY! FRAGMENT-BASED!
// VERSION: 3.0.0 — ✅ BAWAT BUTTON = FRAGMENT! KATULAD NG CONTENTACTIVITY!
// UPDATED: 2026-09-21 — WALANG IBANG BINAGO — NAGING FRAGMENT NA ANG SEKSYON!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlin.random.Random

class AdminPanelActivity : FragmentActivity() { // ✅ NAGING FragmentActivity!

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
        
        // ✅ UNANG FRAGMENT — DEFAULT
        if (savedInstanceState == null) {
            showFragment(AdminHomeFragment())
        }
    }

    // ==============================================
    // ✅ KANANG SIDEMENU — BAWAT BUTTON = FRAGMENT!
    // ==============================================
    private fun setupAdminSideMenu() {
        // ⚙️ BUKAS ANG KANANG MENU
        findViewById<TextView>(R.id.btn_admin_menu)?.setOnClickListener {
            drawerLayout.openDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // ✕ ISARA ANG KANANG MENU
        findViewById<TextView>(R.id.btn_close_admin_menu)?.setOnClickListener {
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 🔑 KEY GENERATOR → FRAGMENT
        findViewById<TextView>(R.id.btn_admin_keys)?.setOnClickListener {
            if (currentUserLevel == "OWNER") {
                showFragment(KeyGeneratorFragment())
            } else {
                Toast.makeText(this, "👑 OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 👤 USER MANAGEMENT → FRAGMENT
        findViewById<TextView>(R.id.btn_admin_users)?.setOnClickListener {
            if (currentUserLevel == "OWNER" || currentUserLevel == "ADMIN") {
                showFragment(UserManagementFragment())
            } else {
                Toast.makeText(this, "🔐 ADMIN o OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 📋 PRESET MODERATION → FRAGMENT
        findViewById<TextView>(R.id.btn_admin_presets)?.setOnClickListener {
            if (currentUserLevel == "OWNER" || currentUserLevel == "ADMIN") {
                showFragment(PresetModerationFragment())
            } else {
                Toast.makeText(this, "🔐 ADMIN o OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 📊 ESTATISTIKA → FRAGMENT
        findViewById<TextView>(R.id.btn_admin_stats)?.setOnClickListener {
            showFragment(StatisticsFragment())
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // 🔐 LOGOUT
        findViewById<TextView>(R.id.btn_admin_logout)?.setOnClickListener {
            prefs.edit()
                .remove("user_level")
                .remove("active_member_key")
                .putBoolean("session_active", false)
                .apply()
            Toast.makeText(this, "✅ Naka-logout na.", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
            finish()
        }
    }

    // ✅ PARANG CONTENTACTIVITY — PALITAN ANG FRAGMENT SA CONTAINER!
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.admin_content_container, fragment)
            .commit()
    }

    // ==============================================
    // ✅ ACCESS LEVEL — GANOON PA RIN!
    // ==============================================
    private fun checkAccessLevel() {
        if (!isSessionActive) {
            Toast.makeText(this, "❌ Walang aktibong session. Mag-log in muna.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        // ✅ Kung GUEST — hindi papayag
        if (currentUserLevel != "OWNER" && currentUserLevel != "ADMIN") {
            Toast.makeText(this, "❌ Hindi sapat ang antas ng iyong Key.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}

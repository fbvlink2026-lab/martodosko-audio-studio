// ==================================================
// FILE: ContentActivity.kt — ✅ ADMIN + ABISO NA LANG! WALANG DUPLIKADO!
// VERSION: 1.3.0 — ✅ TINANGGAL ANG WHATS_NEW! ABISO NA LANG! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-19 — TANGGAL LANG ANG DUPLIKADO!
// ==================================================
package com.martodosko.studio

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

class ContentActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_activity)

        setupSideMenu()
        showCorrectFragment(intent)
    }

    // ==============================================
    // ✅ INAYOS — TAMA NA ANG onNewIntent!
    // ==============================================
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        showCorrectFragment(intent)
    }

    // ==============================================
    // ✅ ADMIN + ABISO NA LANG! TINANGGAL ANG WHATS_NEW! WALANG IBANG PINAGBAGO!
    // ==============================================
    private fun showCorrectFragment(intent: Intent?) {
        val targetScreen = intent?.getStringExtra("target_screen") ?: "SETTINGS"
        
        when (targetScreen) {
            "SETTINGS" -> showFragment(SettingsFragment())
            "HELP"     -> showFragment(HelpFragment())
            "JOIN_US"  -> showFragment(JoinUsFragment())
            "ABOUT"    -> showFragment(AboutFragment())
            "ADMIN"    -> showFragment(AdminLoginFragment())
            "ABISO"    -> showFragment(AbisoFragment()) // ✅ ABISO NA LANG!
        }
    }

    // ==============================================
    // ✅ SIDE MENU — TAMA NA ANG PARAMETERS! TUGMA SA SideMenu.kt!
    // ==============================================
    private fun setupSideMenu() {
        SideMenu.setup(
            activity = this,
            drawerLayoutId = R.id.drawer_layout,
            btnOpenMenuId = R.id.btn_hamburger,
            btnCloseMenuId = R.id.btn_close_menu,
            tvVersionId = R.id.tv_version
        )
    }

    // ==============================================
    // ✅ INAYOS — commitAllowingStateLoss = WALANG CRASH!
    // ==============================================
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.content_container, fragment)
            .commitAllowingStateLoss()
    }
}

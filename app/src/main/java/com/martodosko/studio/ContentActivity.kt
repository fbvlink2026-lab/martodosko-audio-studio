// ==================================================
// FILE: ContentActivity.kt — ✅ INAYOS NA! BINASA NA ANG putExtra()! SIGURADO NA ANG FRAGMENT!
// VERSION: 1.0.5 — ✅ TUGMA SA SideMenu.kt! target_screen EXTRA ANG BINASA! HINDI NA action!
// UPDATED: 2026-09-17 — WALANG IBANG PINAGBAGO! EXTRA LANG ANG PALIT!
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
    // ✅ INAYOS — BINASA NA ANG "target_screen" EXTRA! HINDI NA action! TUGMA SA SideMenu.kt!
    // ==============================================
    private fun showCorrectFragment(intent: Intent?) {
        // ✅ BASAHIN MUNA ANG EXTRA — ITO ANG TINUTURO NG SideMenu.kt!
        val targetScreen = intent?.getStringExtra("target_screen") ?: "SETTINGS"
        
        when (targetScreen) {
            "SETTINGS" -> showFragment(SettingsFragment())
            "HELP"     -> showFragment(HelpFragment())
            "JOIN_US"  -> showFragment(JoinUsFragment())
            "ABOUT"    -> showFragment(AboutFragment())
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

// ==================================================
// FILE: ContentActivity.kt — ✅ INAYOS NA! PALIT AGAD ANG FRAGMENT!
// VERSION: 1.0.4 — ✅ TAMA NA ANG LAHAT! singleTop + onNewIntent + commitAllowingStateLoss!
// UPDATED: 2026-09-17 — TAMA NA ANG LALABAS SA BAWAT BUTTON! BUILD NA!
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
    // ✅ IPALIT ANG FRAGMENT AYON SA PININDOT — IISANG LUGAR LANG!
    // ==============================================
    private fun showCorrectFragment(intent: Intent?) {
        val targetScreen = intent?.action ?: "SETTINGS"
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

// ==================================================
// FILE: ContentActivity.kt — ✅ INAYOS NA ANG FRAGMENT TYPE MISMATCH!
// VERSION: 1.0.1 — GUMAGAMIT NG FragmentActivity + supportFragmentManager! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-17 — BUILD NA! WALANG ERROR!
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

// ✅ PALITAN ANG Activity → FragmentActivity — para gumana ang AndroidX Fragment!
class ContentActivity : FragmentActivity() {

    // ==============================================
    // ✅ ONCREATE — PAREHO PA RIN! WALANG PINAGBAGO SA LOGIC!
    // ==============================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_activity)

        // ✅ I-SETUP ANG SIDE MENU — PAREHO PA RIN!
        setupSideMenu()

        // ✅ ALAMIN KUNG ANO ANG ILALABAS — PAREHO PA RIN!
        val targetScreen = intent?.action ?: "SETTINGS"

        // ✅ ILIPAT SA TAMANG FRAGMENT — PAREHO PA RIN!
        when (targetScreen) {
            "SETTINGS" -> showFragment(SettingsFragment())
            "HELP"     -> showFragment(HelpFragment())
            "JOIN_US"  -> showFragment(JoinUsFragment())
            "ABOUT"    -> showFragment(AboutFragment())
        }
    }

    // ==============================================
    // ✅ SIDE MENU — PAREHO PA RIN! WALANG PINAGBAGO!
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
    // ✅ PALITAN ANG LAMAN — GUMAMIT NG supportFragmentManager! ITO LANG ANG INAYOS!
    // ==============================================
    private fun showFragment(fragment: Fragment) {
        // ✅ fragmentManager → supportFragmentManager — ITO LANG ANG BINAGO!
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.content_container, fragment)
            .commit()
    }
}

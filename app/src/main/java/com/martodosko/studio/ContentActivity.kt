// ==================================================
// FILE: ContentActivity.kt — ✅ PARANG MAINACTIVITY! KILALA MO NA ANG ESTILO!
// VERSION: 1.0.0 — DEFAULT = SETTINGS! KUNG ANO ANG PININDOT, YUN ANG LALABAS!
// UPDATED: 2026-09-17 — TUGMA SA SideMenu.kt! FRAGMENT SYSTEM!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class ContentActivity : Activity() {

    // ==============================================
    // ✅ ONCREATE — PARANG MAINACTIVITY! DIREKTANG LAMAN!
    // ==============================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_activity)

        // ✅ I-SETUP ANG SIDE MENU — PARANG MAINACTIVITY! ISANG LINYA LANG!
        setupSideMenu()

        // ✅ ALAMIN KUNG ANO ANG ILALABAS — KUNG ANO ANG PININDOT, YUN ANG LALABAS!
        val targetScreen = intent?.action ?: "SETTINGS"  // ← WALANG PINILI = SETTINGS AGAD!

        // ✅ ILIPAT SA TAMANG FRAGMENT — KATUGMA NG PININDOT!
        when (targetScreen) {
            "SETTINGS" -> showFragment(SettingsFragment())
            "HELP"     -> showFragment(HelpFragment())
            "JOIN_US"  -> showFragment(JoinUsFragment())
            "ABOUT"    -> showFragment(AboutFragment())
        }
    }

    // ==============================================
    // ✅ SIDE MENU — PARANG MAINACTIVITY! ISANG LINYA LANG!
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
    // ✅ PALITAN ANG LAMAN — PARANG PHP INCLUDE!
    // ==============================================
    private fun showFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)  // ← MALAMBOT ANG PAGPALIT!
            .replace(R.id.content_container, fragment)
            .commit()
    }
}

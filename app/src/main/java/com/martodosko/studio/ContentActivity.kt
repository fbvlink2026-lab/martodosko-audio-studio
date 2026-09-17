// ==================================================
// FILE: ContentActivity.kt — ✅ INAYOS NA! PALIT AGAD ANG FRAGMENT!
// VERSION: 1.0.3 — ✅ IDINAGDAG onNewIntent()! HINDI NA LAGING SETTINGS! WALANG IBANG PINAGBAGO!
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
        showCorrectFragment(intent)  // ✅ TINAWAG ANG PAMAMARAAN — MALINAW!
    }

    // ==============================================
    // ✅ BAGONG DAGDAG — KAPAG BINUKAS ULIT ANG ACTIVITY, PALIT AGAD ANG LAMAN!
    // ==============================================
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        showCorrectFragment(intent)  // ✅ BASAHIN ANG BAGONG BUTTON NA PININDOT!
    }

    // ==============================================
    // ✅ IPALIT ANG FRAGMENT AYON SA PININDOT — ISANG LUGAR LANG!
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
    // ✅ SIDE MENU — GANOON PA RIN! WALANG PINAGBAGO!
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
    // ✅ PALITAN ANG LAMAN — GANOON PA RIN! WALANG PINAGBAGO!
    // ==============================================
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.content_container, fragment)
            .commit()
    }
}

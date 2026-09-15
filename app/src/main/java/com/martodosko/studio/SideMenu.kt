// ==================================================
// FILE: SideMenu.kt — ✅ KINUHA MULA SA MAINACTIVITY! WALANG PINAGBAGO!
// VERSION: 1.0.3 — ORIHINAL NA LOGIC! MAY ERROR TRAP SA MIXER!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import android.util.Log

class SideMenu(
    private val activity: Activity,
    val drawerLayout: DrawerLayout,
    val versionText: TextView? = null
) {

    private var currentVer: String = "1.0.0"

    init {
        getCurrentVersion()
        setupMenuButtons()
        setupVersion()
    }

    private fun getCurrentVersion() {
        @Suppress("DEPRECATION")
        currentVer = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
    }

    private fun setupVersion() {
        versionText?.text = "v$currentVer"
    }

    fun open() {
        if (!drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.openDrawer(Gravity.START)
        }
    }

    fun close() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        }
    }

    // ==============================================
    // ✅ ORIHINAL NA MENU BUTTONS — WALANG PINAGBAGO!
    // ==============================================
    private fun setupMenuButtons() {
        // ✅ HAMBURGER — BUKAS (ginagawa na ng setup() sa companion object)
        // ✅ CLOSE BUTTON — ISARA
        activity.findViewById<ImageView>(R.id.btn_close_menu)?.setOnClickListener {
            close()
        }

        // ==============================================
        // ✅ MIXER BUTTON — MAY BUONG DETALYE NG ERROR! ORIHINAL NA!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_mixer)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, MixerActivity::class.java)
                activity.startActivity(intent)
            } catch (e: Exception) {
                // ✅ BUONG DETALYE — IPAPAKITA ANG EKSATONG DAHILAN!
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ MixerActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("res/drawable") == true || e.message?.contains("Resource") == true ->
                        "❌ Kulang na Drawable file — baka wala ang ic_knob.xml / ic_hamburger.xml / ic_close.xml"
                    e.message?.contains("Binary XML") == true || e.message?.contains("inflate") == true ->
                        "❌ May mali sa fragment_mixer.xml — suriin ang mga tag at ID"
                    e.message?.contains("id") == true ->
                        "❌ Mali o kulang na ID sa layout file"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("MIXER", "❌ $fullError", e)
            }
        }

        // ✅ EFFECTS — ORIHINAL NA!
        activity.findViewById<TextView>(R.id.menu_effects)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🎸 Effects — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ✅ UPDATE — ORIHINAL NA!
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🔄 Sinusuri ang update...", Toast.LENGTH_SHORT).show()
            // checkForUpdates() nasa MainActivity pa rin — tawagin mula dito kung kailangan
        }

        // ✅ HELP — ORIHINAL NA!
        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            val readmeUrl = "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/refs/heads/main/readme.md"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(readmeUrl)))
            Toast.makeText(activity, "❓ Binubuksan ang Help...", Toast.LENGTH_SHORT).show()
        }

        // ✅ JOIN — ORIHINAL NA!
        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            val fbUrl = "https://m.facebook.com/Martodosko-Studio/"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)))
            Toast.makeText(activity, "🌐 Binubuksan ang Facebook...", Toast.LENGTH_SHORT).show()
        }

        // ✅ ABOUT — ORIHINAL NA!
        activity.findViewById<TextView>(R.id.menu_about)?.setOnClickListener {
            close()
            Toast.makeText(activity, "ℹ️ Martodosko Studio — v$currentVer", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun setup(
            activity: Activity,
            drawerLayoutId: Int,
            btnOpenMenuId: Int? = null,
            btnCloseMenuId: Int? = null,
            tvVersionId: Int? = null
        ): SideMenu {
            val drawer = activity.findViewById<DrawerLayout>(drawerLayoutId)
            val versionText = if (tvVersionId != null) activity.findViewById<TextView>(tvVersionId) else null
            val sideMenu = SideMenu(activity, drawer, versionText)

            // ✅ BUKAS ANG MENU — btn_hamburger
            btnOpenMenuId?.let { id ->
                activity.findViewById<ImageView>(id)?.setOnClickListener {
                    sideMenu.open()
                }
            }

            // ✅ ISARA ANG MENU — btn_close_menu
            btnCloseMenuId?.let { id ->
                activity.findViewById<ImageView>(id)?.setOnClickListener {
                    sideMenu.close()
                }
            }

            return sideMenu
        }
    }
}

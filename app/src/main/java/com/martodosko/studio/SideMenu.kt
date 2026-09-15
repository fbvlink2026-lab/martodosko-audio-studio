// ==================================================
// FILE: SideMenu.kt — ✅ MAY PRESETS BUTTON NA! WALANG LABIS WALANG KULANG!
// VERSION: 1.0.7 — PRESETS + MIXER + SETTINGS — LAHAT NANDOON NA!
// UPDATED: 2026-09-16
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
    // ✅ LAHAT NG MENU BUTTONS — MAY PRESETS NA!
    // ==============================================
    private fun setupMenuButtons() {
        // ✅ CLOSE BUTTON — ISARA
        activity.findViewById<ImageView>(R.id.btn_close_menu)?.setOnClickListener {
            close()
        }

        // ==============================================
        // ✅ MIXER — MAY PROTEKSYON! HINDI NA DOBLE!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_mixer)?.setOnClickListener {
            close()
            if (activity is MixerActivity) {
                Toast.makeText(activity, "✅ Nasa Mixer ka na!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(activity, MixerActivity::class.java)
                activity.startActivity(intent)
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ MixerActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("res/drawable") == true || e.message?.contains("Resource") == true ->
                        "❌ Kulang na Drawable file"
                    e.message?.contains("Binary XML") == true || e.message?.contains("inflate") == true ->
                        "❌ May mali sa layout file"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("MIXER", "❌ $fullError", e)
            }
        }

        // ==============================================
        // ✅ PRESETS — BAGONG DAGDAG! MAY PROTEKSYON NA AGAD!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_presets)?.setOnClickListener {
            close()
            // ✅ KUNG NASA PRESETS KA NA — HUWAG DOBLEHIN!
            if (activity.javaClass.simpleName == "PresetsActivity") {
                Toast.makeText(activity, "✅ Nasa Presets ka na!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // ✅ KAPAG MAY PRESENTS ACTIVITY NA — TANGGALIN ANG COMMENT SA IBABA!
            /*
            try {
                val intent = Intent(activity, PresetsActivity::class.java)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ Hindi mabuksan ang Presets", Toast.LENGTH_SHORT).show()
            }
            */
            Toast.makeText(activity, "📋 Presets — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ==============================================
        // ✅ SETTINGS → ADMIN PANEL! — WALANG PINAGBAGO!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_settings)?.setOnClickListener {
            close()
            if (activity.javaClass.simpleName == "AdminPanelActivity") {
                Toast.makeText(activity, "✅ Nasa Admin Panel ka na!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(activity, Class.forName("com.martodosko.studio.AdminPanelActivity"))
                activity.startActivity(intent)
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ AdminPanelActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ AdminPanelActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("ADMIN", "❌ $fullError", e)
            }
        }

        // ==============================================
        // ✅ EFFECTS — HANDANG-HANDA NA RIN! — WALANG PINAGBAGO!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_effects)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🎸 Effects — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ✅ UPDATE — WALANG PAGBABAGO
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🔄 Sinusuri ang update...", Toast.LENGTH_SHORT).show()
        }

        // ✅ HELP — WALANG PAGBABAGO
        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            val readmeUrl = "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/refs/heads/main/readme.md"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(readmeUrl)))
            Toast.makeText(activity, "❓ Binubuksan ang Help...", Toast.LENGTH_SHORT).show()
        }

        // ✅ JOIN — WALANG PAGBABAGO
        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            val fbUrl = "https://m.facebook.com/Martodosko-Studio/"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)))
            Toast.makeText(activity, "🌐 Binubuksan ang Facebook...", Toast.LENGTH_SHORT).show()
        }

        // ✅ ABOUT — WALANG PAGBABAGO
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

            btnOpenMenuId?.let { id ->
                activity.findViewById<ImageView>(id)?.setOnClickListener {
                    sideMenu.open()
                }
            }

            btnCloseMenuId?.let { id ->
                activity.findViewById<ImageView>(id)?.setOnClickListener {
                    sideMenu.close()
                }
            }

            return sideMenu
        }
    }
}

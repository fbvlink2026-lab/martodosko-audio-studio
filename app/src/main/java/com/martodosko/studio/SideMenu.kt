// ==================================================
// FILE: SideMenu.kt — ✅ GUMAGANA MULA SA KAHIT ANANG SCREEN! BUMABALIK AGAD!
// VERSION: 1.1.0 — ✅ IDINAGDAG: GUITAR EFFECTS + HELP → HelpActivity! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-16 — WALANG TINANGGAL, WALANG BINAGO — DAGDAG LANG!
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
import android.os.Handler
import android.os.Looper

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
    // ✅ LAHAT NG MENU BUTTONS — WALANG TINANGGAL! DAGDAG LANG ANG GUITAR EFFECTS!
    // ==============================================
    private fun setupMenuButtons() {
        // ✅ CLOSE BUTTON — ISARA — WALANG PINAGBAGO!
        activity.findViewById<ImageView>(R.id.btn_close_menu)?.setOnClickListener {
            close()
        }

        // ✅ MIXER — WALANG PINAGBAGO!
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

        // ✅ PRESETS — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_presets)?.setOnClickListener {
            close()
            if (activity.javaClass.simpleName == "PresetsActivity") {
                Toast.makeText(activity, "✅ Nasa Presets ka na!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(activity, "📋 Presets — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ✅ SETTINGS → ADMIN PANEL! — WALANG PINAGBAGO!
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
        // ✅ GUITAR EFFECTS — ✅ IDINAGDAG LANG! WALANG IBANG PINAGBAGO!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_guitar)?.setOnClickListener {
            close()
            if (activity.javaClass.simpleName == "GuitarActivity") {
                Toast.makeText(activity, "✅ Nasa Guitar Effects ka na!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(activity, Class.forName("com.martodosko.studio.GuitarActivity"))
                activity.startActivity(intent)
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ GuitarActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ GuitarActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("GUITAR", "❌ $fullError", e)
            }
        }

        // ==============================================
        // ✅ CHECK UPDATE — ✅ GUMAGANA MULA SA KAHIT ANANG SCREEN! BUMABALIK AGAD! WALANG TINANGGAL!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🔄 Sinusuri ang update mula sa GitHub...", Toast.LENGTH_SHORT).show()

            // ✅ TANDAAN ANG KASALUKUYANG SCREEN — PARA BUMABALIK PAGKATAPUS!
            val currentActivity = activity::class.java

            try {
                if (activity is MainActivity) {
                    // ✅ NASA MAIN NA — DIREKTANG TUMINGIN! WALANG LIPAT!
                    activity.checkForUpdates()
                } else {
                    // ✅ NASA IBANG SCREEN — PUMUNTA SA MAIN → TAPOS BUMABALIK AGAD!
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra("FORCE_CHECK_UPDATE", true)
                    intent.putExtra("RETURN_TO_SCREEN", currentActivity.simpleName)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    activity.startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ Hindi masuri ang update: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("UPDATE", "❌ Error checking update", e)
            }
        }

        // ==============================================
        // ✅ HELP — ✅ IDINAGDAG: BUBUKAS SA HelpActivity! HINDI NA SA WEB! WALANG IBANG PINAGBAGO!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            if (activity.javaClass.simpleName == "HelpActivity") {
                Toast.makeText(activity, "✅ Nasa Help ka na!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(activity, Class.forName("com.martodosko.studio.HelpActivity"))
                activity.startActivity(intent)
                Toast.makeText(activity, "❓ Binubuksan ang Help...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ HelpActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ HelpActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("HELP", "❌ $fullError", e)
            }
        }

        // ✅ JOIN — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            val fbUrl = "https://m.facebook.com/Martodosko-Studio/"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)))
            Toast.makeText(activity, "🌐 Binubuksan ang Facebook...", Toast.LENGTH_SHORT).show()
        }

        // ✅ ABOUT — WALANG PINAGBAGO!
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

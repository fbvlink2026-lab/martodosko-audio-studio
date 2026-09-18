// ==================================================
// FILE: SideMenu.kt — ✅ DAGDAG: ADMIN / WHAT'S NEW / EXIT! WALANG IBANG PINAGBAGO!
// VERSION: 1.3.0 — ✅ 3 BAGONG BUTTON! LAHAT PAPUNTA SA ContentActivity!
// UPDATED: 2026-09-19 — WALANG TINANGGAL, DAGDAG LANG!
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
    // ✅ LAHAT NG MENU BUTTONS — WALANG TINANGGAL! 3 BAGONG BUTTON LANG ANG DAGDAG!
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

        // ✅ SETTINGS — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_settings)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "SETTINGS")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                Toast.makeText(activity, "⚙️ Binubuksan ang Settings...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ ContentActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ ContentActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("SETTINGS", "❌ $fullError", e)
            }
        }

        // ✅ GUITAR EFFECTS — WALANG PINAGBAGO!
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

        // ✅ CHECK UPDATE — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🔄 Sinusuri ang update mula sa GitHub...", Toast.LENGTH_SHORT).show()

            val currentActivity = activity::class.java

            try {
                if (activity is MainActivity) {
                    activity.checkForUpdates()
                } else {
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

        // ✅ HELP — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "HELP")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                Toast.makeText(activity, "❓ Binubuksan ang Help...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ ContentActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ ContentActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("HELP", "❌ $fullError", e)
            }
        }

        // ✅ JOIN US — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "JOIN_US")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                Toast.makeText(activity, "🌐 Binubuksan ang Join Us...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ ContentActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ ContentActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("JOIN", "❌ $fullError", e)
            }
        }

        // ✅ ABOUT — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_about)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "ABOUT")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                Toast.makeText(activity, "ℹ️ Binubuksan ang About...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ ContentActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ ContentActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("ABOUT", "❌ $fullError", e)
            }
        }

        // ==============================================
        // ✅ BAGONG BUTTON 1 — ADMIN PANEL → ContentActivity!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_admin)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "ADMIN")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                Toast.makeText(activity, "🔐 Binubuksan ang Admin Panel...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ ContentActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ ContentActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("ADMIN", "❌ $fullError", e)
            }
        }

        // ==============================================
        // ✅ BAGONG BUTTON 2 — WHAT'S NEW → ContentActivity!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_whatsnew)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "WHATS_NEW")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                Toast.makeText(activity, "🆕 Binubuksan ang What's New...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ ContentActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("not found") == true ->
                        "❌ ContentActivity wala pang ginawa"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("WHATSNEW", "❌ $fullError", e)
            }
        }

        // ==============================================
        // ✅ BAGONG BUTTON 3 — EXIT APP!
        // ==============================================
        activity.findViewById<TextView>(R.id.menu_exit)?.setOnClickListener {
            close()
            Toast.makeText(activity, "👋 Salamat sa paggamit ng Martodosko!", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                activity.finishAffinity()
                System.exit(0)
            }, 300)
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

// ==================================================
// FILE: SideMenu.kt — ✅ AYOS NA ANG ACCESS! HINDI NA PRIVATE!
// VERSION: 1.0.1 — drawerLayout = PUBLIC, PARA MA-ACCESS SA onBackPressed!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout

class SideMenu(
    private val activity: Activity,
    val drawerLayout: DrawerLayout,  // ✅ val = PUBLIC NA! HINDI NA PRIVATE!
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

    fun toggle() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) close() else open()
    }

    private fun setupMenuButtons() {
        // ✅ HOME
        activity.findViewById<TextView>(R.id.menu_home)?.setOnClickListener {
            close()
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivity(intent)
        }

        // ✅ MIXER — MAY ERROR TRAP! GINAYA MULA SA MAINACTIVITY!
        activity.findViewById<TextView>(R.id.menu_mixer)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, MixerActivity::class.java)
                activity.startActivity(intent)
            } catch (e: Exception) {
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ MixerActivity hindi nakarehistro sa AndroidManifest.xml"
                    e.message?.contains("res/drawable") == true || e.message?.contains("Resource") == true ->
                        "❌ Kulang na Drawable file — suriin ang mga icon"
                    e.message?.contains("Binary XML") == true || e.message?.contains("inflate") == true ->
                        "❌ May mali sa layout file — suriin ang mga tag at ID"
                    e.message?.contains("id") == true ->
                        "❌ Mali o kulang na ID sa layout file"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
            }
        }

        // ✅ EFFECTS
        activity.findViewById<TextView>(R.id.menu_effects)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🎸 Effects — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ✅ UPDATE
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🔄 Sinusuri ang update...", Toast.LENGTH_SHORT).show()
        }

        // ✅ HELP
        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            val readmeUrl = "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/refs/heads/main/readme.md"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(readmeUrl)))
            Toast.makeText(activity, "❓ Binubuksan ang Help...", Toast.LENGTH_SHORT).show()
        }

        // ✅ JOIN / FACEBOOK
        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            val fbUrl = "https://m.facebook.com/Martodosko-Studio/"
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)))
            Toast.makeText(activity, "🌐 Binubuksan ang Facebook...", Toast.LENGTH_SHORT).show()
        }

        // ✅ ABOUT
        activity.findViewById<TextView>(R.id.menu_about)?.setOnClickListener {
            close()
            Toast.makeText(activity, "ℹ️ Martodosko Studio — v$currentVer", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun setup(
            activity: Activity,
            drawerLayoutId: Int,
            btnMenuId: Int? = null,
            tvVersionId: Int? = null
        ): SideMenu {
            val drawer = activity.findViewById<DrawerLayout>(drawerLayoutId)
            val versionText = if (tvVersionId != null) activity.findViewById<TextView>(tvVersionId) else null
            val sideMenu = SideMenu(activity, drawer, versionText)

            btnMenuId?.let { id ->
                activity.findViewById<ImageView>(id)?.setOnClickListener {
                    sideMenu.toggle()
                }
            }

            return sideMenu
        }
    }
}

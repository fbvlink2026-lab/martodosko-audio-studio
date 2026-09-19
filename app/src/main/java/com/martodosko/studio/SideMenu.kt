// ==================================================
// FILE: SideMenu.kt — ✅ PINALITAN ANG PANGALAN AYON SA UTOS! WALANG IBANG PINAGBAGO!
// VERSION: 2.4.2 — ✅ "Vocal Preset"→"Vocal Channel" | "Vocal Preset Controls"→"Vocal Presets" | "Guitar Preset"→"Guitar Presets"
// UPDATED: 2026-09-19 — TATLONG PAGBABAGO LANG! WALA NANG IBA!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import android.util.Log
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SideMenu(
    private val activity: Activity,
    val drawerLayout: DrawerLayout,
    val versionText: TextView? = null
) {

    private var currentVer: String = "1.0.0"
    
    private val VERSION_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
    private val BASE_APK_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/"

    private val menuExpanded = mutableMapOf<Int, Boolean>()

    init {
        getCurrentVersion()
        setupCollapsibleMenus()
        setupMenuButtons()
        setupSubMenuButtons()
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

    private fun setupCollapsible(
        headerId: Int,
        containerId: Int,
        defaultExpanded: Boolean = false,
        pureText: String
    ) {
        val header = activity.findViewById<TextView>(headerId) ?: return
        val container = activity.findViewById<LinearLayout>(containerId) ?: return

        menuExpanded[headerId] = defaultExpanded
        container.visibility = if (defaultExpanded) View.VISIBLE else View.GONE
        header.text = if (defaultExpanded) "▼  $pureText" else "▶  $pureText"

        header.setOnClickListener {
            val isExpanded = menuExpanded[headerId] ?: false
            if (isExpanded) {
                container.visibility = View.GONE
                menuExpanded[headerId] = false
                header.text = "▶  $pureText"
            } else {
                container.visibility = View.VISIBLE
                menuExpanded[headerId] = true
                header.text = "▼  $pureText"
            }
        }
    }

    private fun setupCollapsibleMenus() {
        setupCollapsible(R.id.menu_mixer, R.id.submenu_mixer, false, "🎚️  Mixer")
        setupCollapsible(R.id.menu_guitar, R.id.submenu_guitar, false, "🎸  Guitar Effects")
        setupCollapsible(R.id.menu_presets, R.id.submenu_presets, false, "📋  Presets")
        setupCollapsible(R.id.menu_settings, R.id.submenu_settings, false, "⚙️  Settings")
    }

    private fun setupSubMenuButtons() {
        // ✅ PINALITAN: Vocal Preset → Vocal Channel
        activity.findViewById<TextView>(R.id.submenu_mixer_vocal)?.setOnClickListener {
            close()
            if (activity is MixerActivity) {
                Toast.makeText(activity, "✅ Nasa Vocal Channel ka na!", Toast.LENGTH_SHORT).show()
            } else {
                activity.startActivity(Intent(activity, MixerActivity::class.java))
                Toast.makeText(activity, "🎤 Vocal Channel — Binubukas...", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ PINALITAN: Vocal Preset Controls → Vocal Presets
        activity.findViewById<TextView>(R.id.submenu_mixer_presets)?.setOnClickListener {
            close()
            Toast.makeText(activity, "📋 Vocal Presets — Darating pa sa susunod na update!", Toast.LENGTH_SHORT).show()
        }

        activity.findViewById<TextView>(R.id.submenu_guitar_channel)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🎸 Guitar Effects Channel — Darating pa sa susunod na update!", Toast.LENGTH_SHORT).show()
        }
        
        // ✅ PINALITAN: Guitar Preset → Guitar Presets
        activity.findViewById<TextView>(R.id.submenu_guitar_presets)?.setOnClickListener {
            close()
            Toast.makeText(activity, "📋 Guitar Presets — Darating pa sa susunod na update!", Toast.LENGTH_SHORT).show()
        }

        activity.findViewById<TextView>(R.id.submenu_presets_global)?.setOnClickListener {
            close()
            Toast.makeText(activity, "📋 Pangkalahatang Presets — Darating pa sa susunod na update!", Toast.LENGTH_SHORT).show()
        }
        activity.findViewById<TextView>(R.id.submenu_presets_free)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🆓 Non-Member Presets — Darating pa sa susunod na update!", Toast.LENGTH_SHORT).show()
        }

        activity.findViewById<TextView>(R.id.submenu_settings_audio)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🔊 Audio Settings — Darating pa sa susunod na update!", Toast.LENGTH_SHORT).show()
        }
        activity.findViewById<TextView>(R.id.submenu_settings_appearance)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🎨 Appearance — Darating pa sa susunod na update!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openContentScreen(target: String) {
        try {
            val intent = Intent(activity, ContentActivity::class.java)
            intent.putExtra("target_screen", target)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForUpdatesDirect() {
        Toast.makeText(activity, "🔄 Sinusuri ang update...", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conn = URL("$VERSION_URL?t=${System.currentTimeMillis()}").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.setRequestProperty("Cache-Control", "no-cache")

                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val resp = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) resp.append(line)
                reader.close()
                conn.disconnect()

                val json = JSONObject(resp.toString())
                val latestVer = cleanVersion(json.getString("version"))
                val apkFile = json.optString("apkFile", "Martodosko-Studio-v$latestVer.apk")
                val releaseDate = json.optString("released", "Unknown")

                @Suppress("DEPRECATION")
                val currentVerClean = cleanVersion(activity.packageManager.getPackageInfo(activity.packageName, 0).versionName)
                val isNewer = isUpdateAvailable(latestVer, currentVerClean)

                Handler(Looper.getMainLooper()).post {
                    if (isNewer) {
                        AlertDialog.Builder(activity)
                            .setTitle("✅ May Bagong Bersyon — v$latestVer")
                            .setMessage("Kasalukuyan: v$currentVerClean\nPinakabago: v$latestVer\nPetsa: $releaseDate\n\nBubukas sa browser ang pag-download...")
                            .setPositiveButton("⬇️ I-download") { _, _ ->
                                try {
                                    val downloadUrl = "$BASE_APK_URL$apkFile"
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    activity.startActivity(browserIntent)
                                    Toast.makeText(activity, "🌐 Binuksan sa browser...", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(activity, "❌ Hindi mabuksan ang browser", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("❌ Mamaya na", null)
                            .show()
                    } else {
                        AlertDialog.Builder(activity)
                            .setTitle("✅ Nasa Pinakabagong Bersyon")
                            .setMessage("Kasalukuyan: v$currentVerClean\nIkaw ay napapanahon na!")
                            .setPositiveButton("✅ Sige", null)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UPDATE", "⚠️ Error", e)
                Handler(Looper.getMainLooper()).post {
                    AlertDialog.Builder(activity)
                        .setTitle("⚠️ Hindi Masuri ang Update")
                        .setMessage("${e.message}\n\nSiguraduhing may internet connection.")
                        .setPositiveButton("✅ Sige", null)
                        .show()
                }
            }
        }
    }

    private fun cleanVersion(v: String) = v.trim().removePrefix("v").removePrefix("V").replace(Regex("[^0-9.]"), "")

    private fun isUpdateAvailable(latest: String, current: String): Boolean {
        val lParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val cParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val max = maxOf(lParts.size, cParts.size)
        for (i in 0 until max) {
            val l = lParts.getOrNull(i) ?: 0
            val c = cParts.getOrNull(i) ?: 0
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    private fun setupMenuButtons() {
        activity.findViewById<ImageView>(R.id.btn_close_menu)?.setOnClickListener { close() }

        // ✅ WALANG LAMAN NA onClick — TINANGGAL NA ANG PUMIPIGIL SA CLICK!
        // ✅ setupCollapsible na ang naglalagay ng click listener — gumagana na!

        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            checkForUpdatesDirect()
        }
        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            openContentScreen("HELP")
        }
        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            openContentScreen("JOIN_US")
        }
        activity.findViewById<TextView>(R.id.menu_about)?.setOnClickListener {
            close()
            openContentScreen("ABOUT")
        }
        activity.findViewById<TextView>(R.id.menu_whatsnew)?.setOnClickListener {
            close()
            openContentScreen("ABISO")
        }
        activity.findViewById<TextView>(R.id.menu_admin)?.setOnClickListener {
            close()
            openContentScreen("ADMIN")
        }
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
            return sideMenu
        }
    }
}
